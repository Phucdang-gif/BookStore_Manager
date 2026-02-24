package DAO;

import config.DatabaseConnection;
import DTO.InvoiceServiceDTO;
import java.sql.*;

public class InvoiceServiceDAO {

    public boolean insert(InvoiceServiceDTO dto) {
        Connection con = DatabaseConnection.getInstance().getConnection();
        // Cột invoice_service_id tự tăng nên không cần INSERT
        String sql = "INSERT INTO invoice_services (invoice_id, service_id, service_type, discount_value, description) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dto.getInvoiceId());
            ps.setInt(2, dto.getServiceId());
            ps.setString(3, dto.getServiceType()); // Sẽ lưu "Phần trăm" hoặc "Số tiền cố định"
            ps.setDouble(4, dto.getDiscountValue());
            ps.setString(5, dto.getDescription());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("========== LỖI SQL LƯU INVOICE_SERVICES ==========");
            System.out.println("Nguyên nhân: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}