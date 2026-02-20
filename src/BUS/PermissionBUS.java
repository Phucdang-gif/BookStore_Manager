package BUS;

import DAO.PermissionDAO;
import DTO.PermissionDetailDTO;
import java.util.ArrayList;

public class PermissionBUS {
    
    private final PermissionDAO permissionDAO = new PermissionDAO();

    // 1. LẤY DANH SÁCH QUYỀN
    public ArrayList<PermissionDetailDTO> getPermissionsByRoleId(int roleId) {
        return permissionDAO.getPermissionsByRoleId(roleId);
    }

   // BÊN TRONG FILE PermissionBUS.java
public boolean checkPermission(int roleId, String functionCode, String action) {
    ArrayList<PermissionDetailDTO> permissions = this.getPermissionsByRoleId(roleId);
    boolean hasPermission = false;
    int i = 0;
    
    while (i < permissions.size() && !hasPermission) {
        // Cập nhật lại tên hàm Getter cho khớp với DTO mới của em
        if (permissions.get(i).getSystemCode().equals(functionCode) && 
            permissions.get(i).hasAction(action)) { 
            hasPermission = true;
        } else {
            i++;
        }
    }
    return hasPermission;
}

    // 3. LƯU QUYỀN MỚI (Đã gỡ bỏ logic chặn cứng Admin)
    public boolean savePermissions(int roleId, ArrayList<PermissionDetailDTO> permissions) {
        if (permissions == null) return false;

        // Lưu trực tiếp những gì giao diện gửi xuống
        // Nếu Admin cố tình gỡ quyền của chính nhóm mình thì họ phải tự chịu trách nhiệm
        return permissionDAO.savePermissions(roleId, permissions);
    }
}