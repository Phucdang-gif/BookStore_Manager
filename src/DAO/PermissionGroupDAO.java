package DAO;

import config.DatabaseConnection;
import DTO.PermissionGroupDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PermissionGroupDAO {

    public ArrayList<PermissionGroupDTO> getAll() {
        ArrayList<PermissionGroupDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM permission_groups WHERE status = 'active'";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PermissionGroupDTO group = new PermissionGroupDTO();
                group.setPermissionGroupId(rs.getInt("permission_group_id"));
                group.setGroupName(rs.getString("group_name"));
                group.setStatus(rs.getString("status"));
                group.setCreatedAt(rs.getTimestamp("created_at")); 
                list.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public PermissionGroupDTO getById(int id) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM permission_groups WHERE permission_group_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PermissionGroupDTO group = new PermissionGroupDTO();
                group.setPermissionGroupId(rs.getInt("permission_group_id"));
                group.setGroupName(rs.getString("group_name"));
                group.setStatus(rs.getString("status"));
                return group;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean add(PermissionGroupDTO group) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO permission_groups (group_name, status, created_at) VALUES (?, 'active', NOW())";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, group.getGroupName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(PermissionGroupDTO group) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE permission_groups SET group_name = ? WHERE permission_group_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, group.getGroupName());
            ps.setInt(2, group.getPermissionGroupId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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