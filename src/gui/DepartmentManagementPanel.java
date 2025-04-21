package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
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

    public DepartmentManagementPanel() {
        setLayout(new BorderLayout());

        // Tạo panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Tìm kiếm");
        
        searchButton.addActionListener(e -> searchDepartments());
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        String[] columnNames = { "Tên phòng ban", "Hành động" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        departmentTable = new JTable(tableModel);
        departmentTable.setRowHeight(30);
        departmentTable.getColumnModel().getColumn(1).setCellRenderer(new ActionButtonRenderer());
        departmentTable.getColumnModel().getColumn(1).setCellEditor(new ActionButtonEditor());
        scrollPane = new JScrollPane(departmentTable);

        buttonPanel = new JPanel();
        addButton = new JButton("Thêm");
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(40, 96, 175));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(51, 122, 255));
            }
        });

        buttonPanel.add(addButton);

        add(new JLabel("Quản lý Phòng ban", SwingConstants.CENTER), BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        departmentDAO = new DepartmentDAO();
        loadDepartmentData();
        addButton.addActionListener(e -> {
            addDepartment();
        });

        departmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = departmentTable.rowAtPoint(evt.getPoint());
                int col = departmentTable.columnAtPoint(evt.getPoint());
                
                if (row >= 0 && col == 1) {
                    Rectangle cellRect = departmentTable.getCellRect(row, col, false);
                    int x = evt.getX() - cellRect.x;
                    int y = evt.getY() - cellRect.y;
                    
                    if (x < cellRect.width / 2) {
                        UUID departmentId = ((ActionButtonPanel) departmentTable.getModel().getValueAt(row, col)).getDepartmentId();
                        editDepartment(departmentId);
                    } else {
                        UUID departmentId = ((ActionButtonPanel) departmentTable.getModel().getValueAt(row, col)).getDepartmentId();
                        deleteDepartment(departmentId);
                    }
                }
            }
        });
    }

    private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");
            
            editButton.setBackground(new Color(51, 122, 255));
            editButton.setForeground(Color.WHITE);
            deleteButton.setBackground(new Color(217, 83, 79));
            deleteButton.setForeground(Color.WHITE);
            
            editButton.setFocusPainted(false);
            deleteButton.setFocusPainted(false);
            
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    private class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private UUID departmentId;

        public ActionButtonEditor() {
            super(new JTextField());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            editButton.setBackground(new Color(66, 139, 202));
            editButton.setForeground(Color.WHITE);
            editButton.setFocusPainted(false);

            deleteButton.setBackground(new Color(217, 83, 79));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);

            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> {
                if (departmentId != null) {
                    editDepartment(departmentId);
                }
            });

            deleteButton.addActionListener(e -> {
                if (departmentId != null) {
                    deleteDepartment(departmentId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            departmentId = (UUID) table.getValueAt(row, 0);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return departmentId;
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
            List<Department> departments = departmentDAO.getAllDepartments();
            tableModel.setRowCount(0);

            for (Department department : departments) {
                tableModel.addRow(new Object[] { 
                    department.getName(),
                    new ActionButtonPanel(department.getId()) 
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu phòng ban: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchDepartments() {
        String searchText = searchField.getText().trim();
        
        try {
            List<Department> departments = departmentDAO.getAllDepartments();
            tableModel.setRowCount(0);
            
            for (Department department : departments) {
                if (department.getName().toLowerCase().contains(searchText.toLowerCase()) || searchText.isEmpty()) {
                    tableModel.addRow(new Object[] { 
                        department.getName(),
                        new ActionButtonPanel(department.getId()) 
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm phòng ban: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addDepartment() {
        AddDepartmentDialog dialog = new AddDepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            DepartmentDTO departmentDTO = dialog.getDepartmentDTO();
            try {
                Department newDepartment = new Department();
                newDepartment.setId(departmentDTO.getId());
                newDepartment.setName(departmentDTO.getName());
                newDepartment.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
                newDepartment.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));

                departmentDAO.addDepartment(newDepartment);

                loadDepartmentData();
                JOptionPane.showMessageDialog(this, "Thêm phòng ban thành công.", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm phòng ban vào database: " + ex.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void editDepartment(UUID departmentId) {
        EditDepartmentDialog dialog = new EditDepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this), departmentId);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            EditDepartmentDTO departmentDTO = dialog.getDepartmentDTO();
            try {
                Department updatedDepartment = new Department();
                updatedDepartment.setId(departmentDTO.getId());
                updatedDepartment.setName(departmentDTO.getName());
                updatedDepartment.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));

                departmentDAO.updateDepartment(updatedDepartment);

                loadDepartmentData();
                JOptionPane.showMessageDialog(this, "Cập nhật phòng ban thành công.", "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật phòng ban trong database: " + ex.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void deleteDepartment(UUID departmentId) {
        DeleteDepartmentDialog dialog = new DeleteDepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this), departmentId);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                departmentDAO.deleteDepartment(departmentId);
                JOptionPane.showMessageDialog(this, "Xóa phòng ban thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDepartmentData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa phòng ban: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}