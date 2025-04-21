package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

import dao.EmployeeDAO;
import dao.UserDAO;
import dto.UserDTO;
import dto.EditUserDTO;

import java.util.UUID;
import java.util.List;

import model.Employee;
import model.User;

@SuppressWarnings("serial")
public class UserManagementPanel extends JPanel {

	private JTable userTable;
	private DefaultTableModel tableModel;
	private JScrollPane scrollPane;
	private JButton addButton;
	private JPanel buttonPanel;
	private UserDAO userDAO;
	private EmployeeDAO employeeDAO;
	private JTextField searchField;
	private JComboBox<String> searchTypeComboBox;
	private JButton searchButton;

	public UserManagementPanel() {
		setLayout(new BorderLayout());

		// Tạo panel tìm kiếm
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchField = new JTextField(20);
		searchTypeComboBox = new JComboBox<>(new String[] {"Tên đăng nhập", "Vai trò", "Tên nhân viên"});
		searchButton = new JButton("Tìm kiếm");
		
		searchButton.addActionListener(e -> searchUsers());
		
		searchPanel.add(new JLabel("Tìm kiếm:"));
		searchPanel.add(searchField);
		searchPanel.add(searchTypeComboBox);
		searchPanel.add(searchButton);

		String[] columnNames = { "Tên đăng nhập", "Vai trò", "Nhân viên", "Hành động" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		userTable = new JTable(tableModel);
		userTable.setRowHeight(30);
		userTable.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());
		userTable.getColumnModel().getColumn(3).setCellEditor(new ActionButtonEditor());
		scrollPane = new JScrollPane(userTable);

		buttonPanel = new JPanel();
		addButton = new JButton("Thêm");
		addButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				addButton.setBackground(new Color(40, 96, 175));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				addButton.setBackground(new Color(51, 122, 255));
			}
		});
		

		buttonPanel.add(addButton);

		add(new JLabel("Quản lý Người dùng", SwingConstants.CENTER), BorderLayout.NORTH);
		add(searchPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		userDAO = new UserDAO();
		employeeDAO = new EmployeeDAO();
		loadUserData();
		addButton.addActionListener(e -> {
			addUser();
		});

		// Thêm MouseListener cho bảng
		userTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = userTable.rowAtPoint(evt.getPoint());
				int col = userTable.columnAtPoint(evt.getPoint());
				
				if (row >= 0 && col == 3) { // Cột Hành động
					System.out.println("DEBUG: Click vào cột Hành động");
					
					// Lấy vị trí click trong cell
					Rectangle cellRect = userTable.getCellRect(row, col, false);
					int x = evt.getX() - cellRect.x;
					int y = evt.getY() - cellRect.y;
					
					// Kiểm tra xem click vào nút nào
					if (x < cellRect.width / 2) { // Click vào nút Sửa
						System.out.println("DEBUG: Click vào nút Sửa");
						UUID userId = ((ActionButtonPanel) userTable.getModel().getValueAt(row, col)).getUserId();
						editUser(userId);
					} else { // Click vào nút Xóa
						System.out.println("DEBUG: Click vào nút Xóa");
						UUID userId = ((ActionButtonPanel) userTable.getModel().getValueAt(row, col)).getUserId();
						deleteUser(userId);
					}
				}
			}
		});
	}

	private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
		private JButton editButton;
		private JButton deleteButton;

		public ActionButtonRenderer() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			editButton = new JButton("Sửa");
			deleteButton = new JButton("Xóa");
			
			// Thiết lập giao diện cho các nút
			editButton.setBackground(new Color(51, 122, 255));
			editButton.setForeground(Color.WHITE);
			deleteButton.setBackground(new Color(217, 83, 79));
			deleteButton.setForeground(Color.WHITE);
			
			editButton.setFocusPainted(false);
			deleteButton.setFocusPainted(false);
			
			add(editButton);
			add(deleteButton);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
			}
			return this;
		}
	}

	private class ActionButtonEditor extends DefaultCellEditor {
		private JPanel panel;
		private JButton editButton;
		private JButton deleteButton;
		private UUID userId;

		public ActionButtonEditor() {
			super(new JTextField());
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
			editButton = new JButton("Sửa");
			deleteButton = new JButton("Xóa");

			// Thiết lập giao diện cho các nút
			editButton.setBackground(new Color(66, 139, 202));
			editButton.setForeground(Color.WHITE);
			editButton.setFocusPainted(false);

			deleteButton.setBackground(new Color(217, 83, 79));
			deleteButton.setForeground(Color.WHITE);
			deleteButton.setFocusPainted(false);

			panel.add(editButton);
			panel.add(deleteButton);

			editButton.addActionListener(e -> {
				if (userId != null) {
					editUser(userId);
				}
			});

			deleteButton.addActionListener(e -> {
				if (userId != null) {
					deleteUser(userId);
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			userId = (UUID) table.getValueAt(row, 0);
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return userId;
		}
	}

	private class ActionButtonPanel extends JPanel {
		private UUID userId;

		public ActionButtonPanel(UUID userId) {
			this.userId = userId;
		}

		public UUID getUserId() {
			return userId;
		}
	}

	private void loadUserData() {
		try {
			List<User> users = userDAO.getAllUsers();
			tableModel.setRowCount(0);

			for (User user : users) {
				System.out.println(user.getEmployee());
				String employeeName = (user.getEmployee() != null) ? user.getEmployee().getEmployeeName() : "N/A";
				tableModel.addRow(new Object[] { user.getUsername(), user.getRole(), employeeName,
						new ActionButtonPanel(user.getId()) });
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void searchUsers() {
		String searchText = searchField.getText().trim();
		String searchType = (String) searchTypeComboBox.getSelectedItem();
		
		try {
			List<User> users = userDAO.getAllUsers();
			tableModel.setRowCount(0);
			
			for (User user : users) {
				boolean match = false;
				String employeeName = (user.getEmployee() != null) ? user.getEmployee().getEmployeeName() : "N/A";
				
				switch(searchType) {
					case "Tên đăng nhập":
						match = user.getUsername().toLowerCase().contains(searchText.toLowerCase());
						break;
					case "Vai trò":
						match = user.getRole().toLowerCase().contains(searchText.toLowerCase());
						break;
					case "Tên nhân viên":
						match = employeeName.toLowerCase().contains(searchText.toLowerCase());
						break;
				}
				
				if (match || searchText.isEmpty()) {
					tableModel.addRow(new Object[] { 
						user.getUsername(), 
						user.getRole(), 
						employeeName,
						new ActionButtonPanel(user.getId()) 
					});
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm người dùng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void addUser() {
		AddUserDialog dialog = new AddUserDialog((Frame) SwingUtilities.getWindowAncestor(this));
		dialog.setVisible(true);
		
		if (dialog.isConfirmed()) {
			UserDTO userDTO = dialog.getUserDTO();
			try {
				// Tạo đối tượng User từ UserDTO
				User newUser = new User();
				newUser.setId(userDTO.getId());
				newUser.setUsername(userDTO.getUsername());
				newUser.setPassword(userDTO.getPassword()); // Lưu ý: Nên mã hóa mật khẩu
				newUser.setRole(userDTO.getRole());
				newUser.setEmployeeId(userDTO.getEmployeeId());
				newUser.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
				newUser.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));

				// Gọi DAO để thêm vào database
				userDAO.addUser(newUser);

				// Load lại dữ liệu để cập nhật bảng
				loadUserData();
				JOptionPane.showMessageDialog(this, "Thêm người dùng thành công.", "Thành công", 
						JOptionPane.INFORMATION_MESSAGE);

			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this, "Lỗi khi thêm người dùng vào database: " + ex.getMessage(), 
						"Lỗi", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	private void editUser(UUID userId) {
		EditUserDialog dialog = new EditUserDialog((Frame) SwingUtilities.getWindowAncestor(this), userId);
		dialog.setVisible(true);
		
		if (dialog.isConfirmed()) {
			EditUserDTO userDTO = dialog.getUserDTO();
			try {
				// Tạo đối tượng User từ EditUserDTO
				User updatedUser = new User();
				updatedUser.setId(userDTO.getId());
				updatedUser.setUsername(userDTO.getUsername());
				if (userDTO.getPassword() != null) {
					updatedUser.setPassword(userDTO.getPassword()); // Lưu ý: Nên mã hóa mật khẩu
				}
				updatedUser.setRole(userDTO.getRole());
				updatedUser.setEmployeeId(userDTO.getEmployeeId());
				updatedUser.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));

				// Gọi DAO để cập nhật trong database
				userDAO.updateUser(updatedUser);

				// Load lại dữ liệu để cập nhật bảng
				loadUserData();
				JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công.", "Thành công", 
						JOptionPane.INFORMATION_MESSAGE);

			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật người dùng trong database: " + ex.getMessage(), 
						"Lỗi", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	private void deleteUser(UUID userId) {
		DeleteUserDialog dialog = new DeleteUserDialog((Frame) SwingUtilities.getWindowAncestor(this), userId);
		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			try {
				userDAO.deleteUser(userId);
				JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
				loadUserData();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Lỗi khi xóa người dùng: " + e.getMessage(),
						"Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
