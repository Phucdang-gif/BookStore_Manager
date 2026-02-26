package config;

import DTO.AccountDTO;
import java.util.HashMap;

public class SessionManager {
    private static AccountDTO currentAccount = null;
    
    // Khai báo một HashMap tĩnh để lưu quyền trong suốt phiên làm việc
    private static HashMap<Integer, String> currentPermissions = new HashMap<>();

    // Nâng cấp hàm đăng nhập: Lưu cả Tài khoản lẫn Danh sách quyền
    public static void login(AccountDTO account, HashMap<Integer, String> permissions) {
        currentAccount = account;
        currentPermissions = permissions;
    }

    public static AccountDTO getCurrentAccount() {
        return currentAccount;
    }

    // TẠO HÀM CHECK QUYỀN ĐA NĂNG TRÊN RAM (CỰC NHANH)
    public static boolean hasPermission(int functionId, String actionName) {
        // Nếu chức năng này có tồn tại trong Map của tài khoản
        if (currentPermissions.containsKey(functionId)) {
            String actions = currentPermissions.get(functionId);
            // Kiểm tra xem chuỗi có chứa hành động (Xem/Thêm/Sửa/Xóa) không
            return actions != null && actions.contains(actionName);
        }
        return false; // Nếu không có mã chức năng trong Map -> Mặc định là không có quyền
    }

    public static void logout() {
        currentAccount = null;
        currentPermissions.clear(); // Xóa sạch quyền khi đăng xuất
    }
}