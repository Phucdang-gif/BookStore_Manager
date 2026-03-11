package BUS;

import DAO.BookDAO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import DTO.ValidationResult;
import GUI.util.ExcelHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookBUS {
    private BookDAO bookDAO = new BookDAO();
    private ArrayList<BookDTO> listBook = new ArrayList<>();

    public BookBUS() {
        if (listBook.isEmpty()) {
            loadDataFromDB();
        }
    }

    public boolean loadDataFromDB() {
        try {
            listBook = bookDAO.selectAll();
            if (listBook == null)
                return false;
            refreshAllStatuses();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<BookDTO> getAll() {
        return listBook;
    }

    public BookDTO getBookDetails(int bookId) {
        try {
            return bookDAO.selectById(bookId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BookDTO getByIsbn(String isbn) {
        try {
            return bookDAO.selectByIsbn(isbn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===================== THÊM MỚI =====================

    /**
     * Trả về ValidationResult.
     * - isValid() == true → thành công
     * - isValid() == false → có lỗi, dùng getError("field") để highlight GUI
     */
    public ValidationResult addBook(BookDTO newBook) {
        ValidationResult vr = Validator.validateBook(newBook, this.listBook);
        if (!vr.isValid())
            return vr;
        try {
            int newId = bookDAO.insertBook(newBook);
            if (newId > 0) {
                newBook.setBookId(newId);

                bookDAO.insertBookAuthors(newId, newBook.getAuthors());
                if (newBook.getAuthorNames() == null || newBook.getAuthorNames().isEmpty()) {
                    List<String> names = newBook.getAuthors().stream().map(a -> a.getAuthorName())
                            .collect(Collectors.toList());
                    newBook.setAuthorNames(names);
                }
                listBook.add(newBook);
                return vr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vr.addError("system", "Lỗi hệ thống khi thêm sách!");
        return vr;
    }

    // ===================== CẬP NHẬT =====================

    public ValidationResult updateBook(BookDTO book) {
        ValidationResult vr = Validator.validateBook(book, this.listBook);
        if (!vr.isValid())
            return vr;
        try {
            if (bookDAO.updateBook(book)) {
                if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                    List<String> names = book.getAuthors().stream()
                            .map(a -> a.getAuthorName())
                            .collect(Collectors.toList());
                    book.setAuthorNames(names);
                }
                for (int i = 0; i < listBook.size(); i++) {
                    if (listBook.get(i).getBookId() == book.getBookId()) {
                        listBook.set(i, book);
                        break;
                    }
                }
                return vr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vr.addError("system", "Lỗi hệ thống khi cập nhật sách!");
        return vr;
    }

    // ===================== XÓA =====================

    public ValidationResult deleteBook(int bookId) {
        ValidationResult vr = new ValidationResult();
        try {
            boolean deleted = bookDAO.delete(bookId);
            if (deleted) {
                listBook.removeIf(b -> b.getBookId() == bookId);
                return vr; // isValid() == true
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        vr.addError("system", "Lỗi hệ thống khi xóa sách!");
        return vr;
    }

    // ===================== TÌM KIẾM / LỌC =====================

    public ArrayList<BookDTO> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty())
            return getAll();
        String key = keyword.toLowerCase();
        return (ArrayList<BookDTO>) listBook.stream()
                .filter(b -> b.getBookTitle().toLowerCase().contains(key) || b.getIsbn().contains(key))
                .collect(Collectors.toList());
    }

    public ArrayList<BookDTO> getBooksByCategory(int catId) {
        try {
            if (catId == 0)
                return getAll();
            return bookDAO.selectByCategoryId(catId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<BookDTO> getBooksByPublisher(int pubId) {
        try {
            if (pubId == 0)
                return getAll();
            return bookDAO.selectByPublisherId(pubId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<BookDTO> getBooksByAuthor(int authorId) {
        try {
            if (authorId == 0)
                return getAll();
            return bookDAO.selectByAuthorId(authorId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<BookDTO> filterBooks(int catId, int pubId, int authorId) {
        if (catId == 0 && pubId == 0 && authorId == 0)
            return getAll();
        return listBook.stream().filter(b -> {
            boolean matchCat = catId == 0 || b.getCategoryId() == catId;
            boolean matchPub = pubId == 0 || b.getPublisherId() == pubId;
            boolean matchAut = authorId == 0 || b.getAuthors().stream()
                    .anyMatch(a -> a.getAuthorId() == authorId);
            return matchCat && matchPub && matchAut;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    // ===================== IMPORT EXCEL =====================

    public String importBooksFromExcel(File file) {
        List<BookDTO> listImport = ExcelHelper.importBooksFromExcel(file);
        if (listImport.isEmpty())
            return "File rỗng hoặc lỗi định dạng!";

        PublisherBUS pubBUS = new PublisherBUS();
        CategoryBUS catBUS = new CategoryBUS();
        List<PublisherDTO> listPub = pubBUS.getAll();
        List<CategoryDTO> listCat = catBUS.getAll();

        int countSuccess = 0, countFail = 0;

        for (BookDTO excelBook : listImport) {
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

            ValidationResult result;
            BookDTO currentDbBook = null;
            if (excelBook.getBookId() > 0) {
                currentDbBook = getBookDetails(excelBook.getBookId());
            }

            if (currentDbBook != null) {
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
                result = updateBook(currentDbBook);
            } else {
                excelBook.setBookId(0);
                result = addBook(excelBook);
            }

            if (result.isValid())
                countSuccess++;
            else
                countFail++;
        }

        return "Kết quả nhập:\n- Thành công: " + countSuccess + "\n- Thất bại: " + countFail;
    }

    // ===================== QUẢN LÝ TRẠNG THÁI =====================

    public void checkAndUpdateStatus(BookDTO book) {
        String oldStatus = book.getStatus();
        String newStatus = oldStatus;

        if (book.getStockQuantity() <= 0) {
            if (!"discontinued".equals(oldStatus))
                newStatus = "out_of_stock";
        } else {
            if ("out_of_stock".equals(oldStatus) || oldStatus == null)
                newStatus = "in_stock";
        }

        if (newStatus != null && !newStatus.equals(oldStatus)) {
            try {
                boolean success = bookDAO.updateStatus(book.getBookId(), newStatus);
                if (success) {
                    book.setStatus(newStatus);
                    for (BookDTO b : listBook) {
                        if (b.getBookId() == book.getBookId()) {
                            b.setStatus(newStatus);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshAllStatuses() {
        if (listBook == null)
            loadDataFromDB();
        for (BookDTO book : listBook) {
            String oldStatus = book.getStatus();
            checkAndUpdateStatus(book);
            if (!book.getStatus().equals(oldStatus)) {
                System.out.println("Auto Fix: Sách ID " + book.getBookId() + " -> " + book.getStatus());
            }
        }
    }
    public void updateStockAndPrice(int bookId, int quantityAdded, double newImportPrice) {
        try {
            BookDTO book = getBookDetails(bookId);
            if (book != null) {
                int newStock = book.getStockQuantity() + quantityAdded;
                double newPrice = newImportPrice; // Hoặc tính trung bình gia quyền nếu muốn
                bookDAO.updateStockAndPrice(bookId, newStock, newPrice);
                book.setStockQuantity(newStock);
                book.setImportPrice(newPrice);
                checkAndUpdateStatus(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateQuantity(int bookId, int quantityChange) {
        try {
            BookDTO book = getBookDetails(bookId);
            if (book != null) {
                int newStock = book.getStockQuantity() + quantityChange;
                bookDAO.updateQuantity(bookId, quantityChange);
                book.setStockQuantity(newStock);
                checkAndUpdateStatus(book);
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}