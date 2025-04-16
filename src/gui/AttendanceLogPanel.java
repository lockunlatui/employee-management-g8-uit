package gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;

@SuppressWarnings("serial")
public class AttendanceLogPanel extends JPanel {

    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    public AttendanceLogPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Mã NV", "Tên NV", "Thời gian vào", "Thời gian ra"};
        tableModel = new DefaultTableModel(columnNames, 0);
        attendanceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(attendanceTable);

        add(new JLabel("Quản lý Chấm công", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);


        tableModel.addRow(new Object[]{"NV001", "Nguyễn Văn A", new Date(), null});
        tableModel.addRow(new Object[]{"NV002", "Trần Thị B", new Date(), new Date()});
    }
}