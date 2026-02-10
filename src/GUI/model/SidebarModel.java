package GUI.model;

public class SidebarModel {
    private String title;
    private String iconPath;
    private String command; // ID để xử lý sự kiện (VD: BOOK, SALES, CATEGORY)
    // private String permissionID; // Sau này mở rộng: "PERM_VIEW_BOOK",
    // "PERM_VIEW_SALES"...

    public SidebarModel(String title, String iconPath, String command) {
        this.title = title;
        this.iconPath = iconPath;
        this.command = command;
    }

    public String getTitle() {
        return title;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getCommand() {
        return command;
    }
}