package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.Timestamp;
import java.sql.SQLException;

import model.Employee;
import model.Department;
import dao.AttendanceLogDAO;
import dao.EmployeeDAO;

@SuppressWarnings("serial")
public class AttendanceManagementPanel extends JPanel {
    
    // Các thành phần GUI
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<EmployeeItem> employeeComboBox;
    private JSpinner datePicker;
    private JTextField timeInField;
    private JTextField timeOutField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Thành phần lọc dữ liệu
    private JPanel filterPanel;
    private JSpinner fromDatePicker;
    private JSpinner toDatePicker;
    private JButton filterButton;
    private JButton resetFilterButton;
    
    // Định dạng thời gian
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // Data Access Object
    private AttendanceLogDAO attendanceLogDAO;
    private EmployeeDAO employeeDAO;
    
    // Lưu trữ ID của bản ghi đang chọn
    private UUID selectedAttendanceId;
    
    // Map để lưu trữ ID tương ứng với mỗi dòng
    private Map<Integer, UUID> rowToIdMap;
    
    /**
     * Lớp nội bộ để hiển thị nhân viên trong ComboBox
     */
    private class EmployeeItem {
        private UUID id;
        private String name;
        private String departmentName;
        
        public EmployeeItem(UUID id, String name, String departmentName) {
            this.id = id;
            this.name = name;
            this.departmentName = departmentName;
        }
        
        public UUID getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return "[" + id.toString().substring(0, 8) + "] " + name;
        }
    }
    
    public AttendanceManagementPanel() {
        // Thiết lập layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Khởi tạo DAO
        attendanceLogDAO = new AttendanceLogDAO();
        employeeDAO = new EmployeeDAO();
        
        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("QUẢN LÝ CHẤM CÔNG NHÂN VIÊN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        
        // Tạo bảng hiển thị thông tin chấm công
        createAttendanceTable();
        
        // Tạo form nhập liệu
        JPanel inputPanel = createInputPanel();
        
        // Tạo panel lọc dữ liệu
        filterPanel = createFilterPanel();
        
        // Panel nút chức năng
        JPanel buttonPanel = createButtonPanel();
        
        // Thêm các panel vào panel chính
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel chứa bảng và panel lọc
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        add(inputPanel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load dữ liệu nhân viên vào ComboBox
        loadEmployees();
        
        // Load dữ liệu chấm công từ database
        loadAttendanceLogs();
    }
    
    /**
     * Load danh sách nhân viên từ cơ sở dữ liệu vào ComboBox
     */
    private void loadEmployees() {
        try {
            // Xóa dữ liệu cũ trong ComboBox
            employeeComboBox.removeAllItems();
            
            // Lấy danh sách nhân viên từ database
            java.util.List<Employee> employees = employeeDAO.getAllEmployees();
            
            // Thêm vào ComboBox
            for (Employee employee : employees) {
                String departmentName = employee.getDepartment() != null ? 
                        employee.getDepartment().getDepartmentName() : "";
                EmployeeItem item = new EmployeeItem(
                        employee.getId(), 
                        employee.getEmployeeName(), 
                        departmentName);
                employeeComboBox.addItem(item);
            }
            
            // Mặc định không chọn nhân viên nào
            employeeComboBox.setSelectedIndex(-1);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách nhân viên: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            
            // Nếu không load được từ database thì thêm dữ liệu mẫu
            employeeComboBox.addItem(new EmployeeItem(UUID.randomUUID(), "Nguyễn Văn A", "Phòng Nhân sự"));
            employeeComboBox.addItem(new EmployeeItem(UUID.randomUUID(), "Trần Thị B", "Phòng Kỹ thuật"));
            employeeComboBox.addItem(new EmployeeItem(UUID.randomUUID(), "Lê Văn C", "Phòng Marketing"));
        }
    }
    
    /**
     * Tạo bảng hiển thị thông tin chấm công
     */
    private void createAttendanceTable() {
        // Định nghĩa các cột
        String[] columnNames = {"Mã nhân viên", "Họ tên", "Phòng ban", "Ngày chấm công", "Giờ vào", "Giờ ra"};
        
        // Tạo model cho bảng
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
        };
        
        // Tạo bảng
        attendanceTable = new JTable(tableModel);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attendanceTable.setRowHeight(25);
        
        // Thiết lập độ rộng cho các cột
        TableColumnModel columnModel = attendanceTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);  // Mã nhân viên
        columnModel.getColumn(1).setPreferredWidth(150);  // Họ tên
        columnModel.getColumn(2).setPreferredWidth(150);  // Phòng ban
        columnModel.getColumn(3).setPreferredWidth(120);  // Ngày chấm công
        columnModel.getColumn(4).setPreferredWidth(100);  // Giờ vào
        columnModel.getColumn(5).setPreferredWidth(100);  // Giờ ra
        
        // Xử lý sự kiện khi chọn một dòng trong bảng
        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && attendanceTable.getSelectedRow() != -1) {
                int row = attendanceTable.getSelectedRow();
                displaySelectedRow(row);
            }
        });
    }
    
    /**
     * Tạo panel nhập liệu
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chấm công"));
        inputPanel.setPreferredSize(new Dimension(300, 0));
        
        // ComboBox chọn nhân viên
        JLabel employeeLabel = new JLabel("Nhân viên:");
        employeeComboBox = new JComboBox<>();
        employeeComboBox.setMaximumSize(new Dimension(280, 25));
        
        // DatePicker chọn ngày
        JLabel dateLabel = new JLabel("Ngày chấm công:");
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        datePicker = new JSpinner(dateModel);
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "dd/MM/yyyy"));
        datePicker.setMaximumSize(new Dimension(280, 25));
        
        // TextField nhập giờ vào
        JLabel timeInLabel = new JLabel("Giờ vào (hh:mm:ss):");
        timeInField = new JTextField();
        timeInField.setMaximumSize(new Dimension(280, 25));
        
        // TextField nhập giờ ra
        JLabel timeOutLabel = new JLabel("Giờ ra (hh:mm:ss):");
        timeOutField = new JTextField();
        timeOutField.setMaximumSize(new Dimension(280, 25));
        
        // Thêm các component vào panel
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(employeeLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(employeeComboBox);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(dateLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(datePicker);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(timeInLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(timeInField);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(timeOutLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(timeOutField);
        inputPanel.add(Box.createVerticalStrut(10));
        
        return inputPanel;
    }
    
    /**
     * Tạo panel chứa các nút chức năng
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        addButton = new JButton("Thêm mới");
        updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xoá");
        refreshButton = new JButton("Làm mới");
        
        // Thiết lập giao diện cho các nút
        Dimension buttonSize = new Dimension(100, 30);
        addButton.setPreferredSize(buttonSize);
        updateButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);
        
        // Thêm các sự kiện
        addButton.addActionListener(e -> addAttendance());
        updateButton.addActionListener(e -> updateAttendance());
        deleteButton.addActionListener(e -> deleteAttendance());
        refreshButton.addActionListener(e -> refreshForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        return buttonPanel;
    }
    
    /**
     * Tạo panel lọc dữ liệu theo khoảng ngày
     */
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lọc dữ liệu"));
        
        // DatePicker cho từ ngày
        JLabel fromDateLabel = new JLabel("Từ ngày:");
        SpinnerDateModel fromDateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        fromDatePicker = new JSpinner(fromDateModel);
        fromDatePicker.setEditor(new JSpinner.DateEditor(fromDatePicker, "dd/MM/yyyy"));
        Dimension datePickerSize = new Dimension(120, 25);
        fromDatePicker.setPreferredSize(datePickerSize);
        
        // DatePicker cho đến ngày
        JLabel toDateLabel = new JLabel("Đến ngày:");
        SpinnerDateModel toDateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        toDatePicker = new JSpinner(toDateModel);
        toDatePicker.setEditor(new JSpinner.DateEditor(toDatePicker, "dd/MM/yyyy"));
        toDatePicker.setPreferredSize(datePickerSize);
        
        // Các nút lọc và reset
        filterButton = new JButton("Lọc");
        resetFilterButton = new JButton("Hiển thị tất cả");
        
        // Sự kiện cho nút lọc
        filterButton.addActionListener(e -> {
            Date fromDate = (Date) fromDatePicker.getValue();
            Date toDate = (Date) toDatePicker.getValue();
            filterAttendanceLogs(fromDate, toDate);
        });
        
        // Sự kiện cho nút reset
        resetFilterButton.addActionListener(e -> {
            loadAttendanceLogs();
        });
        
        // Thêm các thành phần vào panel
        panel.add(fromDateLabel);
        panel.add(fromDatePicker);
        panel.add(toDateLabel);
        panel.add(toDatePicker);
        panel.add(filterButton);
        panel.add(resetFilterButton);
        
        return panel;
    }
    
    /**
     * Hiển thị thông tin của dòng được chọn lên form
     */
    private void displaySelectedRow(int row) {
        if (row >= 0) {
            // Lấy ID của bản ghi đang chọn từ map
            selectedAttendanceId = rowToIdMap.get(row);
            
            // Lấy thông tin từ dòng được chọn
            String employeeId = tableModel.getValueAt(row, 0).toString();
            String employeeName = tableModel.getValueAt(row, 1).toString();
            String date = tableModel.getValueAt(row, 3).toString();
            String timeIn = tableModel.getValueAt(row, 4).toString();
            String timeOut = tableModel.getValueAt(row, 5).toString();
            
            // Tìm và chọn nhân viên trong combobox
            for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
                EmployeeItem item = employeeComboBox.getItemAt(i);
                if (item.toString().contains(employeeId)) {
                    employeeComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            // Đặt giá trị cho datePicker
            try {
                datePicker.setValue(dateFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            // Đặt giá trị cho các trường thời gian
            timeInField.setText(timeIn);
            timeOutField.setText(timeOut);
        }
    }
    
    /**
     * Xử lý sự kiện thêm mới chấm công
     */
    private void addAttendance() {
        System.out.println("DEBUG: Bắt đầu thêm mới chấm công...");
        try {
            // Kiểm tra các trường nhập liệu
            EmployeeItem selectedEmployee = (EmployeeItem) employeeComboBox.getSelectedItem();
            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy ngày từ datePicker
            Date selectedDate = (Date) datePicker.getValue();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày chấm công!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy giờ vào, kiểm tra định dạng
            String timeIn = timeInField.getText().trim();
            if (timeIn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giờ vào!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Kiểm tra định dạng giờ vào
            String[] timeInParts = timeIn.split(":");
            if (timeInParts.length < 3) {
                JOptionPane.showMessageDialog(this, "Định dạng giờ vào không hợp lệ. Vui lòng nhập theo định dạng hh:mm:ss", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Lấy giờ ra (có thể trống)
            String timeOut = timeOutField.getText().trim();
            
            // Tạo đối tượng Date cho checkIn
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeInParts[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeInParts[1]));
            calendar.set(Calendar.SECOND, Integer.parseInt(timeInParts[2]));
            Date checkInTime = calendar.getTime();
            
            // Xử lý giờ ra (nếu có)
            Date checkOutTime = null;
            if (!timeOut.trim().isEmpty()) {
                String[] timeOutParts = timeOut.split(":");
                if (timeOutParts.length < 3) {
                    JOptionPane.showMessageDialog(this, "Định dạng giờ ra không hợp lệ. Vui lòng nhập theo định dạng hh:mm:ss", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeOutParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeOutParts[1]));
                calendar.set(Calendar.SECOND, Integer.parseInt(timeOutParts[2]));
                checkOutTime = calendar.getTime();
            }
            
            System.out.println("DEBUG: Bắt đầu lưu vào cơ sở dữ liệu...");
            System.out.println("DEBUG: Employee ID: " + selectedEmployee.getId());
            System.out.println("DEBUG: Check In: " + checkInTime);
            System.out.println("DEBUG: Check Out: " + (checkOutTime != null ? checkOutTime : "null"));
            
            // Lưu vào cơ sở dữ liệu
            boolean success = attendanceLogDAO.addAttendanceLog(selectedEmployee.getId(), checkInTime, checkOutTime);
            System.out.println("DEBUG: Kết quả thêm chấm công: " + (success ? "Thành công" : "Thất bại"));
            
            if (success) {
                // Làm mới form
                clearForm();
                
                System.out.println("DEBUG: Bắt đầu tải lại dữ liệu...");
                // Tải lại dữ liệu
                loadAttendanceLogs();
                
                JOptionPane.showMessageDialog(this, "Thêm chấm công thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm chấm công. Vui lòng thử lại sau!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Làm mới các trường nhập liệu
     */
    private void clearForm() {
        employeeComboBox.setSelectedIndex(-1);
        datePicker.setValue(new Date());
        timeInField.setText("");
        timeOutField.setText("");
        attendanceTable.clearSelection();
        selectedAttendanceId = null;
    }
    
    /**
     * Xử lý sự kiện cập nhật chấm công
     */
    private void updateAttendance() {
        if (selectedAttendanceId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Kiểm tra các trường nhập liệu
            if (employeeComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy ngày từ datePicker
            Date selectedDate = (Date) datePicker.getValue();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày chấm công!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy giờ vào, kiểm tra định dạng
            String timeIn = timeInField.getText().trim();
            if (timeIn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giờ vào!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Kiểm tra định dạng giờ vào
            String[] timeInParts = timeIn.split(":");
            if (timeInParts.length < 3) {
                JOptionPane.showMessageDialog(this, "Định dạng giờ vào không hợp lệ. Vui lòng nhập theo định dạng hh:mm:ss", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Lấy giờ ra (có thể trống)
            String timeOut = timeOutField.getText().trim();
            
            // Tạo đối tượng Date cho checkIn
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeInParts[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeInParts[1]));
            calendar.set(Calendar.SECOND, Integer.parseInt(timeInParts[2]));
            Date checkInTime = calendar.getTime();
            
            // Xử lý giờ ra (nếu có)
            Date checkOutTime = null;
            if (!timeOut.trim().isEmpty()) {
                String[] timeOutParts = timeOut.split(":");
                if (timeOutParts.length < 3) {
                    JOptionPane.showMessageDialog(this, "Định dạng giờ ra không hợp lệ. Vui lòng nhập theo định dạng hh:mm:ss", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeOutParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeOutParts[1]));
                calendar.set(Calendar.SECOND, Integer.parseInt(timeOutParts[2]));
                checkOutTime = calendar.getTime();
            }
            
            // Lưu vào cơ sở dữ liệu
            boolean success = attendanceLogDAO.updateAttendanceLog(selectedAttendanceId, checkInTime, checkOutTime);
            
            if (success) {
                // Làm mới form
                clearForm();
                
                // Tải lại dữ liệu
                loadAttendanceLogs();
                
                JOptionPane.showMessageDialog(this, "Cập nhật chấm công thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật chấm công. Vui lòng thử lại sau!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xử lý sự kiện xoá chấm công
     */
    private void deleteAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xoá!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Lấy ID của bản ghi đã chọn
        UUID attendanceId = rowToIdMap.get(selectedRow);
        if (attendanceId == null) {
            JOptionPane.showMessageDialog(this, "Không thể xác định ID của bản ghi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xoá dữ liệu chấm công này không?", 
                "Xác nhận xoá", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Xóa dữ liệu từ database
                boolean success = attendanceLogDAO.deleteAttendanceLog(attendanceId);
                
                if (success) {
                    // Làm mới form
                    clearForm();
                    
                    // Tải lại dữ liệu
                    loadAttendanceLogs();
                    
                    JOptionPane.showMessageDialog(this, "Xoá chấm công thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xoá chấm công. Vui lòng thử lại sau!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xoá chấm công: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Làm mới form nhập liệu và tải lại dữ liệu
     */
    private void refreshForm() {
        // Làm mới các trường nhập liệu
        clearForm();
        
        // Tải lại dữ liệu từ database
        loadAttendanceLogs();
    }
    
    /**
     * Load dữ liệu chấm công từ cơ sở dữ liệu và hiển thị lên bảng
     */
    private void loadAttendanceLogs() {
        System.out.println("DEBUG: Bắt đầu phương thức loadAttendanceLogs()...");
        try {
            // Kiểm tra thông tin phòng ban và nhân viên
            attendanceLogDAO.debugDepartmentInfo();
            
            // Xóa dữ liệu cũ trong bảng
            tableModel.setRowCount(0);
            System.out.println("DEBUG: Đã xóa dữ liệu cũ trong bảng");
            
            // Khởi tạo map mới để lưu trữ ID tương ứng với mỗi dòng
            rowToIdMap = new HashMap<>();
            
            // Lấy dữ liệu từ database
            System.out.println("DEBUG: Gọi attendanceLogDAO.getAllAttendanceLogs()...");
            java.util.List<Map<String, Object>> attendanceLogs = attendanceLogDAO.getAllAttendanceLogs();
            System.out.println("DEBUG: Đã nhận được " + attendanceLogs.size() + " bản ghi từ database");
            
            // Thêm dữ liệu vào bảng
            int rowIndex = 0;
            for (Map<String, Object> log : attendanceLogs) {
                // Lấy các thông tin từ map
                UUID id = (UUID) log.get("id");
                UUID employeeId = (UUID) log.get("employeeId");
                String employeeName = (String) log.get("employeeName");
                String departmentName = (String) log.get("departmentName");
                Timestamp checkIn = (Timestamp) log.get("checkIn");
                Timestamp checkOut = (Timestamp) log.get("checkOut");
                
                System.out.println("DEBUG: Dữ liệu chi tiết của bản ghi #" + rowIndex + ":");
                System.out.println("  - ID: " + id);
                System.out.println("  - EmployeeID: " + employeeId);
                System.out.println("  - EmployeeName: " + employeeName);
                System.out.println("  - DepartmentName (original): '" + departmentName + "'");
                System.out.println("  - DepartmentName (length): " + (departmentName != null ? departmentName.length() : 0));
                System.out.println("  - CheckIn: " + checkIn);
                
                // Lưu ID vào map với key là chỉ số dòng
                rowToIdMap.put(rowIndex, id);
                
                // Format dữ liệu theo yêu cầu
                String employeeIdStr = employeeId.toString().substring(0, 8);
                
                // Tách ngày và giờ từ checkIn
                String checkInDate = "";
                String checkInTime = "";
                if (checkIn != null) {
                    checkInDate = dateFormat.format(new Date(checkIn.getTime()));
                    checkInTime = timeFormat.format(new Date(checkIn.getTime()));
                }
                
                // Format giờ checkOut
                String checkOutTime = "";
                if (checkOut != null) {
                    checkOutTime = timeFormat.format(new Date(checkOut.getTime()));
                }
                
                System.out.println("DEBUG: Thêm dòng #" + rowIndex + ": " + employeeName + ", " + departmentName + ", " + checkInDate + ", " + checkInTime);
                
                // Thêm một dòng mới vào bảng
                Object[] rowData = {
                    employeeIdStr,
                    employeeName,
                    departmentName,
                    checkInDate,
                    checkInTime,
                    checkOutTime
                };
                
                // Ghi log dữ liệu thực tế được thêm vào tableModel
                System.out.print("DEBUG: rowData: [");
                for (Object cell : rowData) {
                    System.out.print("'" + cell + "', ");
                }
                System.out.println("]");
                
                tableModel.addRow(rowData);
                rowIndex++;
            }
            
            System.out.println("DEBUG: Đã thêm " + rowIndex + " dòng vào bảng");
            System.out.println("DEBUG: Kiểm tra dữ liệu trong tableModel:");
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                System.out.print("DEBUG: Row " + row + ": [");
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    Object value = tableModel.getValueAt(row, col);
                    System.out.print("'" + value + "', ");
                }
                System.out.println("]");
            }
            
            // Thông báo bảng đã được cập nhật
            tableModel.fireTableDataChanged();
            
            // Làm mới lại giao diện
            this.revalidate();
            this.repaint();
            
            System.out.println("DEBUG: Đã gọi revalidate() và repaint()");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu chấm công: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Lọc danh sách chấm công theo khoảng ngày
     * 
     * @param fromDate Từ ngày
     * @param toDate Đến ngày
     */
    private void filterAttendanceLogs(Date fromDate, Date toDate) {
        try {
            // Kiểm tra nếu fromDate lớn hơn toDate
            if (fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(this, 
                        "Ngày bắt đầu không thể sau ngày kết thúc!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Đặt thời gian của fromDate về 00:00:00
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            fromDate = calendar.getTime();
            
            // Đặt thời gian của toDate về 23:59:59
            calendar.setTime(toDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            toDate = calendar.getTime();
            
            // Xóa dữ liệu cũ trong bảng
            tableModel.setRowCount(0);
            
            // Map để lưu trữ ID tương ứng với mỗi dòng
            rowToIdMap = new HashMap<>();
            
            // Lấy dữ liệu từ database theo khoảng ngày
            java.util.List<Map<String, Object>> attendanceLogs = 
                    attendanceLogDAO.getAttendanceLogsByDateRange(fromDate, toDate);
            
            // Thêm dữ liệu vào bảng
            int rowIndex = 0;
            for (Map<String, Object> log : attendanceLogs) {
                // Lấy các thông tin từ map
                UUID id = (UUID) log.get("id");
                UUID employeeId = (UUID) log.get("employeeId");
                String employeeName = (String) log.get("employeeName");
                String departmentName = (String) log.get("departmentName");
                Timestamp checkIn = (Timestamp) log.get("checkIn");
                Timestamp checkOut = (Timestamp) log.get("checkOut");
                
                // Lưu ID vào map với key là chỉ số dòng
                rowToIdMap.put(rowIndex, id);
                
                // Format dữ liệu theo yêu cầu
                String employeeIdStr = employeeId.toString().substring(0, 8);
                
                // Tách ngày và giờ từ checkIn
                String checkInDate = "";
                String checkInTime = "";
                if (checkIn != null) {
                    checkInDate = dateFormat.format(new Date(checkIn.getTime()));
                    checkInTime = timeFormat.format(new Date(checkIn.getTime()));
                }
                
                // Format giờ checkOut
                String checkOutTime = "";
                if (checkOut != null) {
                    checkOutTime = timeFormat.format(new Date(checkOut.getTime()));
                }
                
                // Thêm một dòng mới vào bảng
                Object[] rowData = {
                    employeeIdStr,
                    employeeName,
                    departmentName,
                    checkInDate,
                    checkInTime,
                    checkOutTime
                };
                
                tableModel.addRow(rowData);
                rowIndex++;
            }
            
            // Thông báo bảng đã được cập nhật
            tableModel.fireTableDataChanged();
            
            // Hiển thị thông báo kết quả
            JOptionPane.showMessageDialog(this,
                    "Đã tìm thấy " + attendanceLogs.size() + " bản ghi từ " + 
                    dateFormat.format(fromDate) + " đến " + dateFormat.format(toDate),
                    "Kết quả lọc",
                    JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lọc dữ liệu chấm công: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 