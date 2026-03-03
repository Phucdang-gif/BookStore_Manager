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
}