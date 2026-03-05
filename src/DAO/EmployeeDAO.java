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
                emp.setAvatar(rs.getString("avatar") != null ? rs.getString("avatar") : null);
                list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(EmployeeDTO emp) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "INSERT INTO employees (full_name, date_of_birth, gender, phone, address, position, salary, hire_date, avatar, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,'active')";
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
            ps.setString(9, emp.getAvatar());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(EmployeeDTO emp) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "UPDATE employees SET full_name=?, date_of_birth=?, gender=?, phone=?, address=?, position=?, salary=?, hire_date=?, avatar = ? WHERE employee_id=?";
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
            ps.setString(9, emp.getAvatar());
            ps.setInt(10, emp.getEmployeeId());
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

    // Lấy danh sách nhân viên CHƯA được cấp tài khoản
    public ArrayList<EmployeeDTO> getEmployeesWithoutAccount() {
        ArrayList<EmployeeDTO> list = new ArrayList<>();
        Connection con = config.DatabaseConnection.getInstance().getConnection();

        // Truy vấn: Lấy nhân viên mà khi JOIN sang bảng accounts, không tìm thấy ID tài
        // khoản
        String sql = "SELECT e.* FROM employees e " +
                "LEFT JOIN accounts a ON e.employee_id = a.employee_id " +
                "WHERE a.account_id IS NULL AND e.status = 'active'";
        // Chỉ cấp tài khoản cho nhân viên còn đang làm việc
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeDTO emp = new EmployeeDTO();
                emp.setEmployeeId(rs.getInt("employee_id"));
                emp.setFullName(rs.getString("full_name"));
                emp.setPhone(rs.getString("phone"));
                emp.setGender(rs.getString("gender"));
                emp.setAddress(rs.getString("address"));
                emp.setSalary(rs.getDouble("salary"));
                emp.setPosition(rs.getString("position"));
                emp.setHireDate(rs.getDate("hire_date"));
                emp.setStatus(rs.getString("status"));
                emp.setTerminationDate(rs.getDate("termination_date"));
                emp.setAvatar(rs.getString("avatar"));
                list.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public EmployeeDTO getById(int employeeId) {
        EmployeeDTO emp = null;
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM employees WHERE employee_id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                emp = new EmployeeDTO();
                emp.setEmployeeId(rs.getInt("employee_id"));
                emp.setFullName(rs.getString("full_name"));
                emp.setDateOfBirth(rs.getDate("date_of_birth"));
                emp.setGender(rs.getString("gender"));
                emp.setPhone(rs.getString("phone"));
                emp.setAddress(rs.getString("address"));
                emp.setPosition(rs.getString("position"));
                emp.setSalary(rs.getDouble("salary"));
                emp.setHireDate(rs.getDate("hire_date"));
                emp.setStatus(rs.getString("status"));
                emp.setTerminationDate(rs.getDate("termination_date"));
                emp.setAvatar(rs.getString("avatar"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emp;

    }
}