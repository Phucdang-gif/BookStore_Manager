package DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;
import java.sql.Timestamp;

public class BookDTO {
    private int bookId; // book_id
    private int publisherId; // publisher_id
    private int categoryId; // category_id
    private String isbn; // isbn (từ ma_hang)
    private String bookTitle; // book_title
    private int pageCount; // page_count
    private String language; // language
    private int publicationYear; // publication_year
    private String coverType; // cover_type
    private double importPrice; // import_price
    private double sellingPrice; // selling_price
    private int stockQuantity; // stock_quantity
    private int minimumStock; // minimum_stock
    private String image; // image
    private String status; // status (in_stock, out_of_stock, discontinued)
    private Timestamp addedAt; // added_at

    // Fields bổ sung cho hiển thị (JOIN với publishers, categories)
    private String publisherName;
    private String categoryName;
    private List<String> authorNames; // Từ bảng book_authors
    private List<AuthorDTO> authors = new ArrayList<>();

    // Constructor rỗng
    public BookDTO() {
    }

    // Constructor đầy đủ
    public BookDTO(int bookId, int publisherId, int categoryId, String isbn,
            String bookTitle, int pageCount, String language,
            int publicationYear, String coverType, double importPrice,
            double sellingPrice, int stockQuantity, int minimumStock,
            String image, String status) {
        this.bookId = bookId;
        this.publisherId = publisherId;
        this.categoryId = categoryId;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.pageCount = pageCount;
        this.language = language;
        this.publicationYear = publicationYear;
        this.coverType = coverType;
        this.importPrice = importPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
        this.image = image;
        this.status = status;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public List<AuthorDTO> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDTO> authors) {
        this.authors = authors;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        this.importPrice = importPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    public String getStatusVietnamese() {
        if ("in_stock".equals(this.status))
            return "Còn hàng";
        if ("out_of_stock".equals(this.status))
            return "Hết hàng";
        return "Ngừng kinh doanh";
    }

    public String getFormattedSellingPrice() {
        return NumberFormat.getCurrencyInstance(Locale.of("vi", "VN")).format(this.sellingPrice);
    }

    public String getFormattedImportPrice() {
        return NumberFormat.getCurrencyInstance(Locale.of("vi", "VN")).format(this.importPrice);
    }

    // toString() để debug dễ dàng
    @Override
    public String toString() {
        return "BookDTO {" +
                "Title='" + bookTitle + '\'' +
                ", ISBN='" + isbn + '\'' +
                ", Author='" + authors + '\'' +
                ", Year=" + publicationYear +
                ", Page=" + pageCount +
                ", ImportPrice=" + importPrice +
                ", ExportPrice=" + sellingPrice +
                ", Qty=" + stockQuantity +
                ", Category='" + categoryName + '\'' +
                ", Publisher='" + publisherName + '\'' +
                ", Status='" + status + '\'' +
                ", Cover='" + coverType + '\'' +
                '}';
    }

}