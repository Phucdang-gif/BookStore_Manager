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

    // Lấy tất cả tác giả để nạp vào ComboBox
    public ArrayList<AuthorDTO> selectAll() throws SQLException {
        ArrayList<AuthorDTO> list = new ArrayList<>();
        // Bỏ điều kiện status vì bảng không còn cột status
        String sql = "SELECT * FROM authors";
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

    // Thêm tác giả mới (Dùng cho option "+ Thêm tác giả mới...")
    public boolean insert(AuthorDTO author) throws SQLException {
        // Cập nhật câu INSERT
        String sql = "INSERT INTO authors (author_name) VALUES (?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, author.getAuthorName());

            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        author.setAuthorId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
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
}