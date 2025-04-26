package dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Lớp AttendanceLogDTO đại diện cho bản ghi chấm công
 * Sử dụng để hiển thị và tương tác trên giao diện
 */
public class AttendanceLogDTO {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String departmentName;
    private LocalDate checkDate;
    private LocalTime checkIn;
    private LocalTime checkOut;

    /**
     * Constructor mặc định
     */
    public AttendanceLogDTO() {
        this.id = UUID.randomUUID();
    }

    /**
     * Constructor đầy đủ các tham số
     * 
     * @param id ID của bản ghi chấm công
     * @param employeeId ID của nhân viên
     * @param employeeName Tên nhân viên
     * @param departmentName Tên phòng ban
     * @param checkDate Ngày chấm công
     * @param checkIn Giờ vào
     * @param checkOut Giờ ra
     */
    public AttendanceLogDTO(UUID id, UUID employeeId, String employeeName, String departmentName,
                           LocalDate checkDate, LocalTime checkIn, LocalTime checkOut) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.departmentName = departmentName;
        this.checkDate = checkDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    // Getters và Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }

    /**
     * Ghi đè phương thức toString() để debug
     */
    @Override
    public String toString() {
        return "AttendanceLogDTO{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", checkDate=" + checkDate +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                '}';
    }
} 