package DTO;

import java.sql.Timestamp;

public class PermissionGroupDTO {
    private int permissionGroupId; // ma_nhom_quyen
    private String groupName;      // ten_nhom
    private String status;         // trang_thai
    private Timestamp createdAt;   // ngay_tao

    public PermissionGroupDTO() {}

    public PermissionGroupDTO(int permissionGroupId, String groupName, String status, Timestamp createdAt) {
        this.permissionGroupId = permissionGroupId;
        this.groupName = groupName;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor rút gọn dùng cho giao diện
    public PermissionGroupDTO(int permissionGroupId, String groupName) {
        this.permissionGroupId = permissionGroupId;
        this.groupName = groupName;
    }

    public int getPermissionGroupId() { return permissionGroupId; }
    public void setPermissionGroupId(int permissionGroupId) { this.permissionGroupId = permissionGroupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return groupName; // Để hiển thị đẹp trong ComboBox
    }
}