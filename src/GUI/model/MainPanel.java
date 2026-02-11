package GUI.model;

import javax.swing.*;
import java.awt.*;
import BUS.BookBUS;
import GUI.util.ThemeColor;

public class MainPanel extends JPanel {
    private Header header;
    private JPanel centerPanel;
    private CardLayout cardLayout;

    // Các màn hình con
    private BookTablePanel pnlBook;
    private GroupPanel pnlGroup;

    private BookBUS bookBUS;

    public MainPanel() {
        bookBUS = new BookBUS();
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ThemeColor.bgPanel);
        initComponents();
    }

    private void initComponents() {
        header = new Header();
        add(header, BorderLayout.NORTH);

        // 2. Center Panel (Dùng CardLayout để tráo đổi nội dung)
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setOpaque(true);
        centerPanel.setBackground(ThemeColor.bgPanel);

        // Khởi tạo các màn hình con
        pnlBook = new BookTablePanel(bookBUS);
        pnlGroup = new GroupPanel(this);

        // Thêm các màn hình con vào Center Panel
        centerPanel.add(pnlBook, "BOOK");
        centerPanel.add(pnlGroup, "GROUP");

        add(centerPanel, BorderLayout.CENTER);

        // Mặc định ban đầu Header điều khiển bảng Sách
        header.setController(pnlBook);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setOpaque(true);
        setBackground(ThemeColor.bgPanel);
        if (centerPanel != null) {
            centerPanel.setOpaque(true);
            centerPanel.setBackground(ThemeColor.bgPanel);
        }
    }

    // Hàm chuyển tab (Được gọi từ MainFrame)
    public void showPanel(String panelName) {
        cardLayout.show(centerPanel, panelName);
        switch (panelName) {
            case "BOOK":
                header.setController(pnlBook);
                setHeaderVisible(true);
                break;
            case "GROUP":
                pnlGroup.resetToDashboard(); // Reset về màn hình 3 nút (Author, Publisher, Category)
                setHeaderVisible(false); // Ẩn Header chính của MainPanel vì GroupPanel có HeaderBar riêng
                break;
            default:
                // Nếu chưa có panel nào thì set null để vô hiệu hóa nút
                header.setController(null);
                break;
        }
    }

    public Header getHeader() {
        return header;
    }

    public void setHeaderVisible(boolean visible) {
        if (header != null) {
            header.setVisible(visible);
            this.revalidate();
            this.repaint();
        }
    }

}