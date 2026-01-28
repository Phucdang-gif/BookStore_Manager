package BUS;

import DAO.BookDAO;
import DTO.BookDTO;
import java.util.ArrayList;

public class BookBUS {
    private BookDAO bookDAO = new BookDAO();
    public static ArrayList<BookDTO> listBook = new ArrayList<>();

    public BookBUS() {
        if (listBook.isEmpty()) {
            loadDataFromDB();
        }
    }

    public void loadDataFromDB() {
        try {
            listBook = bookDAO.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
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
}