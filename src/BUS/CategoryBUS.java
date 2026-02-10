package BUS;

import DAO.CategoryDAO;
import DTO.CategoryDTO;
import java.util.ArrayList;

public class CategoryBUS {
    private CategoryDAO categoryDAO;
    private ArrayList<CategoryDTO> categoryList;

    public CategoryBUS() {
        this.categoryDAO = new CategoryDAO();
        loadData();
    }

    public void loadData() {
        this.categoryList = categoryDAO.selectAll();
        if (this.categoryList == null) {
            this.categoryList = new ArrayList<>();
        }
    }

    public ArrayList<CategoryDTO> getAll() {
        return categoryList;
    }

    public String getNameById(int id) {
        for (CategoryDTO cat : categoryList) {
            if (cat.getId() == id)
                return cat.getName();
        }
        return "";
    }

    public CategoryDTO getById(int id) {
        for (CategoryDTO cat : categoryList) {
            if (cat.getId() == id)
                return cat;
        }
        return null;
    }

    public boolean add(CategoryDTO category) {
        // Kiểm tra trùng tên
        if (categoryDAO.checkDuplicate(category.getName())) {
            return false;
        }

        int result = categoryDAO.insert(category);
        if (result > 0) {
            loadData(); // Reload data sau khi thêm
            return true;
        }
        return false;
    }

    public boolean update(CategoryDTO category) {
        // Kiểm tra trùng tên (trừ chính nó)
        if (categoryDAO.checkDuplicateExclude(category.getName(), category.getId())) {
            return false;
        }

        int result = categoryDAO.update(category);
        if (result > 0) {
            loadData(); // Reload data sau khi cập nhật
            return true;
        }
        return false;
    }

    public boolean delete(int id) {
        int result = categoryDAO.delete(id);
        if (result > 0) {
            loadData(); // Reload data sau khi xóa
            return true;
        }
        return false;
    }

    public ArrayList<CategoryDTO> search(String keyword) {
        ArrayList<CategoryDTO> result = new ArrayList<>();
        keyword = keyword.toLowerCase();

        for (CategoryDTO cat : categoryList) {
            if (cat.getName().toLowerCase().contains(keyword) ||
                    String.valueOf(cat.getId()).contains(keyword)) {
                result.add(cat);
            }
        }
        return result;
    }

    public boolean checkDuplicate(String name) {
        return categoryDAO.checkDuplicate(name);
    }

    public boolean checkDuplicateExclude(String name, int excludeId) {
        return categoryDAO.checkDuplicateExclude(name, excludeId);
    }
}