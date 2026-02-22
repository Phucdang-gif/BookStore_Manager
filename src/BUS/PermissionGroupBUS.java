package BUS;

import DAO.PermissionGroupDAO; 
import DTO.PermissionGroupDTO;
import java.util.ArrayList;

public class PermissionGroupBUS {
    
    private PermissionGroupDAO permissionGroupDAO = new PermissionGroupDAO(); 

    // Hàm 1: Lấy tất cả danh sách Nhóm quyền 
    public ArrayList<PermissionGroupDTO> getAll() {
        return permissionGroupDAO.getAll();
    }

    // Hàm 2: Lấy thông tin của 1 Nhóm quyền dựa vào ID
    public PermissionGroupDTO getPermissionGroupDTO(int permissionGroupId) {
        return permissionGroupDAO.getById(permissionGroupId); 
    }
    public boolean addGroup(PermissionGroupDTO group) {
        return permissionGroupDAO.add(group);
    }

    public boolean updateGroup(PermissionGroupDTO group) {
        return permissionGroupDAO.update(group);
    }

    public boolean deleteGroup(int groupId) {
        return permissionGroupDAO.delete(groupId);
    }
    // 4. Hàm tìm kiếm nhóm quyền theo tên
    public ArrayList<PermissionGroupDTO> search(String text) {
        ArrayList<PermissionGroupDTO> result = new ArrayList<>();
        text = text.toLowerCase(); // Chuyển chữ thường để tìm kiếm không phân biệt hoa/thường
        
        // Lấy danh sách gốc và lọc
        ArrayList<PermissionGroupDTO> allGroups = this.getAll();
        for (PermissionGroupDTO group : allGroups) {
            if (group.getGroupName().toLowerCase().contains(text)) {
                result.add(group);
            }
        }
        return result;
    }
}