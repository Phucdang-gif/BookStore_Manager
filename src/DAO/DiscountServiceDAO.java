package DAO;

import config.DatabaseConnection;
import DTO.DiscountServiceDTO;
import java.sql.*;
import java.util.ArrayList;

public class DiscountServiceDAO {

    public ArrayList<DiscountServiceDTO> getAll() {
        ArrayList<DiscountServiceDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM discount_services ORDER BY service_id DESC";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DiscountServiceDTO dto = new DiscountServiceDTO();
                dto.setServiceId(rs.getInt("service_id"));
                dto.setServiceName(rs.getString("service_name"));
                dto.setDiscountType(rs.getString("discount_type"));
                dto.setDiscountValue(rs.getDouble("discount_value"));
              
                dto.setMinimumAmount(rs.getDouble("minimum_value")); 
                dto.setMaximumDiscount(rs.getDouble("maximum_discount"));
                dto.setStartDate(rs.getTimestamp("start_date"));
                dto.setEndDate(rs.getTimestamp("end_date"));
                dto.setStatus(rs.getString("status"));
                dto.setDescription(rs.getString("description"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(DiscountServiceDTO dto) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
       
        String sql = "INSERT INTO discount_services (service_name, discount_type, discount_value, minimum_value, maximum_discount, start_date, end_date, status, description) VALUES (?, ?, ?, ?, ?, ?, ?, 'active', ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dto.getServiceName());
            ps.setString(2, dto.getDiscountType());
            ps.setDouble(3, dto.getDiscountValue());
            ps.setDouble(4, dto.getMinimumAmount());
            ps.setDouble(5, dto.getMaximumDiscount());
            ps.setTimestamp(6, dto.getStartDate());
            ps.setTimestamp(7, dto.getEndDate());
            ps.setString(8, dto.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(DiscountServiceDTO dto) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
       
        String sql = "UPDATE discount_services SET service_name=?, discount_type=?, discount_value=?, minimum_value=?, maximum_discount=?, start_date=?, end_date=?, status=?, description=? WHERE service_id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dto.getServiceName());
            ps.setString(2, dto.getDiscountType());
            ps.setDouble(3, dto.getDiscountValue());
            ps.setDouble(4, dto.getMinimumAmount());
            ps.setDouble(5, dto.getMaximumDiscount());
            ps.setTimestamp(6, dto.getStartDate());
            ps.setTimestamp(7, dto.getEndDate());
            ps.setString(8, dto.getStatus());
            ps.setString(9, dto.getDescription());
            ps.setInt(10, dto.getServiceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE discount_services SET status = 'inactive' WHERE service_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}