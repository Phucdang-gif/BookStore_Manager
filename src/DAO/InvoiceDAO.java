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
            ResultSet rs = ps.executeQuery(); // su dung cho cau lenh SELECT
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

    // LƯU Ý: Hàm này phải trả về INT (Mã hóa đơn tự tăng)
    // LƯU Ý: Hàm này phải trả về INT (Mã hóa đơn tự tăng)
    public int insert(InvoiceDTO dto) {
        int generatedId = -1;
        Connection con = DatabaseConnection.getInstance().getConnection();
        
        // 1. Câu lệnh lưu Hóa đơn
        String sqlInvoice = "INSERT INTO invoices (customer_id, employee_id, created_at, total_amount, total_discount, points_used, points_value, final_amount, payment_method, status, points_earned) "
                + "VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, 'Completed', ?)";

        // 2. Câu lệnh Cập nhật điểm Khách hàng (Trừ điểm đã dùng, Cộng điểm mới thưởng)
        String sqlUpdatePoints = "UPDATE customers SET loyalty_points = loyalty_points - ? + ? WHERE customer_id = ?";
        
        // 3. Câu lệnh Lưu Lịch sử quy đổi điểm (Vào bảng point_redemption_history)
        String sqlHistory = "INSERT INTO point_redemption_history (customer_id, points_redeemed, value_received, redemption_type, redemption_date) "
                + "VALUES (?, ?, ?, 'Giảm giá hóa đơn', NOW())";

        try {
            // BẬT CHẾ ĐỘ TRANSACTION: Đảm bảo nếu bị lỗi giữa chừng thì không bị lệch điểm
            con.setAutoCommit(false);

            // BƯỚC 1: LƯU HÓA ĐƠN
            PreparedStatement ps = con.prepareStatement(sqlInvoice, Statement.RETURN_GENERATED_KEYS);
            if (dto.getCustomerId() > 0) {
                ps.setInt(1, dto.getCustomerId());
            } else {
                ps.setNull(1, Types.INTEGER); // Khách vãng lai
            }
            
            ps.setInt(2, dto.getEmployeeId());
            ps.setDouble(3, dto.getTotalAmount());
            ps.setDouble(4, dto.getTotalDiscount());
            ps.setInt(5, dto.getPointsUsed());      // Chèn số điểm dùng
            ps.setDouble(6, dto.getPointsValue());  // Chèn số tiền tương ứng
            ps.setDouble(7, dto.getFinalAmount());
            ps.setString(8, dto.getPaymentMethod());
            ps.setInt(9, dto.getPointsEarned());    // Chèn số điểm thưởng

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1); 
                }
            }

            // BƯỚC 2 & 3: CẬP NHẬT ĐIỂM (Chỉ chạy nếu có Khách Hàng)
            if (dto.getCustomerId() > 0) {
                
                // BƯỚC 2: Cập nhật ví điểm của Khách (Cộng/Trừ)
                PreparedStatement psUpdatePoint = con.prepareStatement(sqlUpdatePoints);
                psUpdatePoint.setInt(1, dto.getPointsUsed());    // Trừ điểm dùng
                psUpdatePoint.setInt(2, dto.getPointsEarned());  // Cộng điểm mới
                psUpdatePoint.setInt(3, dto.getCustomerId());
                psUpdatePoint.executeUpdate();

                // BƯỚC 3: Lưu Lịch sử dùng điểm (Chỉ lưu nếu khách có XÀI điểm)
                if (dto.getPointsUsed() > 0) {
                    PreparedStatement psHistory = con.prepareStatement(sqlHistory);
                    psHistory.setInt(1, dto.getCustomerId());
                    psHistory.setInt(2, dto.getPointsUsed());
                    psHistory.setDouble(3, dto.getPointsValue());
                    psHistory.executeUpdate();
                }
            }

            // CHỐT GIAO DỊCH: Lưu cứng mọi thay đổi vào Database
            con.commit();

        } catch (SQLException e) {
            try {
                // NẾU CÓ LỖI BẤT KỲ: Quay ngược thời gian, không lưu gì cả
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("========== LỖI SQL LƯU HÓA ĐƠN & ĐIỂM ==========");
            System.out.println("Nguyên nhân: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                // Trả kết nối về chế độ bình thường
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
        }
        
        return generatedId;
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