package DTO;

import java.sql.Timestamp;

public class AccountDTO {
    private int accountId;      
    private int employeeId;     
    private int permissionGroupId; // Đổi từ roleId
    private String username;    
    private String password;    
    private String status;      
    private Timestamp createdAt;   // Đổi cho giống DB

    public AccountDTO() {}

    public AccountDTO(int accountId, int employeeId, int permissionGroupId, String username, String password, String status, Timestamp createdAt) {
        this.accountId = accountId;
        this.employeeId = employeeId;
        this.permissionGroupId = permissionGroupId;
        this.username = username;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getPermissionGroupId() { return permissionGroupId; }
    public void setPermissionGroupId(int permissionGroupId) { this.permissionGroupId = permissionGroupId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}