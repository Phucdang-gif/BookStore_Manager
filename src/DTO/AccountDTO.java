package DTO;

import java.sql.Timestamp;

public class AccountDTO {
    // 1. Các thuộc tính chuẩn (Khớp 100% với ảnh bảng TAI_KHOAN)
    private int accountId;      // ma_tai_khoan
    private int employeeId;     // ma_nhan_vien (Khóa ngoại để liên kết sang EmployeeDTO)
    private int roleId;         // ma_nhom_quyen
    private String username;    // ten_dang_nhap
    private String password;    // mat_khau
    private String status;      // trang_thai
    private Timestamp createDate; // ngay_tao (datetime -> Timestamp)

    // 2. Constructor
    public AccountDTO() {}

    public AccountDTO(int accountId, int employeeId, int roleId, String username, String password, String status, Timestamp createDate) {
        this.accountId = accountId;
        this.employeeId = employeeId;
        this.roleId = roleId;
        this.username = username;
        this.password = password;
        this.status = status;
        this.createDate = createDate;
    }

    // 3. Getter & Setter
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreateDate() { return createDate; }
    public void setCreateDate(Timestamp createDate) { this.createDate = createDate; }
}
