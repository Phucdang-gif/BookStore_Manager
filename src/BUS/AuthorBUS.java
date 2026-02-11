package BUS;

import DAO.AuthorDAO;
import DTO.AuthorDTO;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorBUS {
    private AuthorDAO authorDAO;
    private ArrayList<AuthorDTO> authorList;

    public AuthorBUS() {
        this.authorDAO = new AuthorDAO();
        loadData();
    }

    public void loadData() {
        try {
            this.authorList = authorDAO.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
            this.authorList = new ArrayList<>();
        }
    }

    public ArrayList<AuthorDTO> getAll() {
        if (authorList == null)
            loadData();
        return this.authorList;
    }

    public AuthorDTO getById(int id) {
        for (AuthorDTO a : authorList) {
            if (a.getAuthorId() == id)
                return a;
        }
        return null;
    }

    // --- XỬ LÝ NGHIỆP VỤ ---

    public boolean addAuthor(AuthorDTO author) {
        try {
            if (authorDAO.isNameExists(author.getAuthorName())) {
                System.out.println("Tên tác giả đã tồn tại!");
                return false;
            }
            if (authorDAO.insert(author)) {
                loadData(); // Reload lại list để cập nhật ID mới nhất và thứ tự
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAuthor(AuthorDTO author) {
        try {
            // Kiểm tra trùng tên nếu tên mới khác tên cũ (Optional: tùy logic nghiệp vụ)
            // Ở đây tạm bỏ qua check trùng khi update để đơn giản

            int result = authorDAO.update(author);
            if (result > 0) {
                loadData(); // Reload cache
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAuthor(int id) {
        try {
            int result = authorDAO.delete(id);
            if (result > 0) {
                loadData(); // Reload cache
                return true;
            }
        } catch (SQLException e) {
            // Có thể lỗi do ràng buộc khóa ngoại (đang có sách thuộc tác giả này)
            System.err.println("Không thể xóa tác giả ID " + id + " vì ràng buộc dữ liệu.");
            e.printStackTrace();
        }
        return false;
    }
}