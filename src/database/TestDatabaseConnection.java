package database;

public class TestDatabaseConnection {

    public static void main(String[] args) {
        if (ConnectionManager.isConnectionSuccessful()) {
            System.out.println("Database connection test successful!");
            ConnectionManager.closeConnection();
        } else {
            System.out.println("Database connection test failed.");
        }
    }
}