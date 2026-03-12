package GUI.dialog;

import BUS.*;
import DAO.*;
import DTO.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class CreateInvoiceDialog extends JDialog {

    // Các BUS, DAO xử lý dữ liệu (Chuẩn 3 lớp)
    private BookBUS bookBUS = new BookBUS();
    private CustomerBUS customerBUS = new CustomerBUS();
    private DiscountServiceBUS discountBUS = new DiscountServiceBUS();
    private InvoiceDetailBUS detailBUS = new InvoiceDetailBUS();
    private InvoiceServiceBUS invoiceServiceBUS = new InvoiceServiceBUS();
    private InvoiceBUS invoiceBUS = new InvoiceBUS();

    // Các thành phần giao diện
    private JTable tblBooks, tblInvoiceDetails;
    private DefaultTableModel bookModel, detailModel;
    private JComboBox<CustomerDTO> cbCustomer;
    private JComboBox<DiscountServiceDTO> cbDiscount;
    private JComboBox<String> cbPaymentMethod;
    private JLabel lblTotalAmount, lblDiscountAmount, lblFinalAmount;

    // --- THÊM: Các thành phần cho tính năng Điểm ---
    private JLabel lblCurrentPoints, lblPointsEarned;
    private JTextField txtPointsUsed;
    private final double DIEM_TO_TIEN = 100; // 1 điểm = 100 VNĐ
    private final double TIEN_TO_DIEM = 10000; // 10.000 VNĐ = 1 điểm
    // ----------------------------------------------

    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private double currentTotal = 0; // Tổng tiền hàng chưa giảm

    public CreateInvoiceDialog(Frame owner, boolean modal) {
        super(owner, modal);
        setTitle("LẬP HÓA ĐƠN MỚI");
        setSize(1100, 650);
        setLocationRelativeTo(owner);
        initUI();
        loadData();
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout(10, 10));
        JPanel pnlMain = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================================
        // BÊN TRÁI: KHO SÁCH (Giữ nguyên gốc 100%)
        // ==========================================
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), "KHO SÁCH"));

        String[] bookCols = { "ID", "Tên Sách", "Giá Bán", "Tồn Kho" };
        bookModel = new DefaultTableModel(bookCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblBooks = new JTable(bookModel);
        tblBooks.setRowHeight(30);
        tblBooks.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlLeft.add(new JScrollPane(tblBooks), BorderLayout.CENTER);

        JButton btnAddDetail = new JButton("Đưa vào Chi tiết HĐ >>");
        btnAddDetail.setPreferredSize(new Dimension(100, 40));
        btnAddDetail.setBackground(new Color(15, 108, 189));
        btnAddDetail.setForeground(Color.WHITE);
        btnAddDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddDetail.setOpaque(true);
        btnAddDetail.setBorderPainted(false);
        btnAddDetail.setFocusPainted(false);
        btnAddDetail.addActionListener(e -> addInvoiceDetail());
        pnlLeft.add(btnAddDetail, BorderLayout.SOUTH);

        // ==========================================
        // BÊN PHẢI: CHI TIẾT & THANH TOÁN
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(5, 5));
        pnlRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), "CHI TIẾT HÓA ĐƠN"));

        String[] detailCols = { "ID", "Tên Sách", "Đơn Giá", "SL", "Thành Tiền" };
        detailModel = new DefaultTableModel(detailCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblInvoiceDetails = new JTable(detailModel);
        tblInvoiceDetails.setRowHeight(30);
        tblInvoiceDetails.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnRemoveDetail = new JButton("Xóa dòng đã chọn");
        btnRemoveDetail.setForeground(Color.RED);
        btnRemoveDetail.setFocusPainted(false);
        btnRemoveDetail.addActionListener(e -> removeInvoiceDetail());

        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.add(new JScrollPane(tblInvoiceDetails), BorderLayout.CENTER);
        pnlTableContainer.add(btnRemoveDetail, BorderLayout.SOUTH);

        pnlRight.add(pnlTableContainer, BorderLayout.CENTER);

        // ----------------------------------------------------
        // KHU VỰC TỔNG KẾT (Chèn thêm giao diện Điểm)
        // ----------------------------------------------------
        JPanel pnlBottomRight = new JPanel(new BorderLayout(0, 10));
        pnlBottomRight.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // THAY ĐỔI: Tăng Grid từ 6 dòng lên 9 dòng để đủ chỗ nhét các ô Điểm
        JPanel pnlInfo = new JPanel(new GridLayout(9, 2, 5, 8));

        pnlInfo.add(new JLabel("Khách hàng:"));
        cbCustomer = new JComboBox<>();
        // THÊM: Bắt sự kiện đổi khách hàng để hiện Điểm
        cbCustomer.addActionListener(e -> {
            CustomerDTO cus = (CustomerDTO) cbCustomer.getSelectedItem();
            if (cus != null && cus.getCustomerId() > 0) {
                lblCurrentPoints.setText(cus.getLoyaltyPoints() + " điểm");
                txtPointsUsed.setEnabled(true); // Mở khóa ô nhập điểm
            } else {
                lblCurrentPoints.setText("0 điểm");
                txtPointsUsed.setText("0");
                txtPointsUsed.setEnabled(false); // Khóa lại nếu là khách vãng lai
            }
            updateTotals();
        });
        pnlInfo.add(cbCustomer);

        // --- THÊM: Hiển thị điểm hiện có ---
        pnlInfo.add(new JLabel("Điểm hiện có:"));
        lblCurrentPoints = new JLabel("0 điểm");
        lblCurrentPoints.setForeground(new Color(0, 102, 204));
        lblCurrentPoints.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlInfo.add(lblCurrentPoints);

        // --- THÊM: Ô nhập điểm muốn xài ---
        pnlInfo.add(new JLabel("Dùng điểm (1đ = 100đ):"));
        txtPointsUsed = new JTextField("0");
        txtPointsUsed.setEnabled(false);
        txtPointsUsed.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateTotals();
            }
        });
        pnlInfo.add(txtPointsUsed);

        pnlInfo.add(new JLabel("Khuyến mãi:"));
        cbDiscount = new JComboBox<>();
        cbDiscount.addActionListener(e -> updateTotals());
        pnlInfo.add(cbDiscount);

        pnlInfo.add(new JLabel("Thanh toán:"));
        cbPaymentMethod = new JComboBox<>(new String[] { "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử" });
        pnlInfo.add(cbPaymentMethod);

        pnlInfo.add(new JLabel("Tổng tiền hàng:"));
        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlInfo.add(lblTotalAmount);

        pnlInfo.add(new JLabel("Giảm giá (Mã + Điểm):"));
        lblDiscountAmount = new JLabel("0 VNĐ");
        lblDiscountAmount.setForeground(Color.RED);
        lblDiscountAmount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlInfo.add(lblDiscountAmount);

        // --- THÊM: Ô hiện điểm tích lũy mới ---
        pnlInfo.add(new JLabel("Điểm thưởng mới:"));
        lblPointsEarned = new JLabel("+0 điểm");
        lblPointsEarned.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPointsEarned.setForeground(new Color(255, 102, 0));
        pnlInfo.add(lblPointsEarned);

        pnlInfo.add(new JLabel("TỔNG CỘNG:"));
        lblFinalAmount = new JLabel("0 VNĐ");
        lblFinalAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFinalAmount.setForeground(new Color(0, 153, 51));
        pnlInfo.add(lblFinalAmount);

        JButton btnSaveInvoice = new JButton("THANH TOÁN & LƯU HÓA ĐƠN");
        btnSaveInvoice.setPreferredSize(new Dimension(100, 35)); // Chỉnh lại size một chút cho đẹp
        btnSaveInvoice.setBackground(new Color(0, 153, 51));
        btnSaveInvoice.setForeground(Color.WHITE);
        btnSaveInvoice.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSaveInvoice.setOpaque(true);
        btnSaveInvoice.setBorderPainted(false);
        btnSaveInvoice.setFocusPainted(false);
        btnSaveInvoice.addActionListener(e -> saveInvoice());

        pnlBottomRight.add(pnlInfo, BorderLayout.CENTER);
        pnlBottomRight.add(btnSaveInvoice, BorderLayout.SOUTH);

        pnlRight.add(pnlBottomRight, BorderLayout.SOUTH);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain, BorderLayout.CENTER);
    }

    private void loadData() {
        for (BookDTO b : bookBUS.getAll()) {
            if (b.getStockQuantity() > 0 && !"discontinued".equals(b.getStatus())) {
                bookModel.addRow(
                        new Object[] { b.getBookId(), b.getBookTitle(), b.getSellingPrice(), b.getStockQuantity() });
            }
        }
        cbCustomer.addItem(new CustomerDTO(0, "-- Khách vãng lai --", "", 0, null));
        for (CustomerDTO c : customerBUS.getAll()) {
            cbCustomer.addItem(c);
        }
        cbDiscount.addItem(new DiscountServiceDTO(0, "-- Không áp dụng --", "", 0, 0, 0, null, null, "", ""));
        for (DiscountServiceDTO d : discountBUS.getValidPromotions()) {
            cbDiscount.addItem(d);
        }
    }

    // Giữ nguyên 100% gốc
    private void addInvoiceDetail() {
        int row = tblBooks.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ kho!");
            return;
        }

        int bookId = (int) tblBooks.getValueAt(row, 0);
        String title = (String) tblBooks.getValueAt(row, 1);
        double price = (double) tblBooks.getValueAt(row, 2);
        int maxStock = (int) tblBooks.getValueAt(row, 3);

        String qtyStr = JOptionPane.showInputDialog(this, "Nhập số lượng xuất (Tồn kho: " + maxStock + "):", "1");
        if (qtyStr == null || qtyStr.trim().isEmpty())
            return;

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0 || qty > maxStock) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ hoặc vượt quá tồn kho!");
                return;
            }

            boolean exists = false;
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                if ((int) detailModel.getValueAt(i, 0) == bookId) {
                    int oldQty = (int) detailModel.getValueAt(i, 3);
                    if (oldQty + qty > maxStock) {
                        JOptionPane.showMessageDialog(this, "Tổng số lượng xuất vượt quá tồn kho hiện tại!");
                        return;
                    }
                    detailModel.setValueAt(oldQty + qty, i, 3);
                    detailModel.setValueAt((oldQty + qty) * price, i, 4);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                detailModel.addRow(new Object[] { bookId, title, price, qty, price * qty });
            }
            updateTotals();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên!");
        }
    }

    private void removeInvoiceDetail() {
        int row = tblInvoiceDetails.getSelectedRow();
        if (row != -1) {
            detailModel.removeRow(row);
            updateTotals();
        }
    }

    private void updateTotals() {
        currentTotal = 0;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            currentTotal += (double) detailModel.getValueAt(i, 4);
        }

        // 1. Tính giảm giá theo Khuyến mãi (Gốc)
        double discountVal = 0;
        DiscountServiceDTO selectedPromo = (DiscountServiceDTO) cbDiscount.getSelectedItem();

        if (selectedPromo != null && selectedPromo.getServiceId() != 0) {
            if (currentTotal >= selectedPromo.getMinimumAmount()) {
                if ("Phần trăm".equals(selectedPromo.getDiscountType())
                        || "percent".equals(selectedPromo.getDiscountType())) {
                    discountVal = currentTotal * (selectedPromo.getDiscountValue() / 100.0);
                } else {
                    discountVal = selectedPromo.getDiscountValue();
                }
                if (selectedPromo.getMaximumDiscount() > 0 && discountVal > selectedPromo.getMaximumDiscount()) {
                    discountVal = selectedPromo.getMaximumDiscount();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hóa đơn chưa đạt mức tối thiểu ("
                        + df.format(selectedPromo.getMinimumAmount()) + ") để áp mã này!");
                cbDiscount.setSelectedIndex(0);
            }
        }

        // --- THÊM: Tính tiền từ Điểm ---
        int pointsUsed = 0;
        CustomerDTO cus = (CustomerDTO) cbCustomer.getSelectedItem();
        int maxPoints = (cus != null && cus.getCustomerId() > 0) ? cus.getLoyaltyPoints() : 0;

        if (!txtPointsUsed.getText().trim().isEmpty()) {
            try {
                pointsUsed = Integer.parseInt(txtPointsUsed.getText().trim());
            } catch (Exception e) {
                pointsUsed = 0;
            }
        }

        // Chặn không cho xài quá số điểm đang có
        if (pointsUsed > maxPoints) {
            pointsUsed = maxPoints;
            // Dùng invokeLater để tránh lỗi nháy con trỏ khi đang gõ
            int finalPointsUsed = pointsUsed;
            SwingUtilities.invokeLater(() -> txtPointsUsed.setText(String.valueOf(finalPointsUsed)));
        }
        if (pointsUsed < 0) {
            pointsUsed = 0;
            SwingUtilities.invokeLater(() -> txtPointsUsed.setText("0"));
        }

        double pointsValue = pointsUsed * DIEM_TO_TIEN;

        // CỘNG GỘP TỔNG KHUYẾN MÃI = Khuyến mãi + Tiền từ điểm
        double totalDiscount = discountVal + pointsValue;
        // ------------------------------

        double finalAmount = currentTotal - totalDiscount;
        if (finalAmount < 0)
            finalAmount = 0;

        // --- THÊM: Tính điểm sẽ được cộng thêm ---
        int pointsEarned = 0;
        if (cus != null && cus.getCustomerId() > 0) {
            pointsEarned = (int) (finalAmount / TIEN_TO_DIEM);
        }
        // ----------------------------------------

        lblTotalAmount.setText(df.format(currentTotal));
        lblDiscountAmount.setText("- " + df.format(totalDiscount));
        lblFinalAmount.setText(df.format(finalAmount));
        lblPointsEarned.setText("+" + pointsEarned + " điểm");
    }

    private void saveInvoice() {
        try {
            // 1. GOM DỮ LIỆU TỪ GIAO DIỆN (Gốc 100%)
            boolean hasDetails = detailModel.getRowCount() > 0;

            CustomerDTO customer = (CustomerDTO) cbCustomer.getSelectedItem();
            int customerId = (customer != null && customer.getCustomerId() > 0) ? customer.getCustomerId() : 0;

            int employeeId = config.SessionManager.getCurrentAccount() != null
                    ? config.SessionManager.getCurrentAccount().getEmployeeId()
                    : 1;

            DiscountServiceDTO promo = (DiscountServiceDTO) cbDiscount.getSelectedItem();
            String paymentMethod = (String) cbPaymentMethod.getSelectedItem();

            updateTotals();
            String finalStr = lblFinalAmount.getText().replace(" VNĐ", "").replace(",", "").trim();
            String discStr = lblDiscountAmount.getText().replace("- ", "").replace(" VNĐ", "").replace(",", "").trim();
            double finalToPay = Double.parseDouble(finalStr);
            double discountApplied = Double.parseDouble(discStr); // Tổng giảm (bao gồm cả mã + điểm)

            // --- THÊM: Lấy số liệu điểm ---
            int pointsUsed = 0;
            try {
                pointsUsed = Integer.parseInt(txtPointsUsed.getText().trim());
            } catch (Exception e) {
            }
            double pointsValue = pointsUsed * DIEM_TO_TIEN;

            int pointsEarned = 0;
            try {
                pointsEarned = Integer.parseInt(lblPointsEarned.getText().replace("+", "").replace(" điểm", "").trim());
            } catch (Exception e) {
            }
            // ------------------------------

            // Đóng gói DTO (Gốc)
            InvoiceDTO invoice = new InvoiceDTO();
            invoice.setCustomerId(customerId);
            invoice.setEmployeeId(employeeId);
            invoice.setTotalAmount(currentTotal);
            invoice.setTotalDiscount(discountApplied);
            invoice.setFinalAmount(finalToPay);
            invoice.setPaymentMethod(paymentMethod);

            // --- THÊM: Gắn điểm vào DTO ---
            invoice.setPointsUsed(pointsUsed);
            invoice.setPointsValue(pointsValue);
            invoice.setPointsEarned(pointsEarned);
            // ------------------------------

            // 2. GỌI LỚP BUS KIỂM DUYỆT (Gốc)
            DTO.ValidationResult result = invoiceBUS.addInvoice(invoice, hasDetails);

            // 3. XỬ LÝ KẾT QUẢ HIỂN THỊ (Gốc)
            if (result.isValid()) {
                ArrayList<InvoiceDetailDTO> listDetails = new ArrayList<>();
                for (int i = 0; i < detailModel.getRowCount(); i++) {
                    int bId = (int) detailModel.getValueAt(i, 0);
                    double price = (double) detailModel.getValueAt(i, 2);
                    int qty = (int) detailModel.getValueAt(i, 3);
                    double subTotal = (double) detailModel.getValueAt(i, 4);

                    listDetails.add(new InvoiceDetailDTO(0, invoice.getInvoiceId(), bId, qty, price, 0, subTotal));

                    bookBUS.updateQuantity(bId, qty); // Trừ tồn kho qua BUS
                }
                detailBUS.insertBatch(listDetails); // Lưu chi tiết qua BUS

                if (promo != null && promo.getServiceId() != 0) {
                    InvoiceServiceDTO serviceLog = new InvoiceServiceDTO();
                    serviceLog.setInvoiceId(invoice.getInvoiceId());
                    serviceLog.setServiceId(promo.getServiceId());
                    serviceLog.setServiceType(promo.getDiscountType());
                    // Chỉ lưu tiền giảm của Mã Khuyến Mãi (Tổng giảm - Tiền của điểm)
                    serviceLog.setDiscountValue(discountApplied - pointsValue);
                    serviceLog.setDescription("Áp dụng mã KM khi lập Hóa đơn");

                    invoiceServiceBUS.insert(serviceLog); // Lưu qua BUS
                }

                JOptionPane.showMessageDialog(this, "LƯU HÓA ĐƠN THÀNH CÔNG!\nMã Hóa đơn: #" + invoice.getInvoiceId());
                if (GUI.model.BookTablePanel.getInstance() != null) {
                    GUI.model.BookTablePanel.getInstance().refreshTable();
                }
                bookBUS.loadDataFromDB();
                dispose();

            } else {
                // NẾU CÓ LỖI: Xử lý màu mè trên giao diện (Đúng chuẩn ValidationUI của em)
                GUI.util.ValidationUI.resetAll(tblInvoiceDetails);

                if (result.getError("details") != null) {
                    GUI.util.ValidationUI.setError(tblInvoiceDetails, result.getError("details"));
                }

                if (result.getError("employeeId") != null) {
                    JOptionPane.showMessageDialog(this, result.getError("employeeId"), "Lỗi Phân Quyền",
                            JOptionPane.ERROR_MESSAGE);
                }

                JOptionPane.showMessageDialog(this, result.getSummary(), "Lỗi Thanh Toán", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}