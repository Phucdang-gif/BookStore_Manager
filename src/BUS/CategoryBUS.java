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

    // Hàm cực kỳ quan trọng để mapping tên hiển thị trong BookDialog
    public String getNameById(int id) {
        for (CategoryDTO cat : categoryList) {
            if (cat.getId() == id)
                return cat.getName();
        }
        return "";
    }
}