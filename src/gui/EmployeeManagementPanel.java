package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import database.ConnectionManager;

public class EmployeeManagementPanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JComboBox<String> departmentComboBox;

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout());

        // Tạo bảng hiển thị nhân viên
        String[] columnNames = {"ID", "Tên nhân viên", "Phòng ban", "Trạng thái", "Ngày vào làm"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Thêm nhân viên");
        editButton = new JButton("Sửa thông tin");
        deleteButton = new JButton("Xóa nhân viên");
        
        // ComboBox chọn phòng ban
        departmentComboBox = new JComboBox<>();
        loadDepartments();
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JLabel("Lọc theo phòng ban:"));
        buttonPanel.add(departmentComboBox);

        // Thêm các thành phần vào panel chính
        add(new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load dữ liệu nhân viên
        loadEmployees();

        // Xử lý sự kiện cho các nút
        addButton.addActionListener(e -> showAddEmployeeDialog());
        editButton.addActionListener(e -> showEditEmployeeDialog());
        deleteButton.addActionListener(e -> showDeleteEmployeeDialog());
        departmentComboBox.addActionListener(e -> filterEmployeesByDepartment());
    }

    private void loadDepartments() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_name FROM Departments")) {
            
            departmentComboBox.removeAllItems();
            departmentComboBox.addItem("Tất cả");
            
            while (rs.next()) {
                departmentComboBox.addItem(rs.getString("department_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách phòng ban: " + e.getMessage());
        }
    }

    private void loadEmployees() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT BIN_TO_UUID(e.id) as id, e.employee_name, d.department_name, e.status, e.join_date 
                 FROM Employees e 
                 LEFT JOIN Departments d ON e.department_id = d.id
                 ORDER BY e.employee_name
                 """)) {
            
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("id"),
                    rs.getString("employee_name"),
                    rs.getString("department_name"),
                    rs.getString("status"),
                    rs.getDate("join_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
        }
    }

    private void showAddEmployeeDialog() {
        AddEmployeeDialog dialog = new AddEmployeeDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            new DialogCallback() {
                @Override
                public void onSuccess() {
                    loadEmployees();
                }
            }
        );
        dialog.setVisible(true);
    }

    private void showEditEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!");
            return;
        }

        String employeeId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentDepartment = (String) tableModel.getValueAt(selectedRow, 2);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 3);
        String currentJoinDate = tableModel.getValueAt(selectedRow, 4).toString();

        EditEmployeeDialog dialog = new EditEmployeeDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            employeeId,
            currentName,
            currentDepartment,
            currentStatus,
            currentJoinDate,
            new DialogCallback() {
                @Override
                public void onSuccess() {
                    loadEmployees();
                }
            }
        );
        dialog.setVisible(true);
    }

    private void showDeleteEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
            return;
        }

        String employeeId = (String) tableModel.getValueAt(selectedRow, 0);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);

        DeleteEmployeeDialog dialog = new DeleteEmployeeDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            employeeId,
            employeeName,
            new DialogCallback() {
                @Override
                public void onSuccess() {
                    loadEmployees();
                }
            }
        );
        dialog.setVisible(true);
    }

    private void filterEmployeesByDepartment() {
        String selectedDepartment = (String) departmentComboBox.getSelectedItem();
        if (selectedDepartment.equals("Tất cả")) {
            loadEmployees();
            return;
        }

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("""
                 SELECT BIN_TO_UUID(e.id) as id, e.employee_name, d.department_name, e.status, e.join_date 
                 FROM Employees e 
                 LEFT JOIN Departments d ON e.department_id = d.id
                 WHERE d.department_name = ?
                 ORDER BY e.employee_name
                 """)) {
            
            pstmt.setString(1, selectedDepartment);
            ResultSet rs = pstmt.executeQuery();
            
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("id"),
                    rs.getString("employee_name"),
                    rs.getString("department_name"),
                    rs.getString("status"),
                    rs.getDate("join_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc nhân viên: " + e.getMessage());
        }
    }
}