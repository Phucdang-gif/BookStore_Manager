package BUS;

import DAO.RoleDAO; // Đổi thành NhomQuyenDAO nếu em đang dùng tên đó
import DTO.RoleDTO;
import java.util.ArrayList;

public class RoleBUS {
    
    private RoleDAO roleDAO = new RoleDAO(); 

    // Hàm 1: Lấy tất cả danh sách Nhóm quyền (dùng để đổ vào Combobox lúc Thêm/Sửa tài khoản)
    public ArrayList<RoleDTO> getAll() {
        return roleDAO.getAll();
    }

    // Hàm 2: Lấy thông tin của 1 Nhóm quyền dựa vào ID (dùng để in chữ "Admin" ra bảng Account)
    public RoleDTO getRoleDTO(int roleId) {
        // Nếu roleDAO của em nhận vào String thì em đổi thành roleId + ""
        return roleDAO.getById(roleId); 
    }
}