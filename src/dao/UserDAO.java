package dao;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.ConnectionManager;
import model.Employee;
import model.User;

public class UserDAO {

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.role, u.employee_id, e.id as employee_id, e.employee_name "
                + "FROM Users u LEFT JOIN Employees e ON u.employee_id = e.id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(convertBytesToUUID1(rs.getBytes("id")));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));

                byte[] employeeIdBytes = rs.getBytes("employee_id");
                if (employeeIdBytes != null) {
                    Employee employee = new Employee();
                    employee.setId(convertBytesToUUID1(employeeIdBytes));
                    employee.setEmployeeName(rs.getString("employee_name"));
                    user.setEmployee(employee);
                }

                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return users;
    }

    private UUID convertBytesToUUID1(byte[] bytes) throws SQLException {
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

    public void deleteUser(UUID id) throws SQLException {
        String sql = "DELETE FROM Users WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1, convertUUIDtoBytes(id));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in deleteUser: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public User getUserById(UUID id) throws SQLException {
        User user = null;
        String sql = "SELECT u.id, u.username, u.role, u.employee_id, e.id as employee_id, e.employee_name "
                + "FROM Users u LEFT JOIN Employees e ON u.employee_id = e.id WHERE u.id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1, convertUUIDtoBytes(id));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(convertBytesToUUID1(rs.getBytes("id")));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));

                byte[] employeeIdBytes = rs.getBytes("employee_id");
                if (employeeIdBytes != null) {
                    Employee employee = new Employee();
                    employee.setId(convertBytesToUUID1(employeeIdBytes));
                    employee.setEmployeeName(rs.getString("employee_name"));
                    user.setEmployee(employee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getUserById: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return user;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET username = ?, role = ?, employee_id = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getRole());
            if (user.getEmployeeId() != null) {
                pstmt.setBytes(3, convertUUIDtoBytes(user.getEmployeeId()));
            } else {
                pstmt.setNull(3, Types.BINARY);
            }
            pstmt.setBytes(4, convertUUIDtoBytes(user.getId()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in updateUser: " + e.getMessage());
            throw e;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

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

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (id, username, password, role, employee_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1, convertUUIDtoBytes(user.getId()));
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword()); // Remember to hash the password!
            pstmt.setString(4, user.getRole());
            if (user.getEmployeeId() != null) {
                pstmt.setBytes(5, convertUUIDtoBytes(user.getEmployeeId()));
            } else {
                pstmt.setNull(5, Types.BINARY); // Or set to null
            }
            pstmt.setDate(6, new java.sql.Date(user.getCreatedAt().getTime()));
            pstmt.setDate(7, new java.sql.Date(user.getUpdatedAt().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in addUser: " + e.getMessage());
            throw e; // Re-throw the exception for the calling code to handle
        } finally {
            closeResources(conn, pstmt, null); // Use the helper method to close resources
        }
    }

}

