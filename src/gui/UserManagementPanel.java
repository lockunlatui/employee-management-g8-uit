package gui;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;

import javax.swing.table.DefaultTableModel;

import dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.ConnectionManager; // Import ConnectionManager
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Employee;
import model.User; // Import User model

@SuppressWarnings("serial")
public class UserManagementPanel extends JPanel {

	private JTable userTable;
	private DefaultTableModel tableModel;
	private JScrollPane scrollPane;
	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
	private JPanel buttonPanel;
	private UserDAO userDAO;

	public UserManagementPanel() {
		setLayout(new BorderLayout());

		String[] columnNames = { "Tên đăng nhập", "Vai trò", "Nhân viên" };
		tableModel = new DefaultTableModel(columnNames, 0);
		userTable = new JTable(tableModel);
		scrollPane = new JScrollPane(userTable);

		buttonPanel = new JPanel();
		addButton = new JButton("Thêm");
		editButton = new JButton("Sửa");
		deleteButton = new JButton("Xóa");

		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);

		add(new JLabel("Quản lý Người dùng", SwingConstants.CENTER), BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		userDAO = new UserDAO();
		loadUserData();
	}

	private void loadUserData() {
		try {
			List<User> users = userDAO.getAllUsers();
			tableModel.setRowCount(0);
			
			for (User user : users) {
				System.out.println(user.getEmployee());
				String employeeName = (user.getEmployee() != null) ? user.getEmployee().getEmployeeName() : "N/A";
				tableModel.addRow(new Object[] { user.getUsername(), user.getRole(), employeeName });
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

}
