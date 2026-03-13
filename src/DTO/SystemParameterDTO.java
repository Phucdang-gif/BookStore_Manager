package DTO;

public class SystemParameterDTO {
    private String parameterCode;
    private String parameterValue;
    private String description;

    public SystemParameterDTO() {
    }

    public SystemParameterDTO(String parameterCode, String parameterValue, String description) {
        this.parameterCode = parameterCode;
        this.parameterValue = parameterValue;
        this.description = description;
    }

    public String getParameterCode() {
        return parameterCode;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public String getDescription() {
        return description;
    }

    public void setParameterCode(String parameterCode) {
        this.parameterCode = parameterCode;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Tiện ích: trả về value đã ép kiểu
    public int getValueAsInt() {
        return Integer.parseInt(parameterValue.trim());
    }

    public double getValueAsDouble() {
        return Double.parseDouble(parameterValue.trim());
    }

    @Override
    public String toString() {
        return parameterCode + " = " + parameterValue + " (" + description + ")";
    }
}