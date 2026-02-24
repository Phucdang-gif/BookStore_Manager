package DTO;

import java.sql.Date;

public class CustomerDTO {
    private int customerId;
    private String fullName;
    private String phone;
    private int loyaltyPoints;
    private Date registrationDate;

    public CustomerDTO() {}

    public CustomerDTO(int customerId, String fullName, String phone, int loyaltyPoints, Date registrationDate) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.loyaltyPoints = loyaltyPoints;
        this.registrationDate = registrationDate;
    }

    // --- GETTERS & SETTERS ---
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    @Override
    public String toString() {
        // Nếu không có số điện thoại (ví dụ: Khách vãng lai), chỉ hiện tên
        if (this.phone == null || this.phone.isEmpty()) {
            return this.fullName; 
        }
        // Nếu là khách hàng bình thường, hiện "Tên - Số điện thoại" cho dễ chọn
        return this.fullName + " - " + this.phone;
    }
}