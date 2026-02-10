package GUI.model;

public interface FeatureController {
    void onAdd();

    void onEdit();

    void onDelete();

    void onDetail();

    void onSearch(String text);

    void onRefresh();

    void onExportExcel();

    void onImportExcel();

    // Trả về trạng thái hiển thị của 6 nút
    boolean[] getButtonConfig();

    default boolean hasSearch() {
        return true; // Mặc định là hiện thanh tìm kiếm
    }

    default boolean hasRefresh() {
        return true; // Mặc định là hiện nút làm mới
    }
}
