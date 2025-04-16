package gui;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class HomePagePanel extends JPanel {

    public HomePagePanel() {
        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Chào mừng đến với Trang chủ!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);

        JLabel descriptionLabel = new JLabel("Đây là trang chủ của ứng dụng quản lý nhân viên.", SwingConstants.CENTER);
        add(descriptionLabel, BorderLayout.SOUTH);
    }
}