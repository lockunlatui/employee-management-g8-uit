package dao;

import model.AttendanceLog;
import database.ConnectionManager;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttendanceLogDAO {

    /**
     * Thêm một bản ghi chấm công mới vào database
     * 
     * @param employeeId UUID của nhân viên
     * @param checkIn    thời gian check-in
     * @param checkOut   thời gian check-out (có thể null)
     * @return true nếu thêm thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public boolean addAttendanceLog(UUID employeeId, Date checkIn, Date checkOut) throws SQLException {
        System.out.println("DEBUG: Bắt đầu thêm bản ghi chấm công mới...");
        String sql = "INSERT INTO AttendanceLogs (id, employee_id, check_in, check_out, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, CURDATE(), CURDATE())";

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Tạo UUID mới cho bản ghi
            UUID id = UUID.randomUUID();
            System.out.println("DEBUG: Tạo UUID mới: " + id);
            
            // Thiết lập tham số cho PreparedStatement
            pstmt.setBytes(1, convertUUIDtoBytes(id));
            pstmt.setBytes(2, convertUUIDtoBytes(employeeId));
            
            // Chuyển đổi Date thành Timestamp cho DATETIME của MySQL
            pstmt.setTimestamp(3, checkIn != null ? new Timestamp(checkIn.getTime()) : null);
            pstmt.setTimestamp(4, checkOut != null ? new Timestamp(checkOut.getTime()) : null);
            
            // In ra thông tin debug
            System.out.println("DEBUG: Thông tin chấm công:");
            System.out.println("- ID: " + id);
            System.out.println("- Nhân viên ID: " + employeeId);
            System.out.println("- Check-in: " + (checkIn != null ? new Timestamp(checkIn.getTime()) : "null"));
            System.out.println("- Check-out: " + (checkOut != null ? new Timestamp(checkOut.getTime()) : "null"));
            
            // Thực thi câu lệnh SQL
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("DEBUG: Số dòng được thêm: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error in addAttendanceLog: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Lấy tất cả bản ghi chấm công từ database
     * 
     * @return Danh sách các bản ghi chấm công dạng Map
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public List<Map<String, Object>> getAllAttendanceLogs() throws SQLException {
        System.out.println("DEBUG: Bắt đầu lấy dữ liệu chấm công...");
        List<Map<String, Object>> attendanceLogs = new ArrayList<>();
        
        // Sửa câu truy vấn SQL để sử dụng LEFT JOIN thay vì INNER JOIN để không bỏ qua các bản ghi không có department_id
        String sql = "SELECT a.id, e.id as employee_id, e.employee_name, d.department_name, "
                   + "a.check_in, a.check_out "
                   + "FROM AttendanceLogs a "
                   + "LEFT JOIN Employees e ON a.employee_id = e.id "
                   + "LEFT JOIN Departments d ON e.department_id = d.id "
                   + "ORDER BY a.check_in DESC";
        
        System.out.println("DEBUG: SQL Query: " + sql);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            System.out.println("DEBUG: Truy vấn đã thực thi thành công.");
            
            int count = 0;
            while (rs.next()) {
                count++;
                Map<String, Object> record = new HashMap<>();
                
                try {
                    // Lấy dữ liệu từ ResultSet
                    byte[] idBytes = rs.getBytes("id");
                    byte[] employeeIdBytes = rs.getBytes("employee_id");
                    
                    UUID id = convertBytesToUUID(idBytes);
                    UUID employeeId = convertBytesToUUID(employeeIdBytes);
                    String employeeName = rs.getString("employee_name");
                    String departmentName = rs.getString("department_name");
                    Timestamp checkIn = rs.getTimestamp("check_in");
                    Timestamp checkOut = rs.getTimestamp("check_out");
                    
                    System.out.println("DEBUG: Bản ghi #" + count + ": ID=" + id + ", EmployeeID=" + employeeId + 
                            ", Name=" + employeeName + ", Dept=" + departmentName + 
                            ", CheckIn=" + checkIn);
                    
                    // Lưu vào Map
                    record.put("id", id);
                    record.put("employeeId", employeeId);
                    record.put("employeeName", employeeName != null ? employeeName : "N/A");
                    record.put("departmentName", departmentName != null ? departmentName : "N/A");
                    record.put("checkIn", checkIn);
                    record.put("checkOut", checkOut);
                    
                    attendanceLogs.add(record);
                } catch (Exception e) {
                    System.err.println("ERROR processing row #" + count + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("DEBUG: Đã lấy được " + count + " bản ghi chấm công.");
            return attendanceLogs;
        } catch (SQLException e) {
            System.err.println("Error in getAllAttendanceLogs: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /**
     * Chuyển đổi UUID thành mảng byte để lưu vào cơ sở dữ liệu
     */
    private byte[] convertUUIDtoBytes(UUID uuid) throws SQLException {
        if (uuid == null) {
            throw new SQLException("UUID is null, cannot convert to bytes");
        }
        byte[] bytes = new byte[16];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bytes;
    }
    
    /**
     * Chuyển đổi mảng byte từ cơ sở dữ liệu thành UUID
     */
    private UUID convertBytesToUUID(byte[] bytes) throws SQLException {
        if (bytes == null) {
            return null;
        }
        if (bytes.length != 16) {
            throw new SQLException("Invalid byte array length for UUID conversion: " + bytes.length);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
    
    /**
     * Đóng tài nguyên sau khi sử dụng
     */
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Cập nhật một bản ghi chấm công trong database
     * 
     * @param id ID của bản ghi cần cập nhật
     * @param checkIn thời gian check-in mới
     * @param checkOut thời gian check-out mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public boolean updateAttendanceLog(UUID id, Date checkIn, Date checkOut) throws SQLException {
        String sql = "UPDATE AttendanceLogs SET check_in = ?, check_out = ?, updated_at = CURDATE() WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Thiết lập tham số cho PreparedStatement
            pstmt.setTimestamp(1, checkIn != null ? new Timestamp(checkIn.getTime()) : null);
            pstmt.setTimestamp(2, checkOut != null ? new Timestamp(checkOut.getTime()) : null);
            pstmt.setBytes(3, convertUUIDtoBytes(id));
            
            // Thực thi câu lệnh SQL
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error in updateAttendanceLog: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * Xóa một bản ghi chấm công từ database
     * 
     * @param id ID của bản ghi cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public boolean deleteAttendanceLog(UUID id) throws SQLException {
        String sql = "DELETE FROM AttendanceLogs WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Thiết lập tham số cho PreparedStatement
            pstmt.setBytes(1, convertUUIDtoBytes(id));
            
            // Thực thi câu lệnh SQL
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error in deleteAttendanceLog: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * Lấy các bản ghi chấm công theo khoảng thời gian
     * 
     * @param fromDate Từ ngày (bao gồm)
     * @param toDate Đến ngày (bao gồm)
     * @return Danh sách các bản ghi chấm công dạng Map
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public List<Map<String, Object>> getAttendanceLogsByDateRange(Date fromDate, Date toDate) throws SQLException {
        System.out.println("DEBUG: Bắt đầu lấy dữ liệu chấm công theo khoảng thời gian...");
        List<Map<String, Object>> attendanceLogs = new ArrayList<>();
        
        String sql = "SELECT a.id, e.id as employee_id, e.employee_name, d.department_name, "
                   + "a.check_in, a.check_out "
                   + "FROM AttendanceLogs a "
                   + "LEFT JOIN Employees e ON a.employee_id = e.id "
                   + "LEFT JOIN Departments d ON e.department_id = d.id "
                   + "WHERE DATE(a.check_in) BETWEEN DATE(?) AND DATE(?) "
                   + "ORDER BY a.check_in DESC";
        
        System.out.println("DEBUG: SQL Query (Date Range): " + sql);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Chuyển đổi Date thành Timestamp cho MySQL
            pstmt.setTimestamp(1, new Timestamp(fromDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(toDate.getTime()));
            
            rs = pstmt.executeQuery();
            System.out.println("DEBUG: Truy vấn khoảng thời gian đã thực thi thành công.");
            
            int count = 0;
            while (rs.next()) {
                count++;
                Map<String, Object> record = new HashMap<>();
                
                try {
                    // Lấy dữ liệu từ ResultSet
                    byte[] idBytes = rs.getBytes("id");
                    byte[] employeeIdBytes = rs.getBytes("employee_id");
                    
                    UUID id = convertBytesToUUID(idBytes);
                    UUID employeeId = convertBytesToUUID(employeeIdBytes);
                    String employeeName = rs.getString("employee_name");
                    String departmentName = rs.getString("department_name");
                    Timestamp checkIn = rs.getTimestamp("check_in");
                    Timestamp checkOut = rs.getTimestamp("check_out");
                    
                    System.out.println("DEBUG: Bản ghi khoảng thời gian #" + count + ": ID=" + id + ", EmployeeID=" + employeeId + 
                            ", Name=" + employeeName + ", Dept=" + departmentName + 
                            ", CheckIn=" + checkIn);
                    
                    // Lưu vào Map
                    record.put("id", id);
                    record.put("employeeId", employeeId);
                    record.put("employeeName", employeeName != null ? employeeName : "N/A");
                    record.put("departmentName", departmentName != null ? departmentName : "N/A");
                    record.put("checkIn", checkIn);
                    record.put("checkOut", checkOut);
                    
                    attendanceLogs.add(record);
                } catch (Exception e) {
                    System.err.println("ERROR processing date range row #" + count + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("DEBUG: Đã lấy được " + count + " bản ghi chấm công trong khoảng thời gian.");
            return attendanceLogs;
        } catch (SQLException e) {
            System.err.println("Error in getAttendanceLogsByDateRange: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * Kiểm tra thông tin phòng ban
     * 
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public void debugDepartmentInfo() throws SQLException {
        System.out.println("DEBUG: Bắt đầu kiểm tra thông tin phòng ban...");
        
        String sql = "SELECT id, department_name, created_at FROM Departments";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("DEBUG: Truy vấn phòng ban đã thực thi thành công.");
            
            int count = 0;
            while (rs.next()) {
                count++;
                byte[] idBytes = rs.getBytes("id");
                UUID id = convertBytesToUUID(idBytes);
                String departmentName = rs.getString("department_name");
                Date createdAt = rs.getDate("created_at");
                
                System.out.println("DEBUG: Phòng ban #" + count + ": ID=" + id + 
                        ", Name=" + departmentName + 
                        ", Created=" + createdAt);
            }
            
            System.out.println("DEBUG: Đã lấy được " + count + " phòng ban.");
            
            // Kiểm tra mối quan hệ giữa nhân viên và phòng ban
            System.out.println("DEBUG: Kiểm tra thông tin nhân viên và phòng ban...");
            String employeeSql = "SELECT e.id, e.employee_name, e.department_id, d.department_name " +
                                "FROM Employees e " +
                                "LEFT JOIN Departments d ON e.department_id = d.id";
            
            PreparedStatement empStmt = conn.prepareStatement(employeeSql);
            ResultSet empRs = empStmt.executeQuery();
            
            count = 0;
            while (empRs.next()) {
                count++;
                byte[] idBytes = empRs.getBytes("id");
                UUID id = convertBytesToUUID(idBytes);
                String employeeName = empRs.getString("employee_name");
                
                byte[] deptIdBytes = empRs.getBytes("department_id");
                UUID departmentId = null;
                try {
                    departmentId = convertBytesToUUID(deptIdBytes);
                } catch (Exception e) {
                    System.err.println("ERROR: Không thể chuyển đổi department_id cho nhân viên " + employeeName);
                }
                
                String departmentName = empRs.getString("department_name");
                
                System.out.println("DEBUG: Nhân viên #" + count + ": ID=" + id + 
                        ", Name=" + employeeName + 
                        ", DeptID=" + departmentId + 
                        ", DeptName=" + departmentName);
            }
            
            System.out.println("DEBUG: Đã kiểm tra " + count + " nhân viên.");
            
            closeResources(null, empStmt, empRs);
            
        } catch (SQLException e) {
            System.err.println("Error in debugDepartmentInfo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
} 