package DTO;

public class ImportReceiptDetailDTO {
    private int receiptId;  // receipt_id
    private int bookId;     // book_id
    private int quantity;   // quantity
    private double unitPrice; // unit_price
    private double subtotal;  // subtotal

    public ImportReceiptDetailDTO() {}

    public ImportReceiptDetailDTO(int receiptId, int bookId, int quantity, double unitPrice, double subtotal) {
        this.receiptId = receiptId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // GETTERS & SETTERS
    public int getReceiptId() { return receiptId; }
    public void setReceiptId(int receiptId) { this.receiptId = receiptId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}