package DAO;

import config.DatabaseConnection;
import DTO.PermissionDetailDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PermissionDAO {

    // 1. Lấy danh sách quyền của một nhóm cụ thể
    public ArrayList<PermissionDetailDTO> getPermissionsByRoleId(int roleId) {
        ArrayList<PermissionDetailDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        
        // JOIN bảng functions để lấy luôn mã code (BOOK_MANAGE) phục vụ check quyền
        String sql = "SELECT pd.*, f.system_function_code " +
                     "FROM permission_details pd " +
                     "JOIN functions f ON pd.function_id = f.function_id " +
                     "WHERE pd.permission_group_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PermissionDetailDTO dto = new PermissionDetailDTO();
                dto.setDetailId(rs.getInt("detail_id"));
                dto.setRoleId(rs.getInt("permission_group_id"));
                dto.setFunctionId(rs.getInt("function_id"));
                dto.setActions(rs.getString("actions")); // Chuỗi "Xem,Thêm,Sửa"
                dto.setSystemCode(rs.getString("system_function_code"));
                
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lưu quyền (Sử dụng Transaction an toàn)
    // Nguyên lý: Xóa hết quyền cũ của nhóm -> Insert quyền mới
    public boolean savePermissions(int roleId, ArrayList<PermissionDetailDTO> permissions) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        
        try {
            // A. Tắt chế độ tự động lưu (Bắt đầu Transaction)
            conn.setAutoCommit(false);

            // B. Xóa quyền cũ
            String deleteSql = "DELETE FROM permission_details WHERE permission_group_id = ?";
            psDelete = conn.prepareStatement(deleteSql);
            psDelete.setInt(1, roleId);
            psDelete.executeUpdate();

            // C. Thêm quyền mới (Dùng Batch Insert để tối ưu tốc độ)
            String insertSql = "INSERT INTO permission_details (permission_group_id, function_id, actions, assigned_at) VALUES (?, ?, ?, NOW())";
            psInsert = conn.prepareStatement(insertSql);
            
            for (PermissionDetailDTO dto : permissions) {
                psInsert.setInt(1, roleId);
                psInsert.setInt(2, dto.getFunctionId());
                psInsert.setString(3, dto.getActions()); // Ví dụ: "view,create"
                psInsert.addBatch(); // Gom lại
            }
            
            // Thực thi insert một lần
            psInsert.executeBatch();

            // D. Xác nhận Transaction (Lưu thật sự vào DB)
            conn.commit();
            return true;

        } catch (SQLException e) {
            // E. Nếu có lỗi, hoàn tác mọi thứ (Rollback)
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // F. Bật lại AutoCommit và đóng kết nối
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (psDelete != null) psDelete.close();
                if (psInsert != null) psInsert.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
