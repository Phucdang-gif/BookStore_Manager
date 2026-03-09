package GUI.model;

import javax.swing.*;
import java.awt.*;
import BUS.BookBUS;
import BUS.AuthorBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import GUI.util.ThemeColor;

public class MainPanel extends JPanel {
    private Header header;
    private JPanel centerPanel;
    private CardLayout cardLayout;

    // Các màn hình con
    private BookTablePanel pnlBook;
    private GroupPanel pnlGroup;
    private AccountPanel pnlAccount;
    private PermissionGroupPanel pnlPermissionGroup;
    private ImportReceiptPanel pnlImport;
    private EmployeePanel pnlEmployee;
    private InvoicePanel pnlInvoice;
    private CustomerPanel pnlCustomer;
    private DiscountPanel pnlDiscount;
    private StatisticPanel pnlStatistic;

    private BookBUS bookBUS;
    private AuthorBUS authorBUS;
    private CategoryBUS categoryBUS;
    private PublisherBUS publisherBUS;

    public MainPanel() {
        bookBUS = new BookBUS();
        authorBUS = new AuthorBUS();
        categoryBUS = new CategoryBUS();
        publisherBUS = new PublisherBUS();

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
        pnlGroup = new GroupPanel(this, bookBUS, authorBUS, categoryBUS, publisherBUS);
        pnlImport = new ImportReceiptPanel();
        pnlEmployee = new EmployeePanel();
        pnlAccount = new AccountPanel();
        pnlPermissionGroup = new PermissionGroupPanel();
        pnlInvoice = new InvoicePanel();
        pnlCustomer = new CustomerPanel();
        pnlDiscount = new DiscountPanel();
        pnlStatistic = new StatisticPanel();

        // Thêm các màn hình con vào Center Panel
        centerPanel.add(pnlBook, "BOOK");
        centerPanel.add(pnlGroup, "GROUP");
        centerPanel.add(pnlImport, "IMPORT");
        centerPanel.add(pnlEmployee, "EMPLOYEE");
        centerPanel.add(pnlAccount, "ACCOUNT");
        centerPanel.add(pnlPermissionGroup, "PERMISSION");
        centerPanel.add(pnlDiscount, "DISCOUNT");
        centerPanel.add(pnlCustomer, "CUSTOMER");
        centerPanel.add(pnlInvoice, "INVOICE");
        centerPanel.add(pnlStatistic, "STATISTIC");

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
                bookBUS.loadDataFromDB();
                break;
            case "GROUP":
                pnlGroup.resetToDashboard(); // Reset về màn hình 3 nút (Author, Publisher, Category)
                setHeaderVisible(false);
                authorBUS.loadDataFromDB();
                categoryBUS.loadDataFromDB();
                publisherBUS.loadDataFromDB();
                break;
            case "ACCOUNT":
                header.setController(pnlAccount);
                setHeaderVisible(true);

                break;
            case "PERMISSION":
                header.setController(pnlPermissionGroup);
                setHeaderVisible(true);
                break;
            case "IMPORT":
                header.setController(pnlImport);
                setHeaderVisible(true);
                break;
            case "EMPLOYEE":
                header.setController(pnlEmployee);
                setHeaderVisible(true);
                break;
            case "INVOICE":
                header.setController(pnlInvoice);
                setHeaderVisible(true);
                break;
            case "CUSTOMER":
                header.setController(pnlCustomer);
                setHeaderVisible(true);
                break;
            case "DISCOUNT":
                header.setController(pnlDiscount);
                setHeaderVisible(true);
                break;
            case "STATISTIC":
                header.setController(null);
                setHeaderVisible(false);
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

    public void setOnAvatarUpdated(Runnable callback) {
        if (pnlEmployee != null) {
            pnlEmployee.setOnAvatarUpdated(callback);
        }
    }
}