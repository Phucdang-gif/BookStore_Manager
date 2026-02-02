package GUI.dialog.sale;

import GUI.components.RoundedPanel;
import GUI.components.SearchTextField;
import GUI.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SalesDialog extends JDialog {
    // --- KHAI BÁO COMPONENTS (Để Controller truy cập được) ---
    private JTable tblBooks;
    private DefaultTableModel modelBooks;
    private JTable tblCart;
    private DefaultTableModel modelCart;
    private JLabel lblTotalMoney;
    private JTextField txtMoneyReceived;
    private JLabel lblChangeAmount;
    private JButton btnPay;
    private SearchTextField txtSearchBook;
    private JPanel mainPanel; // Để add key listener

    // Controller
    private SalesDialogController controller;

    public SalesDialog(JFrame parent) {
        super(parent, "Tạo Hóa Đơn Bán Hàng", true);
        setSize(1250, 750);
        setLocationRelativeTo(null);

        // 1. Khởi tạo giao diện
        initComponents();

        // 2. Khởi tạo Controller và gắn kết
        controller = new SalesDialogController(this);
        controller.initEvents(); // Gắn sự kiện
        controller.loadBookData(""); // Load dữ liệu ban đầu
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setFocusable(true); // Để bắt sự kiện phím tắt (F9)
        setContentPane(mainPanel);

        // --- PHẦN TRÁI ---
        JPanel pnlLeft = createLeftPanel();
        mainPanel.add(pnlLeft, BorderLayout.CENTER);

        // --- PHẦN PHẢI ---
        JPanel pnlRight = createRightPanel();
        mainPanel.add(pnlRight, BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        // Sử dụng RoundedPanel (Nếu bạn đã có class này)
        JPanel pnl = new JPanel(new BorderLayout(0, 10)); // Hoặc new RoundedPanel(20, Color.WHITE)
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtSearchBook = new SearchTextField();
        pnl.add(txtSearchBook, BorderLayout.NORTH);

        String[] bookHeader = { "ID", "Tên sách", "Giá bán", "Tồn kho" };
        modelBooks = new DefaultTableModel(bookHeader, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblBooks = new JTable(modelBooks);
        tblBooks.setRowHeight(35);
        tblBooks.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblBooks.getTableHeader().setBackground(UIConstants.TABLE_HEADER_BACKGROUND);
        tblBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sc = new JScrollPane(tblBooks);
        sc.getViewport().setBackground(Color.WHITE);
        pnl.add(sc, BorderLayout.CENTER);

        return pnl;
    }

    private JPanel createRightPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(550, 0));

        // A. Giỏ hàng
        JPanel pnlCart = new JPanel(new BorderLayout()); // Hoặc RoundedPanel
        pnlCart.setBackground(Color.WHITE);
        pnlCart.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("Giỏ hàng (Click phải để xóa)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlCart.add(lblTitle, BorderLayout.NORTH);

        String[] cartHeader = { "ID", "Tên sách", "SL", "Đơn giá", "Thành tiền" };
        modelCart = new DefaultTableModel(cartHeader, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblCart = new JTable(modelCart);
        tblCart.setRowHeight(35);

        // Menu chuột phải xóa
        JPopupMenu popup = new JPopupMenu();
        JMenuItem itemDel = new JMenuItem("Xóa khỏi giỏ");
        // Logic xóa sẽ được Controller xử lý, View chỉ tạo Menu thôi
        itemDel.addActionListener(e -> controller.removeFromCart());
        popup.add(itemDel);
        tblCart.setComponentPopupMenu(popup);

        pnlCart.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        // B. Thanh toán
        JPanel pnlPay = new JPanel(new GridLayout(4, 2, 10, 15)); // Hoặc RoundedPanel
        pnlPay.setBackground(Color.WHITE);
        pnlPay.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblTotalMoney = new JLabel("0 VNĐ");
        lblTotalMoney.setForeground(Color.RED);
        lblTotalMoney.setFont(new Font("Segoe UI", Font.BOLD, 18));

        txtMoneyReceived = new JTextField();
        txtMoneyReceived.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblChangeAmount = new JLabel("0 VNĐ");
        lblChangeAmount.setForeground(new Color(0, 153, 51));
        lblChangeAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnPay = new JButton("THANH TOÁN (F9)");
        btnPay.setBackground(UIConstants.GREEN_BACKGROUND);
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));

        pnlPay.add(new JLabel("Tổng tiền:"));
        pnlPay.add(lblTotalMoney);
        pnlPay.add(new JLabel("Khách đưa:"));
        pnlPay.add(txtMoneyReceived);
        pnlPay.add(new JLabel("Tiền thừa:"));
        pnlPay.add(lblChangeAmount);
        pnlPay.add(new JLabel(""));
        pnlPay.add(btnPay);

        container.add(pnlCart, BorderLayout.CENTER);
        container.add(pnlPay, BorderLayout.SOUTH);
        return container;
    }

    // --- GETTERS (Để Controller lấy dùng) ---
    public JTable getTblBooks() {
        return tblBooks;
    }

    public DefaultTableModel getModelBooks() {
        return modelBooks;
    }

    public JTable getTblCart() {
        return tblCart;
    }

    public DefaultTableModel getModelCart() {
        return modelCart;
    }

    public SearchTextField getTxtSearch() {
        return txtSearchBook;
    }

    public JLabel getLblTotalMoney() {
        return lblTotalMoney;
    }

    public JTextField getTxtMoneyReceived() {
        return txtMoneyReceived;
    }

    public JLabel getLblChangeAmount() {
        return lblChangeAmount;
    }

    public JButton getBtnPay() {
        return btnPay;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // Delegate kết quả về Controller
    public boolean isSucceeded() {
        return controller.isSuccess();
    }
}