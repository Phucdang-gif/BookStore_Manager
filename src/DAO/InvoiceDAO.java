package DAO;

import config.DatabaseConnection;
import DTO.InvoiceDTO;
import java.sql.*;
import java.util.ArrayList;

public class InvoiceDAO {

    public ArrayList<InvoiceDTO> getAll() {
        ArrayList<InvoiceDTO> list = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM invoices ORDER BY created_at DESC";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InvoiceDTO dto = new InvoiceDTO();
                dto.setInvoiceId(rs.getInt("invoice_id"));
                dto.setCustomerId(rs.getInt("customer_id"));
                dto.setEmployeeId(rs.getInt("employee_id"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));
                dto.setTotalAmount(rs.getDouble("total_amount"));
                dto.setTotalDiscount(rs.getDouble("total_discount"));
                dto.setPointsUsed(rs.getInt("points_used"));
                dto.setPointsValue(rs.getDouble("points_value"));
                dto.setFinalAmount(rs.getDouble("final_amount"));
                dto.setPaymentMethod(rs.getString("payment_method"));
                dto.setStatus(rs.getString("status"));
                dto.setPointsEarned(rs.getInt("points_earned"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Dùng khi thanh toán xong tạo hóa đơn mới
    public boolean insert(InvoiceDTO dto) {
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO invoices (customer_id, employee_id, created_at, total_amount, total_discount, points_used, points_value, final_amount, payment_method, status, points_earned) " +
                     "VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, 'Completed', ?)";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dto.getCustomerId());
            ps.setInt(2, dto.getEmployeeId());
            ps.setDouble(3, dto.getTotalAmount());
            ps.setDouble(4, dto.getTotalDiscount());
            ps.setInt(5, dto.getPointsUsed());
            ps.setDouble(6, dto.getPointsValue());
            ps.setDouble(7, dto.getFinalAmount());
            ps.setString(8, dto.getPaymentMethod());
            ps.setInt(9, dto.getPointsEarned());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hủy hóa đơn (Không xóa khỏi DB, chỉ đổi trạng thái)
    public boolean updateStatus(int invoiceId, String status) {
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE invoices SET status = ? WHERE invoice_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}