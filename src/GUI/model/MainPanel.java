package GUI.model;

import javax.swing.*;
import java.awt.*;
import BUS.BookBUS;
import GUI.util.UIConstants;

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
        setBackground(UIConstants.BACKGROUND_COLOR);
        initComponents();
    }

    private void initComponents() {
        // 1. Header (Luôn hiển thị ở trên cùng)
        header = new Header(bookBUS);
        add(header, BorderLayout.NORTH);

        // 2. Center Panel (Dùng CardLayout để tráo đổi nội dung)
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setOpaque(false);

        // Khởi tạo các màn hình con
        pnlBook = new BookTablePanel(bookBUS);
        pnlInvoice = new InvoiceTablePanel();

        // Thêm vào centerPanel với tên định danh (Key)
        centerPanel.add(pnlBook, "BOOK");
        centerPanel.add(pnlInvoice, "SALES");

        add(centerPanel, BorderLayout.CENTER);

        // Mặc định ban đầu Header điều khiển bảng Sách
        header.setPanelTable(pnlBook);
        header.setPanelInvoice(pnlInvoice);
    }

    // Hàm chuyển tab (Được gọi từ MainFrame)
    public void showPanel(String panelName) {
        cardLayout.show(centerPanel, panelName);

        header.setVisible(true);
        if (panelName.equals("BOOK")) { // Hoặc chỉ setVisible cho Toolbar bên trong
            header.setPanelTable(pnlBook); // Gán lại bảng để Header tìm kiếm đúng
        } else if (panelName.equals("SALES")) {
        }
    }
}