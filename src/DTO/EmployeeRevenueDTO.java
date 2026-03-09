package DTO;

public class EmployeeRevenueDTO {
    private int EmployeeID;
    private String fullname;
    private int ordinalnumber;
    private int totalInvoice;
    private Double totalRevenue;
    //constructor 
    public EmployeeRevenueDTO(){

    }
    public int getEmployeeID() {
        return EmployeeID;
    }
    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
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
    public int getTotalInvoice() {
        return totalInvoice;
    }
    public void setTotalInvoice(int totalInvoice) {
        this.totalInvoice = totalInvoice;
    }
    public Double getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    
    
}
