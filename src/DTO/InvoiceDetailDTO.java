package DTO;

public class InvoiceDetailDTO {
    private int detailId;
    private int invoiceId;
    private int bookId;
    private int quantity;
    private double unitPrice;
    private double discount;
    private double subtotal;

    public InvoiceDetailDTO() {}

    public InvoiceDetailDTO(int detailId, int invoiceId, int bookId, int quantity, double unitPrice, double discount, double subtotal) {
        this.detailId = detailId;
        this.invoiceId = invoiceId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.subtotal = subtotal;
    }

    // --- GETTERS & SETTERS ---
    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
