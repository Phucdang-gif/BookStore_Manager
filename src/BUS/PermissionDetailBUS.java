package BUS;

import DAO.PermissionDetailDAO;
import DTO.PermissionDetailDTO;
import java.util.ArrayList;
import java.util.HashMap;

public class PermissionDetailBUS {
    
    private final PermissionDetailDAO permissionDetailDAO = new PermissionDetailDAO();
    public HashMap<Integer, String> getAllPermissionsByGroupId(int groupId) {
        ArrayList<PermissionDetailDTO> list = permissionDetailDAO.getPermissionsByGroupId(groupId);
        HashMap<Integer, String> permissionsMap = new HashMap<>();
        for (PermissionDetailDTO dto : list) {
            permissionsMap.put(dto.getFunctionId(), dto.getActions());
        }
        return permissionsMap;
    }
    // 1. LẤY DANH SÁCH QUYỀN
    public ArrayList<PermissionDetailDTO> getPermissionsByGroupId(int groupId) {
        return permissionDetailDAO.getPermissionsByGroupId(groupId);
    }

    // 2. KIỂM TRA QUYỀN
    public boolean checkPermission(int groupId, String functionCode, String action) {
        ArrayList<PermissionDetailDTO> permissions = this.getPermissionsByGroupId(groupId);
        boolean hasPermission = false;
        int i = 0;
        
        while (i < permissions.size() && !hasPermission) {
            if (permissions.get(i).getSystemCode().equals(functionCode) && 
                permissions.get(i).hasAction(action)) { 
                hasPermission = true;
            } else {
                i++;
            }
        }
        return hasPermission;
    }

    // 3. LƯU QUYỀN MỚI
    public boolean saveAllPermissions(int groupId, ArrayList<PermissionDetailDTO> permissions) {
        if (permissions == null) return false;
        return permissionDetailDAO.savePermissions(groupId, permissions);
    }
    public boolean checkActionPermission(int groupId, String moduleCode, String actionColumn) {
        return permissionDetailDAO.checkActionPermission(groupId, moduleCode, actionColumn);
    }
}