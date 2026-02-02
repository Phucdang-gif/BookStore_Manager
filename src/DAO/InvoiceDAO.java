package DAO;

import DTO.InvoiceDTO;
import config.DatabaseConnection; // Sửa lại package config cho đúng với bài bạn
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public int insert(InvoiceDTO invoice) {
        int generatedId = -1;
        try {
            String sql = "INSERT INTO invoices (customer_id, employee_id, total_amount, " +
                    "total_discount, final_amount, payment_method, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            // RETURN_GENERATED_KEYS: Quan trọng! Để lấy ID tự tăng của hóa đơn vừa tạo
            PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);

            if (invoice.getCustomerId() == 0) {
                ps.setNull(1, java.sql.Types.INTEGER); // Khách vãng lai
            } else {
                ps.setInt(1, invoice.getCustomerId());
            }

            ps.setInt(2, invoice.getEmployeeId());
            ps.setDouble(3, invoice.getTotalAmount());
            ps.setDouble(4, invoice.getTotalDiscount());
            ps.setDouble(5, invoice.getFinalAmount());
            ps.setString(6, invoice.getPaymentMethod());
            ps.setString(7, invoice.getStatus());
            ps.setTimestamp(8, invoice.getCreatedAt());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Lấy ID vừa sinh ra
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId; // Trả về -1 nếu lỗi, trả về số > 0 nếu thành công
    }

    public List<InvoiceDTO> selectAll() {
        List<InvoiceDTO> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM invoices ORDER BY created_at DESC"; // Mới nhất lên đầu
            Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                InvoiceDTO inv = new InvoiceDTO();
                inv.setInvoiceId(rs.getInt("invoice_id"));
                inv.setEmployeeId(rs.getInt("employee_id"));
                inv.setCustomerId(rs.getInt("customer_id"));
                inv.setTotalAmount(rs.getDouble("total_amount"));
                inv.setFinalAmount(rs.getDouble("final_amount"));
                inv.setCreatedAt(rs.getTimestamp("created_at"));
                inv.setStatus(rs.getString("status"));
                list.add(inv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public InvoiceDTO selectById(int invoiceId) {
        InvoiceDTO invoice = null;
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM invoices WHERE invoice_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                invoice = new InvoiceDTO();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setCustomerId(rs.getInt("customer_id"));
                invoice.setEmployeeId(rs.getInt("employee_id"));
                invoice.setTotalAmount(rs.getDouble("total_amount"));
                invoice.setTotalDiscount(rs.getDouble("total_discount"));
                invoice.setFinalAmount(rs.getDouble("final_amount"));
                invoice.setPaymentMethod(rs.getString("payment_method"));
                invoice.setStatus(rs.getString("status"));
                invoice.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoice;
    }
}