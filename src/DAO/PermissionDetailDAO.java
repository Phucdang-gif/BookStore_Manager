package DAO;

import config.DatabaseConnection;
import DTO.PermissionDetailDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PermissionDetailDAO {

    public ArrayList<PermissionDetailDTO> getPermissionsByGroupId(int groupId) {
        ArrayList<PermissionDetailDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        
        String sql = "SELECT pd.*, f.system_function_code " +
                     "FROM permission_details pd " +
                     "JOIN functions f ON pd.function_id = f.function_id " +
                     "WHERE pd.permission_group_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PermissionDetailDTO dto = new PermissionDetailDTO();
                dto.setDetailId(rs.getInt("detail_id"));
                dto.setPermissionGroupId(rs.getInt("permission_group_id")); // Đổi tên biến
                dto.setFunctionId(rs.getInt("function_id"));
                dto.setActions(rs.getString("actions")); 
                dto.setSystemCode(rs.getString("system_function_code"));
                
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean savePermissions(int groupId, ArrayList<PermissionDetailDTO> permissions) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        
        try {
            conn.setAutoCommit(false);

            // B. Xóa quyền cũ
            String deleteSql = "DELETE FROM permission_details WHERE permission_group_id = ?";
            psDelete = conn.prepareStatement(deleteSql);
            psDelete.setInt(1, groupId);
            psDelete.executeUpdate();

            // C. Thêm quyền mới (Dịch system_function_code thành function_id)
            // Sửa lại câu lệnh insertSql để thêm cột assigned_at
            String insertSql = "INSERT INTO permission_details (permission_group_id, function_id, actions, assigned_at) " +
                               "VALUES (?, (SELECT function_id FROM functions WHERE system_function_code = ?), ?, NOW())";
            
            psInsert = conn.prepareStatement(insertSql);
            
            for (PermissionDetailDTO dto : permissions) {
                psInsert.setInt(1, groupId);
                psInsert.setString(2, dto.getSystemCode()); // Dựa vào "BOOK_MANAGE", "SALE_MANAGE" để tìm function_id
                psInsert.setString(3, dto.getActions());    // Truyền "Xem,Thêm,Sửa"
                psInsert.addBatch(); 
            }
            
            psInsert.executeBatch();
            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (psDelete != null) psDelete.close();
                if (psInsert != null) psInsert.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    // Hàm kiểm tra quyền đa năng. 
    // Biến actionColumn sẽ nhận các chuỗi như: "can_add", "can_edit", "can_delete"
    public boolean checkActionPermission(int groupId, String moduleCode, String actionColumn) {
        Connection con = config.DatabaseConnection.getInstance().getConnection();
        
        // Nối trực tiếp actionColumn vào câu SQL
        String sql = "SELECT " + actionColumn + " FROM permission_details WHERE group_id = ? AND module_code = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, groupId);
            ps.setString(2, moduleCode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean(1); // Trả về true nếu được cấp quyền, false nếu bị cấm
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; 
    }
}