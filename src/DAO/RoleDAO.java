package DAO;

import config.DatabaseConnection;
import DTO.RoleDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoleDAO {

    // 1. Lấy danh sách tất cả nhóm quyền (chỉ lấy loại đang hoạt động)
    public ArrayList<RoleDTO> getAll() {
        ArrayList<RoleDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM permission_groups WHERE status = 'active'";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoleDTO role = new RoleDTO();
                role.setRoleId(rs.getInt("permission_group_id"));
                role.setRoleName(rs.getString("group_name"));
                role.setStatus(rs.getString("status"));
                // role.setCreatedAt(rs.getTimestamp("created_at")); // Nếu cần
                list.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy chi tiết 1 nhóm quyền theo ID
    public RoleDTO getById(int id) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM permission_groups WHERE permission_group_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RoleDTO role = new RoleDTO();
                role.setRoleId(rs.getInt("permission_group_id"));
                role.setRoleName(rs.getString("group_name"));
                role.setStatus(rs.getString("status"));
                return role;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. Thêm mới nhóm quyền
    public boolean add(RoleDTO role) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO permission_groups (group_name, status, created_at) VALUES (?, 'active', NOW())";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, role.getRoleName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Cập nhật tên nhóm quyền
    public boolean update(RoleDTO role) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE permission_groups SET group_name = ? WHERE permission_group_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, role.getRoleName());
            ps.setInt(2, role.getRoleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Xóa nhóm quyền (Chuyển status thành inactive chứ không xóa thật)
    public boolean delete(int id) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE permission_groups SET status = 'inactive' WHERE permission_group_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}