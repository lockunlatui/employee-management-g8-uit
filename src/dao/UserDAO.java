package dao;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setRole(rs.getString("role"));

				byte[] employeeIdBytes = rs.getBytes("employee_id");
				if (employeeIdBytes != null) {
					Employee employee = new Employee();
					employee.setId(convertBytesToUUID(employeeIdBytes));
					employee.setEmployeeName(rs.getString("employee_name"));
					user.setEmployee(employee);
				}

				users.add(user);
			}
		}
		return users;
	}

	private UUID convertBytesToUUID(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		long high = byteBuffer.getLong();
		long low = byteBuffer.getLong();
		return new UUID(high, low);
	}
}
