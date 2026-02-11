package DAO;

import DTO.PublisherDTO;
import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class PublisherDAO {
    private Connection conn;

    public PublisherDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    public ArrayList<PublisherDTO> selectAll() {
        ArrayList<PublisherDTO> list = new ArrayList<>();
        // Sắp xếp theo ID hoặc tên tùy chọn
        String sql = "SELECT * FROM publishers ORDER BY publisher_id";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PublisherDTO(
                        rs.getInt("publisher_id"),
                        rs.getString("publisher_name"),
                        rs.getString("phone"), // Lấy thêm số điện thoại
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public PublisherDTO selectById(int id) {
        String sql = "SELECT * FROM publishers WHERE publisher_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PublisherDTO(
                        rs.getInt("publisher_id"),
                        rs.getString("publisher_name"),
                        rs.getString("phone"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(PublisherDTO pub) {
        String sql = "INSERT INTO publishers (publisher_name, phone, status) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pub.getName());
            pstmt.setString(2, pub.getPhone());
            pstmt.setString(3, pub.getStatus());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int update(PublisherDTO pub) {
        String sql = "UPDATE publishers SET publisher_name = ?, phone = ?, status = ? WHERE publisher_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pub.getName());
            pstmt.setString(2, pub.getPhone());
            pstmt.setString(3, pub.getStatus());
            pstmt.setInt(4, pub.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(int id) {
        String sql = "DELETE FROM publishers WHERE publisher_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}