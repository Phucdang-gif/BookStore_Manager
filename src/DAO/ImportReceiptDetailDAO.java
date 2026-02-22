package DAO;

import config.DatabaseConnection;
import DTO.ImportReceiptDetailDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ImportReceiptDetailDAO {

    public ArrayList<ImportReceiptDetailDTO> getByReceiptId(int receiptId) {
        ArrayList<ImportReceiptDetailDTO> list = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM import_receipt_details WHERE receipt_id = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, receiptId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ImportReceiptDetailDTO dto = new ImportReceiptDetailDTO();
                dto.setReceiptId(rs.getInt("receipt_id"));
                dto.setBookId(rs.getInt("book_id"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setUnitPrice(rs.getDouble("unit_price"));
                dto.setSubtotal(rs.getDouble("subtotal"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertBatch(ArrayList<ImportReceiptDetailDTO> details) {
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO import_receipt_details (receipt_id, book_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            for (ImportReceiptDetailDTO dto : details) {
                ps.setInt(1, dto.getReceiptId());
                ps.setInt(2, dto.getBookId());
                ps.setInt(3, dto.getQuantity());
                ps.setDouble(4, dto.getUnitPrice());
                ps.setDouble(5, dto.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
