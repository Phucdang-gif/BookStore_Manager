package DAO;

import config.DatabaseConnection;
import DTO.ImportReceiptDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Statement;

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
               
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thay thế hàm insert cũ bằng hàm này
    public int insert(ImportReceiptDTO dto) {
        int generatedId = -1;
        Connection con = DatabaseConnection.getInstance().getConnection();
        // Dùng đúng tên cột receipt_date như trong CSDL của em
        String sql = "INSERT INTO import_receipts (supplier_id, employee_id, receipt_date, total_amount, status, note) VALUES (?, ?, NOW(), ?, ?, ?)";
        
        try {
            // QUAN TRỌNG: Thêm Statement.RETURN_GENERATED_KEYS
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getSupplierId());
            ps.setInt(2, dto.getEmployeeId());
            ps.setDouble(3, dto.getTotalAmount());
            // Trạng thái mặc định là Completed nếu chưa có
            ps.setString(4, dto.getStatus() != null ? dto.getStatus() : "Completed"); 
           
            
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1); // Lấy ID phiếu nhập vừa tạo
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId;
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