package BUS;

import DAO.AccountDAO;
import DAO.RoleDAO; // Nếu chưa có file này, em tạo thêm để thay thế NhomQuyenDAO nhé
import DTO.AccountDTO;
import DTO.RoleDTO;
import java.util.ArrayList;

public class AccountBUS {

    // Khai báo các DAO để giao tiếp CSDL
    private AccountDAO accountDAO = new AccountDAO();
    private RoleDAO roleDAO = new RoleDAO(); 

    // Mảng lưu bộ đệm (Cache) để truy xuất trên giao diện cực nhanh
    private ArrayList<AccountDTO> listAccount;
    private ArrayList<RoleDTO> listRole;

    // ==========================================================
    // 1. CONSTRUCTOR: Khởi tạo và nạp dữ liệu từ DB lên RAM
    // ==========================================================
    public AccountBUS() {
        this.listAccount = accountDAO.selectAll();
        this.listRole = roleDAO.getAll();
    }

    // ==========================================================
    // 2. CÁC HÀM LẤY DỮ LIỆU (READ)
    // ==========================================================
    
    public ArrayList<AccountDTO> getAll() {
        return this.listAccount;
    }
    
    public AccountDTO getByIndex(int index) {
        return this.listAccount.get(index);
    }

    // Thay thế hàm getTaiKhoanByMaNV
    public int getAccountIndexByEmployeeId(int employeeId) {
        int i = 0;
        int index = -1;
        while (i < this.listAccount.size() && index == -1) {
            // Lưu ý: Đảm bảo AccountDTO của em có trường employeeId và hàm getEmployeeId()
            if (this.listAccount.get(i).getEmployeeId() == employeeId) {
                index = i;
            } else {
                i++;
            }
        }
        return index;
    }
    
    // Thay thế getNhomQuyenDTO
    public RoleDTO getRoleDTO(int roleId) {
        return roleDAO.getById(roleId);
    }

    // ==========================================================
    // 3. CÁC HÀM THAO TÁC (CREATE, UPDATE, DELETE)
    // ==========================================================
    
    // Thay thế addAcc (Đã fix lỗi không lưu xuống DB)
    public boolean addAccount(AccountDTO acc) {
        boolean success = accountDAO.insert(acc);
        if (success) {
            this.listAccount.add(acc); // Lưu xuống DB thành công mới nhét vào danh sách trên RAM
        }
        return success;
    }
    
    // Thay thế updateAcc (Đã fix lỗi không lưu xuống DB)
    public boolean updateAccount(int index, AccountDTO acc) {
        boolean success = accountDAO.update(acc);
        if (success) {
            this.listAccount.set(index, acc); // Cập nhật trên RAM
        }
        return success;
    }

    // Logic xóa an toàn em vừa yêu cầu
    public String deleteAccount(int accountIdToDelete, int currentLoggedInAccountId) {
        // LOGIC NGHIỆP VỤ: Chống tự xóa chính mình
        if (accountIdToDelete == currentLoggedInAccountId) {
            return "Lỗi: Bạn không thể tự xóa tài khoản của chính mình!";
        }

        // Nếu qua được vòng kiểm tra -> Gọi DAO để xóa
        boolean success = accountDAO.delete(accountIdToDelete);
        
        if (success) {
            // Xóa xong dưới DB thì cũng phải xóa trong cái danh sách trên RAM
            this.listAccount.removeIf(acc -> acc.getAccountId() == accountIdToDelete);
            return "Xóa tài khoản thành công!";
        } else {
            return "Lỗi: Không thể xóa tài khoản này khỏi hệ thống!";
        }
    }

    // ==========================================================
    // 4. CÁC HÀM TIỆN ÍCH (LOGIN, ĐỔI MẬT KHẨU, TÌM KIẾM)
    // ==========================================================

    public AccountDTO login(String username, String password) {
        return accountDAO.login(username, password);
    }
    
    public boolean changePassword(int accountId, String newPassword) {
        return accountDAO.changePassword(accountId, newPassword);
    }
    
    public boolean updateStatus(int accountId, String newStatus) {
        boolean success = accountDAO.updateStatus(accountId, newStatus);
        if(success) {
            // Cập nhật lại list trên RAM (gọi lại DB cho an toàn hoặc tự update field)
            this.listAccount = accountDAO.selectAll();
        }
        return success;
    }

    // Thay thế hàm search
    public ArrayList<AccountDTO> search(String txt, String type) {
        ArrayList<AccountDTO> result = new ArrayList<>();
        txt = txt.toLowerCase();
        
        switch (type) {
            case "Tất cả":
                for (AccountDTO acc : listAccount) {
                    if (Integer.toString(acc.getEmployeeId()).contains(txt) || 
                        acc.getUsername().toLowerCase().contains(txt)) {
                        result.add(acc);
                    }
                }
                break;
                
            case "Mã nhân viên":
                for (AccountDTO acc : listAccount) {
                    if (Integer.toString(acc.getEmployeeId()).contains(txt)) {
                        result.add(acc);
                    }
                }
                break;
                
            case "Username":
                for (AccountDTO acc : listAccount) {
                    if (acc.getUsername().toLowerCase().contains(txt)) {
                        result.add(acc);
                    }
                }
                break;
        }
        return result;
    }
}