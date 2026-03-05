package BUS;

import DAO.AccountDAO;
import DAO.PermissionGroupDAO;
import DTO.AccountDTO;
import DTO.PermissionGroupDTO;
import DTO.ValidationResult;
import java.util.ArrayList;

public class AccountBUS {

    private AccountDAO accountDAO = new AccountDAO();
    private PermissionGroupDAO permissionGroupDAO = new PermissionGroupDAO();

    private ArrayList<AccountDTO> listAccount;

    public AccountBUS() {
        this.listAccount = accountDAO.selectAll();
    }

    public ArrayList<AccountDTO> getAll() {
        return this.listAccount;
    }

    public AccountDTO getByIndex(int index) {
        return this.listAccount.get(index);
    }

    public void refreshData() {
        this.listAccount = accountDAO.selectAll();
    }

    public PermissionGroupDTO getPermissionGroupDTO(int permissionGroupId) {
        return permissionGroupDAO.getById(permissionGroupId);
    }

    public int getAccountIndexByEmployeeId(int employeeId) {
        int i = 0;
        int index = -1;
        while (i < this.listAccount.size() && index == -1) {
            if (this.listAccount.get(i).getEmployeeId() == employeeId) {
                index = i;
            } else {
                i++;
            }
        }
        return index;
    }

    public String getFullNameByEmployeeId(int employeeId) {
        return accountDAO.getFullNameByEmployeeId(employeeId);
    }

    public ValidationResult addAccount(AccountDTO acc) {
        ValidationResult vr = Validator.validateAccount(acc, this.listAccount);
        if (!vr.isValid())
            return vr;

        if (accountDAO.insert(acc)) {
            this.listAccount = accountDAO.selectAll();
        } else {
            vr.addError("system", "Lỗi hệ thống: Không thể thêm tài khoản vào CSDL");
        }
        return vr;
    }

    public ValidationResult updateAccount(AccountDTO acc) {
        ValidationResult vr = Validator.validateAccount(acc, this.listAccount);
        if (!vr.isValid())
            return vr;

        if (accountDAO.update(acc)) {
            // Update lại list RAM
            for (int i = 0; i < listAccount.size(); i++) {
                if (listAccount.get(i).getAccountId() == acc.getAccountId()) {
                    listAccount.set(i, acc);
                    break;
                }
            }
            this.listAccount = accountDAO.selectAll();
        } else {
            vr.addError("system", "Lỗi hệ thống: Cập nhật thất bại");
        }
        return vr;
    }

    public ValidationResult deleteAccount(int accountIdToDelete, int currentLoggedInAccountId) {
        ValidationResult vr = new ValidationResult();

        if (accountIdToDelete == currentLoggedInAccountId) {
            vr.addError("accountId", "Bạn không thể tự xóa tài khoản của chính mình!");
            return vr;
        }

        boolean success = accountDAO.delete(accountIdToDelete);
        if (success) {
            this.listAccount.removeIf(acc -> acc.getAccountId() == accountIdToDelete);
        } else {
            vr.addError("system", "Lỗi: Không thể xóa tài khoản này (Có thể đang có dữ liệu liên quan)!");
        }
        return vr;
    }

    // =========================================================

    public AccountDTO login(String username, String password) {
        return accountDAO.login(username, password);
    }

    public boolean changePassword(int accountId, String newPassword) {
        return accountDAO.changePassword(accountId, newPassword);
    }

    public boolean checkDuplicateUsername(String username) {
        return accountDAO.isUsernameExists(username);
    }

    public AccountDTO checkLogin(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        return accountDAO.checkLogin(username, password);
    }

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