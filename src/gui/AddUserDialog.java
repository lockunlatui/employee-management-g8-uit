package gui;
import service.UserServiceImpl; 
import service.UserService; 
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import dao.EmployeeDAO;
import dao.UserDAO;
import dto.UserDTO;
import model.Employee;
import model.User;

public class AddUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> employeeComboBox;
    private JButton okButton;
    private JButton cancelButton;
    private boolean confirmed;
    private UserDTO userDTO;
    private EmployeeDAO employeeDAO;
    private List<Employee> employees;

    public AddUserDialog(Frame parent) {
        super(parent, "Thêm người dùng mới", true);
        employeeDAO = new EmployeeDAO();
        userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID());

        try {
            // Lấy danh sách nhân viên
            employees = employeeDAO.getAllEmployees();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách nhân viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Thiết lập font chữ
        Font regularFont = new Font("SF Pro Text", Font.PLAIN, 13);
        Font mediumFont = new Font("SF Pro Text", Font.BOLD, 13);
        
        // Username field
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(regularFont);
        usernameField = new JTextField(20);
        usernameField.setFont(regularFont);
        
        // Password field
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(regularFont);
        passwordField = new JPasswordField(20);
        passwordField.setFont(regularFont);
        
        // Role combobox
        JLabel roleLabel = new JLabel("Vai trò:");
        roleLabel.setFont(regularFont);
        String[] roles = {"ADMIN", "USER"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(regularFont);
        roleComboBox.setSelectedItem("USER"); // Mặc định là USER
        
        // Employee combobox
        JLabel employeeLabel = new JLabel("Nhân viên:");
        employeeLabel.setFont(regularFont);
        String[] employeeNames = new String[employees.size()];
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            employeeNames[i] = emp.getEmployeeName() + " - " + emp.getEmployeeName();
        }
        employeeComboBox = new JComboBox<>(employeeNames);
        employeeComboBox.setFont(regularFont);
        
        // Buttons
        okButton = new JButton("Thêm");
        okButton.setFont(mediumFont);
        okButton.setBackground(new Color(0, 122, 255)); // Màu xanh của Apple
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setOpaque(true);
        okButton.setPreferredSize(new Dimension(80, 28));
        
        cancelButton = new JButton("Hủy");
        cancelButton.setFont(mediumFont);
        cancelButton.setBackground(new Color(242, 242, 247)); // Màu xám nhạt của Apple
        cancelButton.setForeground(new Color(0, 122, 255));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setPreferredSize(new Dimension(80, 28));
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(roleComboBox, gbc);

        // Employee
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(employeeComboBox, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupEventHandlers() {
    	okButton.addActionListener(e -> {
    	    if (validateInput()) {
    	        userDTO.setUsername(usernameField.getText().trim());
    	        userDTO.setPassword(new String(passwordField.getPassword()));
    	        userDTO.setRole((String) roleComboBox.getSelectedItem());
    	        userDTO.setEmployeeId(employees.get(employeeComboBox.getSelectedIndex()).getId());

    	        try {
    	            UserService userService = new UserServiceImpl();

    	            // Convert DTO -> Entity
    	            User user = new User();
    	            user.setId(userDTO.getId());
    	            user.setUsername(userDTO.getUsername());
    	            user.setPassword(userDTO.getPassword()); // nên hash nếu cần
    	            user.setRole(userDTO.getRole());
    	            user.setEmployeeId(userDTO.getEmployeeId());
    	            user.setCreatedAt(new java.util.Date());
    	            user.setUpdatedAt(new java.util.Date());

    	            userService.addUser(user);

    	            JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    	            confirmed = true;
    	            setVisible(false);
    	        } catch (SQLException ex) {
    	            JOptionPane.showMessageDialog(this, "Lỗi khi thêm người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    	            ex.printStackTrace();
    	        }
    	    }
    	});


        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false); // Đóng cửa sổ khi người dùng hủy
        });
    }

    private boolean validateInput() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isUserAdded() {
        return confirmed;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }
} 