package DTO;

import java.util.Objects;

public class AuthorDTO {
    private int authorID;
    private String authorName;

    public AuthorDTO() {
    }

    public AuthorDTO(int authorID, String authorName) {
        this.authorID = authorID;
        this.authorName = authorName;
    }

    public int getAuthorId() {
        return authorID;
    }

    public void setAuthorId(int authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return this.getAuthorName(); // Trả về tên để hiển thị trên ComboBox
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AuthorDTO authorDTO = (AuthorDTO) o;
        return authorID == authorDTO.authorID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorID);
    }
}
