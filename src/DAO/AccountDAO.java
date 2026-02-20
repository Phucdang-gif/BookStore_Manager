package DAO;

import config.DatabaseConnection;
import DTO.AccountDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccountDAO {
   
    /**
     * Hàm Đăng nhập: Kiểm tra user/pass và lấy thông tin user + nhóm quyền
     */
    public AccountDTO login(String username, String password) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        AccountDTO account = null;
        
        // JOIN bảng accounts với employees để lấy luôn tên hiển thị và avatar
        // JOIN bảng permission_groups để lấy tên nhóm quyền (Admin, Staff...)
        String sql = "SELECT a.*, e.full_name, e.avatar, p.group_name " +
                     "FROM accounts a " +
                     "JOIN employees e ON a.employee_id = e.employee_id " +
                     "JOIN permission_groups p ON a.permission_group_id = p.permission_group_id " +
                     "WHERE a.username = ? AND a.password = ? AND a.status = 'active' AND e.status = 'active'";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                account = new AccountDTO();
                // Mapping dữ liệu từ DB sang DTO
                account.setAccountId(rs.getInt("account_id"));
                account.setEmployeeId(rs.getInt("employee_id"));
                account.setRoleId(rs.getInt("permission_group_id")); // Quan trọng nhất: ID nhóm quyền
                account.setUsername(rs.getString("username"));
                // account.setPassword(rs.getString("password")); // Không nên lưu pass vào session
                account.setStatus(rs.getString("status"));
                
                // Các trường mở rộng (Bạn cần thêm vào AccountDTO nếu chưa có)
                // account.setFullName(rs.getString("full_name"));
                // account.setAvatar(rs.getString("avatar"));
                // account.setRoleName(rs.getString("group_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
    public boolean changePassword(int accountId, String newPassword) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE accounts SET password = ? WHERE account_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, accountId);
            
            int affected = ps.executeUpdate();
            return affected > 0; // Nếu có dòng nào bị ảnh hưởng thì đổi pass thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStatus(int accountId, String newStatus) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus); // Ví dụ: "active" hoặc "inactive"
            ps.setInt(2, accountId);
            
            int affected = ps.executeUpdate();
            return affected > 0; // Nếu có dòng nào bị ảnh hưởng thì cập nhật thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean changeActive(int accountId) {
        // Thay vì xóa thật, ta sẽ chuyển status thành 'inactive'
        return updateStatus(accountId, "inactive");
    }
    public boolean restore(int accountId) {
        // Khôi phục tài khoản bằng cách chuyển status thành 'active'
        return updateStatus(accountId, "active");
    }
    public boolean delete(int accountId) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "DELETE FROM accounts WHERE account_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            
            int affected = ps.executeUpdate();
            return affected > 0; // Nếu có dòng nào bị ảnh hưởng thì xóa thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insert(AccountDTO account) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO accounts (employee_id, permission_group_id, username, password, status) VALUES (?, ?, ?, ?, 'active')";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, account.getEmployeeId());
            ps.setInt(2, account.getRoleId());
            ps.setString(3, account.getUsername());
            ps.setString(4, account.getPassword());
            
            int affected = ps.executeUpdate();
            return affected > 0; // Nếu có dòng nào bị ảnh hưởng thì thêm thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(AccountDTO account) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE accounts SET employee_id = ?, permission_group_id = ?, username = ?, password = ?, status = ? WHERE account_id = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, account.getEmployeeId());
            ps.setInt(2, account.getRoleId());
            ps.setString(3, account.getUsername());
            ps.setString(4, account.getPassword());
            ps.setString(5, account.getStatus());
            ps.setInt(6, account.getAccountId());
            
            int affected = ps.executeUpdate();
            return affected > 0; // Nếu có dòng nào bị ảnh hưởng thì cập nhật thành công
        }catch (SQLException e) {
                    e.printStackTrace();
                    return false;
            }
        }
        public ArrayList<AccountDTO> selectAll() {
        ArrayList<AccountDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT a.*, e.full_name, e.avatar, p.group_name " +
                     "FROM accounts a " +
                     "JOIN employees e ON a.employee_id = e.employee_id " +
                     "JOIN permission_groups p ON a.permission_group_id = p.permission_group_id " +
                     "ORDER BY a.account_id ASC";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AccountDTO account = new AccountDTO();
                account.setAccountId(rs.getInt("account_id"));
                account.setEmployeeId(rs.getInt("employee_id"));
                account.setRoleId(rs.getInt("permission_group_id"));
                account.setUsername(rs.getString("username"));
                account.setStatus(rs.getString("status"));
                // account.setFullName(rs.getString("full_name"));
                // account.setAvatar(rs.getString("avatar"));
                // account.setRoleName(rs.getString("group_name"));
                
                list.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}