package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

@SuppressWarnings("serial")
public class HomePagePanel extends JPanel {
    
    private MainFrame mainFrame;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(230, 126, 34);
    private Color greenColor = new Color(46, 204, 113);
    private Color redColor = new Color(231, 76, 60);
    private Color darkColor = new Color(44, 62, 80);
    private Color lightTextColor = new Color(236, 240, 241);

    public HomePagePanel() {
        this(null);
    }
    
    public HomePagePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content - Menu cards
        JPanel menuCardsPanel = createMenuCardsPanel();
        add(menuCardsPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Dashboard quản lý nhân sự");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(darkColor);
        
        // Date and time panel
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dateTimePanel.setOpaque(false);
        
        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(darkColor);
        
        dateTimePanel.add(dateLabel);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateTimePanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMenuCardsPanel() {
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setOpaque(false);
        
        // Thêm các card menu
        cardsPanel.add(createMenuCard("Quản lý nhân viên", 
                UIManager.getIcon("FileView.computerIcon"), 
                "Quản lý thông tin nhân viên", 
                primaryColor, e -> navigateTo("employee")));
                
        cardsPanel.add(createMenuCard("Quản lý phòng ban", 
                UIManager.getIcon("FileView.directoryIcon"), 
                "Quản lý thông tin phòng ban", 
                secondaryColor, e -> navigateTo("department")));
                
        cardsPanel.add(createMenuCard("Quản lý chấm công", 
                UIManager.getIcon("FileView.fileIcon"), 
                "Quản lý chấm công nhân viên", 
                accentColor, e -> navigateTo("attendance")));
                
        cardsPanel.add(createMenuCard("Quản lý lương", 
                UIManager.getIcon("Tree.leafIcon"), 
                "Quản lý lương nhân viên", 
                greenColor, e -> navigateTo("salary")));
                
        cardsPanel.add(createMenuCard("Quản lý người dùng", 
                UIManager.getIcon("FileChooser.detailsViewIcon"), 
                "Quản lý tài khoản người dùng", 
                redColor, e -> navigateTo("user")));
                
        cardsPanel.add(createMenuCard("Báo cáo thống kê", 
                UIManager.getIcon("Table.ascendingSortIcon"), 
                "Xem báo cáo và thống kê", 
                darkColor, e -> navigateTo("report")));
        
        return cardsPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel copyrightLabel = new JLabel("© 2025 Hệ thống Quản lý Nhân sự | Phiên bản 1.0");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(150, 150, 150));
        
        footerPanel.add(copyrightLabel, BorderLayout.CENTER);
        
        return footerPanel;
    }
    
    private JPanel createMenuCard(String title, Icon icon, String description, Color color, ActionListener actionListener) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Vẽ header color
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), 60, 15, 15);
                g2d.fillRect(0, 30, getWidth(), 30);
                
                g2d.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.setOpaque(false);
        
        // Icon and title in one panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel iconLabel = new JLabel();
        if (icon != null) {
            iconLabel.setIcon(createColoredIcon(icon, lightTextColor));
        }
        iconLabel.setForeground(lightTextColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(lightTextColor);
        
        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Description
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(darkColor);
        
        contentPanel.add(descLabel, BorderLayout.NORTH);
        
        // Add to card
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Kích hoạt ActionListener khi click
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, title));
                }
            }
        });
        
        return card;
    }
    
    // Icon với màu tùy chỉnh
    private Icon createColoredIcon(Icon originalIcon, Color color) {
        int width = originalIcon.getIconWidth();
        int height = originalIcon.getIconHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = image.createGraphics();
        originalIcon.paintIcon(null, g2d, 0, 0);
        g2d.dispose();
        
        // Tạo filter để đổi màu icon
        ImageFilter filter = new RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                if ((rgb & 0xFF000000) != 0) { // Nếu pixel không trong suốt
                    return (rgb & 0xFF000000) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
                }
                return rgb;
            }
        };
        
        ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
        Image coloredImage = Toolkit.getDefaultToolkit().createImage(producer);
        
        return new ImageIcon(coloredImage);
    }
    
    private void navigateTo(String destination) {
        if (mainFrame == null) return;
        
        switch (destination) {
            case "employee":
                mainFrame.showEmployeePanel();
                break;
            case "department":
                mainFrame.showDepartmentPanel();
                break;
            case "attendance":
                mainFrame.setMainContent(new AttendanceManagementPanel());
                break;
            case "salary":
                mainFrame.setMainContent(new SalaryManagementPanel());
                break;
            case "user":
                mainFrame.showUserPanel();
                break;
            case "report":
                JOptionPane.showMessageDialog(mainFrame, 
                    "Chức năng Báo cáo thống kê đang được phát triển.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }
}