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
        // Sửa câu SQL nếu cần thiết, đảm bảo bảng categories có cột 'status'
        String sql = "SELECT * FROM categories";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // SỬA LẠI DÒNG NÀY: Thêm tham số status cho khớp với DTO
                list.add(new CategoryDTO(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status") // Lấy thêm cột status
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}