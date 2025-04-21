package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    
    private JButton activeButton = null;
    private MainFrame mainFrame;
    
    // Màu sắc hiện đại
    private Color backgroundColor = new Color(52, 73, 94); // Màu xanh đậm
    private Color buttonColor = new Color(44, 62, 80); // Màu nút không active
    private Color hoverColor = new Color(30, 144, 255); // Màu xanh dương khi hover
    private Color activeColor = new Color(41, 128, 185); // Màu xanh dương khi active
    private Color textColor = new Color(236, 240, 241); // Màu chữ trắng

    public LeftMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Thiết lập layout và màu nền
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(230, 0)); // Chiều rộng cố định
        setBorder(new EmptyBorder(15, 0, 15, 0)); // Padding trên dưới
        
        // Thêm logo hoặc tiêu đề
        JLabel titleLabel = new JLabel("QUẢN LÝ NHÂN SỰ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        add(titleLabel);
        
        // Tạo các nút menu với icon
        homeButton = createMenuButton("Trang chủ", UIManager.getIcon("FileView.homeIcon"));
        employeeButton = createMenuButton("Quản lí nhân viên", UIManager.getIcon("FileView.computerIcon"));
        departmentButton = createMenuButton("Quản lí phòng ban", UIManager.getIcon("FileView.directoryIcon"));
        attendanceLogButton = createMenuButton("Quản lí chấm công", UIManager.getIcon("FileView.fileIcon"));
        salaryButton = createMenuButton("Quản lí lương", UIManager.getIcon("Tree.leafIcon"));
        userListButton = createMenuButton("Quản lí người dùng", UIManager.getIcon("FileChooser.detailsViewIcon"));

        // Thêm action listener
        homeButton.addActionListener(this);
        employeeButton.addActionListener(this);
        departmentButton.addActionListener(this);
        attendanceLogButton.addActionListener(this);
        salaryButton.addActionListener(this);
        userListButton.addActionListener(this);

        // Thêm các separator giữa các nhóm chức năng
        add(Box.createVerticalStrut(5));
        add(homeButton);
        add(Box.createVerticalStrut(2));
        
        JSeparator separator1 = new JSeparator();
        separator1.setMaximumSize(new Dimension(200, 1));
        separator1.setForeground(new Color(52, 73, 94));
        separator1.setBackground(new Color(44, 62, 80));
        add(Box.createVerticalStrut(10));
        add(separator1);
        add(Box.createVerticalStrut(10));
        
        add(employeeButton);
        add(Box.createVerticalStrut(2));
        add(departmentButton);
        add(Box.createVerticalStrut(2));
        add(attendanceLogButton);
        add(Box.createVerticalStrut(2));
        add(salaryButton);
        
        JSeparator separator2 = new JSeparator();
        separator2.setMaximumSize(new Dimension(200, 1));
        separator2.setForeground(new Color(52, 73, 94));
        separator2.setBackground(new Color(44, 62, 80));
        add(Box.createVerticalStrut(10));
        add(separator2);
        add(Box.createVerticalStrut(10));
        
        add(userListButton);

        // Đẩy mọi thứ lên trên
        add(Box.createVerticalGlue());
        
        // Thiết lập nút home là active mặc định
        setActiveButton(homeButton);
    }

    private JButton createMenuButton(String text, Icon icon) {
        JButton button = new JButton(text);
        if (icon != null) {
            button.setIcon(new ColorIcon(icon, textColor));
        }
        
        // Thiết lập style cho nút
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setMaximumSize(new Dimension(230, 45));
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        
        // Bỏ viền và thiết lập hiệu ứng hover
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(hoverColor);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(buttonColor);
                }
            }
        });
        
        return button;
    }
    
    // Đổi màu icon
    private class ColorIcon implements Icon {
        private Icon originalIcon;
        private Color color;
        
        public ColorIcon(Icon originalIcon, Color color) {
            this.originalIcon = originalIcon;
            this.color = color;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(color);
            originalIcon.paintIcon(c, g2d, x, y);
            g2d.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return originalIcon.getIconWidth();
        }
        
        @Override
        public int getIconHeight() {
            return originalIcon.getIconHeight();
        }
    }
    
    // Thiết lập nút active
    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(buttonColor);
        }
        
        activeButton = button;
        activeButton.setBackground(activeColor);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        JPanel contentPanel = null;
        
        // Thiết lập nút active
        JButton clickedButton = (JButton) source;
        setActiveButton(clickedButton);
        
        if (source == homeButton) {
            contentPanel = new HomePagePanel();
        } else if (source == employeeButton) {
            contentPanel = new EmployeeManagementPanel();
        } else if (source == departmentButton) {
            contentPanel = new DepartmentManagementPanel();
        } else if (source == attendanceLogButton) {
            contentPanel = new AttendanceManagementPanel();
        } else if (source == salaryButton) {
            contentPanel = new SalaryManagementPanel();
        } else if (source == userListButton) {
            contentPanel = new UserManagementPanel();
        }

        if (contentPanel != null) {
            mainFrame.setMainContent(contentPanel);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ đường viền bên phải
        g.setColor(new Color(44, 62, 80));
        g.fillRect(getWidth() - 1, 0, 1, getHeight());
    }
}

