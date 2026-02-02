package GUI.dialog.invoice;

import GUI.components.RoundedPanel;
import GUI.components.SearchTextField;
import GUI.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InvoiceDialogView extends JPanel {
    // --- Public Components (Để Controller truy cập) ---
    public JLabel lblInvoiceCode;
    public JLabel lblDate;
    public JTextField txtEmployeeName;
    public JTextField txtCustomerName;

    public SearchTextField txtSearchBook;
    public JTable tblBooks;
    public DefaultTableModel modelBooks;

    public JTable tblCart;
    public DefaultTableModel modelCart;

    public JLabel lblTotalMoney;
    public JTextField txtMoneyReceived;
    public JLabel lblChangeAmount;
    public JButton btnPay;

    public InvoiceDialogView() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // A. Thông tin chung (Top)
        add(createInfoPanel(), BorderLayout.NORTH);

        // B. Khu vực chính (Center)
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlCenter.setOpaque(false);
        pnlCenter.add(createLeftPanel()); // Kho sách
        pnlCenter.add(createRightPanel()); // Giỏ hàng

        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createInfoPanel() {
        RoundedPanel pnl = new RoundedPanel(15, Color.WHITE);
        pnl.setLayout(new GridLayout(2, 4, 20, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pnl.setPreferredSize(new Dimension(0, 100));

        pnl.add(new JLabel("Mã hóa đơn (Auto):"));
        lblInvoiceCode = new JLabel("#NEW");
        lblInvoiceCode.setForeground(Color.BLUE);
        lblInvoiceCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnl.add(lblInvoiceCode);

        pnl.add(new JLabel("Ngày lập:"));
        lblDate = new JLabel("...");
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnl.add(lblDate);

        pnl.add(new JLabel("Nhân viên:"));
        txtEmployeeName = new JTextField();
        txtEmployeeName.setEditable(false);
        txtEmployeeName.setBackground(new Color(240, 240, 240));
        pnl.add(txtEmployeeName);

        pnl.add(new JLabel("Khách hàng:"));
        txtCustomerName = new JTextField("Khách lẻ");
        pnl.add(txtCustomerName);

        return pnl;
    }

    private JPanel createLeftPanel() {
        RoundedPanel pnl = new RoundedPanel(20, Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header tìm kiếm
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(new JLabel("KHO SÁCH"), BorderLayout.NORTH);
        txtSearchBook = new SearchTextField();
        header.add(txtSearchBook, BorderLayout.CENTER);
        pnl.add(header, BorderLayout.NORTH);

        // Bảng sách
        String[] bookHeader = { "Mã SP", "Tên sản phẩm", "Đơn giá", "Tồn kho" };
        modelBooks = new DefaultTableModel(bookHeader, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblBooks = new JTable(modelBooks);
        tblBooks.setRowHeight(35);
        tblBooks.getTableHeader().setBackground(UIConstants.TABLE_HEADER_BACKGROUND);
        tblBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Độ rộng cột
        tblBooks.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblBooks.getColumnModel().getColumn(1).setPreferredWidth(200);

        JScrollPane sc = new JScrollPane(tblBooks);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(null);
        pnl.add(sc, BorderLayout.CENTER);

        return pnl;
    }

    private JPanel createRightPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setOpaque(false);

        // --- GIỎ HÀNG ---
        RoundedPanel pnlCart = new RoundedPanel(20, Color.WHITE);
        pnlCart.setLayout(new BorderLayout());
        pnlCart.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("GIỎ HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlCart.add(lblTitle, BorderLayout.NORTH);

        String[] cartHeader = { "STT", "Mã SP", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền" };
        modelCart = new DefaultTableModel(cartHeader, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblCart = new JTable(modelCart);
        tblCart.setRowHeight(35);

        // Cột giỏ hàng
        tblCart.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(40);

        JScrollPane scCart = new JScrollPane(tblCart);
        scCart.getViewport().setBackground(Color.WHITE);
        scCart.setBorder(null);
        pnlCart.add(scCart, BorderLayout.CENTER);

        // --- THANH TOÁN ---
        RoundedPanel pnlPay = new RoundedPanel(20, Color.WHITE);
        pnlPay.setLayout(new GridLayout(4, 2, 10, 10));
        pnlPay.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlPay.setPreferredSize(new Dimension(0, 180));

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
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlPay.add(new JLabel("Tổng tiền hàng:"));
        pnlPay.add(lblTotalMoney);
        pnlPay.add(new JLabel("Tiền khách đưa:"));
        pnlPay.add(txtMoneyReceived);
        pnlPay.add(new JLabel("Tiền thừa:"));
        pnlPay.add(lblChangeAmount);
        pnlPay.add(new JLabel(""));
        pnlPay.add(btnPay);

        container.add(pnlCart, BorderLayout.CENTER);
        container.add(pnlPay, BorderLayout.SOUTH);
        return container;
    }
}