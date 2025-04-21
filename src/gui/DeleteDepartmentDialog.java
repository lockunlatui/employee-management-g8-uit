package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.UUID;

import dao.DepartmentDAO;
import model.Department;

@SuppressWarnings("serial")
public class DeleteDepartmentDialog extends JDialog {
    private JLabel messageLabel;
    private JButton okButton;
    private JButton cancelButton;
    private boolean confirmed;
    private DepartmentDAO departmentDAO;
    private UUID departmentId;
    private String departmentName;
    
    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color dangerColor = new Color(231, 76, 60);
    private Color lightBgColor = new Color(248, 249, 250);
    private Color darkTextColor = new Color(44, 62, 80);
    private Color borderColor = new Color(223, 230, 233);

    public DeleteDepartmentDialog(Frame parent, UUID departmentId) {
        super(parent, "Xóa phòng ban", true);
        this.departmentId = departmentId;
        departmentDAO = new DepartmentDAO();

        try {
            Department department = departmentDAO.getDepartmentById(departmentId);
            if (department != null) {
                this.departmentName = department.getName();
                
                initializeComponents();
                layoutComponents();
                setupEventHandlers();
                
                pack();
                setSize(450, 200);
                setLocationRelativeTo(parent);
                setResizable(false);
            } else {
                JOptionPane.showMessageDialog(parent, "Không tìm thấy phòng ban!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin phòng ban: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void initializeComponents() {
        messageLabel = new JLabel("Bạn có chắc chắn muốn xóa phòng ban sau không?");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(darkTextColor);
        
        JLabel departmentLabel = new JLabel(departmentName);
        departmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        departmentLabel.setForeground(dangerColor);
        departmentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel warningLabel = new JLabel("Hành động này không thể khôi phục lại!");
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        warningLabel.setForeground(Color.GRAY);
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        okButton = new JButton("Xóa phòng ban");
        okButton.setPreferredSize(new Dimension(130, 40));
        okButton.setBackground(dangerColor);
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
        
        // Message panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(3, 1, 0, 10));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.add(messageLabel);
        messagePanel.add(new JLabel(departmentName, SwingConstants.CENTER));
        messagePanel.add(new JLabel("Hành động này không thể khôi phục lại!", SwingConstants.CENTER));
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
    }

    private void setupEventHandlers() {
        okButton.addActionListener(e -> {
            try {
                departmentDAO.deleteDepartment(departmentId);
                JOptionPane.showMessageDialog(this, 
                        "Phòng ban '" + departmentName + "' đã được xóa thành công.",
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                confirmed = true;
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                        "Lỗi khi xóa phòng ban: " + ex.getMessage(),
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Hiệu ứng hover cho nút
        addHoverEffect(okButton, dangerColor, new Color(211, 56, 40));
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

    public boolean isConfirmed() {
        return confirmed;
    }
} 