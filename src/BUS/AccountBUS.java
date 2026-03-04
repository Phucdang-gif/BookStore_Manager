package BUS;

import DAO.AccountDAO;
import DAO.PermissionGroupDAO; 
import DTO.AccountDTO;
import DTO.PermissionGroupDTO;
import java.util.ArrayList;

public class AccountBUS {

    private AccountDAO accountDAO = new AccountDAO();
    private PermissionGroupDAO permissionGroupDAO = new PermissionGroupDAO(); 

    private ArrayList<AccountDTO> listAccount;
    private ArrayList<PermissionGroupDTO> listPermissionGroup;

    public AccountBUS() {
        this.listAccount = accountDAO.selectAll();
        this.listPermissionGroup = permissionGroupDAO.getAll();
    }

    public ArrayList<AccountDTO> getAll() {
        return this.listAccount;
    }
    
    public AccountDTO getByIndex(int index) {
        return this.listAccount.get(index);
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
    
    
    public PermissionGroupDTO getPermissionGroupDTO(int permissionGroupId) {
        return permissionGroupDAO.getById(permissionGroupId);
    }

    public boolean addAccount(AccountDTO acc) {
        boolean success = accountDAO.insert(acc);
        if (success) {
            this.listAccount.add(acc); 
        }
        return success;
    }
    
    public boolean updateAccount(int index, AccountDTO acc) {
        boolean success = accountDAO.update(acc);
        if (success) {
            this.listAccount.set(index, acc); 
        }
        return success;
    }

    public String deleteAccount(int accountIdToDelete, int currentLoggedInAccountId) {
        if (accountIdToDelete == currentLoggedInAccountId) {
            return "Lỗi: Bạn không thể tự xóa tài khoản của chính mình!";
        }
        boolean success = accountDAO.delete(accountIdToDelete);
        if (success) {
            this.listAccount.removeIf(acc -> acc.getAccountId() == accountIdToDelete);
            return "Xóa tài khoản thành công!";
        } else {
            return "Lỗi: Không thể xóa tài khoản này khỏi hệ thống!";
        }
    }

    public AccountDTO login(String username, String password) {
        return accountDAO.login(username, password);
    }
    
    public boolean changePassword(int accountId, String newPassword) {
        return accountDAO.changePassword(accountId, newPassword);
    }
    
    public boolean updateStatus(int accountId, String newStatus) {
        boolean success = accountDAO.updateStatus(accountId, newStatus);
        if(success) {
            this.listAccount = accountDAO.selectAll();
        }
        return success;
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
    public boolean checkDuplicateUsername(String username) {
    return accountDAO.isUsernameExists(username);
}

    public AccountDTO checkLogin(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        return accountDAO.checkLogin(username, password);
    }
}