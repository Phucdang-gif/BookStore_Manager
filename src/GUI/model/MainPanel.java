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
    private InvoiceTablePanel pnlInvoice;
    private AuthorPanel pnlAuthor;
    private CategoryPanel pnlCategory;
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
        pnlInvoice = new InvoiceTablePanel();
        pnlAuthor = new AuthorPanel();
        pnlCategory = new CategoryPanel(this);

        // Thêm các màn hình con vào Center Panel
        centerPanel.add(pnlBook, "BOOK");
        centerPanel.add(pnlInvoice, "SALES");
        centerPanel.add(pnlAuthor, "AUTHOR");
        centerPanel.add(pnlCategory, "CATEGORY");

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
            case "SALES":
                // InvoiceTablePanel PHẢI implements SearchAndActions
                // header.setController((SearchAndActions) pnlInvoice);
                break;
            case "AUTHOR":
                // AuthorPanel PHẢI implements SearchAndActions
                header.setController((FeatureController) pnlAuthor);
                break;
            case "CATEGORY":
                setHeaderVisible(false);
                pnlCategory.resetToDashboard();
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