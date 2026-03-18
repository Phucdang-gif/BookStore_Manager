package BUS;

import DAO.CategoryDAO;
import DTO.CategoryDTO;
import DTO.ValidationResult;
import java.util.ArrayList;

public class CategoryBUS {
    private CategoryDAO categoryDAO;
    private ArrayList<CategoryDTO> categoryList;

    public CategoryBUS() {
        this.categoryDAO = new CategoryDAO();
        loadDataFromDB();
    }

    public void loadDataFromDB() {
        this.categoryList = categoryDAO.selectAll();
        if (this.categoryList == null)
            this.categoryList = new ArrayList<>();
    }

    public ArrayList<CategoryDTO> getAll() {
        return categoryList;
    }

    public CategoryDTO getById(int id) {
        return categoryList.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public String getNameById(int id) {
        CategoryDTO cat = getById(id);
        return cat != null ? cat.getName() : "";
    }

    public ValidationResult add(CategoryDTO category) {
        ValidationResult vr = Validator.validateCategory(category, this.categoryList);
        if (!vr.isValid())
            return vr;

        int result = categoryDAO.insert(category);
        if (result > 0) {
            loadDataFromDB();
            return vr;
        } else {
            vr.addError("system", "Lỗi hệ thống khi thêm thể loại!");
        }
        return vr;
    }

    // ===================== CẬP NHẬT =====================

    public ValidationResult update(CategoryDTO category) {
        ValidationResult vr = Validator.validateCategory(category, this.categoryList);
        if (!vr.isValid())
            return vr;

        int result = categoryDAO.update(category);
        if (result > 0) {
            loadDataFromDB();
            return vr;
        } else {
            vr.addError("system", "Lỗi hệ thống khi cập nhật thể loại!");
        }
        return vr;
    }

    // ===================== XÓA =====================

    public ValidationResult delete(int id) {
        ValidationResult vr = new ValidationResult();
        try {
            int result = categoryDAO.delete(id);
            if (result > 0) {
                loadDataFromDB();
                return vr; // isValid() == true
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                vr.addError("system", "Không thể xóa! Thể loại này đang được sử dụng bởi một hoặc nhiều sách.");
                return vr;
            }
            e.printStackTrace();
        }

        vr.addError("system", "Lỗi hệ thống khi xóa thể loại!");
        return vr;
    }

    public ArrayList<CategoryDTO> search(String keyword) {
        ArrayList<CategoryDTO> result = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (CategoryDTO cat : categoryList) {
            if (cat.getName().toLowerCase().contains(key) || String.valueOf(cat.getId()).contains(key))
                result.add(cat);
        }
        return result;
    }
}