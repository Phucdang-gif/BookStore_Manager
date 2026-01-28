package DTO;

import java.util.Objects;

public class CategoryDTO {
    private int categoryID;
    private String categoryName, status;

    public CategoryDTO() {
    }

    public CategoryDTO(int categoryID, String categoryName, String status) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.status = status;
    }

    public int getID() {
        return categoryID;
    }

    public void setID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return categoryName;
    }

    public void setName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CategoryDTO that = (CategoryDTO) o;
        return categoryID == that.categoryID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryID);
    }
}