package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import database.ConnectionManager;

public class EditEmployeeDialog extends JDialog {
    private final String employeeId;
    private JTextField nameField;
    private JComboBox<String> departmentCombo;
    private JComboBox<String> statusCombo;
    private JTextField joinDateField;
    private final DialogCallback callback;

    public EditEmployeeDialog(Frame owner, String employeeId, String currentName, 
                            String currentDepartment, String currentStatus, 
                            String currentJoinDate, DialogCallback callback) {
        super(owner, "Sửa thông tin nhân viên", true);
        this.employeeId = employeeId;
        this.callback = callback;
        initializeUI(currentName, currentDepartment, currentStatus, currentJoinDate);
    }

    private void initializeUI(String currentName, String currentDepartment, 
                            String currentStatus, String currentJoinDate) {
        setLayout(new GridLayout(5, 2, 5, 5));
        setSize(400, 200);

        nameField = new JTextField(currentName);
        departmentCombo = new JComboBox<>();
        statusCombo = new JComboBox<>(new String[]{"Hoạt động", "Nghỉ việc"});
        statusCombo.setSelectedItem(currentStatus);
        joinDateField = new JTextField(currentJoinDate);

        // Load departments vào combobox
        loadDepartments(currentDepartment);

        add(new JLabel("Tên nhân viên:"));
        add(nameField);
        add(new JLabel("Phòng ban:"));
        add(departmentCombo);
        add(new JLabel("Trạng thái:"));
        add(statusCombo);
        add(new JLabel("Ngày vào làm (YYYY-MM-DD):"));
        add(joinDateField);

        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> updateEmployee());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(new JLabel());
        add(buttonPanel);

        setLocationRelativeTo(getOwner());
    }

    private void loadDepartments(String currentDepartment) {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_name FROM Departments")) {
            
            while (rs.next()) {
                departmentCombo.addItem(rs.getString("department_name"));
            }
            departmentCombo.setSelectedItem(currentDepartment);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách phòng ban: " + e.getMessage());
        }
    }

    private String getDepartmentIdByName(String departmentName) {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT BIN_TO_UUID(id) as id FROM Departments WHERE department_name = ?")) {
            
            pstmt.setString(1, departmentName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateEmployee() {
        try {
            String name = nameField.getText();
            String department = (String) departmentCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            String joinDate = joinDateField.getText();

            if (name.isEmpty() || department == null || status.isEmpty() || joinDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            // Lấy department_id từ tên phòng ban
            String departmentId = getDepartmentIdByName(department);
            if (departmentId == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phòng ban!");
                return;
            }

            // Cập nhật thông tin nhân viên
            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("""
                     UPDATE Employees 
                     SET department_id = UUID_TO_BIN(?), 
                         employee_name = ?, 
                         status = ?, 
                         join_date = ?,
                         updated_at = CURDATE()
                     WHERE id = UUID_TO_BIN(?)
                     """)) {
                
                pstmt.setString(1, departmentId);
                pstmt.setString(2, name);
                pstmt.setString(3, status);
                pstmt.setString(4, joinDate);
                pstmt.setString(5, employeeId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                callback.onSuccess();
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin: " + ex.getMessage());
        }
    }
} 