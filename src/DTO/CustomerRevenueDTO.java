package DTO;

public class CustomerRevenueDTO {
    private int customerID;
    private String fullname;
    private int ordinalnumber;
    private double totalamount;
    private int totalinvoices;
    public CustomerRevenueDTO(){}
    public int getCustomerID() {
        return customerID;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public int getOrdinalnumber() {
        return ordinalnumber;
    }
    public void setOrdinalnumber(int ordinalnumber) {
        this.ordinalnumber = ordinalnumber;
    }
    public double getTotalamount() {
        return totalamount;
    }
    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }
    public void setTotalinvoices(int totalinvoices) {
        this.totalinvoices = totalinvoices;
    }
    public int getTotalinvoices() {
        return totalinvoices;
    }
    
    
}
