package DTO;

import java.sql.Timestamp;

public class DiscountServiceDTO {
    private int serviceId;
    private String serviceName;
    private String discountType; // 'percent' (phần trăm) hoặc 'amount' (số tiền)
    private double discountValue; 
    private double minimumAmount; 
    private double maximumDiscount; 
    private Timestamp startDate;
    private Timestamp endDate;
    private String status; // 'active' hoặc 'inactive'
    private String description;

    public DiscountServiceDTO() {}

    public DiscountServiceDTO(int serviceId, String serviceName, String discountType, double discountValue, double minimumAmount, double maximumDiscount, Timestamp startDate, Timestamp endDate, String status, String description) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumAmount = minimumAmount;
        this.maximumDiscount = maximumDiscount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.description = description;
    }

    // --- GETTERS & SETTERS ---
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }

    public double getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(double minimumAmount) { this.minimumAmount = minimumAmount; }

    public double getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(double maximumDiscount) { this.maximumDiscount = maximumDiscount; }

    public Timestamp getStartDate() { return startDate; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }

    public Timestamp getEndDate() { return endDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
   
    @Override
    public String toString() {
        // Chỉ cần hiển thị Tên chương trình khuyến mãi
        return this.serviceName; 
    }
}