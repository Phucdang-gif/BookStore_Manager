package GUI.dialog;

import DTO.BookDTO;
import GUI.dialog.book.BookDialog;
import GUI.dialog.book.DialogMode;

import javax.swing.*;
// import java.time.LocalDateTime;

public class BookDialogTest {
    public static void main(String[] args) {
        // 1. Giả lập giao diện hệ thống (để nhìn đẹp như thật)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Cần một JFrame giả làm "cha" (Owner) cho Dialog
            JFrame dummyFrame = new JFrame();
            // Tạo một cuốn sách giả (Dummy Data)
            BookDTO dummyBook = new BookDTO();
            dummyBook.setBookId(99);
            dummyBook.setBookTitle("Harry Potter và Hòn Đá Phù Thủy");
            dummyBook.setIsbn("978-1234567890");
            dummyBook.setPublicationYear(2000);
            dummyBook.setPageCount(350);
            dummyBook.setImportPrice(100000);
            dummyBook.setSellingPrice(150000);
            dummyBook.setStockQuantity(50);
            dummyBook.setPublisherId(1); // Giả sử ID 1 tồn tại trong ComboBox của bạn
            dummyBook.setCategoryId(2); // Giả sử ID 2 tồn tại

            // --- TRƯỜNG HỢP 1: TEST GIAO DIỆN THÊM MỚI ---
            System.out.println("Đang mở form THÊM MỚI...");
            BookDialog dialogAdd = new BookDialog(dummyFrame, dummyBook, DialogMode.READ);
            dialogAdd.setVisible(true);
            // (Chương trình sẽ dừng ở đây cho đến khi bạn tắt dialog Add)

            // --- TRƯỜNG HỢP 2: TEST GIAO DIỆN SỬA (Dữ liệu giả) ---
            System.out.println("Đang mở form SỬA...");

            // Kết thúc test
            System.exit(0);
        });
    }
}