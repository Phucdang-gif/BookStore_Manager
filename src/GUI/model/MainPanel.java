package GUI.model;

import javax.swing.*;
import java.awt.*;
import BUS.BookBUS;
import GUI.util.ThemeColor;

public class MainPanel extends JPanel {
    private Header header;
    private JPanel centerPanel; // Container chứa các màn hình con
    private CardLayout cardLayout;

    // Các màn hình con
    private BookTablePanel pnlBook;
    private InvoiceTablePanel pnlInvoice;

    private BookBUS bookBUS;

    public MainPanel() {
        bookBUS = new BookBUS();
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ThemeColor.bgPanel);
        initComponents();
    }

    private void initComponents() {
        // 1. Header (Luôn hiển thị ở trên cùng)
        header = new Header(bookBUS);
        add(header, BorderLayout.NORTH);

        // 2. Center Panel (Dùng CardLayout để tráo đổi nội dung)
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setOpaque(true);
        centerPanel.setBackground(ThemeColor.bgPanel);

        // Khởi tạo các màn hình con
        pnlBook = new BookTablePanel(bookBUS);
        pnlInvoice = new InvoiceTablePanel();

        centerPanel.add(pnlBook, "BOOK");
        centerPanel.add(pnlInvoice, "SALES");
        add(centerPanel, BorderLayout.CENTER);

        // Mặc định ban đầu Header điều khiển bảng Sách
        header.setPanelTable(pnlBook);
        header.setPanelInvoice(pnlInvoice);
    }

    // FlatLaf gọi updateUI() khi đổi theme → cần re-apply màu ở đây
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

        header.setVisible(true);
        header.setPnlName(panelName);
        if (panelName.equals("BOOK")) {
            header.setPanelTable(pnlBook);
        } else if (panelName.equals("SALES")) {
            // Logic riêng cho sales nếu cần
        }
    }
}