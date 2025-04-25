package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import database.ConnectionManager;

public class AddEmployeeDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<String> departmentCombo;
    private JComboBox<String> statusCombo;
    private JTextField joinDateField;
    private final DialogCallback callback;

    public AddEmployeeDialog(Frame owner, DialogCallback callback) {
        super(owner, "Thêm nhân viên mới", true);
        this.callback = callback;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridLayout(5, 2, 5, 5));
        setSize(400, 200);

        nameField = new JTextField();
        departmentCombo = new JComboBox<>();
        statusCombo = new JComboBox<>(new String[]{"Hoạt động"});
        statusCombo.setEnabled(false);
        joinDateField = new JTextField();

        // Load departments vào combobox
        loadDepartments();

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

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(new JLabel());
        add(buttonPanel);

        setLocationRelativeTo(getOwner());
    }

    private void loadDepartments() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_name FROM Departments")) {
            
            while (rs.next()) {
                departmentCombo.addItem(rs.getString("department_name"));
            }
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

    private void saveEmployee() {
        try {
            String name = nameField.getText();
            String departmentName = (String) departmentCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            String joinDate = joinDateField.getText();

            if (name.isEmpty() || departmentName == null || status.isEmpty() || joinDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            // Lấy department_id từ tên phòng ban
            String departmentId = getDepartmentIdByName(departmentName);
            if (departmentId == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phòng ban!");
                return;
            }

            // Thêm nhân viên mới
            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("""
                     INSERT INTO Employees (id, department_id, employee_name, status, join_date, created_at, updated_at)
                     VALUES (UUID_TO_BIN(UUID()), UUID_TO_BIN(?), ?, ?, ?, CURDATE(), CURDATE())
                     """)) {
                
                pstmt.setString(1, departmentId);
                pstmt.setString(2, name);
                pstmt.setString(3, status);
                pstmt.setString(4, joinDate);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                callback.onSuccess();
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên: " + ex.getMessage());
        }
    }
} 