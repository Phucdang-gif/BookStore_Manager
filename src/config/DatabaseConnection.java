package config;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    String url = "jdbc:mysql://localhost:3306/bookstore_db";
    String userName = "root";
    String password = "";

    private DatabaseConnection() {
        try {
            // Đăng ký Driver
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            // Tạo kết nối và GÁN VÀO BIẾN this.connection
            this.connection = DriverManager.getConnection(url, userName, password);

            System.out.println("Connect to database success");

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
            JOptionPane.showMessageDialog(null, "Connect Failed" + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // 4. Hàm lấy Instance duy nhất
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            // Khi gọi new ở đây, nó sẽ chạy vào cái Constructor private ở trên
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // 5. Hàm lấy Connection
    public Connection getConnection() {
        return connection;
    }

    // Hàm đóng kết nối
    public static void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Test thử
    public static void main(String[] args) {
        System.out.println("Đang thử kết nối...");

        // Gọi getInstance để kích hoạt Constructor
        Connection c = DatabaseConnection.getInstance().getConnection();

        if (c != null) {
            System.out.println("Test OK!");
        } else {
            System.out.println("Test Fail: Connection vẫn null");
        }
    }
}