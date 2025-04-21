package dao;

import database.ConnectionManager;
import dto.AttendanceLogDTO;

import java.nio.ByteBuffer;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Lớp DAO để tương tác với bảng AttendanceLogs và ánh xạ dữ liệu vào đối tượng AttendanceLogDTO
 */
public class AttendanceLogDTO_DAO {
    
    /**
     * Lấy tất cả bản ghi chấm công từ database
     * 
     * @return Danh sách các đối tượng AttendanceLogDTO
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public List<AttendanceLogDTO> getAllLogs() throws SQLException {
        List<AttendanceLogDTO> logs = new ArrayList<>();
        
        String sql = "SELECT a.id, e.id as employee_id, e.employee_name, d.department_name, "
                   + "a.check_in, a.check_out "
                   + "FROM AttendanceLogs a "
                   + "JOIN Employees e ON a.employee_id = e.id "
                   + "JOIN Departments d ON e.department_id = d.id "
                   + "ORDER BY a.check_in DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AttendanceLogDTO log = mapResultSetToDTO(rs);
                logs.add(log);
            }
            
            return logs;
        } catch (SQLException e) {
            System.err.println("Error in getAllLogs: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /**
     * Thêm một bản ghi chấm công mới vào database
     * 
     * @param log đối tượng AttendanceLogDTO cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public boolean insertLog(AttendanceLogDTO log) throws SQLException {
        String sql = "INSERT INTO AttendanceLogs (id, employee_id, check_in, check_out, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, CURDATE(), CURDATE())";

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Tạo UUID mới nếu chưa có
            if (log.getId() == null) {
                log.setId(UUID.randomUUID());
            }
            
            // Thiết lập tham số cho PreparedStatement
            pstmt.setBytes(1, convertUUIDtoBytes(log.getId()));
            pstmt.setBytes(2, convertUUIDtoBytes(log.getEmployeeId()));
            
            // Chuyển đổi LocalDate và LocalTime thành Timestamp cho MySQL
            LocalDate checkDate = log.getCheckDate();
            LocalTime checkInTime = log.getCheckIn();
            LocalTime checkOutTime = log.getCheckOut();
            
            Timestamp checkInTimestamp = null;
            Timestamp checkOutTimestamp = null;
            
            if (checkDate != null && checkInTime != null) {
                checkInTimestamp = Timestamp.valueOf(checkDate.atTime(checkInTime));
            }
            
            if (checkDate != null && checkOutTime != null) {
                checkOutTimestamp = Timestamp.valueOf(checkDate.atTime(checkOutTime));
            }
            
            pstmt.setTimestamp(3, checkInTimestamp);
            pstmt.setTimestamp(4, checkOutTimestamp);
            
            // Thực thi câu lệnh SQL
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error in insertLog: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Cập nhật một bản ghi chấm công trong database
     * 
     * @param log đối tượng AttendanceLogDTO cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi khi thực thi SQL
     */
    public boolean updateLog(AttendanceLogDTO log) throws SQLException {
        String sql = "UPDATE AttendanceLogs SET employee_id = ?, check_in = ?, check_out = ?, updated_at = CURDATE() "
                   + "WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Chuyển đổi LocalDate và LocalTime thành Timestamp cho MySQL
            LocalDate checkDate = log.getCheckDate();
            LocalTime checkInTime = log.getCheckIn();
            LocalTime checkOutTime = log.getCheckOut();
            
            Timestamp checkInTimestamp = null;
            Timestamp checkOutTimestamp = null;
            
            if (checkDate != null && checkInTime != null) {
                checkInTimestamp = Timestamp.valueOf(checkDate.atTime(checkInTime));
            }
            
            if (checkDate != null && checkOutTime != null) {
                checkOutTimestamp = Timestamp.valueOf(checkDate.atTime(checkOutTime));
            }
            
            // Thiết lập tham số cho PreparedStatement
            pstmt.setBytes(1, convertUUIDtoBytes(log.getEmployeeId()));
            pstmt.setTimestamp(2, checkInTimestamp);
            pstmt.setTimestamp(3, checkOutTimestamp);
            pstmt.setBytes(4, convertUUIDtoBytes(log.getId()));
            
            // Thực thi câu lệnh SQL
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error in updateLog: " + e.getMessage());
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
    public boolean deleteLog(UUID id) throws SQLException {
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
            System.err.println("Error in deleteLog: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Ánh xạ kết quả từ ResultSet vào đối tượng AttendanceLogDTO
     */
    private AttendanceLogDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        AttendanceLogDTO log = new AttendanceLogDTO();
        
        // Thiết lập các thuộc tính từ dữ liệu ResultSet
        log.setId(convertBytesToUUID(rs.getBytes("id")));
        log.setEmployeeId(convertBytesToUUID(rs.getBytes("employee_id")));
        log.setEmployeeName(rs.getString("employee_name"));
        log.setDepartmentName(rs.getString("department_name"));
        
        // Chuyển đổi Timestamp thành LocalDate và LocalTime
        Timestamp checkIn = rs.getTimestamp("check_in");
        Timestamp checkOut = rs.getTimestamp("check_out");
        
        if (checkIn != null) {
            log.setCheckDate(checkIn.toLocalDateTime().toLocalDate());
            log.setCheckIn(checkIn.toLocalDateTime().toLocalTime());
        }
        
        if (checkOut != null) {
            log.setCheckOut(checkOut.toLocalDateTime().toLocalTime());
        }
        
        return log;
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
} 