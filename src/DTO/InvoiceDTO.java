package DTO;

import java.sql.Timestamp;

public class InvoiceDTO {
    private int invoiceId;
    private int customerId;
    private int employeeId;
    private Timestamp createdAt;
    private double totalAmount;
    private double totalDiscount;
    private int pointsUsed;
    private double pointsValue;
    private double finalAmount;
    private String paymentMethod; // VD: Tiền mặt, Chuyển khoản, Thẻ
    private String status;        // VD: Completed, Cancelled
    private int pointsEarned;

    public InvoiceDTO() {}

    public InvoiceDTO(int invoiceId, int customerId, int employeeId, Timestamp createdAt, double totalAmount, double totalDiscount, int pointsUsed, double pointsValue, double finalAmount, String paymentMethod, String status, int pointsEarned) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.pointsUsed = pointsUsed;
        this.pointsValue = pointsValue;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.pointsEarned = pointsEarned;
    }

    // --- GETTERS & SETTERS ---
    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(double totalDiscount) { this.totalDiscount = totalDiscount; }

    public int getPointsUsed() { return pointsUsed; }
    public void setPointsUsed(int pointsUsed) { this.pointsUsed = pointsUsed; }

    public double getPointsValue() { return pointsValue; }
    public void setPointsValue(double pointsValue) { this.pointsValue = pointsValue; }

    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
}