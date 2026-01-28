package BUS;

import DAO.AuthorDAO; // Bạn cần tạo thêm file AuthorDAO
import DTO.AuthorDTO; // Đảm bảo đã có AuthorDTO với hàm toString()
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorBUS {
    private AuthorDAO authorDAO;
    private ArrayList<AuthorDTO> authorList;

    public AuthorBUS() {
        this.authorDAO = new AuthorDAO();
        loadData();
    }

    /**
     * Tải toàn bộ danh sách tác giả từ Database vào bộ nhớ đệm (cache)
     */
    public void loadData() {
        try {
            this.authorList = authorDAO.selectAll();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tải danh sách tác giả: " + e.getMessage());
            this.authorList = new ArrayList<>();
        }
    }

    /**
     * Trả về danh sách tất cả tác giả để đổ vào ComboBox
     */
    public ArrayList<AuthorDTO> getAll() {
        if (authorList == null || authorList.isEmpty()) {
            loadData();
        }
        return new ArrayList<>(authorList);
    }

    /**
     * Tìm ID của tác giả dựa trên tên (Hữu ích khi xử lý chuỗi từ JTextArea)
     */
    public int findIdByName(String name) {
        return authorList.stream()
                .filter(a -> a.getAuthorName().equalsIgnoreCase(name.trim()))
                .map(AuthorDTO::getAuthorId)
                .findFirst()
                .orElse(-1); // Trả về -1 nếu là tác giả mới hoàn toàn
    }

    /**
     * Thêm một tác giả mới vào Database và cập nhật lại cache
     */
    public boolean addAuthor(AuthorDTO author) {
        try {
            if (authorDAO.insert(author)) {
                authorList.add(author);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}