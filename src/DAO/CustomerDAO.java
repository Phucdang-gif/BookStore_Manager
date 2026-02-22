package DAO;

import config.DatabaseConnection;
import DTO.CustomerDTO;
import java.sql.*;
import java.util.ArrayList;

public class CustomerDAO {

    public ArrayList<CustomerDTO> getAll() {
        ArrayList<CustomerDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM customers ORDER BY customer_id DESC";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CustomerDTO dto = new CustomerDTO();
                dto.setCustomerId(rs.getInt("customer_id"));
                dto.setFullName(rs.getString("full_name"));
                dto.setPhone(rs.getString("phone"));
                dto.setLoyaltyPoints(rs.getInt("loyalty_points"));
                dto.setRegistrationDate(rs.getDate("registration_date"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(CustomerDTO dto) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        // Khi thêm mới khách hàng, điểm tích lũy mặc định là 0, ngày đăng ký là ngày hiện tại
        String sql = "INSERT INTO customers (full_name, phone, loyalty_points, registration_date) VALUES (?, ?, 0, NOW())";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dto.getFullName());
            ps.setString(2, dto.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(CustomerDTO dto) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        // Cho phép cập nhật Tên, SĐT và Điểm tích lũy (Admin có thể tự cộng/trừ điểm)
        String sql = "UPDATE customers SET full_name = ?, phone = ?, loyalty_points = ? WHERE customer_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dto.getFullName());
            ps.setString(2, dto.getPhone());
            ps.setInt(3, dto.getLoyaltyPoints());
            ps.setInt(4, dto.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int customerId) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Lỗi khóa ngoại (Khách hàng này đã có Hóa Đơn)
            System.out.println("Không thể xóa khách hàng đã có lịch sử giao dịch!");
            return false;
        }
    }
}