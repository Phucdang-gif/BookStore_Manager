package DTO;

public class SupplierDTO {
    private int supplierId;
    private String supplierName;
    private String phone;
    private String status;

    public SupplierDTO() {}

    public SupplierDTO(int supplierId, String supplierName, String phone, String status) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.phone = phone;
        this.status = status;
    }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // HÀM NÀY RẤT QUAN TRỌNG ĐỂ HIỂN THỊ TRÊN COMBOBOX
    @Override
    public String toString() {
        return supplierName + " - " + phone; 
    }
}