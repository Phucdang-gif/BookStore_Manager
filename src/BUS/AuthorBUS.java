package BUS;

import DAO.AuthorDAO;
import DTO.AuthorDTO;
import DTO.ValidationResult;

import java.util.ArrayList;

public class AuthorBUS {
    private AuthorDAO authorDAO = new AuthorDAO();
    private static ArrayList<AuthorDTO> listAuthor = new ArrayList<>();

    public AuthorBUS() {
        if (listAuthor.isEmpty()) {
            loadDataFromDB();
        }
    }

    public boolean loadDataFromDB() {
        try {
            listAuthor = authorDAO.selectAll();
            return listAuthor != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<AuthorDTO> getAll() {
        return listAuthor;
    }

    public AuthorDTO getById(int id) {
        try {
            return authorDAO.selectById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===================== THÊM MỚI =====================

    public ValidationResult addAuthor(AuthorDTO author) {
        ValidationResult vr = Validator.validateAuthor(author);
        if (!vr.isValid())
            return vr;

        try {
            if (authorDAO.isNameExists(author.getAuthorName())) {
                vr.addError("authorName", "Tên tác giả \"" + author.getAuthorName() + "\" đã tồn tại");
                return vr;
            }

            boolean inserted = authorDAO.insert(author);
            if (inserted) {
                listAuthor.add(0, author);
                return vr; // isValid() == true
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vr.addError("system", "Lỗi hệ thống khi thêm tác giả!");
        return vr;
    }

    // ===================== CẬP NHẬT =====================

    public ValidationResult updateAuthor(AuthorDTO author) {
        ValidationResult vr = Validator.validateAuthor(author);
        if (!vr.isValid())
            return vr;

        // Kiểm tra trùng tên (trừ chính nó)
        boolean isDuplicate = listAuthor.stream()
                .anyMatch(a -> a.getAuthorName().equalsIgnoreCase(author.getAuthorName())
                        && a.getAuthorId() != author.getAuthorId());
        if (isDuplicate) {
            vr.addError("authorName", "Tên tác giả \"" + author.getAuthorName() + "\" đã tồn tại");
            return vr;
        }

        try {
            boolean updated = authorDAO.update(author);
            if (updated) {
                for (int i = 0; i < listAuthor.size(); i++) {
                    if (listAuthor.get(i).getAuthorId() == author.getAuthorId()) {
                        listAuthor.set(i, author);
                        break;
                    }
                }
                return vr; // isValid() == true
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vr.addError("system", "Lỗi hệ thống khi cập nhật tác giả!");
        return vr;
    }

    // ===================== XÓA =====================

    public ValidationResult deleteAuthor(int authorId) {
        ValidationResult vr = new ValidationResult();
        try {
            boolean deleted = authorDAO.delete(authorId);
            if (deleted) {
                listAuthor.removeIf(a -> a.getAuthorId() == authorId);
                return vr; // isValid() == true
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                vr.addError("system", "Không thể xóa! Tác giả này đang liên kết với một hoặc nhiều sách.");
                return vr;
            }
            e.printStackTrace();
        }

        vr.addError("system", "Lỗi hệ thống khi xóa tác giả!");
        return vr;
    }
}