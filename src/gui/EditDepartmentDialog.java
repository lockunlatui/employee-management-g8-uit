package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.UUID;

import dao.DepartmentDAO;
import dto.EditDepartmentDTO;
import model.Department;

public class EditDepartmentDialog extends JDialog {
    private JTextField nameField;
    private JButton okButton;
    private JButton cancelButton;
    private boolean confirmed = false;
    private EditDepartmentDTO departmentDTO;
    private DepartmentDAO departmentDAO;

    public EditDepartmentDialog(Frame parent, UUID departmentId) {
        super(parent, "Sửa phòng ban", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setResizable(false);

        departmentDAO = new DepartmentDAO();
        departmentDTO = new EditDepartmentDTO();
        departmentDTO.setId(departmentId);

        // Khởi tạo các thành phần UI
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tên phòng ban
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Tên phòng ban:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);

        // Nút OK và Cancel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    departmentDTO.setName(nameField.getText().trim());
                    confirmed = true;
                    dispose();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        // Load dữ liệu phòng ban
        try {
            Department department = departmentDAO.getDepartmentById(departmentId);
            if (department != null) {
                nameField.setText(department.getName());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin phòng ban: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng ban", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public EditDepartmentDTO getDepartmentDTO() {
        return departmentDTO;
    }
} 