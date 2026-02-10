package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import DTO.PublisherDTO;
import config.DatabaseConnection;

public class PublisherDAO {
    private Connection connection;

    public PublisherDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public PublisherDAO(Connection connection) {
        this.connection = connection;
    }

    // Hàm lấy tất cả nhà xuất bản
    public ArrayList<PublisherDTO> selectAll() throws SQLException {
        ArrayList<PublisherDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM publishers";
        try (PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new PublisherDTO(
                        rs.getInt("publisher_id"),
                        rs.getString("publisher_name"),
                        rs.getString("status")));
            }
        }
        return list;
    }

    public PublisherDTO selectById(int id) throws SQLException {
        String sql = "SELECT * FROM publishers WHERE publisher_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new PublisherDTO(
                            rs.getInt("publisher_id"),
                            rs.getString("publisher_name"),
                            rs.getString("status"));
                }
            }
        }
        return null;
    }
}