package DAO;

import config.DatabaseConnection;
import DTO.InvoiceDetailDTO;
import java.sql.*;
import java.util.ArrayList;

public class InvoiceDetailDAO {

    public ArrayList<InvoiceDetailDTO> getByInvoiceId(int invoiceId) {
        ArrayList<InvoiceDetailDTO> list = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM invoice_details WHERE invoice_id = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InvoiceDetailDTO dto = new InvoiceDetailDTO();
                dto.setDetailId(rs.getInt("detail_id"));
                dto.setInvoiceId(rs.getInt("invoice_id"));
                dto.setBookId(rs.getInt("book_id"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setUnitPrice(rs.getDouble("unit_price"));
                dto.setDiscount(rs.getDouble("discount"));
                dto.setSubtotal(rs.getDouble("subtotal"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertBatch(ArrayList<InvoiceDetailDTO> details) {
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, discount, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            for (InvoiceDetailDTO dto : details) {
                ps.setInt(1, dto.getInvoiceId());
                ps.setInt(2, dto.getBookId());
                ps.setInt(3, dto.getQuantity());
                ps.setDouble(4, dto.getUnitPrice());
                ps.setDouble(5, dto.getDiscount());
                ps.setDouble(6, dto.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}