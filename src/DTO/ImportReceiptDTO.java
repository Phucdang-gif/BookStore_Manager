package DTO;

import java.sql.Timestamp;

public class ImportReceiptDTO {
    private int receiptId;      // receipt_id
    private int supplierId;     // supplier_id
    private int employeeId;     // employee_id
    private Timestamp receiptDate; // receipt_date
    private double totalAmount; // total_amount
    private String status;      // status
    private String note;        // note

    public ImportReceiptDTO() {}

    public ImportReceiptDTO(int receiptId, int supplierId, int employeeId, Timestamp receiptDate, double totalAmount, String status, String note) {
        this.receiptId = receiptId;
        this.supplierId = supplierId;
        this.employeeId = employeeId;
        this.receiptDate = receiptDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.note = note;
    }

    // GETTERS & SETTERS
    public int getReceiptId() { return receiptId; }
    public void setReceiptId(int receiptId) { this.receiptId = receiptId; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Timestamp getReceiptDate() { return receiptDate; }
    public void setReceiptDate(Timestamp receiptDate) { this.receiptDate = receiptDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
