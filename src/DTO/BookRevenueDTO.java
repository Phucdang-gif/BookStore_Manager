package DTO;

public class BookRevenueDTO {
    private int bookID;
    private String bookTitle;
    public String getBookTitle() {
        return bookTitle;
    }
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    private int ordinalNumber;
    private int totalSold;
    private double sellingPrice;
    private Double totalRevenue;
    public BookRevenueDTO(){}
    public int getBookID() {
        return bookID;
    }
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }
    public int getOrdinalNumber() {
        return ordinalNumber;
    }
    public void setOrdinalNumber(int ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }
    public int getTotalSold() {
        return totalSold;
    }
    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public Double getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
   
    
    
    
}
