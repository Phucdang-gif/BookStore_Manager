package BUS;

import DAO.BookDAO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import GUI.util.ExcelHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookBUS {
    private BookDAO bookDAO = new BookDAO();
    public static ArrayList<BookDTO> listBook = new ArrayList<>();

    public BookBUS() {
        if (listBook.isEmpty()) {
            loadDataFromDB();
        }
    }

    public boolean loadDataFromDB() {
        try {
            listBook = bookDAO.selectAll();
            // Nếu list trả về null (do lỗi kết nối bên DAO) thì coi như thất bại
            if (listBook == null)
                return false;
            refreshAllStatuses();
            return true; // Thành công
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Có lỗi xảy ra
        }
    }

    public ArrayList<BookDTO> getAll() {
        return listBook;
    }

    // Lấy chi tiết sách (Gọi DAO để lấy mới nhất kèm tác giả)
    public BookDTO getBookDetails(int bookId) {
        try {
            return bookDAO.selectById(bookId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Thêm mới (add)
    public boolean addBook(BookDTO newBook) {
        try {
            int newBookId = bookDAO.insertBook(newBook);
            if (newBookId > 0) {
                newBook.setBookId(newBookId);
                if (newBook.getAuthors() != null && !newBook.getAuthors().isEmpty()) {
                    bookDAO.insertBookAuthors(newBookId, newBook.getAuthors());
                }
                listBook.add(newBook);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. CẬP NHẬT (Update)
    public boolean updateBook(BookDTO book) {
        try {
            boolean isUpdated = bookDAO.updateBook(book);
            if (isUpdated) {
                // Cập nhật lại trong danh sách Cache (listBook) để bảng hiển thị đúng ngay lập
                // tức
                for (int i = 0; i < listBook.size(); i++) {
                    if (listBook.get(i).getBookId() == book.getBookId()) {
                        listBook.set(i, book);
                        break;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBook(int bookId) {
        try {
            return bookDAO.delete(bookId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Trong file BUS/BookBUS.java

    public String importBooksFromExcel(File file) {
        // 1. Đọc file (Gọi ExcelHelper ở đây hoặc truyền List vào cũng được)
        List<BookDTO> listImport = ExcelHelper.importBooksFromExcel(file);
        if (listImport.isEmpty())
            return "File rỗng hoặc lỗi định dạng!";

        // 2. Chuẩn bị dữ liệu tra cứu
        PublisherBUS pubBUS = new PublisherBUS();
        CategoryBUS catBUS = new CategoryBUS();
        List<PublisherDTO> listPub = pubBUS.getAll();
        List<CategoryDTO> listCat = catBUS.getAll();

        int countSuccess = 0;
        int countFail = 0;

        // 3. Xử lý logic
        for (BookDTO excelBook : listImport) {
            // --- Map ID từ Tên ---
            for (PublisherDTO p : listPub) {
                if (p.getName().equalsIgnoreCase(excelBook.getPublisherName())) {
                    excelBook.setPublisherId(p.getId());
                    break;
                }
            }
            for (CategoryDTO c : listCat) {
                if (c.getName().equalsIgnoreCase(excelBook.getCategoryName())) {
                    excelBook.setCategoryId(c.getId());
                    break;
                }
            }

            // --- Merge & Save ---
            boolean isSuccess = false;
            BookDTO currentDbBook = null;
            if (excelBook.getBookId() > 0) {
                currentDbBook = getBookDetails(excelBook.getBookId()); // Gọi hàm có sẵn của BUS
            }

            if (currentDbBook != null) {
                // Update & Merge
                currentDbBook.setIsbn(excelBook.getIsbn());
                currentDbBook.setBookTitle(excelBook.getBookTitle());
                currentDbBook.setAuthorNames(excelBook.getAuthorNames());
                currentDbBook.setPublisherId(excelBook.getPublisherId());
                currentDbBook.setCategoryId(excelBook.getCategoryId());
                currentDbBook.setImportPrice(excelBook.getImportPrice());
                currentDbBook.setSellingPrice(excelBook.getSellingPrice());
                currentDbBook.setStockQuantity(excelBook.getStockQuantity());
                currentDbBook.setMinimumStock(excelBook.getMinimumStock());
                currentDbBook.setStatus(excelBook.getStatus());
                if (excelBook.getImage() != null && !excelBook.getImage().isEmpty()) {
                    currentDbBook.setImage(excelBook.getImage());
                }
                isSuccess = updateBook(currentDbBook); // Gọi hàm update của BUS
            } else {
                // Insert
                excelBook.setBookId(0);
                isSuccess = addBook(excelBook); // Gọi hàm add của BUS
            }

            if (isSuccess)
                countSuccess++;
            else
                countFail++;
        }

        return "Kết quả nhập:\n- Thành công: " + countSuccess + "\n- Thất bại: " + countFail;
    }

    /**
     * Hàm 1: Kiểm tra và cập nhật trạng thái cho 1 cuốn sách cụ thể.
     * Logic:
     * - Nếu Tồn kho = 0 -> Chuyển thành "Hết hàng" (out_of_stock).
     * - Nếu Tồn kho > 0 và đang là "Hết hàng" -> Chuyển thành "Còn hàng"
     * (in_stock).
     * - Giữ nguyên nếu đang là "Ngừng kinh doanh" (discontinued).
     */
    public void checkAndUpdateStatus(BookDTO book) {
        String oldStatus = book.getStatus();
        String newStatus = oldStatus;

        // Logic kiểm tra tồn kho
        if (book.getStockQuantity() <= 0) {
            if (!"discontinued".equals(oldStatus)) {
                newStatus = "out_of_stock";
            }
        } else {
            if ("out_of_stock".equals(oldStatus) || oldStatus == null) {
                newStatus = "in_stock";
            }
        }

        // Nếu có thay đổi trạng thái
        if (newStatus != null && !newStatus.equals(oldStatus)) {
            try {
                // --- SỬA ĐOẠN NÀY ---
                // Thay vì gọi updateBook (nguy hiểm), hãy gọi updateStatus (an toàn)
                boolean success = bookDAO.updateStatus(book.getBookId(), newStatus);

                if (success) {
                    book.setStatus(newStatus);

                    // Cập nhật lại Cache
                    for (BookDTO b : listBook) {
                        if (b.getBookId() == book.getBookId()) {
                            b.setStatus(newStatus);
                            break;
                        }
                    }
                    System.out.println("Đã cập nhật trạng thái sách ID " + book.getBookId() + " -> " + newStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hàm 2: Quét toàn bộ kho sách để sửa lỗi trạng thái (Dùng sau khi Import hoặc
     * nút "Làm mới")
     */
    public void refreshAllStatuses() {
        if (listBook == null)
            loadDataFromDB();

        int countFixed = 0;
        for (BookDTO book : listBook) {
            String oldStatus = book.getStatus();
            checkAndUpdateStatus(book);
            if (!book.getStatus().equals(oldStatus)) {
                countFixed++;
            }
        }
        if (countFixed > 0) {
            System.out.println("Auto Fix completed " + countFixed + " books.");
        }
    }
}