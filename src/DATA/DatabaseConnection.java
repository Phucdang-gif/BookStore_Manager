package DATA;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstoredb", "root", "123456");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        Connection v_conn = getConnection();
        if (v_conn != null) {
            System.out.println("SUCCESS.");
        } else {
            System.out.println("FAILED");
        }
    }
}