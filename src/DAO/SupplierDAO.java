package DAO;

import config.DatabaseConnection;
import DTO.SupplierDTO;
import java.sql.*;
import java.util.ArrayList;

public class SupplierDAO {

    public ArrayList<SupplierDTO> getAll() {
        ArrayList<SupplierDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM suppliers WHERE status = 'active' ORDER BY supplier_id DESC";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SupplierDTO dto = new SupplierDTO();
                dto.setSupplierId(rs.getInt("supplier_id"));
                dto.setSupplierName(rs.getString("supplier_name"));
                dto.setPhone(rs.getString("phone"));
                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(SupplierDTO dto) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO suppliers (supplier_name, phone, status) VALUES (?, ?, 'active')";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dto.getSupplierName());
            ps.setString(2, dto.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tương tự, em có thể tự thêm hàm update() và delete() (ẩn status) nếu cần quản lý riêng NCC.
}