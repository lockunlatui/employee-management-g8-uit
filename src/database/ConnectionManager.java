package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/employee_management_database?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static Connection connection;

    private ConnectionManager() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load the JDBC driver
                connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                System.out.println("Connected to MySQL database!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found!", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection to MySQL database closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }

    public static boolean isConnectionSuccessful() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(1);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            return false;
        }
    }
}