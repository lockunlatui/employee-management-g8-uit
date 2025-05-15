package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.UUID;

import javax.swing.table.*;

import service.UserService;
import service.UserServiceImpl;
import dto.UserDTO;
import dto.EditUserDTO;

import model.User;

import java.util.List;

@SuppressWarnings("serial")
public class UserManagementPanel extends JPanel {

	private JTable userTable;
	private DefaultTableModel tableModel;
	private JScrollPane scrollPane;
	private JButton addButton;
	private JPanel buttonPanel;
	private UserService userService;
	private JTextField searchField;
	private JComboBox<String> searchTypeComboBox;
	private JButton searchButton;
	private JButton refreshButton;
	
	// Màu sắc
	private Color primaryColor = new Color(41, 128, 185);
	private Color secondaryColor = new Color(52, 152, 219);
	private Color accentColor = new Color(230, 126, 34);
	private Color lightBgColor = new Color(248, 249, 250);
	private Color darkTextColor = new Color(44, 62, 80);
	private Color lightTextColor = new Color(236, 240, 241);
	private Color borderColor = new Color(223, 230, 233);

	public UserManagementPanel() {
		setLayout(new BorderLayout(0, 0));
		setBackground(lightBgColor);
		
		// ===== Header Panel =====
		JPanel headerPanel = createHeaderPanel();
		add(headerPanel, BorderLayout.NORTH);
		
		// ===== Content Panel =====
		JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
		contentPanel.setBackground(lightBgColor);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		// Bảng người dùng
		createUserTable();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		// ===== Footer Panel =====
		JPanel footerPanel = createFooterPanel();
		contentPanel.add(footerPanel, BorderLayout.SOUTH);
		
		add(contentPanel, BorderLayout.CENTER);
		
		// Tải dữ liệu người dùng
		userService = new UserServiceImpl();
		loadUserData();
		
		// Thêm sự kiện
		setupEventHandlers();
	}
	
	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(Color.WHITE);
		headerPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
			BorderFactory.createEmptyBorder(20, 20, 20, 20)
		));
		
		// Tiêu đề
		JLabel titleLabel = new JLabel("Quản lý người dùng");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
		titleLabel.setForeground(darkTextColor);
		
		// Panel tìm kiếm
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		searchPanel.setOpaque(false);
		
		searchField = new JTextField(20);
		searchField.setPreferredSize(new Dimension(200, 32));
		searchField.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		
		searchTypeComboBox = new JComboBox<>(new String[] {"Tên đăng nhập", "Vai trò", "Tên nhân viên"});
		searchTypeComboBox.setPreferredSize(new Dimension(150, 32));
		
		searchButton = new JButton("Tìm kiếm");
		searchButton.setPreferredSize(new Dimension(100, 32));
		searchButton.setBackground(secondaryColor);
		searchButton.setForeground(Color.WHITE);
		searchButton.setFocusPainted(false);
		searchButton.setBorderPainted(false);
		
		refreshButton = new JButton("Làm mới");
		refreshButton.setPreferredSize(new Dimension(90, 32));
		refreshButton.setBackground(Color.WHITE);
		refreshButton.setForeground(secondaryColor);
		refreshButton.setBorder(BorderFactory.createLineBorder(secondaryColor));
		refreshButton.setFocusPainted(false);
		
		searchPanel.add(new JLabel("Tìm kiếm: "));
		searchPanel.add(searchField);
		searchPanel.add(searchTypeComboBox);
		searchPanel.add(searchButton);
		searchPanel.add(Box.createHorizontalStrut(10));
		searchPanel.add(refreshButton);
		
		headerPanel.add(titleLabel, BorderLayout.WEST);
		headerPanel.add(searchPanel, BorderLayout.EAST);
		
		return headerPanel;
	}
	
	private void createUserTable() {
		String[] columnNames = { "Tên đăng nhập", "Vai trò", "Nhân viên", "Hành động" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 3; // Chỉ cho phép edit cột hành động
			}
			
			@Override
			public Class<?> getColumnClass(int column) {
				if (column == 3) {
					return ActionButtonPanel.class;
				}
				return Object.class;
			}
		};
		
		userTable = new JTable(tableModel);
		userTable.setRowHeight(45);
		userTable.setShowGrid(false);
		userTable.setIntercellSpacing(new Dimension(0, 0));
		userTable.setBackground(Color.WHITE);
		userTable.setSelectionBackground(new Color(232, 242, 254));
		userTable.setSelectionForeground(darkTextColor);
		userTable.getTableHeader().setBackground(Color.WHITE);
		userTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		userTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
		userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		
		// Thiết lập độ rộng các cột
		userTable.getColumnModel().getColumn(0).setPreferredWidth(250);
		userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		userTable.getColumnModel().getColumn(2).setPreferredWidth(250);
		userTable.getColumnModel().getColumn(3).setPreferredWidth(150);
		
		// Thiết lập renderer và editor cho cột hành động
		userTable.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());
		userTable.getColumnModel().getColumn(3).setCellEditor(new ActionButtonEditor());
		
		// ScrollPane với border đẹp
		scrollPane = new JScrollPane(userTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
		scrollPane.getViewport().setBackground(Color.WHITE);
		
		// Tạo row sorter
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		userTable.setRowSorter(sorter);
	}
	
	private JPanel createFooterPanel() {
		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setOpaque(false);
		
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setOpaque(false);
		
		addButton = new JButton("+ Thêm người dùng");
		addButton.setPreferredSize(new Dimension(150, 38));
		addButton.setBackground(primaryColor);
		addButton.setForeground(Color.WHITE);
		addButton.setFocusPainted(false);
		addButton.setBorderPainted(false);
		addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
		
		buttonPanel.add(addButton);
		
		footerPanel.add(buttonPanel, BorderLayout.EAST);
		
		return footerPanel;
	}
	
	private void setupEventHandlers() {
		searchButton.addActionListener(e -> searchUsers());
		refreshButton.addActionListener(e -> loadUserData());
		addButton.addActionListener(e -> addUser());
		
		// Hiệu ứng hover cho nút
		addHoverEffect(searchButton, secondaryColor, new Color(30, 132, 199));
		addHoverEffect(addButton, primaryColor, new Color(30, 108, 165));
		addHoverEffect(refreshButton, Color.WHITE, new Color(240, 240, 240), secondaryColor, secondaryColor);
	}
	
	private void addHoverEffect(JButton button, Color normalBg, Color hoverBg) {
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(hoverBg);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(normalBg);
			}
		});
	}
	
	private void addHoverEffect(JButton button, Color normalBg, Color hoverBg, Color normalFg, Color hoverFg) {
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(hoverBg);
				button.setForeground(hoverFg);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(normalBg);
				button.setForeground(normalFg);
			}
		});
	}

	private class ActionButtonRenderer implements TableCellRenderer {
		private JPanel panel;
		private JButton editButton;
		private JButton deleteButton;

		public ActionButtonRenderer() {
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
			panel.setOpaque(true);
			
			editButton = new JButton("Sửa");
			editButton.setPreferredSize(new Dimension(70, 30));
			editButton.setBackground(secondaryColor);
			editButton.setForeground(Color.WHITE);
			editButton.setFocusPainted(false);
			editButton.setBorderPainted(false);
			editButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
			
			deleteButton = new JButton("Xóa");
			deleteButton.setPreferredSize(new Dimension(70, 30));
			deleteButton.setBackground(new Color(231, 76, 60));
			deleteButton.setForeground(Color.WHITE);
			deleteButton.setFocusPainted(false);
			deleteButton.setBorderPainted(false);
			deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
			
			panel.add(editButton);
			panel.add(deleteButton);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				panel.setBackground(table.getSelectionBackground());
			} else {
				panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
			}
			return panel;
		}
	}

	private class ActionButtonEditor extends DefaultCellEditor {
		private JPanel panel;
		private JButton editButton;
		private JButton deleteButton;
		private UUID userId;
		private boolean isPushed;

		public ActionButtonEditor() {
			super(new JTextField());
			
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
			
			editButton = new JButton("Sửa");
			editButton.setPreferredSize(new Dimension(70, 30));
			editButton.setBackground(secondaryColor);
			editButton.setForeground(Color.WHITE);
			editButton.setFocusPainted(false);
			editButton.setBorderPainted(false);
			editButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
			
			deleteButton = new JButton("Xóa");
			deleteButton.setPreferredSize(new Dimension(70, 30));
			deleteButton.setBackground(new Color(231, 76, 60));
			deleteButton.setForeground(Color.WHITE);
			deleteButton.setFocusPainted(false);
			deleteButton.setBorderPainted(false);
			deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

			panel.add(editButton);
			panel.add(deleteButton);
			
			// Thêm hover effect cho các nút
			addHoverEffect(editButton, secondaryColor, new Color(30, 132, 199));
			addHoverEffect(deleteButton, new Color(231, 76, 60), new Color(211, 56, 40));

			editButton.addActionListener(e -> {
				isPushed = true;
				fireEditingStopped();
				if (userId != null) {
					editUser(userId);
				}
			});

			deleteButton.addActionListener(e -> {
				isPushed = true;
				fireEditingStopped();
				if (userId != null) {
					deleteUser(userId);
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (value instanceof ActionButtonPanel) {
				userId = ((ActionButtonPanel) value).getUserId();
			} else {
				// Lấy userId từ dòng hiện tại
				try {
					int modelRow = table.convertRowIndexToModel(row);
					Object idObj = tableModel.getValueAt(modelRow, 0);
					if (idObj instanceof UUID) {
						userId = (UUID) idObj;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			panel.setBackground(table.getSelectionBackground());
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			isPushed = false;
			return new ActionButtonPanel(userId);
		}
		
		@Override
		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
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
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			List<User> users = userService.getAllUsers();
			tableModel.setRowCount(0);

			for (User user : users) {
				String employeeName = (user.getEmployee() != null) ? user.getEmployee().getEmployeeName() : "N/A";
				tableModel.addRow(new Object[] { 
					user.getUsername(), 
					user.getRole(), 
					employeeName,
					new ActionButtonPanel(user.getId()) 
				});
			}
			setCursor(Cursor.getDefaultCursor());
			
			if (users.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không có dữ liệu người dùng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException e) {
			setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void searchUsers() {
		String searchText = searchField.getText().trim();
		String searchType = (String) searchTypeComboBox.getSelectedItem();
		
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			List<User> users = userService.searchUsers(searchText, searchType);
			tableModel.setRowCount(0);
			
			for (User user : users) {
				String employeeName = (user.getEmployee() != null) ? user.getEmployee().getEmployeeName() : "N/A";
				tableModel.addRow(new Object[] { 
					user.getUsername(), 
					user.getRole(), 
					employeeName,
					new ActionButtonPanel(user.getId()) 
				});
			}
			setCursor(Cursor.getDefaultCursor());
			
			if (users.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException e) {
			setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm người dùng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void addUser() {
		AddUserDialog dialog = new AddUserDialog((Frame) SwingUtilities.getWindowAncestor(this));
		dialog.setVisible(true);
		
		if (dialog.isUserAdded()) {
			loadUserData();
		}
	}

	private void editUser(UUID userId) {
		try {
			User user = userService.getUserById(userId);
			if (user != null) {
				EditUserDialog dialog = new EditUserDialog((Frame) SwingUtilities.getWindowAncestor(this), userId);
				dialog.setVisible(true);
				
				if (dialog.isUserUpdated()) {
					loadUserData();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin người dùng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void deleteUser(UUID userId) {
		try {
			User user = userService.getUserById(userId);
			if (user != null) {
				DeleteUserDialog dialog = new DeleteUserDialog((Frame) SwingUtilities.getWindowAncestor(this), userId);
				dialog.setVisible(true);
				
				if (dialog.isConfirmed()) {
					userService.deleteUser(userId); // <
					loadUserData();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin người dùng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
