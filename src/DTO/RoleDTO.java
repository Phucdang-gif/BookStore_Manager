package DTO;
import java.sql.Timestamp;
public class RoleDTO {
    private int roleId;
    private String roleName;
    private String status;
    private Timestamp createDate;
    private String description; // Mô tả nhóm quyền (Nếu có trong DB)
     public RoleDTO() {}

    public RoleDTO(int roleId, String roleName, String status, Timestamp createDate, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.status = status;
        this.createDate = createDate;
        this.description = description;
    }

    // Getter & Setter
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreateDate() { return createDate; }
    public void setCreateDate(Timestamp createDate) { this.createDate = createDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    @Override
    public String toString() {
        return roleName; // Để hiển thị trong ComboBox
    }
}
