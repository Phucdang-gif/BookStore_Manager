package DTO;

public class InvoiceDetailDTO {
    private int detailId;
    private int invoiceId;
    private int bookId;
    private String bookTitle;
    private int quantity; // số lượng
    private double unitPrice; // Giá bán tại thời điểm mua // đơn giá
    private double subtotal; // quantity * unitPrice // thành tiền

    public InvoiceDetailDTO() {
    }

    public InvoiceDetailDTO(int invoiceId, int bookId, String bookTitle, int quantity, double unitPrice) {
        this.invoiceId = invoiceId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice; // Tự động tính
    }

    // Getter & Setter
    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.quantity * this.unitPrice; // Cập nhật lại thành tiền khi đổi số lượng
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}