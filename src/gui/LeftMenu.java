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

		setPreferredSize(new Dimension(200, mainFrame.getHeight()));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(new Color(240, 240, 240));

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

		add(Box.createVerticalGlue());
		add(homeButton);
		add(employeeButton);
		add(departmentButton);
		add(attendanceLogButton);
		add(salaryButton);
		add(userListButton);
		add(Box.createVerticalGlue());
	}

	private JButton createMenuButton(String text) {
		JButton button = new JButton(text);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		JPanel contentLabel = null;

		if (command.equals("Trang chủ")) {
			contentLabel = new HomePagePanel();
		} else if (command.equals("Quản lí thông tin nhân viên")) {
			contentLabel = new EmployeeManagementPanel();
		} else if (command.equals("Quản lí phòng ban")) {
			contentLabel = new DepartmentManagementPanel();
		} else if (command.equals("Quản lí chấm công")) {
			contentLabel = new AttendanceLogPanel();
		} else if (command.equals("Quản lí lương")) {
			contentLabel = new SalaryManagementPanel();
		} else if (command.equals("Quản lí người dùng")) {
			contentLabel = new UserManagementPanel();
		}

		if (contentLabel != null) {
			mainFrame.setMainContent(contentLabel);
		}
	}
}