package GUI.dialog;

import BUS.*;
import DTO.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

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

        String[] detailCols = { "Mã sách", "Tên sách", "Đơn giá", "Số lượng", "Thành tiền" };
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
        pnlInfo.add(new JLabel("Dùng điểm (1 điểm = 100 VNĐ):"));
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
        btnSaveInvoice.addActionListener(e -> processPaymentAndPrint());

        pnlBottomRight.add(pnlInfo, BorderLayout.CENTER);
        pnlBottomRight.add(btnSaveInvoice, BorderLayout.SOUTH);

        pnlRight.add(pnlBottomRight, BorderLayout.SOUTH);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain, BorderLayout.CENTER);
    }

    private double getDiemToTien() {
        return SystemParameterBUS.getInstance().getDouble("TY_LE_QUI_DOI_DIEM", 100);
    }

    private double getTienToDiem() {
        return SystemParameterBUS.getInstance().getDouble("TY_LE_TICH_DIEM", 10000);
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

        // 1. Tính giảm giá theo Khuyến mãi
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

        // =======================================================
        // FIX LOGIC: CHẶN DÙNG LỐ ĐIỂM CỦA KHÁCH HÀNG
        // =======================================================
        int pointsUsed = 0;
        CustomerDTO cus = (CustomerDTO) cbCustomer.getSelectedItem();
        int maxCustomerPoints = (cus != null && cus.getCustomerId() > 0) ? cus.getLoyaltyPoints() : 0;

        // 2a. Tính số tiền còn lại phải trả (Sau khi đã trừ khuyến mãi)
        double amountAfterPromo = currentTotal - discountVal;
        if (amountAfterPromo < 0)
            amountAfterPromo = 0;

        // 2b. Tính số điểm TỐI ĐA cần thiết để trả cục tiền còn lại này
        // Math.ceil để làm tròn lên, đảm bảo đủ trả số tiền lẻ
        int maxPointsNeeded = (int) Math.ceil(amountAfterPromo / getDiemToTien());

        // 2c. Chốt "Giới hạn điểm được phép nhập" = Min(Điểm khách có, Điểm hóa đơn
        // cần)
        int maxAllowedPoints = Math.min(maxCustomerPoints, maxPointsNeeded);

        if (!txtPointsUsed.getText().trim().isEmpty()) {
            try {
                pointsUsed = Integer.parseInt(txtPointsUsed.getText().trim());
            } catch (Exception e) {
                pointsUsed = 0;
            }
        }

        // Bắt lỗi nhập lố hoặc nhập số âm
        if (pointsUsed > maxAllowedPoints) {
            pointsUsed = maxAllowedPoints; // Ép về mức tối đa cho phép
            int finalPointsUsed = pointsUsed;
            SwingUtilities.invokeLater(() -> txtPointsUsed.setText(String.valueOf(finalPointsUsed)));
        }
        if (pointsUsed < 0) {
            pointsUsed = 0;
            SwingUtilities.invokeLater(() -> txtPointsUsed.setText("0"));
        }

        // 3. Tính toán các con số cuối cùng
        double pointsValue = pointsUsed * getDiemToTien();
        double totalDiscount = discountVal + pointsValue;

        double finalAmount = currentTotal - totalDiscount;
        if (finalAmount < 0)
            finalAmount = 0;

        // 4. Tính điểm thưởng mới (Chỉ được tích điểm trên phần tiền mặt thực trả)
        int pointsEarned = 0;
        if (cus != null && cus.getCustomerId() > 0) {
            pointsEarned = (int) (finalAmount / getTienToDiem());
        }

        // 5. Render lại UI
        lblTotalAmount.setText(df.format(currentTotal));
        lblDiscountAmount.setText("- " + df.format(totalDiscount));
        lblFinalAmount.setText(df.format(finalAmount));
        lblPointsEarned.setText("+" + pointsEarned + " điểm");
    }

    // private void saveInvoice() {
    //     try {
    //         // 1. GOM DỮ LIỆU TỪ GIAO DIỆN (Gốc 100%)
    //         boolean hasDetails = detailModel.getRowCount() > 0;

    //         CustomerDTO customer = (CustomerDTO) cbCustomer.getSelectedItem();
    //         int customerId = (customer != null && customer.getCustomerId() > 0) ? customer.getCustomerId() : 0;

    //         int employeeId = config.SessionManager.getCurrentAccount() != null
    //                 ? config.SessionManager.getCurrentAccount().getEmployeeId()
    //                 : 1;

    //         DiscountServiceDTO promo = (DiscountServiceDTO) cbDiscount.getSelectedItem();
    //         String paymentMethod = (String) cbPaymentMethod.getSelectedItem();

    //         updateTotals();
    //         String finalStr = lblFinalAmount.getText().replace(" VNĐ", "").replace(",", "").trim();
    //         String discStr = lblDiscountAmount.getText().replace("- ", "").replace(" VNĐ", "").replace(",", "").trim();
    //         double finalToPay = Double.parseDouble(finalStr);
    //         double discountApplied = Double.parseDouble(discStr); // Tổng giảm (bao gồm cả mã + điểm)

    //         // --- THÊM: Lấy số liệu điểm ---
    //         int pointsUsed = 0;
    //         try {
    //             pointsUsed = Integer.parseInt(txtPointsUsed.getText().trim());
    //         } catch (Exception e) {
    //         }
    //         double pointsValue = pointsUsed * getDiemToTien();

    //         int pointsEarned = 0;
    //         try {
    //             pointsEarned = Integer.parseInt(lblPointsEarned.getText().replace("+", "").replace(" điểm", "").trim());
    //         } catch (Exception e) {
    //         }
    //         // ------------------------------

    //         // Đóng gói DTO (Gốc)
    //         InvoiceDTO invoice = new InvoiceDTO();
    //         invoice.setCustomerId(customerId);
    //         invoice.setEmployeeId(employeeId);
    //         invoice.setTotalAmount(currentTotal);
    //         invoice.setTotalDiscount(discountApplied);
    //         invoice.setFinalAmount(finalToPay);
    //         invoice.setPaymentMethod(paymentMethod);

    //         // --- THÊM: Gắn điểm vào DTO ---
    //         invoice.setPointsUsed(pointsUsed);
    //         invoice.setPointsValue(pointsValue);
    //         invoice.setPointsEarned(pointsEarned);
    //         // ------------------------------

    //         // 2. GỌI LỚP BUS KIỂM DUYỆT (Gốc)
    //         DTO.ValidationResult result = invoiceBUS.addInvoice(invoice, hasDetails, customer);

    //         // 3. XỬ LÝ KẾT QUẢ HIỂN THỊ (Gốc)
    //         if (result.isValid()) {
    //             ArrayList<InvoiceDetailDTO> listDetails = new ArrayList<>();
    //             for (int i = 0; i < detailModel.getRowCount(); i++) {
    //                 int bId = (int) detailModel.getValueAt(i, 0);
    //                 double price = (double) detailModel.getValueAt(i, 2);
    //                 int qty = (int) detailModel.getValueAt(i, 3);
    //                 double subTotal = (double) detailModel.getValueAt(i, 4);

    //                 listDetails.add(new InvoiceDetailDTO(0, invoice.getInvoiceId(), bId, qty, price, 0, subTotal));

    //                 bookBUS.updateQuantity(bId, qty); // Trừ tồn kho qua BUS
    //             }
    //             detailBUS.insertBatch(listDetails); // Lưu chi tiết qua BUS

    //             if (promo != null && promo.getServiceId() != 0) {
    //                 InvoiceServiceDTO serviceLog = new InvoiceServiceDTO();
    //                 serviceLog.setInvoiceId(invoice.getInvoiceId());
    //                 serviceLog.setServiceId(promo.getServiceId());
    //                 serviceLog.setServiceType(promo.getDiscountType());
    //                 // Chỉ lưu tiền giảm của Mã Khuyến Mãi (Tổng giảm - Tiền của điểm)
    //                 serviceLog.setDiscountValue(discountApplied - pointsValue);
    //                 serviceLog.setDescription("Áp dụng mã KM khi lập Hóa đơn");

    //                 invoiceServiceBUS.insert(serviceLog); // Lưu qua BUS
    //             }

    //             JOptionPane.showMessageDialog(this, "LƯU HÓA ĐƠN THÀNH CÔNG!\nMã Hóa đơn: #" + invoice.getInvoiceId());
    //             if (GUI.model.BookTablePanel.getInstance() != null) {
    //                 GUI.model.BookTablePanel.getInstance().refreshTable();
    //             }
    //             bookBUS.loadDataFromDB();
    //             dispose();

    //         } else {
    //             GUI.util.ValidationUI.resetAll(tblInvoiceDetails);
    //             txtPointsUsed.setBorder(UIManager.getBorder("TextField.border"));

    //             if (result.getError("details") != null) {
    //                 GUI.util.ValidationUI.setError(tblInvoiceDetails, result.getError("details"));
    //             }
    //             if (result.getError("pointsUsed") != null) {
    //                 GUI.util.ValidationUI.setError(txtPointsUsed, result.getError("pointsUsed"));
    //             }

    //             JOptionPane.showMessageDialog(this, result.getSummary(), "Lỗi Thanh Toán", JOptionPane.WARNING_MESSAGE);
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //         JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    //     }
    // }
    // --- HÀM 1: XỬ LÝ THANH TOÁN (PAYMENT) ---
    private void processPaymentAndPrint() {
        try {
            boolean hasDetails = detailModel.getRowCount() > 0;
            CustomerDTO customer = (CustomerDTO) cbCustomer.getSelectedItem();
            int customerId = (customer != null && customer.getCustomerId() > 0) ? customer.getCustomerId() : 0;
            int employeeId = config.SessionManager.getCurrentAccount() != null
                    ? config.SessionManager.getCurrentAccount().getEmployeeId() : 1;

            DiscountServiceDTO promo = (DiscountServiceDTO) cbDiscount.getSelectedItem();
            String paymentMethod = (String) cbPaymentMethod.getSelectedItem();

            updateTotals();
            String finalStr = lblFinalAmount.getText().replace(" VNĐ", "").replace(",", "").trim();
            String discStr = lblDiscountAmount.getText().replace("- ", "").replace(" VNĐ", "").replace(",", "").trim();
            double finalToPay = Double.parseDouble(finalStr);
            double discountApplied = Double.parseDouble(discStr); 

            int pointsUsed = 0;
            try { pointsUsed = Integer.parseInt(txtPointsUsed.getText().trim()); } catch (Exception e) {}
            double pointsValue = pointsUsed * getDiemToTien();

            int pointsEarned = 0;
            try { pointsEarned = Integer.parseInt(lblPointsEarned.getText().replace("+", "").replace(" điểm", "").trim()); } catch (Exception e) {}

            // Set data to DTO
            InvoiceDTO invoice = new InvoiceDTO();
            invoice.setCustomerId(customerId);
            invoice.setEmployeeId(employeeId);
            invoice.setTotalAmount(currentTotal);
            invoice.setTotalDiscount(discountApplied);
            invoice.setFinalAmount(finalToPay);
            invoice.setPaymentMethod(paymentMethod);
            invoice.setPointsUsed(pointsUsed);
            invoice.setPointsValue(pointsValue);
            invoice.setPointsEarned(pointsEarned);

            // Save to DB
            DTO.ValidationResult result = invoiceBUS.addInvoice(invoice, hasDetails, customer);

            if (result.isValid()) {
                // Save details
                ArrayList<InvoiceDetailDTO> listDetails = new ArrayList<>();
                for (int i = 0; i < detailModel.getRowCount(); i++) {
                    int bId = (int) detailModel.getValueAt(i, 0);
                    double price = (double) detailModel.getValueAt(i, 2);
                    int qty = (int) detailModel.getValueAt(i, 3);
                    double subTotal = (double) detailModel.getValueAt(i, 4);
                    listDetails.add(new InvoiceDetailDTO(0, invoice.getInvoiceId(), bId, qty, price, 0, subTotal));
                    bookBUS.updateQuantity(bId, qty); 
                }
                detailBUS.insertBatch(listDetails); 

                if (promo != null && promo.getServiceId() != 0) {
                    InvoiceServiceDTO serviceLog = new InvoiceServiceDTO();
                    serviceLog.setInvoiceId(invoice.getInvoiceId());
                    serviceLog.setServiceId(promo.getServiceId());
                    serviceLog.setServiceType(promo.getDiscountType());
                    serviceLog.setDiscountValue(discountApplied - pointsValue);
                    serviceLog.setDescription("Áp dụng mã KM khi lập Hóa đơn");
                    invoiceServiceBUS.insert(serviceLog); 
                }

                // ==========================================
                // PAYMENT SUCCESS -> START PRINTING (GỌI HÀM IN)
                // ==========================================
                printReceipt(invoice, customer, paymentMethod);

                JOptionPane.showMessageDialog(this, "Payment success! (Thanh toán thành công!)\nInvoice ID: #" + invoice.getInvoiceId());
                
                // Refresh data
                if (GUI.model.BookTablePanel.getInstance() != null) {
                    GUI.model.BookTablePanel.getInstance().refreshTable();
                }
                bookBUS.loadDataFromDB();
                dispose(); // Close dialog

            } else {
                GUI.util.ValidationUI.resetAll(tblInvoiceDetails);
                txtPointsUsed.setBorder(UIManager.getBorder("TextField.border"));
                if (result.getError("details") != null) GUI.util.ValidationUI.setError(tblInvoiceDetails, result.getError("details"));
                if (result.getError("pointsUsed") != null) GUI.util.ValidationUI.setError(txtPointsUsed, result.getError("pointsUsed"));
                JOptionPane.showMessageDialog(this, result.getSummary(), "Payment Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "System Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- HÀM 2: CHỨC NĂNG IN HÓA ĐƠN (PRINT RECEIPT) ---
    private void printReceipt(InvoiceDTO invoice, CustomerDTO customer, String paymentMethod) {
        try {
            // Lấy data từ JTable
            JRTableModelDataSource dataSource = new JRTableModelDataSource(detailModel);
            java.util.Map<String, Object> parameters = new java.util.HashMap<>();
            
            // Lấy tên thật của nhân viên từ Session
            String empName = config.SessionManager.getCurrentAccount() != null ? 
                             config.SessionManager.getCurrentAccount().getUsername() : "Nhân viên";
            String cusName = (customer != null && customer.getCustomerId() > 0) ? 
                             customer.getFullName() : "Khách vãng lai";
                             
            // Format ngày giờ hiện tại
            String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());

            // Map variables (Khớp tên với file Jasper)
            parameters.put("invoiceId", String.valueOf(invoice.getInvoiceId()));
            parameters.put("createdAt", currentDate);
            parameters.put("customerName", cusName);
            parameters.put("employeeName", empName);
            parameters.put("paymentMethod", paymentMethod);
            
            parameters.put("totalAmount", lblTotalAmount.getText());
            parameters.put("totalDiscount", lblDiscountAmount.getText());
            parameters.put("pointsUsed", txtPointsUsed.getText());
            parameters.put("pointsValue", df.format(invoice.getPointsValue())); 
            parameters.put("finalAmount", lblFinalAmount.getText());

            // Compile & Print
            String reportPath = "BookStore_Manager\\src\\reports\\Invoice.jrxml"; 
            net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reportPath);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Print Error: " + ex.getMessage());
        }
    }
}