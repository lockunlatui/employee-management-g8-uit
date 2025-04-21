package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.UUID;

import dao.DepartmentDAO;
import dto.DeleteDepartmentDTO;
import model.Department;

public class DeleteDepartmentDialog extends JDialog {
    private JLabel messageLabel;
    private JButton okButton;
    private JButton cancelButton;
    private boolean confirmed;
    private DeleteDepartmentDTO departmentDTO;
    private DepartmentDAO departmentDAO;

    public DeleteDepartmentDialog(Frame parent, UUID departmentId) {
        super(parent, "Xóa phòng ban", true);
        departmentDAO = new DepartmentDAO();
        departmentDTO = new DeleteDepartmentDTO();
        departmentDTO.setId(departmentId);

        try {
            Department department = departmentDAO.getDepartmentById(departmentId);
            if (department != null) {
                departmentDTO.setName(department.getName());
                departmentDTO.setCode(department.getCode());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin phòng ban: " + e.getMessage(),
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
        
        messageLabel = new JLabel("Bạn có chắc chắn muốn xóa phòng ban '" + departmentDTO.getName() + "' không?");
        messageLabel.setFont(regularFont);
        messageLabel.setForeground(new Color(51, 51, 51));
        
        okButton = new JButton("Xóa");
        okButton.setFont(mediumFont);
        okButton.setBackground(new Color(255, 59, 48)); // Màu đỏ của Apple
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

        // Message
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.add(messageLabel);
        mainPanel.add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupEventHandlers() {
        okButton.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public DeleteDepartmentDTO getDepartmentDTO() {
        return departmentDTO;
    }
} 