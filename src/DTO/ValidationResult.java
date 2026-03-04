package DTO;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationResult {
    private final Map<String, String> errors = new LinkedHashMap<>();

    public void addError(String field, String message) {
        errors.put(field, message);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getError(String field) {
        return errors.getOrDefault(field, null);
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        errors.values().forEach(msg -> sb.append("• ").append(msg).append("\n"));
        return sb.toString().trim();
    }

    public boolean showAlert(java.awt.Component parentComponent) {
        if (isValid()) {
            return true; // Dữ liệu đúng, cho phép đi tiếp
        } else {
            // Dữ liệu sai, hiện thông báo lỗi
            javax.swing.JOptionPane.showMessageDialog(
                    parentComponent,
                    this.getSummary(), // Lấy toàn bộ lỗi
                    "Thông báo lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false; // Dữ liệu sai, báo hiệu dừng lại
        }
    }
}