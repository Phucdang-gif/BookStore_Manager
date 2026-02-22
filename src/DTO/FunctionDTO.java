package DTO;

public class FunctionDTO {
    private int functionId;
    private String functionName;
    private String systemFunctionCode; // Khớp với system_function_code
    private String functionGroup;
    
    public FunctionDTO() {}

    public FunctionDTO(int functionId, String functionName, String systemFunctionCode, String functionGroup) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.systemFunctionCode = systemFunctionCode;
        this.functionGroup = functionGroup;
    }

    public int getFunctionId() { return functionId; }
    public void setFunctionId(int functionId) { this.functionId = functionId; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public String getSystemFunctionCode() { return systemFunctionCode; }
    public void setSystemFunctionCode(String systemFunctionCode) { this.systemFunctionCode = systemFunctionCode; }
    
    public String getFunctionGroup() { return functionGroup; }
    public void setFunctionGroup(String functionGroup) { this.functionGroup = functionGroup; }

    @Override
    public String toString() {
        return functionName; 
    }
}