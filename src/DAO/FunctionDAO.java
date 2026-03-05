package DAO;

import config.DatabaseConnection;
import DTO.FunctionDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FunctionDAO {

    // Lấy tất cả chức năng, sắp xếp theo nhóm để hiển thị đẹp
    public ArrayList<FunctionDTO> getAll() {
        ArrayList<FunctionDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        // Sắp xếp: Nhóm trước -> ID sau
        String sql = "SELECT * FROM functions ORDER BY function_id ASC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FunctionDTO func = new FunctionDTO();
                func.setFunctionId(rs.getInt("function_id"));
                func.setFunctionName(rs.getString("function_name"));
                func.setSystemFunctionCode(rs.getString("system_function_code"));
                func.setFunctionGroup(rs.getString("function_group"));
                list.add(func);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public FunctionDTO getById(int id) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM functions WHERE function_id = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FunctionDTO func = new FunctionDTO();
                func.setFunctionId(rs.getInt("function_id"));
                func.setFunctionName(rs.getString("function_name"));
                func.setSystemFunctionCode(rs.getString("system_function_code"));
                func.setFunctionGroup(rs.getString("function_group"));
                return func;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FunctionDTO getBySystemFunctionCode(String code) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM functions WHERE system_function_code = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FunctionDTO func = new FunctionDTO();
                func.setFunctionId(rs.getInt("function_id"));
                func.setFunctionName(rs.getString("function_name"));
                func.setSystemFunctionCode(rs.getString("system_function_code"));
                func.setFunctionGroup(rs.getString("function_group"));
                return func;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FunctionDTO getByName(String name) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM functions WHERE function_name = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FunctionDTO func = new FunctionDTO();
                func.setFunctionId(rs.getInt("function_id"));
                func.setFunctionName(rs.getString("function_name"));
                func.setSystemFunctionCode(rs.getString("system_function_code"));
                func.setFunctionGroup(rs.getString("function_group"));
                return func;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public FunctionDTO getByGroup(String group) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM functions WHERE function_group = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, group);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FunctionDTO func = new FunctionDTO();
                func.setFunctionId(rs.getInt("function_id"));
                func.setFunctionName(rs.getString("function_name"));
                func.setSystemFunctionCode(rs.getString("system_function_code"));
                func.setFunctionGroup(rs.getString("function_group"));
                return func;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
