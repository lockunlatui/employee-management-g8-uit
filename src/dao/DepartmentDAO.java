package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.Department;
import database.ConnectionManager;

public class DepartmentDAO {

    public List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments";

        try (Connection conn = ConnectionManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Department department = new Department();
                byte[] idBytes = resultSet.getBytes("id");
                if (idBytes != null) {
                    department.setId(bytesToUUID(idBytes));
                }
                department.setName(resultSet.getString("department_name"));
                departments.add(department);
            }
        }

        return departments;
    }

    public Department getDepartmentById(UUID id) throws SQLException {
        String sql = "SELECT * FROM departments WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setBytes(1, uuidToBytes(id));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Department department = new Department();
                    byte[] idBytes = resultSet.getBytes("id");
                    if (idBytes != null) {
                        department.setId(bytesToUUID(idBytes));
                    }
                    department.setName(resultSet.getString("department_name"));
                    return department;
                }
            }
        }

        return null;
    }

    public void addDepartment(Department department) throws SQLException {
        String sql = "INSERT INTO departments (id, department_name) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setBytes(1, uuidToBytes(department.getId()));
            statement.setString(2, department.getName());
            statement.executeUpdate();
        }
    }

    public void updateDepartment(Department department) throws SQLException {
        String sql = "UPDATE departments SET department_name = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, department.getName());
            statement.setBytes(2, uuidToBytes(department.getId()));
            statement.executeUpdate();
        }
    }

    public void deleteDepartment(UUID id) throws SQLException {
        String sql = "DELETE FROM departments WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setBytes(1, uuidToBytes(id));
            statement.executeUpdate();
        }
    }

    public boolean isDepartmentNameExists(String departmentName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM departments WHERE department_name = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, departmentName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean isDepartmentCodeExists(String departmentCode) throws SQLException {
        return false;
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

    private UUID bytesToUUID(byte[] bytes) {
        long msb = 0;
        long lsb = 0;

        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }
}
