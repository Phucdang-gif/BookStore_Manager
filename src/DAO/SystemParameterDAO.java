package DAO;

import DTO.SystemParameterDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;

public class SystemParameterDAO {

    private Connection getConnection() throws SQLException {
        // Thay bằng DBConnection của hệ thống bạn
        return DatabaseConnection.getInstance().getConnection();
    }

    // Lấy tất cả tham số
    public List<SystemParameterDTO> getAll() {
        List<SystemParameterDTO> list = new ArrayList<>();
        String sql = "SELECT parameter_code, parameter_value, description FROM system_parameters";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new SystemParameterDTO(
                        rs.getString("parameter_code"),
                        rs.getString("parameter_value"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy theo mã — dùng nhiều nhất
    public SystemParameterDTO getByCode(String code) {
        String sql = "SELECT parameter_code, parameter_value, description " +
                "FROM system_parameters WHERE parameter_code = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new SystemParameterDTO(
                        rs.getString("parameter_code"),
                        rs.getString("parameter_value"),
                        rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật giá trị
    public boolean update(SystemParameterDTO param) {
        String sql = "UPDATE system_parameters SET parameter_value = ?, description = ? " +
                "WHERE parameter_code = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param.getParameterValue());
            ps.setString(2, param.getDescription());
            ps.setString(3, param.getParameterCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Thêm mới (nếu cần mở rộng)
    public boolean insert(SystemParameterDTO param) {
        String sql = "INSERT INTO system_parameters(parameter_code, parameter_value, description) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param.getParameterCode());
            ps.setString(2, param.getParameterValue());
            ps.setString(3, param.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}