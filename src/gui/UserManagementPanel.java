package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dao.EmployeeDAO;
import dao.UserDAO;

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

	public UserManagementPanel() {
		setLayout(new BorderLayout());

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
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		userDAO = new UserDAO();
		employeeDAO = new EmployeeDAO();
		loadUserData();
		addButton.addActionListener(e -> {
			addUser();
		});
	}

	private class ActionButtonRenderer extends JPanel implements TableCellRenderer {

		private JButton editButton;
		private JButton deleteButton;

		public ActionButtonRenderer() {
			setOpaque(true);
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			editButton = new JButton("Sửa");
			editButton.setActionCommand("Sửa");
			deleteButton = new JButton("Xóa");
			deleteButton.setActionCommand("Xóa");
			editButton.setFocusPainted(false);
			editButton.setMargin(new Insets(2, 5, 2, 5)); // Increased vertical margin
			deleteButton.setFocusPainted(false);
			deleteButton.setMargin(new Insets(2, 5, 2, 5)); // Increased vertical margin
			editButton.setFont(new Font("Arial", Font.PLAIN, 12));
			deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));
			editButton.setBackground(new Color(51, 122, 255)); // Blue
			editButton.setForeground(Color.WHITE);
			deleteButton.setBackground(new Color(217, 83, 79)); // Red
			deleteButton.setForeground(Color.WHITE);
			editButton.setBorderPainted(false);
			deleteButton.setBorderPainted(false);

			editButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseEntered(java.awt.event.MouseEvent evt) {
					editButton.setBackground(new Color(40, 96, 175)); // Darker blue on hover
				}

				public void mouseExited(java.awt.event.MouseEvent evt) {
					editButton.setBackground(new Color(51, 122, 255));
				}
			});
			deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseEntered(java.awt.event.MouseEvent evt) {
					deleteButton.setBackground(new Color(192, 8, 74)); // Darker red on hover
				}

				public void mouseExited(java.awt.event.MouseEvent evt) {
					deleteButton.setBackground(new Color(217, 83, 79));
				}
			});

			add(editButton);
			add(deleteButton);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			if (isSelected) {
				editButton.setForeground(table.getSelectionForeground());
				editButton.setBackground(table.getSelectionBackground());
				deleteButton.setForeground(table.getSelectionForeground());
				deleteButton.setBackground(table.getSelectionBackground());
			} else {
				editButton.setForeground(Color.WHITE);
				editButton.setBackground(new Color(51, 122, 255));
				deleteButton.setForeground(Color.WHITE);
				deleteButton.setBackground(new Color(217, 83, 79));
			}
			return this;
		}
	}

	private class ActionButtonEditor extends DefaultCellEditor {

		private JButton editButton;
		private JButton deleteButton;
		private JPanel panel;
		private UUID userId;
		private boolean isPushed;

		public ActionButtonEditor() {
			super(new JCheckBox());
			panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			editButton = new JButton("Sửa");
			editButton.setActionCommand("Sửa");
			deleteButton = new JButton("Xóa");
			deleteButton.setActionCommand("Xóa");
			editButton.setFocusPainted(false);
			deleteButton.setFocusPainted(false);
			panel.add(editButton);
			panel.add(deleteButton);

			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (isPushed) {
						if (e.getActionCommand().equals("Sửa")) {
//                            editUser(userId);
						} else if (e.getActionCommand().equals("Xóa")) {
//                            deleteUser(userId);
						}
					}
					isPushed = false;
					fireEditingStopped();
				}
			};
			editButton.addActionListener(listener);
			deleteButton.addActionListener(listener);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			if (isSelected) {
				panel.setForeground(table.getSelectionForeground());
				panel.setBackground(table.getSelectionBackground());
			} else {
				panel.setForeground(table.getForeground());
				panel.setBackground(UIManager.getColor("Button.background"));
			}
			if (value instanceof ActionButtonPanel) {
				ActionButtonPanel buttonPanel = (ActionButtonPanel) value;
				this.userId = buttonPanel.getUserId();
			}
			isPushed = true;
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			isPushed = false;
			return "";
		}

		@Override
		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		@Override
		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}

	private class ActionButtonPanel extends JPanel {

		private JButton editButton;
		private JButton deleteButton;
		private UUID userId;

		public ActionButtonPanel(UUID id) {
			this.userId = id;
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			editButton = new JButton("Sửa");
			editButton.setActionCommand("Sửa");
			deleteButton = new JButton("Xóa");
			deleteButton.setActionCommand("Xóa");
			editButton.setFocusPainted(false);
			deleteButton.setFocusPainted(false);
			add(editButton);
			add(deleteButton);
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

	
	  private void addUser() {
	        // Create a JPanel to hold the input fields
	        JPanel panel = new JPanel();
	        panel.setLayout(new GridLayout(0, 2, 10, 10)); // Grid layout with spacing
	        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

	        // Create labels and text fields
	        JLabel usernameLabel = new JLabel("Tên đăng nhập:", SwingConstants.LEFT);
	        JTextField usernameField = new JTextField(15);
	        JLabel passwordLabel = new JLabel("Mật khẩu:", SwingConstants.LEFT);
	        JPasswordField passwordField = new JPasswordField(15);
	        JLabel roleLabel = new JLabel("Vai trò:", SwingConstants.LEFT);
	        JTextField roleField = new JTextField(15);
	        JLabel employeeIdLabel = new JLabel("Mã nhân viên:", SwingConstants.LEFT);
	        
	        // Create JComboBox for Employee ID
	        JComboBox<String> employeeIdComboBox = new JComboBox<>();
	        try {
	            List<Employee> employees = employeeDAO.getAllEmployees(); // Get employees from DAO
	            employeeIdComboBox.addItem("null"); // Add null option
	            for (Employee employee : employees) {
	                employeeIdComboBox.addItem(employee.getId().toString()); // Add employee IDs to combobox
	            }
	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	            e.printStackTrace();
	        }
	        
	        

	        // Add components to the panel
	        panel.add(usernameLabel);
	        panel.add(usernameField);
	        panel.add(passwordLabel);
	        panel.add(passwordField);
	        panel.add(roleLabel);
	        panel.add(roleField);
	        panel.add(employeeIdLabel);
	        panel.add(employeeIdComboBox);

	        // Create the dialog
	        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm người dùng",
	                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

	        if (result == JOptionPane.OK_OPTION) {
	            String username = usernameField.getText();
	            String password = new String(passwordField.getPassword()); 
	            String role = roleField.getText();
	            String employeeIdStr = (String) employeeIdComboBox.getSelectedItem();

	            // Kiểm tra dữ liệu nhập vào (validate)
	            if (username.trim().isEmpty() || password.trim().isEmpty() || role.trim().isEmpty()) {
	                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            try {
	                // Chuyển đổi employeeIdStr thành UUID (nếu có)
	                UUID employeeId = null;
	                if (!employeeIdStr.equals("null") && !employeeIdStr.trim().isEmpty()) {
	                    employeeId = UUID.fromString(employeeIdStr);
	                }
	                // Mã hóa mật khẩu (nên dùng thư viện như BCrypt)
	                // Lưu ý: KHÔNG BAO GIỜ lưu mật khẩu dưới dạng plain text
	                String hashedPassword = password; //  THAY ĐỔI THÀNH HÀM MÃ HÓA MẬT KHẨU

	                // Tạo đối tượng User
	                User newUser = new User();
	                newUser.setId(UUID.randomUUID());
	                newUser.setUsername(username);
	                newUser.setPassword(hashedPassword);
	                newUser.setRole(role);
	                newUser.setEmployeeId(employeeId);
	                newUser.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
	                newUser.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));

	                // Gọi DAO để thêm vào database
	                UserDAO userDao = new UserDAO(); 
	                userDao.addUser(newUser);

	                // Load lại dữ liệu để cập nhật bảng
	                loadUserData();
	                JOptionPane.showMessageDialog(this, "Thêm người dùng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);

	            } catch (IllegalArgumentException ex) {
	                JOptionPane.showMessageDialog(this, "Mã nhân viên không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            } catch (SQLException ex) {
	                JOptionPane.showMessageDialog(this, "Lỗi khi thêm người dùng vào database: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	                ex.printStackTrace();
	            }
	        }
	    }

}
