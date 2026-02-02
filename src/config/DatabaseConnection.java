package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private String url = "jdbc:mysql://localhost:3306/bookstore_db";
    private String user = "root";
    private String password = "";

    private DatabaseConnection() {
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Logic tiết kiệm tài nguyên:
            // Chỉ tạo mới KHI VÀ CHỈ KHI:
            // 1. connection chưa từng được tạo (null)
            // 2. HOẶC connection đã bị đóng (closed) do lỗi mạng hoặc tắt XAMPP
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println(">> Connection successful");
            }
            // Nếu connection vẫn đang sống, trả về cái cũ (Không tốn tài nguyên tạo mới)
            else {
                System.out.println(">> Used existing connection");
            }
        } catch (Exception e) {
            return null;
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}