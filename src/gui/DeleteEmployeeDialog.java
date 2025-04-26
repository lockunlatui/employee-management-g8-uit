package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import database.ConnectionManager;

public class DeleteEmployeeDialog extends JDialog {
    private final String employeeId;
    private final String employeeName;
    private final DialogCallback callback;

    public DeleteEmployeeDialog(Frame owner, String employeeId, String employeeName, DialogCallback callback) {
        super(owner, "Xác nhận xóa nhân viên", true);
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.callback = callback;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(300, 150);

        // Panel chứa thông báo
        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel("Bạn có chắc chắn muốn xóa nhân viên " + employeeName + "?"));
        add(messagePanel, BorderLayout.CENTER);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Xác nhận");
        JButton cancelButton = new JButton("Hủy");

        confirmButton.addActionListener(e -> deleteEmployee());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(getOwner());
    }

    private void deleteEmployee() {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Employees WHERE id = UUID_TO_BIN(?)")) {
            
            pstmt.setString(1, employeeId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
            callback.onSuccess();
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên: " + e.getMessage());
        }
    }
} 