package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import database.ConnectionManager;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private LeftMenu leftMenu;
	private JPanel contentPanel;

	public MainFrame() {
		super("Employee Management");

		setSize(1366, 768);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
        if (!ConnectionManager.isConnectionSuccessful()) {
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra cấu hình.",
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE);
   
            System.exit(1);
            return;
        } else {
            System.out.println("Database connection successful upon application start.");
        }

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
