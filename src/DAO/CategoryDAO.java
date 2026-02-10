package DAO;

import DTO.CategoryDTO;
import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class CategoryDAO {
    private Connection conn;

    public CategoryDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    public ArrayList<CategoryDTO> selectAll() {
        ArrayList<CategoryDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY category_id";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CategoryDTO(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public CategoryDTO selectById(int id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new CategoryDTO(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(CategoryDTO category) {
        String sql = "INSERT INTO categories (category_name, status) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Trả về ID vừa thêm
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int update(CategoryDTO category) {
        String sql = "UPDATE categories SET category_name = ?, status = ? WHERE category_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getStatus());
            pstmt.setInt(3, category.getId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(int id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean checkDuplicate(String name) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkDuplicateExclude(String name, int excludeId) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_name = ? AND category_id != ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}