package DAO;

import config.DatabaseConnection;
import DTO.EmployeeDTO;
import java.sql.*;
import java.util.ArrayList;

public class EmployeeDAO {

    public ArrayList<EmployeeDTO> getAll() {
        ArrayList<EmployeeDTO> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM employees ORDER BY employee_id ASC";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeDTO emp = new EmployeeDTO();
                emp.setEmployeeId(rs.getInt("employee_id"));
                emp.setFullName(rs.getString("full_name"));
                emp.setDateOfBirth(rs.getDate("date_of_birth"));
                emp.setGender(rs.getString("gender"));
                emp.setPhone(rs.getString("phone"));
                emp.setAddress(rs.getString("address"));
                emp.setPosition(rs.getString("position"));
                emp.setSalary(rs.getDouble("salary"));
                emp.setHireDate(rs.getDate("hire_date"));
                emp.setTerminationDate(rs.getDate("termination_date"));
                emp.setStatus(rs.getString("status") != null ? rs.getString("status") : "active");
                list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(EmployeeDTO emp) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO employees (full_name, date_of_birth, gender, phone, address, position, salary, hire_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'active')";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getFullName());
            ps.setDate(2, emp.getDateOfBirth());
            ps.setString(3, emp.getGender());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getAddress());
            ps.setString(6, emp.getPosition());
            ps.setDouble(7, emp.getSalary());
            ps.setDate(8, emp.getHireDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(EmployeeDTO emp) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE employees SET full_name=?, date_of_birth=?, gender=?, phone=?, address=?, position=?, salary=?, hire_date=? WHERE employee_id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getFullName());
            ps.setDate(2, emp.getDateOfBirth());
            ps.setString(3, emp.getGender());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getAddress());
            ps.setString(6, emp.getPosition());
            ps.setDouble(7, emp.getSalary());
            ps.setDate(8, emp.getHireDate());
            ps.setInt(9, emp.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int employeeId) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        // Thay vì xóa hẳn, ta đổi trạng thái thành inactive và cập nhật ngày nghỉ việc
        String sql = "UPDATE employees SET status='inactive', termination_date=NOW() WHERE employee_id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}