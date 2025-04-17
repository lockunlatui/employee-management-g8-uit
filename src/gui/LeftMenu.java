package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeftMenu extends JPanel implements ActionListener {

    private JButton homeButton;
    private JButton employeeButton;
    private JButton departmentButton;
    private JButton attendanceLogButton;
    private JButton salaryButton;
    private JButton userListButton;

    private MainFrame mainFrame;

    public LeftMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Use BoxLayout for a vertical menu
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(230, 230, 230)); // Light background
        //  setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)); // Remove bottom border, if it exists

        homeButton = createMenuButton("Trang chủ");
        employeeButton = createMenuButton("Quản lí thông tin nhân viên");
        departmentButton = createMenuButton("Quản lí phòng ban");
        attendanceLogButton = createMenuButton("Quản lí chấm công");
        salaryButton = createMenuButton("Quản lí lương");
        userListButton = createMenuButton("Quản lí người dùng");

        homeButton.addActionListener(this);
        employeeButton.addActionListener(this);
        departmentButton.addActionListener(this);
        attendanceLogButton.addActionListener(this);
        salaryButton.addActionListener(this);
        userListButton.addActionListener(this);

        // Add buttons directly
        add(homeButton);
        add(employeeButton);
        add(departmentButton);
        add(attendanceLogButton);
        add(salaryButton);
        add(userListButton);

        // Add vertical glue to push buttons to the top
        add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align the button text
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setBackground(new Color(240, 240, 240));
        button.setForeground(new Color(50, 50, 50));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 200, 200));
                button.setForeground(new Color(0, 0, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
                button.setForeground(new Color(50, 50, 50));
            }
        });
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        JPanel contentPanel = null;

        if (command.equals("Trang chủ")) {
            contentPanel = new HomePagePanel();
        } else if (command.equals("Quản lí thông tin nhân viên")) {
            contentPanel = new EmployeeManagementPanel();
        } else if (command.equals("Quản lí phòng ban")) {
            contentPanel = new DepartmentManagementPanel();
        } else if (command.equals("Quản lí chấm công")) {
            contentPanel = new AttendanceLogPanel();
        } else if (command.equals("Quản lí lương")) {
            contentPanel = new SalaryManagementPanel();
        } else if (command.equals("Quản lí người dùng")) {
            contentPanel = new UserManagementPanel();
        }

        if (contentPanel != null) {
            mainFrame.setMainContent(contentPanel);
        }
    }
}

