package DTO;

public class FunctionDTO {
    // 1. ma_chuc_nang (PK) - Dùng để lưu xuống DB khi tick chọn
    private int functionId;
    
    // 2. ten_chuc_nang - Dùng để hiển thị lên màn hình (VD: "Quản lý sách")
    private String functionName;
    
    // 3. ma_chuc_nang_he_thong - Dùng để code nhận diện (VD: "BOOK_MANAGE")
    private String systemCode;
    
    // 4. nhom_chuc_nang (Nếu có trong DB) - Để gom nhóm (VD: "Kho", "Bán hàng")
    // Trong ảnh sơ đồ của bạn chưa thấy cột này, nhưng nếu có thì thêm vào.
    private String functionGroup;
    public FunctionDTO() {}

    public FunctionDTO(int functionId, String functionName, String systemCode, String functionGroup) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.systemCode = systemCode;
        this.functionGroup = functionGroup;
    }

    // Getter & Setter
    public int getFunctionId() { return functionId; }
    public void setFunctionId(int functionId) { this.functionId = functionId; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public String getSystemCode() { return systemCode; }
    public void setSystemCode(String systemCode) { this.systemCode = systemCode; }
    
    public String getFunctionGroup() { return functionGroup; }
    public void setFunctionGroup(String functionGroup) { this.functionGroup = functionGroup; }
    @Override
    public String toString() {
        return functionName; // Để hiển thị trên ComboBox hoặc Log
    }
}
