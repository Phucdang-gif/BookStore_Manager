package BUS;

import DAO.PermissionGroupDAO;
import DTO.PermissionGroupDTO;
import DTO.ValidationResult;
import config.SessionManager;
import DAO.AccountDAO;
import java.util.ArrayList;

public class PermissionGroupBUS {

    private PermissionGroupDAO permissionGroupDAO = new PermissionGroupDAO();
    private AccountDAO accountDAO = new AccountDAO(); // Thêm DAO để kiểm tra phụ thuộc tài khoản

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

    public ValidationResult deleteGroup(int groupId) {
        ValidationResult vr = new ValidationResult();

        // 1. Kiểm tra xem Nhóm quyền này có phải là nhóm Quản trị tối cao không (Tránh
        // việc tự xóa nhóm Admin)
        if (groupId == 151||groupId==config.SessionManager.getCurrentAccount().getPermissionGroupId()) {
            vr.addError("groupId", "Lỗi: Không được phép xóa Nhóm quyền Quản trị viên mặc định và Nhóm quyền của bạn!");
            return vr;
        }

        // 2. Đếm số lượng tài khoản đang phụ thuộc
        int accountCount = accountDAO.countAccountsByGroupId(groupId);

        // 3. Nếu đang có người dùng -> CHẶN LẠI NGAY LẬP TỨC
        if (accountCount > 0) {
            vr.addError("groupId", "Từ chối xóa: Đang có " + accountCount
                    + " tài khoản sử dụng Nhóm quyền này!\nVui lòng chuyển các tài khoản đó sang nhóm khác trước.");
            return vr;
        }

        // 4. Nếu an toàn (count == 0), tiến hành XÓA CỨNG
        boolean isSuccess = permissionGroupDAO.delete(groupId);
        if (!isSuccess) {
            vr.addError("system", "Lỗi CSDL: Không thể xóa nhóm quyền này do lỗi hệ thống!");
        }

        return vr;
    }

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

    public ArrayList<PermissionGroupDTO> refreshData() {
        return permissionGroupDAO.getAll();
    }
}