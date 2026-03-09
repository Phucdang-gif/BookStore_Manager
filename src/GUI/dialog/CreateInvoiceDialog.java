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

    // Các BUS, DAO xử lý dữ liệu
    private BookBUS bookBUS = new BookBUS();
    private CustomerBUS customerBUS = new CustomerBUS();
    private DiscountServiceBUS discountBUS = new DiscountServiceBUS();
    private InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();
    private InvoiceServiceDAO serviceDAO = new InvoiceServiceDAO();
    private BookDAO bookDAO = new BookDAO();
    private InvoiceBUS invoiceBUS = new InvoiceBUS(); // Thêm BUS hóa đơn để gọi phương thức addInvoice đúng chuẩn 3 lớp

    // Các thành phần giao diện
    private JTable tblBooks, tblInvoiceDetails;
    private DefaultTableModel bookModel, detailModel;
    private JComboBox<CustomerDTO> cbCustomer;
    private JComboBox<DiscountServiceDTO> cbDiscount;
    private JComboBox<String> cbPaymentMethod;
    private JLabel lblTotalAmount, lblDiscountAmount, lblFinalAmount;

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
        // 1. Cài đặt giao diện hệ thống để các thành phần đẹp hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout(10, 10));
        JPanel pnlMain = new JPanel(new GridLayout(1, 2, 15, 0)); // Tăng khoảng cách giữa 2 cột lớn
        pnlMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================================
        // BÊN TRÁI: KHO SÁCH
        // ==========================================
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), "KHO SÁCH")); // Viền xám nhẹ

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
        btnAddDetail.setPreferredSize(new Dimension(100, 40)); // Set chiều cao nút
        btnAddDetail.setBackground(new Color(15, 108, 189));
        btnAddDetail.setForeground(Color.WHITE);
        btnAddDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        // --- KHẮC PHỤC LỖI HIỂN THỊ NÚT ---
        btnAddDetail.setOpaque(true);
        btnAddDetail.setBorderPainted(false);
        btnAddDetail.setFocusPainted(false);
        // ----------------------------------
        btnAddDetail.addActionListener(e -> addInvoiceDetail());
        pnlLeft.add(btnAddDetail, BorderLayout.SOUTH);

        // ==========================================
        // BÊN PHẢI: CHI TIẾT & THANH TOÁN
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(5, 5));
        pnlRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), "CHI TIẾT HÓA ĐƠN"));

        // 1. Bảng Chi tiết
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

        // Nút xóa dòng nằm ngay dưới bảng
        JButton btnRemoveDetail = new JButton("Xóa dòng đã chọn");
        btnRemoveDetail.setForeground(Color.RED);
        btnRemoveDetail.setFocusPainted(false);
        btnRemoveDetail.addActionListener(e -> removeInvoiceDetail());

        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.add(new JScrollPane(tblInvoiceDetails), BorderLayout.CENTER);
        pnlTableContainer.add(btnRemoveDetail, BorderLayout.SOUTH);

        pnlRight.add(pnlTableContainer, BorderLayout.CENTER);

        // 2. Form Tổng kết (Sửa lại layout cho gọn)
        JPanel pnlBottomRight = new JPanel(new BorderLayout(0, 10));
        pnlBottomRight.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Panel chứa thông tin (Grid 6 dòng, 2 cột)
        JPanel pnlInfo = new JPanel(new GridLayout(6, 2, 5, 10));

        pnlInfo.add(new JLabel("Khách hàng:"));
        cbCustomer = new JComboBox<>();
        pnlInfo.add(cbCustomer);

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

        pnlInfo.add(new JLabel("Giảm giá:"));
        lblDiscountAmount = new JLabel("0 VNĐ");
        lblDiscountAmount.setForeground(Color.RED);
        lblDiscountAmount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlInfo.add(lblDiscountAmount);

        pnlInfo.add(new JLabel("TỔNG CỘNG:"));
        lblFinalAmount = new JLabel("0 VNĐ");
        lblFinalAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFinalAmount.setForeground(new Color(0, 153, 51));
        pnlInfo.add(lblFinalAmount);

        // Nút Lưu hóa đơn to bự nằm riêng ở dưới
        JButton btnSaveInvoice = new JButton("THANH TOÁN & LƯU HÓA ĐƠN");
        btnSaveInvoice.setPreferredSize(new Dimension(100, 30));
        btnSaveInvoice.setBackground(new Color(0, 153, 51));
        btnSaveInvoice.setForeground(Color.WHITE);
        btnSaveInvoice.setFont(new Font("Segoe UI", Font.BOLD, 15));
        // --- KHẮC PHỤC LỖI HIỂN THỊ NÚT ---
        btnSaveInvoice.setOpaque(true);
        btnSaveInvoice.setBorderPainted(false);
        btnSaveInvoice.setFocusPainted(false);
        // ----------------------------------
        btnSaveInvoice.addActionListener(e -> saveInvoice());

        pnlBottomRight.add(pnlInfo, BorderLayout.CENTER);
        pnlBottomRight.add(btnSaveInvoice, BorderLayout.SOUTH);

        pnlRight.add(pnlBottomRight, BorderLayout.SOUTH);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain, BorderLayout.CENTER);
    }

    private void loadData() {
        // Load Sách
        for (BookDTO b : bookBUS.getAll()) {
            if (b.getStockQuantity() > 0 && !"discontinued".equals(b.getStatus())) {
                bookModel.addRow(
                        new Object[] { b.getBookId(), b.getBookTitle(), b.getSellingPrice(), b.getStockQuantity() });
            }
        }

        // Load Khách hàng
        cbCustomer.addItem(new CustomerDTO(0, "-- Khách vãng lai --", "", 0, null));
        for (CustomerDTO c : customerBUS.getAll()) {
            cbCustomer.addItem(c);
        }

        // Load Khuyến mãi
        cbDiscount.addItem(new DiscountServiceDTO(0, "-- Không áp dụng --", "", 0, 0, 0, null, null, "", ""));
        for (DiscountServiceDTO d : discountBUS.getValidPromotions()) {
            cbDiscount.addItem(d);
        }
    }

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

            // Gộp dòng nếu sách đã có trong danh sách chi tiết
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

        double finalAmount = currentTotal - discountVal;
        if (finalAmount < 0)
            finalAmount = 0;

        lblTotalAmount.setText(df.format(currentTotal));
        lblDiscountAmount.setText("- " + df.format(discountVal));
        lblFinalAmount.setText(df.format(finalAmount));
    }

    // private void saveInvoice() {
    // if (detailModel.getRowCount() == 0) {
    // JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào trong chi tiết hóa
    // đơn!");
    // return;
    // }

    // CustomerDTO customer = (CustomerDTO) cbCustomer.getSelectedItem();
    // DiscountServiceDTO promo = (DiscountServiceDTO) cbDiscount.getSelectedItem();
    // String paymentMethod = (String) cbPaymentMethod.getSelectedItem();

    // updateTotals();
    // String finalStr = lblFinalAmount.getText().replace(" VNĐ", "").replace(",",
    // "").trim();
    // String discStr = lblDiscountAmount.getText().replace("- ", "").replace("
    // VNĐ", "").replace(",", "").trim();
    // double finalToPay = Double.parseDouble(finalStr);
    // double discountApplied = Double.parseDouble(discStr);

    // InvoiceDTO invoice = new InvoiceDTO();
    // invoice.setCustomerId(customer.getCustomerId());
    // invoice.setEmployeeId(1); // TODO: Lấy ID từ session đăng nhập sau này
    // invoice.setTotalAmount(currentTotal);
    // invoice.setTotalDiscount(discountApplied);
    // invoice.setFinalAmount(finalToPay);
    // invoice.setPaymentMethod(paymentMethod);
    // invoice.setPointsUsed(0);
    // invoice.setPointsValue(0);
    // invoice.setPointsEarned(0);

    // int newInvoiceId = invoiceBUS.addInvoice(invoice); // Gọi đúng chuẩn 3 lớp

    // if (newInvoiceId > 0) {
    // ArrayList<InvoiceDetailDTO> listDetails = new ArrayList<>();
    // for (int i = 0; i < detailModel.getRowCount(); i++) {
    // int bId = (int) detailModel.getValueAt(i, 0);
    // double price = (double) detailModel.getValueAt(i, 2);
    // int qty = (int) detailModel.getValueAt(i, 3);
    // double subTotal = (double) detailModel.getValueAt(i, 4);

    // listDetails.add(new InvoiceDetailDTO(0, newInvoiceId, bId, qty, price, 0,
    // subTotal));
    // bookDAO.updateQuantity(bId, qty); // Trừ tồn kho
    // }
    // detailDAO.insertBatch(listDetails);

    // if (promo != null && promo.getServiceId() != 0) {
    // InvoiceServiceDTO serviceLog = new InvoiceServiceDTO();
    // serviceLog.setInvoiceId(newInvoiceId);
    // serviceLog.setServiceId(promo.getServiceId());
    // serviceLog.setServiceType(promo.getDiscountType());
    // serviceLog.setDiscountValue(discountApplied);
    // serviceLog.setDescription("Áp dụng mã KM khi lập Hóa đơn");
    // serviceDAO.insert(serviceLog);
    // }

    // JOptionPane.showMessageDialog(this, "LƯU HÓA ĐƠN THÀNH CÔNG!\nMã Hóa đơn: #"
    // + newInvoiceId);
    // dispose();
    // } else {
    // JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn trong hệ thống!",
    // "Lỗi", JOptionPane.ERROR_MESSAGE);
    // }
    // }
    private void saveInvoice() {
        try {
            // 1. GOM DỮ LIỆU TỪ GIAO DIỆN
            boolean hasDetails = detailModel.getRowCount() > 0;

            CustomerDTO customer = (CustomerDTO) cbCustomer.getSelectedItem();
            int customerId = (customer != null && customer.getCustomerId() > 0) ? customer.getCustomerId() : 0;

            int employeeId = config.SessionManager.getCurrentAccount() != null
                    ? config.SessionManager.getCurrentAccount().getEmployeeId()
                    : 1;
            if (config.SessionManager.getCurrentAccount() != null) {
                employeeId = config.SessionManager.getCurrentAccount().getEmployeeId();
            }

            DiscountServiceDTO promo = (DiscountServiceDTO) cbDiscount.getSelectedItem();
            String paymentMethod = (String) cbPaymentMethod.getSelectedItem();

            // Tính toán lại tiền một lần cuối cho chắc chắn
            updateTotals();
            String finalStr = lblFinalAmount.getText().replace(" VNĐ", "").replace(",", "").trim();
            String discStr = lblDiscountAmount.getText().replace("- ", "").replace(" VNĐ", "").replace(",", "").trim();
            double finalToPay = Double.parseDouble(finalStr);
            double discountApplied = Double.parseDouble(discStr);

            // Đóng gói DTO
            InvoiceDTO invoice = new InvoiceDTO();
            invoice.setCustomerId(customerId);
            invoice.setEmployeeId(employeeId);
            invoice.setTotalAmount(currentTotal);
            invoice.setTotalDiscount(discountApplied);
            invoice.setFinalAmount(finalToPay);
            invoice.setPaymentMethod(paymentMethod);
            invoice.setPointsUsed(0);
            invoice.setPointsValue(0);
            invoice.setPointsEarned(0);

            // 2. GỌI LỚP BUS KIỂM DUYỆT
            DTO.ValidationResult result = invoiceBUS.addInvoice(invoice, hasDetails);

            // 3. XỬ LÝ KẾT QUẢ HIỂN THỊ
            if (result.isValid()) {
                // NẾU HỢP LỆ: Tiến hành lưu các chi tiết sách vào hóa đơn
                ArrayList<InvoiceDetailDTO> listDetails = new ArrayList<>();
                for (int i = 0; i < detailModel.getRowCount(); i++) {
                    int bId = (int) detailModel.getValueAt(i, 0);
                    double price = (double) detailModel.getValueAt(i, 2);
                    int qty = (int) detailModel.getValueAt(i, 3);
                    double subTotal = (double) detailModel.getValueAt(i, 4);

                    listDetails.add(new InvoiceDetailDTO(0, invoice.getInvoiceId(), bId, qty, price, 0, subTotal));
                    bookDAO.updateQuantity(bId, qty); // Trừ số lượng tồn kho
                }
                detailDAO.insertBatch(listDetails);

                // Lưu lại lịch sử dùng mã khuyến mãi (nếu có)
                if (promo != null && promo.getServiceId() != 0) {
                    InvoiceServiceDTO serviceLog = new InvoiceServiceDTO();
                    serviceLog.setInvoiceId(invoice.getInvoiceId());
                    serviceLog.setServiceId(promo.getServiceId());
                    serviceLog.setServiceType(promo.getDiscountType());
                    serviceLog.setDiscountValue(discountApplied);
                    serviceLog.setDescription("Áp dụng mã KM khi lập Hóa đơn");
                    serviceDAO.insert(serviceLog);
                }

                JOptionPane.showMessageDialog(this, "LƯU HÓA ĐƠN THÀNH CÔNG!\nMã Hóa đơn: #" + invoice.getInvoiceId());
                if (GUI.model.BookTablePanel.getInstance() != null) {
                    GUI.model.BookTablePanel.getInstance().refreshTable();
                }
                bookBUS.loadDataFromDB();
                dispose(); 
               
            } else {
                // NẾU CÓ LỖI: Xử lý màu mè trên giao diện

                // Dọn viền đỏ cũ
                GUI.util.ValidationUI.resetAll(tblInvoiceDetails);

                // Dò lỗi để báo đỏ
                if (result.getError("details") != null) {
                    GUI.util.ValidationUI.setError(tblInvoiceDetails, result.getError("details"));
                }

                if (result.getError("employeeId") != null) {
                    // Cảnh báo nếu nhân viên chưa đăng nhập hợp lệ
                    JOptionPane.showMessageDialog(this, result.getError("employeeId"), "Lỗi Phân Quyền",
                            JOptionPane.ERROR_MESSAGE);
                }

                // Hiển thị thông báo tổng hợp
                JOptionPane.showMessageDialog(this, result.getSummary(), "Lỗi Thanh Toán", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}