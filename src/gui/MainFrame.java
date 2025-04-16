package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
	private LeftMenu leftMenu;
	private JPanel contentPanel;

	public MainFrame() {
		super("Employee Management");

		setSize(1366, 768);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		leftMenu = new LeftMenu(this);
		add(leftMenu, BorderLayout.WEST);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(new JLabel("Chào mừng đến với ứng dụng!", SwingConstants.CENTER), BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);

		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MainFrame());
	}

	public void setMainContent(JPanel newContent) {
		remove(contentPanel);
		contentPanel = newContent;
		add(contentPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}
