package BUS;

import DTO.AccountDTO;
import DTO.AuthorDTO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.CustomerDTO;
import DTO.DiscountServiceDTO;
import DTO.EmployeeDTO;
import DTO.ImportReceiptDTO;
import DTO.InvoiceDTO;
import DTO.PublisherDTO;
import DTO.ValidationResult;
import java.util.Collection;
import java.util.List;

public class Validator {
    private final ValidationResult result;

    private Validator() {
        this.result = new ValidationResult();
    }

    private ValidationResult getResult() {
        return result;
    }

    // Kiểm tra null
    private Validator requireNotBlank(String field, String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            result.addError(field, message);
        }
        return this;
    }

    // Kiểm tra độ dài tối đa
    private Validator requireMaxLength(String field, String value, int max, String message) {
        if (value != null && value.trim().length() > max) {
            result.addError(field, message);
        }
        return this;
    }

    // Kiểm tra độ dài tối thiểu
    private Validator requireMinLength(String field, String value, int min, String message) {
        if (value != null && !value.trim().isEmpty() && value.trim().length() < min) {
            result.addError(field, message);
        }
        return this;
    }

    // Kiểm tra số dương
    private Validator requirePositive(String field, double value, String message) {
        if (value <= 0) {
            result.addError(field, message);
        }
        return this;
    }

    // Kiểm tra số không âm
    private Validator requireNonNegative(String field, double value, String message) {
        if (value < 0) {
            result.addError(field, message);
        }
        return this;
    }

    // Kiểm tra không rỗng
    private Validator requireNotEmpty(String field, Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            result.addError(field, message);
        }
        return this;
    }

    // Kiểm tra Regex
    private Validator requirePattern(String field, String value, String regex, String message) {
        // Chỉ kiểm tra Regex nếu người dùng có nhập dữ liệu
        if (value != null && !value.trim().isEmpty()) {
            if (!value.matches(regex)) {
                result.addError(field, message);
            }
        }
        return this;
    }

    // Kiểm tra điều kiện
    private Validator requireCondition(String field, boolean isValid, String message) {
        if (!isValid) {
            result.addError(field, message);
        }
        return this;
    }

    private Validator requireValidPhone(String field, String phone, String message) {
        if (phone != null && !phone.trim().isEmpty()) {
            // Regex chuẩn: Bắt đầu bằng 0 hoặc +84, theo sau là 8-9 chữ số
            if (!phone.trim().matches("^(0|\\+84)[0-9]{8,9}$")) {
                result.addError(field, message);
            }
        }
        return this;
    }

    private Validator requireUnique(String field, boolean isDuplicate, String message) {
        if (isDuplicate) {
            result.addError(field, message);
        }
        return this;
    }

    public static ValidationResult validateBook(BookDTO book, List<BookDTO> existingList) {
        boolean isIsbnDuplicate = existingList != null && existingList.stream()
                .anyMatch(b -> b.getIsbn().equals(book.getIsbn()) && b.getBookId() != book.getBookId());

        return new Validator()
                .requireNotBlank("bookTitle", book.getBookTitle(), "Tên sách không được để trống")
                .requireUnique("isbn", isIsbnDuplicate, "Mã ISBN đã tồn tại")
                .requireNotBlank("isbn", book.getIsbn(), "Mã ISBN không được để trống")
                .requirePattern("isbn",
                        book.getIsbn() != null ? book.getIsbn().replaceAll("-", "") : "",
                        "\\d{10}|\\d{13}",
                        "Mã ISBN không hợp lệ (phải có 10 hoặc 13 số)")
                .requirePositive("categoryId", book.getCategoryId(), "Vui lòng chọn danh mục")
                .requirePositive("publisherId", book.getPublisherId(), "Vui lòng chọn nhà xuất bản")
                .requireNonNegative("importPrice", book.getImportPrice(), "Giá nhập không được âm")
                .requirePositive("sellingPrice", book.getSellingPrice(), "Giá bán phải lớn hơn 0")
                .requireCondition("sellingPrice",
                        book.getImportPrice() < 0 || book.getSellingPrice() >= book.getImportPrice(),
                        "Giá bán không được nhỏ hơn giá nhập")
                .requireNonNegative("stockQuantity", book.getStockQuantity(), "Số lượng tồn kho không được âm")
                .requireNonNegative("minimumStock", book.getMinimumStock(), "Tồn kho tối thiểu không được âm")
                .requireNotEmpty("authors", book.getAuthors(), "Vui lòng chọn ít nhất một tác giả")
                .getResult();
    }

    public static ValidationResult validateAuthor(AuthorDTO author, List<AuthorDTO> existingList) {
        boolean isNameDuplicate = existingList != null && existingList.stream()
                .anyMatch(a -> a.getAuthorName().equalsIgnoreCase(author.getAuthorName())
                        && a.getAuthorId() != author.getAuthorId());
        return new Validator()
                .requireNotBlank("authorName", author.getAuthorName(), "Tên tác giả không được để trống")
                .requireMaxLength("authorName", author.getAuthorName(), 100,
                        "Tên tác giả không được vượt quá 100 ký tự")
                .requireUnique("authorName", isNameDuplicate, "Tên tác giả đã tồn tại")
                .getResult();
    }

    public static ValidationResult validateCategory(CategoryDTO category, List<CategoryDTO> existingList) {
        boolean isNameDuplicate = existingList != null && existingList.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(category.getName()) && c.getId() != category.getId());
        return new Validator()
                .requireUnique("name", isNameDuplicate, "Tên thể loại đã tồn tại")
                .requireNotBlank("name", category.getName(), "Tên thể loại không được để trống")
                .requireMaxLength("name", category.getName(), 100, "Tên thể loại không được vượt quá 100 ký tự")
                .requireNonNegative("displayOrder", category.getDisplayOrder(), "Thứ tự hiển thị phải là số không âm")
                .getResult();
    }

    public static ValidationResult validatePublisher(PublisherDTO publisher, List<PublisherDTO> existingList) {
        boolean isPhoneDuplicate = existingList != null && existingList.stream()
                .anyMatch(p -> p.getPhone().equals(publisher.getPhone()) && p.getId() != publisher.getId());
        return new Validator()
                .requireUnique("phone", isPhoneDuplicate, "Số điện thoại đã tồn tại")
                .requireNotBlank("name", publisher.getName(), "Tên nhà xuất bản không được để trống")
                .requireMaxLength("name", publisher.getName(), 150, "Tên nhà xuất bản không được vượt quá 150 ký tự")
                .requireValidPhone("phone", publisher.getPhone(), "Số điện thoại không hợp lệ")
                .getResult();
    }

    public static ValidationResult validateAccount(AccountDTO acc, List<AccountDTO> existingList) {
        boolean isUsernameDuplicate = existingList != null && existingList.stream()
                .anyMatch(p -> p.getUsername().equals(acc.getUsername()) && p.getAccountId() != acc.getAccountId());
        return new Validator()
                .requireUnique("username", isUsernameDuplicate, "Tên đăng nhập đã tồn tại")
                .requireNotBlank("username", acc.getUsername(), "Tên đăng nhập không được để trống")
                .requireMinLength("username", acc.getUsername(), 4, "Tên đăng nhập phải có ít nhất 4 ký tự")
                // Nếu có nhập pass thì pass phải >= 6 ký tự. Cho phép rỗng (dùng khi update
                // không đổi pass)
                .requireCondition("password",
                        acc.getPassword() == null || acc.getPassword().isEmpty() || acc.getPassword().length() >= 6,
                        "Mật khẩu phải có ít nhất 6 ký tự")
                .requirePositive("employeeId", acc.getEmployeeId(), "Vui lòng chọn nhân viên")
                .requirePositive("permissionGroupId", acc.getPermissionGroupId(), "Vui lòng chọn nhóm quyền")
                .getResult();
    }

    public static ValidationResult validateCustomer(CustomerDTO cus, List<CustomerDTO> existingList) {
        boolean isPhoneDuplicate = existingList != null && existingList.stream()
                .anyMatch(c -> c.getPhone().equals(cus.getPhone()) && c.getCustomerId() != cus.getCustomerId());
        return new Validator()
                .requireNotBlank("fullName", cus.getFullName(), "Tên khách hàng không được để trống")
                .requireMaxLength("fullName", cus.getFullName(), 100, "Tên khách hàng không được vượt quá 100 ký tự")
                .requireNotBlank("phone", cus.getPhone(), "Số điện thoại không được để trống")
                .requireValidPhone("phone", cus.getPhone(), "Số điện thoại không hợp lệ")
                .requireUnique("phone", isPhoneDuplicate, "Số điện thoại đã tồn tại")
                .requireNonNegative("loyaltyPoints", cus.getLoyaltyPoints(), "Điểm tích lũy không được âm")
                .getResult();
    }

    public static ValidationResult validateEmployee(EmployeeDTO emp, List<EmployeeDTO> existingList) {
        boolean isPhoneDuplicate = existingList != null && existingList.stream()
                .anyMatch(e -> e.getPhone().equals(emp.getPhone()) && e.getEmployeeId() != emp.getEmployeeId());
        return new Validator()
                .requireNotBlank("fullName", emp.getFullName(), "Tên nhân viên không được để trống")
                .requireNotBlank("phone", emp.getPhone(), "Số điện thoại không được để trống")
                .requireValidPhone("phone", emp.getPhone(), "Số điện thoại không hợp lệ")
                .requireUnique("phone", isPhoneDuplicate, "Số điện thoại đã tồn tại")
                .requireNotBlank("address", emp.getAddress(), "Địa chỉ không được để trống")
                .requireNotBlank("position", emp.getPosition(), "Vui lòng nhập chức vụ")
                .requireNonNegative("salary", emp.getSalary(), "Lương nhân viên không được âm")
                .requireCondition("dateOfBirth", emp.getDateOfBirth() != null, "Vui lòng chọn ngày sinh")
                .getResult();
    }

    public static ValidationResult validateDiscountService(DiscountServiceDTO ds,
            List<DiscountServiceDTO> existingList) {
        boolean isNameDuplicate = existingList != null && existingList.stream()
                .anyMatch(d -> d.getServiceName().equalsIgnoreCase(ds.getServiceName())
                        && d.getServiceId() != ds.getServiceId());
        return new Validator()
                .requireNotBlank("serviceName", ds.getServiceName(), "Tên chương trình khuyến mãi không được để trống")
                .requireUnique("serviceName", isNameDuplicate, "Tên chương trình khuyến mãi đã tồn tại")
                .requirePositive("discountValue", ds.getDiscountValue(), "Giá trị giảm giá phải lớn hơn 0")
                .requireNonNegative("minimumAmount", ds.getMinimumAmount(), "Giá trị đơn hàng tối thiểu không được âm")
                .requireNonNegative("maximumDiscount", ds.getMaximumDiscount(), "Mức giảm tối đa không được âm")
                .getResult();
    }

    // ========================================================
    // LUẬT KIỂM TRA CHO PHIẾU NHẬP HÀNG (IMPORT RECEIPT)
    // ========================================================
    public static ValidationResult validateImportReceipt(ImportReceiptDTO receipt, boolean hasDetails) {
        return new Validator()
                // 1. Phải chọn Nhà Cung Cấp (Chặn lỗi rỗng ComboBox)
                .requirePositive("supplierId", receipt.getSupplierId(), "Vui lòng chọn Nhà cung cấp từ danh sách!")
                // 2. Phải có ID Nhân viên đang đăng nhập
                .requirePositive("employeeId", receipt.getEmployeeId(),
                        "Lỗi bảo mật: Không xác định được nhân viên lập phiếu!")
                // 3. Tổng tiền không được âm
                .requireNonNegative("totalAmount", receipt.getTotalAmount(), "Tổng tiền phiếu nhập không được âm!")
                // 4. Bảng chi tiết không được để trống
                .requireCondition("details", hasDetails,
                        "Phiếu nhập đang trống! Vui lòng chọn ít nhất 1 cuốn sách để nhập kho.")
                .getResult();
    }

    public static ValidationResult validateInvoice(InvoiceDTO invoice, boolean hasDetails, CustomerDTO customer) {
        Validator v = new Validator()
                .requirePositive("employeeId", invoice.getEmployeeId(), "Lỗi bảo mật: Không xác định được thu ngân!")
                .requireNonNegative("totalAmount", invoice.getTotalAmount(), "Tổng tiền hóa đơn không được âm!")
                .requireCondition("details", hasDetails, "Giỏ hàng trống! Vui lòng thêm sách trước khi thanh toán.");

        // Kiểm tra logic điểm
        v.requireNonNegative("pointsUsed", invoice.getPointsUsed(), "Số điểm sử dụng không được âm!");

        // Nếu có dùng điểm, phải đảm bảo khách hàng hợp lệ và không dùng lố điểm
        if (invoice.getPointsUsed() > 0) {
            v.requireCondition("pointsUsed",
                    customer != null && customer.getCustomerId() > 0,
                    "Chỉ khách hàng thành viên mới được sử dụng điểm quy đổi!");

            v.requireCondition("pointsUsed",
                    customer != null && invoice.getPointsUsed() <= customer.getLoyaltyPoints(),
                    "Số điểm sử dụng (" + invoice.getPointsUsed() + ") vượt quá số điểm hiện có của khách hàng!");
        }
        return v.getResult();
    }

}