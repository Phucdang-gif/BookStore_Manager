package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import DTO.BookDTO;
import DTO.AuthorDTO;
import config.DatabaseConnection;

public class BookDAO {
    private Connection conn;

    public BookDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public BookDAO(Connection conn) {
        this.conn = conn;
    }

    // --- CÁC HÀM THÊM MỚI (CREATE)
    public int insertBook(BookDTO book) throws SQLException {
        int generatedBookId = -1;
        if (conn == null)
            return -1;
        String sql = "INSERT INTO books (isbn, book_title, publisher_id, category_id, " +
                "page_count, language, publication_year, cover_type, import_price, " +
                "selling_price, stock_quantity, minimum_stock, image, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setBookParameters(pst, book);

            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBookId = rs.getInt(1);
                    }
                }
            }
        }
        return generatedBookId;
    }

    public void insertBookAuthors(int bookId, List<AuthorDTO> authors) throws SQLException {
        if (authors == null || authors.isEmpty())
            return;
        String sql = "INSERT INTO book_authors (book_id, author_id, display_order) VALUE (?,?,?)";
        if (conn == null)
            return;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            for (int i = 0; i < authors.size(); i++) {
                AuthorDTO author = authors.get(i);
                pst.setInt(1, bookId);
                pst.setInt(2, author.getAuthorId());
                pst.setInt(3, i + 1);

                pst.addBatch(); // Gom lệnh lại để chạy 1 lần cho nhanh (Batch Processing)
            }
            pst.executeBatch(); // Thực thi tất cả các lệnh đã gom
        }
    }
    // --- CÁC HÀM SELECT (READ) ---

    // Trong file DAO/BookDAO.java

    public ArrayList<BookDTO> selectAll() throws SQLException {
        ArrayList<BookDTO> books = new ArrayList<>();

        // Câu lệnh SQL nâng cấp: JOIN 5 bảng và gộp tên tác giả
        String sql = "SELECT b.*, p.publisher_name, c.category_name, " +
                "GROUP_CONCAT(a.author_name SEPARATOR ', ') AS authors_list " + // <--- MỚI
                "FROM books b " +
                "LEFT JOIN publishers p ON b.publisher_id = p.publisher_id " +
                "LEFT JOIN categories c ON b.category_id = c.category_id " +
                "LEFT JOIN book_authors ba ON b.book_id = ba.book_id " + // <--- MỚI
                "LEFT JOIN authors a ON ba.author_id = a.author_id " + // <--- MỚI
                "GROUP BY b.book_id " + // <--- MỚI
                "ORDER BY b.book_id ASC";
        if (conn == null) {
            return books;
        }
        try (PreparedStatement pst = conn.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                BookDTO book = mapResultSetToBook(rs);
                // --- XỬ LÝ DANH SÁCH TÁC GIẢ ---
                String authorsStr = rs.getString("authors_list"); // Lấy chuỗi "Tác giả A, Tác giả B"
                List<String> authorNames = new ArrayList<>();

                if (authorsStr != null && !authorsStr.isEmpty()) {
                    // Tách chuỗi thành mảng dựa trên dấu phẩy
                    String[] arr = authorsStr.split(", ");
                    for (String s : arr) {
                        authorNames.add(s);
                    }
                }
                book.setAuthorNames(authorNames);

                books.add(book);
            }
        }
        return books;
    }

    public BookDTO selectById(int bookId) throws SQLException {
        String sql = "SELECT b.*, p.publisher_name, c.category_name " +
                "FROM books b " +
                "LEFT JOIN publishers p ON b.publisher_id = p.publisher_id " +
                "LEFT JOIN categories c ON b.category_id = c.category_id " +
                "WHERE b.book_id = ?";
        if (conn == null)
            return null;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, bookId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    book.setAuthors(getAuthorsByBookId(bookId));
                    return book;
                }
            }
        }
        return null;
    }

    private List<AuthorDTO> getAuthorsByBookId(int bookId) throws SQLException {
        List<AuthorDTO> authors = new ArrayList<>();

        // Vẫn dùng bảng book_authors (số nhiều) và sắp xếp
        String sql = "SELECT a.author_id, a.author_name FROM authors a " +
                "JOIN book_authors ba ON a.author_id = ba.author_id " +
                "WHERE ba.book_id = ? " +
                "ORDER BY ba.display_order ASC";
        if (conn == null)
            return authors;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, bookId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // Constructor mới chỉ có ID và Name
                    authors.add(new AuthorDTO(
                            rs.getInt("author_id"),
                            rs.getString("author_name")));
                }
            }
        }
        return authors;
    }
    // --- CHỨC NĂNG CẬP NHẬT (UPDATE) ---

    /**
     * Cập nhật thông tin sách theo quy trình:
     * 1. Update bảng books
     * 2. Xóa hết tác giả cũ trong bảng book_authors
     * 3. Thêm lại danh sách tác giả mới
     */
    public boolean updateBook(BookDTO book) throws SQLException {
        String sql = "UPDATE books SET isbn=?, book_title=?, publisher_id=?, category_id=?, " +
                "page_count=?, language=?, publication_year=?, cover_type=?, import_price=?, " +
                "selling_price=?, stock_quantity=?, minimum_stock=?, image=?, status=? " +
                "WHERE book_id=?";
        if (conn == null)
            return false;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            // Gán các tham số từ 1 đến 14 (Sử dụng hàm setBookParameters đã viết ở phần
            // trước)
            setBookParameters(pst, book);

            // Tham số thứ 15 là WHERE book_id
            pst.setInt(15, book.getBookId());

            int affected = pst.executeUpdate();

            // Nếu update bảng books thành công
            if (affected > 0) {
                // Bước 2: Xóa sạch danh sách tác giả cũ
                deleteBookAuthors(book.getBookId());

                // Bước 3: Thêm lại danh sách tác giả mới (nếu có)
                if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                    insertBookAuthors(book.getBookId(), book.getAuthors());
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateQuantity(int bookId, int quantitySold) {
        try {
            // Trừ số lượng tồn kho
            String sql = "UPDATE books SET stock_quantity = stock_quantity - ? WHERE book_id = ?";
            PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);

            ps.setInt(1, quantitySold);
            ps.setInt(2, bookId);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm này chỉ update cột status, an toàn tuyệt đối cho dữ liệu Tác giả
    public boolean updateStatus(int bookId, String newStatus) throws SQLException {
        String sql = "UPDATE books SET status = ? WHERE book_id = ?";
        if (conn == null)
            return false;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newStatus);
            pst.setInt(2, bookId);
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Hàm phụ: Xóa tất cả tác giả của một cuốn sách (Dùng khi Update hoặc Delete
     * sách)
     */
    public void deleteBookAuthors(int bookId) throws SQLException {
        String sql = "DELETE FROM book_authors WHERE book_id = ?";
        if (conn == null)
            return;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, bookId);
            pst.executeUpdate();
        }
    }

    public boolean delete(int bookId) throws SQLException {
        // Bước 1: Xóa các liên kết tác giả trước (để tránh lỗi khóa ngoại)
        deleteBookAuthors(bookId);
        // Bước 2: Xóa sách
        String sql = "DELETE FROM books WHERE book_id = ?";
        if (conn == null)
            return false;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, bookId);
            return pst.executeUpdate() > 0;
        }
    }

    private BookDTO mapResultSetToBook(ResultSet rs) throws SQLException {
        BookDTO book = new BookDTO();
        book.setBookId(rs.getInt("book_id"));
        book.setIsbn(rs.getString("isbn"));
        book.setBookTitle(rs.getString("book_title"));
        book.setPublisherId(rs.getInt("publisher_id"));
        book.setCategoryId(rs.getInt("category_id"));
        book.setPageCount(rs.getInt("page_count"));
        book.setLanguage(rs.getString("language"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setCoverType(rs.getString("cover_type"));
        book.setImportPrice(rs.getDouble("import_price"));
        book.setSellingPrice(rs.getDouble("selling_price"));
        book.setStockQuantity(rs.getInt("stock_quantity"));
        book.setMinimumStock(rs.getInt("minimum_stock"));
        book.setImage(rs.getString("image"));
        book.setStatus(rs.getString("status"));
        book.setPublisherName(rs.getString("publisher_name"));
        book.setCategoryName(rs.getString("category_name"));
        return book;
    }

    /**
     * Hàm trợ giúp: Gán các tham số của BookDTO vào PreparedStatement
     * Dùng chung cho cả hàm Insert (Thêm) và Update (Sửa)
     * Thứ tự tham số phải khớp với câu SQL trong hàm insertBook và updateBook
     */
    private void setBookParameters(PreparedStatement pst, BookDTO book) throws SQLException {
        pst.setString(1, book.getIsbn());
        pst.setString(2, book.getBookTitle());
        pst.setInt(3, book.getPublisherId());
        pst.setInt(4, book.getCategoryId());
        pst.setInt(5, book.getPageCount());
        pst.setString(6, book.getLanguage());
        pst.setInt(7, book.getPublicationYear());
        pst.setString(8, book.getCoverType());
        pst.setDouble(9, book.getImportPrice());
        pst.setDouble(10, book.getSellingPrice());
        pst.setInt(11, book.getStockQuantity());
        pst.setInt(12, book.getMinimumStock());
        pst.setString(13, book.getImage());
        pst.setString(14, book.getStatus());
    }
}