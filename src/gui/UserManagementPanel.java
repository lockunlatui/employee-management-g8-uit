package gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class UserManagementPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Tên đăng nhập", "Vai trò"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        add(new JLabel("Quản lý Người dùng", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        tableModel.addRow(new Object[]{"admin", "Quản trị viên"});
        tableModel.addRow(new Object[]{"employee1", "Nhân viên"});
    }
}