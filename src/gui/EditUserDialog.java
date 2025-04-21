package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import dao.EmployeeDAO;
import dao.UserDAO;
import dto.EditUserDTO;
import model.Employee;
import model.User;

public class EditUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> employeeComboBox;
    private JButton okButton;
    private JButton cancelButton;
    private boolean confirmed;
    private EditUserDTO userDTO;
    private UserDAO userDAO;
    private EmployeeDAO employeeDAO;
    private List<Employee> employees;

    public EditUserDialog(Frame parent, UUID userId) {
        super(parent, "Sửa thông tin người dùng", true);
        userDAO = new UserDAO();
        employeeDAO = new EmployeeDAO();
        userDTO = new EditUserDTO();
        userDTO.setId(userId);

        try {
            // Lấy thông tin người dùng hiện tại
            User user = userDAO.getUserById(userId);
            if (user != null) {
                userDTO.setUsername(user.getUsername());
                userDTO.setRole(user.getRole());
                userDTO.setEmployeeId(user.getEmployeeId());
                
                // Cập nhật tiêu đề với tên người dùng
                setTitle("Sửa thông tin người dùng: " + user.getUsername());
            }
            
            // Lấy danh sách nhân viên
            employees = employeeDAO.getAllEmployees();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin: " + e.getMessage(),
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
        usernameField.setText(userDTO.getUsername());
        usernameField.setFont(regularFont);
        usernameField.setEnabled(false); // Không cho phép sửa username
        
        // Password field
        JLabel passwordLabel = new JLabel("Mật khẩu mới:");
        passwordLabel.setFont(regularFont);
        passwordField = new JPasswordField(20);
        passwordField.setFont(regularFont);
        
        // Role combobox
        JLabel roleLabel = new JLabel("Vai trò:");
        roleLabel.setFont(regularFont);
        String[] roles = {"ADMIN", "USER"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(regularFont);
        roleComboBox.setSelectedItem(userDTO.getRole());
        
        // Employee combobox
        JLabel employeeLabel = new JLabel("Nhân viên:");
        employeeLabel.setFont(regularFont);
        String[] employeeNames = new String[employees.size()];
        int selectedIndex = 0;
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            employeeNames[i] = emp.getEmployeeName() + " - " + emp.getEmployeeName();
            if (emp.getId().equals(userDTO.getEmployeeId())) {
                selectedIndex = i;
            }
        }
        employeeComboBox = new JComboBox<>(employeeNames);
        employeeComboBox.setFont(regularFont);
        employeeComboBox.setSelectedIndex(selectedIndex);
        
        // Buttons
        okButton = new JButton("Lưu");
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
        formPanel.add(new JLabel("Mật khẩu mới:"), gbc);
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
                String password = new String(passwordField.getPassword());
                if (!password.isEmpty()) {
                    userDTO.setPassword(password);
                }
                userDTO.setRole((String) roleComboBox.getSelectedItem());
                userDTO.setEmployeeId(employees.get(employeeComboBox.getSelectedIndex()).getId());
                confirmed = true;
                setVisible(false);
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });
    }

    private boolean validateInput() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public EditUserDTO getUserDTO() {
        return userDTO;
    }
} 