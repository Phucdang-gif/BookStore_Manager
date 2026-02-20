package DTO;

import java.sql.Timestamp;

public class PermissionDetailDTO {
    // ==========================================================
    // 1. CÁC THUỘC TÍNH (Chuẩn 5 cột trong ảnh ERD)
    // ==========================================================
    
    // Cột 1: ma_chi_tiet (PK - Int)
    private int detailId;
    
    // Cột 2: ma_nhom_quyen (FK - Int)
    private int roleId;
    
    // Cột 3: ma_chuc_nang (FK - Int)
    private int functionId;
    
    // Cột 4: hanhdong (String - VD: "view,create")
    private String actions;
    
    // Cột 5: ngay_gan (Datetime -> Timestamp)
    private Timestamp assignedDate;

    // ==========================================================
    // 2. THUỘC TÍNH MỞ RỘNG (Bắt buộc phải có để hiện lên giao diện)
    // ==========================================================
    // Lưu ý: 2 cái này KHÔNG có trong bảng, nhưng khi code Java chạy câu lệnh 
    // "SELECT ... JOIN CHUC_NANG", ta cần chỗ để hứng tên chức năng bỏ vào.
    
    private String systemCode;      // Hứng cột: ma_chuc_nang_he_thong
    private String functionName;    // Hứng cột: ten_chuc_nang

    // ==========================================================
    // 3. CONSTRUCTOR
    // ==========================================================

    public PermissionDetailDTO() {}

    // Constructor đầy đủ 5 cột (Dùng khi thêm mới/lấy dữ liệu thô)
    public PermissionDetailDTO(int detailId, int roleId, int functionId, String actions, Timestamp assignedDate) {
        this.detailId = detailId;
        this.roleId = roleId;
        this.functionId = functionId;
        this.actions = actions;
        this.assignedDate = assignedDate;
    }

    // Constructor dùng để hiển thị lên bảng phân quyền (Có kèm tên chức năng)
    public PermissionDetailDTO(int functionId, String systemCode, String functionName, String actions) {
        this.functionId = functionId;
        this.systemCode = systemCode;
        this.functionName = functionName;
        this.actions = actions;
    }

    // ==========================================================
    // 4. GETTER & SETTER
    // ==========================================================

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public int getFunctionId() { return functionId; }
    public void setFunctionId(int functionId) { this.functionId = functionId; }

    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }

    public Timestamp getAssignedDate() { return assignedDate; }
    public void setAssignedDate(Timestamp assignedDate) { this.assignedDate = assignedDate; }

    // Getter/Setter cho thuộc tính mở rộng
    public String getSystemCode() { return systemCode; }
    public void setSystemCode(String systemCode) { this.systemCode = systemCode; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }
    

    public boolean hasAction(String actionToCheck) {
        if (this.actions == null || this.actions.isEmpty()) {
            return false;
        }
        return this.actions.contains(actionToCheck);
    }
}