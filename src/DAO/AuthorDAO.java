package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import DTO.AuthorDTO;
import config.DatabaseConnection;

public class AuthorDAO {
    private Connection connection;

    public AuthorDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ArrayList<AuthorDTO> selectAll() throws SQLException {
        ArrayList<AuthorDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY author_id DESC"; // Sắp xếp mới nhất lên đầu
        try (PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new AuthorDTO(
                        rs.getInt("author_id"),
                        rs.getString("author_name")));
            }
        }
        return list;
    }

    public AuthorDTO selectById(int id) throws SQLException {
        String sql = "SELECT * FROM authors WHERE author_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new AuthorDTO(
                            rs.getInt("author_id"),
                            rs.getString("author_name"));
                }
            }
        }
        return null;
    }

    // --- CÁC HÀM THÊM / SỬA / XÓA ---

    public boolean insert(AuthorDTO author) throws SQLException {
        String sql = "INSERT INTO authors (author_name) VALUES (?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, author.getAuthorName());

            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        author.setAuthorId(rs.getInt(1)); // Cập nhật ID lại cho DTO
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int update(AuthorDTO author) throws SQLException {
        String sql = "UPDATE authors SET author_name = ? WHERE author_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, author.getAuthorName());
            pst.setInt(2, author.getAuthorId());
            return pst.executeUpdate(); // Trả về số dòng bị ảnh hưởng
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM authors WHERE author_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate();
        }
    }

    // Kiểm tra trùng tên (để validation)
    public boolean isNameExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM authors WHERE author_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, name);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}