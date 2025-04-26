package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.UUID;

import dao.DepartmentDAO;
import dto.DepartmentDTO;

@SuppressWarnings("serial")
public class AddDepartmentDialog extends JDialog {
    private JTextField nameField;
    private JTextField codeField;
    private JButton okButton;
    private JButton cancelButton;
    private boolean confirmed = false;
    private DepartmentDTO departmentDTO;
    private DepartmentDAO departmentDAO;
    
    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color lightBgColor = new Color(248, 249, 250);
    private Color darkTextColor = new Color(44, 62, 80);
    private Color borderColor = new Color(223, 230, 233);

    public AddDepartmentDialog(Frame owner) {
        super(owner, "Thêm phòng ban mới", true);
        departmentDAO = new DepartmentDAO();
        departmentDTO = new DepartmentDTO();
        departmentDTO.setId(UUID.randomUUID());
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        
        pack();
        setSize(450, 280);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        nameField.setPreferredSize(new Dimension(300, 35));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        codeField = new JTextField(20);
        codeField.setPreferredSize(new Dimension(300, 35));
        codeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        codeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        okButton = new JButton("Thêm phòng ban");
        okButton.setPreferredSize(new Dimension(150, 40));
        okButton.setBackground(primaryColor);
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(secondaryColor);
        cancelButton.setBorder(BorderFactory.createLineBorder(secondaryColor));
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void layoutComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Header
        JLabel headerLabel = new JLabel("Thêm phòng ban mới");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(darkTextColor);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Tên phòng ban label
        JLabel nameLabel = new JLabel("Tên phòng ban:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(darkTextColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        
        // Tên phòng ban field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
    }

    private void setupEventHandlers() {
        // Tự động tạo mã từ tên
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!nameField.getText().trim().isEmpty()) {
                    String name = nameField.getText().trim();
                    String autoCode = "PB" + name.substring(0, Math.min(3, name.length())).toUpperCase();
                    codeField.setText(autoCode);
                }
            }
        });
        
        okButton.addActionListener(e -> {
            if (validateInput()) {
                departmentDTO.setName(nameField.getText().trim());
                departmentDTO.setCode(codeField.getText().trim());
                
                try {
                    // Kiểm tra tên phòng ban đã tồn tại hay chưa
                    if (departmentDAO.isDepartmentNameExists(departmentDTO.getName())) {
                        JOptionPane.showMessageDialog(this, "Tên phòng ban đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Thêm vào cơ sở dữ liệu trực tiếp
                    model.Department newDepartment = new model.Department();
                    newDepartment.setId(departmentDTO.getId());
                    newDepartment.setName(departmentDTO.getName());
                    departmentDAO.addDepartment(newDepartment);
                    
                    JOptionPane.showMessageDialog(this, "Thêm phòng ban thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    confirmed = true;
                    dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi thêm phòng ban: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Hiệu ứng hover cho nút
        addHoverEffect(okButton, primaryColor, new Color(30, 108, 165));
        addHoverEffect(cancelButton, Color.WHITE, new Color(240, 240, 240), secondaryColor, secondaryColor);
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

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên phòng ban không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (codeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã phòng ban không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            codeField.requestFocus();
            return false;
        }
        
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public DepartmentDTO getDepartmentDTO() {
        return departmentDTO;
    }
} 