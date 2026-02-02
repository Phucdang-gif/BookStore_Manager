package DTO;

import java.sql.Timestamp;

public class InvoiceDTO {
    private int invoiceId;
    private int customerId; // Có thể để 0 hoặc null nếu là khách vãng lai
    private int employeeId;
    private Timestamp createdAt;
    private double totalAmount; // Tổng tiền hàng
    private double totalDiscount; // Giảm giá (mặc định 0)
    private double finalAmount; // Tiền khách phải trả (Total - Discount)
    private String paymentMethod; // "CASH", "TRANSFER"...
    private String status; // "PAID", "PENDING"

    // Constructor mặc định
    public InvoiceDTO() {
    }

    // Constructor đầy đủ
    public InvoiceDTO(int invoiceId, int customerId, int employeeId, Timestamp createdAt,
            double totalAmount, double totalDiscount, double finalAmount,
            String paymentMethod, String status) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getter & Setter (Bạn tự sinh code getter/setter trong IDE nhé)
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}