package DAO;

import config.DatabaseConnection;
import DTO.ImportReceiptDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ImportReceiptDAO {

    public ArrayList<ImportReceiptDTO> getAll() {
        ArrayList<ImportReceiptDTO> list = new ArrayList<>();
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM import_receipts ORDER BY receipt_date DESC";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ImportReceiptDTO dto = new ImportReceiptDTO();
                dto.setReceiptId(rs.getInt("receipt_id"));
                dto.setSupplierId(rs.getInt("supplier_id"));
                dto.setEmployeeId(rs.getInt("employee_id"));
                dto.setReceiptDate(rs.getTimestamp("receipt_date"));
                dto.setTotalAmount(rs.getDouble("total_amount"));
                dto.setStatus(rs.getString("status"));
                dto.setNote(rs.getString("note"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ImportReceiptDTO dto) {
        Connection con = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO import_receipts (supplier_id, employee_id, receipt_date, total_amount, status, note) VALUES (?, ?, NOW(), ?, ?, ?)";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, dto.getSupplierId());
            ps.setInt(2, dto.getEmployeeId());
            ps.setDouble(3, dto.getTotalAmount());
            ps.setString(4, dto.getStatus());
            ps.setString(5, dto.getNote());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        Connection con = DatabaseConnection.getInstance().getConnection();
        // Cập nhật trạng thái thành 'cancelled' thay vì xóa thật
        String sql = "UPDATE import_receipts SET status = 'cancelled' WHERE receipt_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}