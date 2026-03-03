package BUS;

import DTO.AuthorDTO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import DTO.ValidationResult;

public class Validator {

    public static ValidationResult validateBook(BookDTO book) {
        ValidationResult r = new ValidationResult();

        if (book.getBookTitle() == null || book.getBookTitle().trim().isEmpty())
            r.addError("bookTitle", "Tên sách không được để trống");

        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty())
            r.addError("isbn", "Mã ISBN không được để trống");
        else if (!book.getIsbn().replaceAll("-", "").matches("\\d{10}|\\d{13}"))
            r.addError("isbn", "Mã ISBN không hợp lệ (phải có 10 hoặc 13 số)");

        if (book.getCategoryId() <= 0)
            r.addError("categoryId", "Vui lòng chọn danh mục");

        if (book.getPublisherId() <= 0)
            r.addError("publisherId", "Vui lòng chọn nhà xuất bản");

        if (book.getImportPrice() < 0)
            r.addError("importPrice", "Giá nhập không được âm");

        if (book.getSellingPrice() <= 0)
            r.addError("sellingPrice", "Giá bán phải lớn hơn 0");
        else if (book.getImportPrice() >= 0 && book.getSellingPrice() < book.getImportPrice())
            r.addError("sellingPrice", "Giá bán không được nhỏ hơn giá nhập");

        if (book.getStockQuantity() < 0)
            r.addError("stockQuantity", "Số lượng tồn kho không được âm");

        if (book.getMinimumStock() < 0)
            r.addError("minimumStock", "Tồn kho tối thiểu không được âm");

        if (book.getAuthors() == null || book.getAuthors().isEmpty())
            r.addError("authors", "Vui lòng chọn ít nhất một tác giả");

        return r;
    }

    public static ValidationResult validateAuthor(AuthorDTO author) {
        ValidationResult r = new ValidationResult();

        if (author.getAuthorName() == null || author.getAuthorName().trim().isEmpty())
            r.addError("authorName", "Tên tác giả không được để trống");
        else if (author.getAuthorName().trim().length() > 100)
            r.addError("authorName", "Tên tác giả không được vượt quá 100 ký tự");

        return r;
    }

    public static ValidationResult validateCategory(CategoryDTO category) {
        ValidationResult r = new ValidationResult();

        if (category.getName() == null || category.getName().trim().isEmpty())
            r.addError("name", "Tên thể loại không được để trống");
        else if (category.getName().trim().length() > 100)
            r.addError("name", "Tên thể loại không được vượt quá 100 ký tự");

        if (category.getDisplayOrder() < 0)
            r.addError("displayOrder", "Thứ tự hiển thị phải là số không âm");

        return r;
    }

    public static ValidationResult validatePublisher(PublisherDTO publisher) {
        ValidationResult r = new ValidationResult();

        if (publisher.getName() == null || publisher.getName().trim().isEmpty())
            r.addError("name", "Tên nhà xuất bản không được để trống");
        else if (publisher.getName().trim().length() > 150)
            r.addError("name", "Tên nhà xuất bản không được vượt quá 150 ký tự");

        if (publisher.getPhone() != null && !publisher.getPhone().trim().isEmpty()) {
            if (!publisher.getPhone().trim().matches("^[0-9+\\-\\s]{7,15}$"))
                r.addError("phone", "Số điện thoại không đúng định dạng");
        }

        return r;
    }
}