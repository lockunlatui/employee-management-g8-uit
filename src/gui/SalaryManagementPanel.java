package gui;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
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

        // Search and control panel
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

        // Table setup
        String[] columnNames = { "STT", "Mã nhân viên", "Tên nhân viên", "Lương cơ bản", "Khấu trừ",
                "Trạng thái", "Ngày tạo", "Ngày cập nhập", "Hành động" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 7;
            }
        };
        salaryTable = new JTable(tableModel);
        salaryTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel Pagination
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("Trước");
        nextButton = new JButton("Tiếp");
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);
        add(paginationPanel, BorderLayout.SOUTH);

        // Event listeners
        addButton.addActionListener(e -> openAddSalaryDialog());
        // selectButton.addActionListener(e -> {
        // isSelecting = !isSelecting;
        // toggleCheckboxColumn(isSelecting);
        // });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                currentPage = 1;
                filterData();
            }
        });
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

        // Load data on initialization
        loadSalaryData();
    }

    // Load salary data from database
    private void loadSalaryData() {
        SalaryDAO dao = new SalaryDAO();
        try {
            allSalaries = dao.getAllSalaries();
            filterData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    // Filter data based on search input
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

    // Update table data for the current page
    private void updateTable() {
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
            String createdAt = salary.getCreatedAt() != null ? salary.getCreatedAt().toString() : nullString;
            String updatedAt = salary.getUpdatedAt() != null ? salary.getUpdatedAt().toString() : nullString;

            tableModel.addRow(new Object[] {
                    i,
                    employeeId,
                    employeeName,
                    base,
                    deductions,
                    status,
                    createdAt,
                    updatedAt,
                    "Hành động"
            });
        }

        // Set column widths
        salaryTable.getColumnModel().getColumn(0).setPreferredWidth(30); // index column
        salaryTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Employee ID column
        salaryTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Employee Name column
        salaryTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Base Salary column
        salaryTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Deductions column
        salaryTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status column
        salaryTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Created At column
        salaryTable.getColumnModel().getColumn(7).setPreferredWidth(150); // Updated At column
        salaryTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Action column
        // Update record count
        recordCountLabel.setText("Tổng cộng: " + filteredSalaries.size());
    }

    // private void toggleCheckboxColumn(boolean show) {
    // TableColumn col = salaryTable.getColumnModel().getColumn(0);
    // col.setMinWidth(show ? 30 : 0);
    // col.setMaxWidth(show ? 30 : 0);
    // col.setWidth(show ? 30 : 0);
    // }

    // Dialog to add a new salary
    private void openAddSalaryDialog() {
        SalaryDAO salaryDAO = new SalaryDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        JDialog dialog = new JDialog((Frame) null, "Thêm lương", true);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));
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

        // Load employee data into combo box
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            for (Employee employee : employees) {
                empBox.addItem(employee.getEmployeeName() + ":" + employee.getId());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi lấy danh sách nhân viên: " + e.getMessage());
        }
        // Add button event
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

                salaryDAO.insertSalary(newSalary);

                JOptionPane.showMessageDialog(dialog, "Thêm lương thành công!");

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Dữ liệu không hợp lệ");
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Renderer for the action cell (e.g., delete button)
    class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private Salary currentSalary;

        public ActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> {
                // openEditSalaryDialog(currentSalary);
            });

            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        SalaryManagementPanel.this,
                        "Bạn có chắc chắn muốn xóa lương này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        SalaryDAO dao = new SalaryDAO();
                        dao.deleteSalary(currentSalary.getId());
                        loadSalaryData();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(SalaryManagementPanel.this, "Lỗi khi xóa: " + ex.getMessage());
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            int modelRow = salaryTable.convertRowIndexToModel(row);
            currentSalary = filteredSalaries.get((currentPage - 1) * rowsPerPage + modelRow);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

}
