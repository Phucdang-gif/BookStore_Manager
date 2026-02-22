package DTO;

public class PermissionDetailDTO {
    // 1. CÁC THUỘC TÍNH (Chuẩn 4 cột trong ERD bảng permission_details)
    private int detailId;
    private int permissionGroupId; // Đổi từ roleId
    private int functionId;
    private String actions;

    // 2. THUỘC TÍNH MỞ RỘNG (Hứng dữ liệu JOIN để hiện lên bảng)
    private String systemCode;      
    private String functionName;    

    // 3. CONSTRUCTORS
    public PermissionDetailDTO() {}

    // Constructor đầy đủ cho Database
    public PermissionDetailDTO(int detailId, int permissionGroupId, int functionId, String actions) {
        this.detailId = detailId;
        this.permissionGroupId = permissionGroupId;
        this.functionId = functionId;
        this.actions = actions;
    }

    // Constructor hứng dữ liệu từ Giao diện (PermissionDialog) đẩy xuống
    public PermissionDetailDTO(int permissionGroupId, String systemCode, String actions) {
        this.permissionGroupId = permissionGroupId;
        this.systemCode = systemCode;
        this.actions = actions;
    }

    // Constructor hiển thị bảng phân quyền
    public PermissionDetailDTO(int functionId, String systemCode, String functionName, String actions) {
        this.functionId = functionId;
        this.systemCode = systemCode;
        this.functionName = functionName;
        this.actions = actions;
    }

    // 4. GETTER & SETTER
    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getPermissionGroupId() { return permissionGroupId; }
    public void setPermissionGroupId(int permissionGroupId) { this.permissionGroupId = permissionGroupId; }

    public int getFunctionId() { return functionId; }
    public void setFunctionId(int functionId) { this.functionId = functionId; }

    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }

    public String getSystemCode() { return systemCode; }
    public void setSystemCode(String systemCode) { this.systemCode = systemCode; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public boolean hasAction(String actionToCheck) {
        if (this.actions == null || this.actions.isEmpty()) return false;
        return this.actions.contains(actionToCheck);
    }
}