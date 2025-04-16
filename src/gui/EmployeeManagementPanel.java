package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class EmployeeManagementPanel extends JPanel {

    private JList<String> employeeList;
    private DefaultListModel<String> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout());

        List<String> employees = new ArrayList<>();
        employees.add("Nguyễn Văn A");
        employees.add("Trần Thị B");
        employees.add("Lê Văn C");

        listModel = new DefaultListModel<>();
        for (String employee : employees) {
            listModel.addElement(employee);
        }

        employeeList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(employeeList);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Thêm");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JLabel("Danh sách nhân viên:", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String newEmployee = JOptionPane.showInputDialog(this, "Nhập tên nhân viên mới:");
            if (newEmployee != null && !newEmployee.trim().isEmpty()) {
                listModel.addElement(newEmployee);
            }
        });

    }
}