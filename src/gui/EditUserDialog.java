package gui;

import javax.swing.*;
import service.UserServiceImpl;
import service.UserService;
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
    private JComboBox<Employee> employeeComboBox;
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
            	 System.out.println("Tìm thấy user: " + user.getUsername());
                userDTO.setUsername(user.getUsername());
                userDTO.setRole(user.getRole());
                userDTO.setEmployeeId(user.getEmployeeId());
                System.out.println("== EmployeeID cần chọn: " + userDTO);

                setTitle("Sửa thông tin người dùng: " + user.getUsername());
            }

            // Lấy danh sách nhân viên
            employees = employeeDAO.getAllEmployees();
            System.out.println("=== Danh sách Employee ===");
            for (Employee emp : employees) {
                System.out.println("Emp: " + emp.getEmployeeName() + " | ID: " + emp.getId());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        initializeComponents();
        layoutComponents();
        bindDataToForm();
        setupEventHandlers();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        Font regularFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font mediumFont = new Font("Segoe UI", Font.BOLD, 13);

        // Username
        usernameField = new JTextField(20);
        usernameField.setFont(regularFont);
        usernameField.setEnabled(false);

        // Password
        passwordField = new JPasswordField(20);
        passwordField.setFont(regularFont);

        // Role
        String[] roles = {"ADMIN", "USER"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(regularFont);

        // Employee ComboBox
        employeeComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        employeeComboBox.setFont(regularFont);
        for (Employee emp : employees) {
            employeeComboBox.addItem(emp); // Add object directly
        }

        // Buttons
        okButton = new JButton("Lưu");
        okButton.setFont(mediumFont);
        okButton.setBackground(new Color(0, 122, 255));
        okButton.setForeground(Color.WHITE);

        cancelButton = new JButton("Hủy");
        cancelButton.setFont(mediumFont);
        cancelButton.setBackground(new Color(242, 242, 247));
        cancelButton.setForeground(new Color(0, 122, 255));
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Row 2: Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Row 3: Role
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);

        // Row 4: Employee
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(employeeComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void bindDataToForm() {
        usernameField.setText(userDTO.getUsername());
        passwordField.setText("");
        roleComboBox.setSelectedItem(userDTO.getRole());

        // ✅ So sánh UUID rồi chọn đúng index
        if (userDTO.getEmployeeId() != null) {
        	System.out.println(">>> Mapping: employeeId cần chọn: " + userDTO);

        	for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
        	    Employee emp = employeeComboBox.getItemAt(i);
        	    System.out.println("→ So với: " + emp.getEmployeeName() + " | ID: " + emp.getId());

        	    if (emp.getId().equals(userDTO.getEmployeeId())) {
        	        System.out.println("✅ Trùng với nhân viên ở index " + i + ": " + emp.getEmployeeName());
        	    }
        	}
        }
    }

    private void setupEventHandlers() {
        okButton.addActionListener(e -> {
            if (validateInput()) {
                userDTO.setUsername(usernameField.getText().trim());
                userDTO.setPassword(new String(passwordField.getPassword()));
                userDTO.setRole((String) roleComboBox.getSelectedItem());

                Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
                if (selectedEmployee != null) {
                    userDTO.setEmployeeId(selectedEmployee.getId());
                }

                try {
                    UserService userService = new UserServiceImpl();

                    User user = new User();
                    user.setId(userDTO.getId());
                    user.setUsername(userDTO.getUsername());
                    user.setPassword(userDTO.getPassword());
                    user.setRole(userDTO.getRole());
                    user.setEmployeeId(userDTO.getEmployeeId());
                    user.setUpdatedAt(new java.util.Date());

                    userService.updateUser(user);

                    JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    confirmed = true;
                    setVisible(false);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });
    }

    private boolean validateInput() {
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isUserUpdated() {
        return confirmed;
    }

    public EditUserDTO getUserDTO() {
        return userDTO;
    }
}
