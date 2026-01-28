package GUI.dialog.book;

import DTO.BookDTO;
import javax.swing.*;

public class BookDialog extends JDialog {
    private BookDialogView view;
    private BookDialogController controller;

    public BookDialog(JFrame parent, BookDTO _book, DialogMode _mode) {
        super(parent, "Quản lý sách", true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        view = new BookDialogView();

        // 1. Khởi tạo Controller với đủ 3 tham số
        controller = new BookDialogController(view, _book, _mode);

        initFrame();
        add(view);

        // 2. GỌI CÁC HÀM SETUP (BẠN ĐANG THIẾU ĐOẠN NÀY)
        controller.loadComboBoxData(); // Nạp dữ liệu NXB, Danh mục
        controller.applyModeSettings(); // Khóa form nếu là xem, Mở form nếu là thêm/sửa
        controller.fillData(); // Đổ dữ liệu
        controller.initEvents(); // Gán sự kiện nút bấm
    }

    private void initFrame() {
        setSize(1000, 650);
        setLocationRelativeTo(getOwner());
    }

    public boolean isSucceeded() {
        return controller.isSucceeded();
    }
}