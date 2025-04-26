package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import dao.DepartmentDAO;
import dto.DepartmentDTO;
import dto.EditDepartmentDTO;
import dto.DeleteDepartmentDTO;
import model.Department;

@SuppressWarnings("serial")
public class DepartmentManagementPanel extends JPanel {
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JButton addButton;
    private JPanel buttonPanel;
    private DepartmentDAO departmentDAO;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    
    // Màu sắc
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(230, 126, 34);
    private Color lightBgColor = new Color(248, 249, 250);
    private Color darkTextColor = new Color(44, 62, 80);
    private Color lightTextColor = new Color(236, 240, 241);
    private Color borderColor = new Color(223, 230, 233);

    public DepartmentManagementPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(lightBgColor);
        
        // ===== Header Panel =====
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // ===== Content Panel =====
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(lightBgColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Bảng phòng ban
        createDepartmentTable();
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== Footer Panel =====
        JPanel footerPanel = createFooterPanel();
        contentPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Tải dữ liệu phòng ban
        departmentDAO = new DepartmentDAO();
        loadDepartmentData();
        
        // Thêm sự kiện
        setupEventHandlers();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Tiêu đề
        JLabel titleLabel = new JLabel("Quản lý phòng ban");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(darkTextColor);
        
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        searchButton = new JButton("Tìm kiếm");
        searchButton.setPreferredSize(new Dimension(100, 32));
        searchButton.setBackground(secondaryColor);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        
        refreshButton = new JButton("Làm mới");
        refreshButton.setPreferredSize(new Dimension(90, 32));
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setForeground(secondaryColor);
        refreshButton.setBorder(BorderFactory.createLineBorder(secondaryColor));
        refreshButton.setFocusPainted(false);
        
        searchPanel.add(new JLabel("Tìm kiếm: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(refreshButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void createDepartmentTable() {
        String[] columnNames = { "ID", "Tên phòng ban", "Hành động" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Chỉ cho phép edit cột hành động
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) {
                    return ActionButtonPanel.class;
                }
                return Object.class;
            }
        };
        
        departmentTable = new JTable(tableModel);
        departmentTable.setRowHeight(45);
        departmentTable.setShowGrid(false);
        departmentTable.setIntercellSpacing(new Dimension(0, 0));
        departmentTable.setBackground(Color.WHITE);
        departmentTable.setSelectionBackground(new Color(232, 242, 254));
        departmentTable.setSelectionForeground(darkTextColor);
        departmentTable.getTableHeader().setBackground(Color.WHITE);
        departmentTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        departmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        departmentTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        departmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Thiết lập độ rộng các cột
        departmentTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        departmentTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        departmentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        // Thiết lập renderer và editor cho cột hành động
        departmentTable.getColumnModel().getColumn(2).setCellRenderer(new ActionButtonRenderer());
        departmentTable.getColumnModel().getColumn(2).setCellEditor(new ActionButtonEditor());
        
        // ScrollPane với border đẹp
        scrollPane = new JScrollPane(departmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Tạo row sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        departmentTable.setRowSorter(sorter);
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        addButton = new JButton("+ Thêm phòng ban");
        addButton.setPreferredSize(new Dimension(150, 38));
        addButton.setBackground(primaryColor);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        buttonPanel.add(addButton);
        
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private void setupEventHandlers() {
        searchButton.addActionListener(e -> searchDepartments());
        refreshButton.addActionListener(e -> loadDepartmentData());
        addButton.addActionListener(e -> addDepartment());
        
        // Hiệu ứng hover cho nút
        addHoverEffect(searchButton, secondaryColor, new Color(30, 132, 199));
        addHoverEffect(addButton, primaryColor, new Color(30, 108, 165));
        addHoverEffect(refreshButton, Color.WHITE, new Color(240, 240, 240), secondaryColor, secondaryColor);
    }
    
    private void addHoverEffect(JButton button, Color normalBg, Color hoverBg) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalBg);
            }
        });
    }
    
    private void addHoverEffect(JButton button, Color normalBg, Color hoverBg, Color normalFg, Color hoverFg) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
                button.setForeground(hoverFg);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalBg);
                button.setForeground(normalFg);
            }
        });
    }

    private class ActionButtonRenderer implements TableCellRenderer {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;

        public ActionButtonRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            panel.setOpaque(true);
            
            editButton = new JButton("Sửa");
            editButton.setPreferredSize(new Dimension(70, 30));
            editButton.setBackground(secondaryColor);
            editButton.setForeground(Color.WHITE);
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            deleteButton = new JButton("Xóa");
            deleteButton.setPreferredSize(new Dimension(70, 30));
            deleteButton.setBackground(new Color(231, 76, 60));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            panel.add(editButton);
            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
            }
            return panel;
        }
    }

    private class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private UUID departmentId;
        private boolean isPushed;

        public ActionButtonEditor() {
            super(new JTextField());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            
            editButton = new JButton("Sửa");
            editButton.setPreferredSize(new Dimension(70, 30));
            editButton.setBackground(secondaryColor);
            editButton.setForeground(Color.WHITE);
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            deleteButton = new JButton("Xóa");
            deleteButton.setPreferredSize(new Dimension(70, 30));
            deleteButton.setBackground(new Color(231, 76, 60));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

            panel.add(editButton);
            panel.add(deleteButton);
            
            // Thêm hover effect cho các nút
            addHoverEffect(editButton, secondaryColor, new Color(30, 132, 199));
            addHoverEffect(deleteButton, new Color(231, 76, 60), new Color(211, 56, 40));

            editButton.addActionListener(e -> {
                isPushed = true;
                fireEditingStopped();
                if (departmentId != null) {
                    editDepartment(departmentId);
                }
            });

            deleteButton.addActionListener(e -> {
                isPushed = true;
                fireEditingStopped();
                if (departmentId != null) {
                    deleteDepartment(departmentId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof ActionButtonPanel) {
                departmentId = ((ActionButtonPanel) value).getDepartmentId();
            } else {
                // Lấy departmentId từ dòng hiện tại
                try {
                    int modelRow = table.convertRowIndexToModel(row);
                    departmentId = (UUID) tableModel.getValueAt(modelRow, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return new ActionButtonPanel(departmentId);
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private class ActionButtonPanel extends JPanel {
        private UUID departmentId;

        public ActionButtonPanel(UUID departmentId) {
            this.departmentId = departmentId;
        }

        public UUID getDepartmentId() {
            return departmentId;
        }
    }

    private void loadDepartmentData() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Department> departments = departmentDAO.getAllDepartments();
            tableModel.setRowCount(0);

            for (Department department : departments) {
                tableModel.addRow(new Object[] { 
                    department.getId(),
                    department.getName(),
                    new ActionButtonPanel(department.getId()) 
                });
            }
            setCursor(Cursor.getDefaultCursor());
            
            if (departments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu phòng ban.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu phòng ban: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchDepartments() {
        String searchText = searchField.getText().trim();
        
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Department> departments = departmentDAO.getAllDepartments();
            tableModel.setRowCount(0);
            
            for (Department department : departments) {
                if (department.getName().toLowerCase().contains(searchText.toLowerCase()) 
                
                        || searchText.isEmpty()) {
                    tableModel.addRow(new Object[] { 
                        department.getId(),
                        department.getName(),
                        new ActionButtonPanel(department.getId()) 
                    });
                }
            }
            setCursor(Cursor.getDefaultCursor());
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phòng ban phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm phòng ban: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addDepartment() {
        AddDepartmentDialog dialog = new AddDepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadDepartmentData();
        }
    }

    private void editDepartment(UUID departmentId) {
        try {
            Department department = departmentDAO.getDepartmentById(departmentId);
            if (department != null) {
                EditDepartmentDialog dialog = new EditDepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this), departmentId);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    loadDepartmentData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phòng ban.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin phòng ban: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteDepartment(UUID departmentId) {
        try {
            Department department = departmentDAO.getDepartmentById(departmentId);
            if (department != null) {
                DeleteDepartmentDialog dialog = new DeleteDepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this), departmentId);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    loadDepartmentData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phòng ban.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin phòng ban: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}