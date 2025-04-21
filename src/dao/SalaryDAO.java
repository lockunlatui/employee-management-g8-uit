package dao;

import database.ConnectionManager;
import model.Employee;
import model.Salary;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SalaryDAO {

    // Method to get all salaries
    public List<Salary> getAllSalaries() throws SQLException {
        List<Salary> salaries = new ArrayList<>();

        String sql = "SELECT s.id AS salary_id, s.employee_id, s.base_salary, s.deductions, s.status, s.created_at, s.updated_at, "
                +
                "e.employee_name " +
                "FROM Salaries s " +
                "INNER JOIN Employees e ON s.employee_id = e.id " +
                "ORDER BY s.created_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Salary salary = new Salary();
                salary.setId(convertBytesToUUID(rs.getBytes("salary_id")));
                salary.setEmployeeId(convertBytesToUUID(rs.getBytes("employee_id")));
                salary.setBaseSalary(rs.getBigDecimal("base_salary"));
                salary.setDeductions(rs.getBigDecimal("deductions"));
                salary.setStatus(rs.getString("status"));
                salary.setCreatedAt(rs.getDate("created_at"));
                salary.setUpdatedAt(rs.getDate("updated_at"));

                Employee employee = new Employee();
                employee.setEmployeeName(rs.getString("employee_name"));
                salary.setEmployee(employee);

                salaries.add(salary);
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllSalaries: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return salaries;
    }

    // Method to get a single salary by ID-=44444444400000000000kjjjj22222222222k,
    public Salary getOne(UUID salaryId) throws SQLException {
        Salary salary = null;
        String sql = "SELECT s.id AS salary_id, s.employee_id, s.base_salary, s.deductions, s.status, s.created_at, s.updated_at, "
                + "e.employee_name "
                + "FROM Salaries s "
                + "INNER JOIN Employees e ON s.employee_id = e.id "
                + "WHERE s.id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1, convertUUIDToBytes(salaryId)); // Set UUID parameter
            rs = pstmt.executeQuery();

            if (rs.next()) {
                salary = new Salary();
                salary.setId(convertBytesToUUID(rs.getBytes("salary_id")));
                salary.setEmployeeId(convertBytesToUUID(rs.getBytes("employee_id")));
                salary.setBaseSalary(rs.getBigDecimal("base_salary"));
                salary.setDeductions(rs.getBigDecimal("deductions"));
                salary.setStatus(rs.getString("status"));
                salary.setCreatedAt(rs.getDate("created_at"));
                salary.setUpdatedAt(rs.getDate("updated_at"));

                Employee employee = new Employee();
                employee.setEmployeeName(rs.getString("employee_name"));
                salary.setEmployee(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error in getOne: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return salary;
    }

    // Method to insert a new salary
    public void insertSalary(Salary salary) throws SQLException {
        String sql = "INSERT INTO Salaries (id, employee_id, base_salary, deductions, status, created_at, updated_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setBytes(1, convertUUIDToBytes(salary.getId()));
            pstmt.setBytes(2, convertUUIDToBytes(salary.getEmployeeId()));
            pstmt.setBigDecimal(3, salary.getBaseSalary());
            pstmt.setBigDecimal(4, salary.getDeductions());
            pstmt.setString(5, salary.getStatus());
            pstmt.setTimestamp(6, new Timestamp(salary.getCreatedAt().getTime()));
            pstmt.setTimestamp(7, new Timestamp(salary.getUpdatedAt().getTime()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in insertSalary: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    // Method to delete a salary by ID
    public void deleteSalary(UUID salaryId) throws SQLException {
        String sql = "DELETE FROM Salaries WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1, convertUUIDToBytes(salaryId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in deleteSalary: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    // Method to update an existing salary
    public void updateSalary(Salary salary) throws SQLException {
        String sql = "UPDATE Salaries SET base_salary = ?, deductions = ?, status = ?, updated_at = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setBigDecimal(1, salary.getBaseSalary());
            pstmt.setBigDecimal(2, salary.getDeductions());
            pstmt.setString(3, salary.getStatus());
            pstmt.setTimestamp(4, new Timestamp(salary.getUpdatedAt().getTime()));
            pstmt.setBytes(5, convertUUIDToBytes(salary.getId()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in updateSalary: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null); // Đảm bảo đóng tài nguyên
        }
    }

    private UUID convertBytesToUUID(byte[] bytes) throws SQLException {
        if (bytes == null)
            return null;
        if (bytes.length != 16)
            throw new SQLException("Invalid UUID byte array length: " + bytes.length);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    private byte[] convertUUIDToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            System.err.println("Error closing ResultSet: " + e.getMessage());
        }
        try {
            if (pstmt != null)
                pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error closing PreparedStatement: " + e.getMessage());
        }
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing Connection: " + e.getMessage());
        }
    }
}
