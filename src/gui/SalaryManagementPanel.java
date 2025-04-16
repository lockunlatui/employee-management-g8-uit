package gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class SalaryManagementPanel extends JPanel {

    private JTable salaryTable;
    private DefaultTableModel tableModel;

    public SalaryManagementPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Mã NV", "Tên NV", "Lương cơ bản", "Phụ cấp", "Thực nhận"};
        tableModel = new DefaultTableModel(columnNames, 0);
        salaryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salaryTable);

        add(new JLabel("Quản lý Lương", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        tableModel.addRow(new Object[]{"NV001", "Nguyễn Văn A", 10000000, 1500000, 11500000});
        tableModel.addRow(new Object[]{"NV002", "Trần Thị B", 8000000, 1000000, 9000000});
    }
}