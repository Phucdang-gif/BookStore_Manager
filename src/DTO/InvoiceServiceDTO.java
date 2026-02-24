package DTO;

public class InvoiceServiceDTO {
    private int invoiceServiceId;
    private int invoiceId;
    private int serviceId;
    private String serviceType;
    private double discountValue;
    private String description;

    // Constructor rỗng
    public InvoiceServiceDTO() {}

    // Constructor đầy đủ
    public InvoiceServiceDTO(int invoiceServiceId, int invoiceId, int serviceId, String serviceType, double discountValue, String description) {
        this.invoiceServiceId = invoiceServiceId;
        this.invoiceId = invoiceId;
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.discountValue = discountValue;
        this.description = description;
    }

    // --- GETTERS & SETTERS ---
    public int getInvoiceServiceId() {
        return invoiceServiceId;
    }

    public void setInvoiceServiceId(int invoiceServiceId) {
        this.invoiceServiceId = invoiceServiceId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
