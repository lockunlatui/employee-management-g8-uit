package dao;

import model.Employee;
import database.ConnectionManager;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, department_id, employee_name, status, join_date, created_at, updated_at FROM Employees";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(convertBytesToUUID(rs.getBytes("id")));
                employee.setDepartmentId(convertBytesToUUID(rs.getBytes("department_id")));
                employee.setEmployeeName(rs.getString("employee_name"));
                employee.setStatus(rs.getString("status"));
                employee.setJoinDate(rs.getDate("join_date"));
                employee.setCreatedAt(rs.getDate("created_at"));
                employee.setUpdatedAt(rs.getDate("updated_at"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllEmployees: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return employees;
    }

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

    public Employee getEmployeeByName(String employeeName) throws SQLException {
        String sql = "SELECT id, department_id, employee_name, status, join_date, created_at, updated_at FROM Employees WHERE employee_name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(convertBytesToUUID(rs.getBytes("id")));
                employee.setDepartmentId(convertBytesToUUID(rs.getBytes("department_id")));
                employee.setEmployeeName(rs.getString("employee_name"));
                employee.setStatus(rs.getString("status"));
                employee.setJoinDate(rs.getDate("join_date"));
                employee.setCreatedAt(rs.getDate("created_at"));
                employee.setUpdatedAt(rs.getDate("updated_at"));
                return employee;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error in getEmployeeByName: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    public int countByDepartment(UUID departmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employees WHERE department_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1, uuidToBytes(departmentId));  // dùng setBytes vì cột là BINARY(16)
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            System.err.println("Error in countByDepartment: " + e.getMessage());
            throw e;

        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    private byte[] uuidToBytes(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (15 - i));
        }

        return buffer;
    }



}
