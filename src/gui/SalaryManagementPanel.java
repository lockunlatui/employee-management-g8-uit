package gui;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

import dao.EmployeeDAO;
import dao.SalaryDAO;
import model.Employee;
import model.Salary;

@SuppressWarnings("serial")
public class SalaryManagementPanel extends JPanel {

    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel recordCountLabel;
    private boolean isSelecting = false;

    private List<Salary> allSalaries = new ArrayList<>();
    private List<Salary> filteredSalaries = new ArrayList<>();
    private int currentPage = 1;
    private final int rowsPerPage = 30;
    private JButton prevButton, nextButton;

    public SalaryManagementPanel() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("Quản lý lương", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Search & control panel
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm theo tên nhân viên:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        controlPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        recordCountLabel = new JLabel("Tổng cộng: 0");
        buttonPanel.add(recordCountLabel);
        buttonPanel.add(addButton);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table columns
        String[] columnNames = { "STT", "Mã nhân viên", "Tên nhân viên", "Lương cơ bản", "Khấu trừ",
                "Trạng thái", "Ngày tạo", "Ngày cập nhập", "Hành động" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        salaryTable = new JTable(tableModel);
        salaryTable.setRowHeight(30);
        salaryTable.getColumn("Hành động").setCellEditor(new ActionCellEditor());
        salaryTable.getColumn("Hành động").setCellRenderer(new ActionCellRenderer());

        JScrollPane scrollPane = new JScrollPane(salaryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Pagination panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("Trước");
        nextButton = new JButton("Tiếp");
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);
        add(paginationPanel, BorderLayout.SOUTH);

        // Add button logic
        addButton.addActionListener(e -> openAddSalaryDialog());

        // Search field listener
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                currentPage = 1;
                filterData();
            }
        });

        // Pagination buttons
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });

        nextButton.addActionListener(e -> {
            int totalPages = (int) Math.ceil(filteredSalaries.size() / (double) rowsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                updateTable();
            }
        });

        loadSalaryData();
    }

    // Load all salary data from database
    private void loadSalaryData() {
        SalaryDAO dao = new SalaryDAO();
        try {
            allSalaries = dao.getAllSalaries();
            filterData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    // Filter salaries by employee name
    private void filterData() {
        String keyword = searchField.getText().trim().toLowerCase();
        filteredSalaries.clear();
        for (Salary salary : allSalaries) {
            if (salary.getEmployee().getEmployeeName().toLowerCase().contains(keyword)) {
                filteredSalaries.add(salary);
            }
        }
        updateTable();
    }

    // Update table content based on filtered results and current page
    private void updateTable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        tableModel.setRowCount(0);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredSalaries.size());

        String nullString = "Chưa cập nhật";

        for (int i = start; i < end; i++) {
            Salary salary = filteredSalaries.get(i);

            UUID employeeId = salary.getEmployeeId() != null ? salary.getEmployeeId() : null;
            String employeeName = salary.getEmployee() != null ? salary.getEmployee().getEmployeeName() : nullString;
            BigDecimal base = salary.getBaseSalary() != null ? salary.getBaseSalary() : BigDecimal.ZERO;
            BigDecimal deductions = salary.getDeductions() != null ? salary.getDeductions() : BigDecimal.ZERO;
            String status = salary.getStatus() != null ? salary.getStatus() : nullString;
            String createdAt = salary.getCreatedAt() != null ? dateFormat.format(salary.getCreatedAt()) : nullString;
            String updatedAt = salary.getUpdatedAt() != null ? dateFormat.format(salary.getUpdatedAt()) : nullString;

            tableModel.addRow(new Object[] {
                    i + 1,
                    employeeId,
                    employeeName,
                    base,
                    deductions,
                    status,
                    createdAt,
                    updatedAt,
                    ""
            });
        }

        recordCountLabel.setText("Tổng cộng: " + filteredSalaries.size());
    }

    // Dialog for adding a new salary entry
    private void openAddSalaryDialog() {
        SalaryDAO salaryDAO = new SalaryDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        JDialog dialog = new JDialog((Frame) null, "Thêm lương", true);
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.add(panel);

        panel.add(new JLabel("Chọn nhân viên:"));
        JComboBox<String> empBox = new JComboBox<>();
        panel.add(empBox);

        panel.add(new JLabel("Lương cơ bản:"));
        JTextField baseField = new JTextField();
        panel.add(baseField);

        panel.add(new JLabel("Khấu trừ:"));
        JTextField allowanceField = new JTextField();
        panel.add(allowanceField);

        panel.add(new JLabel("Trạng thái:"));
        JComboBox<String> statusBox = new JComboBox<>(
                new String[] { "Đã thanh toán", "Chưa thanh toán", "Đang xử lý" });
        panel.add(statusBox);

        JButton add = new JButton("Thêm");
        JButton cancel = new JButton("Hủy");
        panel.add(add);
        panel.add(cancel);

        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            for (Employee employee : employees) {
                empBox.addItem(employee.getEmployeeName() + ":" + employee.getId());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi lấy danh sách nhân viên: " + e.getMessage());
        }

        add.addActionListener(e -> {
            try {
                BigDecimal base = new BigDecimal(baseField.getText());
                BigDecimal deductions = new BigDecimal(allowanceField.getText());

                String selectedEmployee = (String) empBox.getSelectedItem();
                String status = (String) statusBox.getSelectedItem();
                String[] partsSelectedEmploy = selectedEmployee.split(":");
                String emName = partsSelectedEmploy[0];
                UUID emUuid = UUID.fromString(partsSelectedEmploy[1]);

                Salary newSalary = new Salary();
                newSalary.setBaseSalary(base);
                newSalary.setDeductions(deductions);
                newSalary.setStatus(status);
                newSalary.setEmployeeId(emUuid);
                Employee employee = new Employee();
                employee.setEmployeeName(emName);
                newSalary.setEmployee(employee);
                salaryDAO.insertSalary(newSalary);
                allSalaries.add(0, newSalary);
                filterData();

                JOptionPane.showMessageDialog(dialog, "Thêm lương thành công!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Dữ liệu không hợp lệ");
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setSize(700, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Dialog for editing an existing salary entry
    private void openEditSalaryDialog(Salary salary) {
        SalaryDAO salaryDAO = new SalaryDAO();

        JDialog dialog = new JDialog((Frame) null, "Sửa thông tin lương", true);
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.add(panel);

        panel.add(new JLabel("Mã nhân viên:"));
        JTextField employField = new JTextField(
                salary.getDeductions() != null ? salary.getEmployeeId().toString() : "");
        employField.setEditable(false);
        panel.add(employField);

        panel.add(new JLabel("Lương cơ bản:"));
        JTextField baseField = new JTextField(salary.getBaseSalary() != null ? salary.getBaseSalary().toString() : "");
        panel.add(baseField);

        panel.add(new JLabel("Khấu trừ:"));
        JTextField deductionField = new JTextField(
                salary.getDeductions() != null ? salary.getDeductions().toString() : "");
        panel.add(deductionField);

        panel.add(new JLabel("Trạng thái:"));
        JComboBox<String> statusBox = new JComboBox<>(
                new String[] { "Đã thanh toán", "Chưa thanh toán", "Đang xử lý" });
        statusBox.setSelectedItem(salary.getStatus());
        panel.add(statusBox);

        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        panel.add(saveButton);
        panel.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                BigDecimal base = new BigDecimal(baseField.getText().trim());
                BigDecimal deductions = new BigDecimal(deductionField.getText().trim());
                String status = (String) statusBox.getSelectedItem();
                salary.setBaseSalary(base);
                salary.setDeductions(deductions);
                salary.setStatus(status);
                salary.setUpdatedAt(new java.util.Date());
                salaryDAO.updateSalary(salary);

                // Update the salary in the list
                UUID targetUuid = salary.getId(); // hoặc salary.uuid nếu bạn dùng public field
                int index = -1;

                for (int i = 0; i < allSalaries.size(); i++) {
                    if (allSalaries.get(i).getId().equals(targetUuid)) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    allSalaries.set(index, salary);
                }
                // Refresh the table
                filterData();
                JOptionPane.showMessageDialog(dialog, "Cập nhật lương thành công!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Dữ liệu không hợp lệ hoặc lỗi xảy ra.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setSize(700, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ===== Custom Cell Editor for "Hành động" Column =====
    class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private int rowIndex;

        public ActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            panel.add(editButton);
            panel.add(deleteButton);

            // Edit action (to be implemented)
            editButton.addActionListener(e -> {
                fireEditingStopped();
                UUID salaryUuid = filteredSalaries.get(rowIndex).getId();
                SalaryDAO salaryDAO = new SalaryDAO();
                Salary salary = null;
                try {
                    salary = salaryDAO.getOne(salaryUuid);
                    openEditSalaryDialog(salary);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(SalaryManagementPanel.this,
                            "Lỗi khi lấy thông tin lương: " + ex.getMessage());
                }
            });

            // Delete action
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                Salary salary = filteredSalaries.get(rowIndex);
                int confirm = JOptionPane.showConfirmDialog(SalaryManagementPanel.this,
                        "Bạn có chắc chắn muốn xóa lương này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        SalaryDAO dao = new SalaryDAO();
                        dao.deleteSalary(salary.getId());
                        allSalaries.remove(salary);
                        filterData();
                        JOptionPane.showMessageDialog(SalaryManagementPanel.this, "Xóa thành công!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(SalaryManagementPanel.this, "Lỗi khi xóa: " + ex.getMessage());
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            rowIndex = (currentPage - 1) * rowsPerPage + row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    // ===== Renderer for "Hành động" Column =====
    class ActionCellRenderer extends JPanel implements TableCellRenderer {
        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(new JButton("Sửa"));
            add(new JButton("Xóa"));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }
}
