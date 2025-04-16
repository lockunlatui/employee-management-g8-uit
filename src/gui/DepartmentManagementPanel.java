package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DepartmentManagementPanel extends JPanel {

    private JList<String> departmentList;
    private DefaultListModel<String> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public DepartmentManagementPanel() {
        setLayout(new BorderLayout());

        List<String> departments = new ArrayList<>();
        departments.add("Phòng Kinh doanh");
        departments.add("Phòng Kỹ thuật");
        departments.add("Phòng Hành chính");

        listModel = new DefaultListModel<>();
        for (String department : departments) {
            listModel.addElement(department);
        }

        departmentList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(departmentList);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Thêm");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JLabel("Danh sách phòng ban:", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String newDepartment = JOptionPane.showInputDialog(this, "Nhập tên phòng ban mới:");
            if (newDepartment != null && !newDepartment.trim().isEmpty()) {
                listModel.addElement(newDepartment);
            }
        });
    }
}