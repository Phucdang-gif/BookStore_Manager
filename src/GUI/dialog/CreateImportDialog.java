package GUI.dialog;

import BUS.*;
import DAO.*;
import DTO.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class CreateImportDialog extends JDialog {

    // Các lớp xử lý dữ liệu (BUS, DAO)
    private BookBUS bookBUS = new BookBUS();
    private SupplierBUS supplierBUS = new SupplierBUS();
    private ImportReceiptDetailDAO detailDAO = new ImportReceiptDetailDAO();
    private BookDAO bookDAO = new BookDAO();

    // Các thành phần Giao diện
    private JTable tblBooks, tblImportDetails;
    private DefaultTableModel bookModel, detailModel;
    private JComboBox<SupplierDTO> cbSupplier;
    private JLabel lblTotalAmount;

    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private double currentTotal = 0; // Tổng tiền phiếu nhập

    public CreateImportDialog(Frame owner, boolean modal) {
        super(owner, modal);
        setTitle("LẬP PHIẾU NHẬP HÀNG MỚI");
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
        JPanel pnlMain = new JPanel(new GridLayout(1, 2, 15, 0)); // Tăng khoảng cách cột
        pnlMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding thoáng hơn

        // ==========================================
        // BÊN TRÁI: DANH SÁCH SÁCH TRONG HỆ THỐNG
        // ==========================================
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), "KHO SÁCH HIỆN CÓ"));

        String[] bookCols = { "ID", "Tên Sách", "Giá Nhập HT", "Tồn Kho" };
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

        JButton btnAddDetail = new JButton("Đưa vào Phiếu Nhập >>");
        btnAddDetail.setPreferredSize(new Dimension(100, 40));
        btnAddDetail.setBackground(new Color(15, 108, 189));
        btnAddDetail.setForeground(Color.WHITE);
        btnAddDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        // --- FIX LỖI HIỂN THỊ NÚT ---
        btnAddDetail.setOpaque(true);
        btnAddDetail.setBorderPainted(false);
        btnAddDetail.setFocusPainted(false);
        // ----------------------------
        btnAddDetail.addActionListener(e -> addImportDetail());
        pnlLeft.add(btnAddDetail, BorderLayout.SOUTH);

        // ==========================================
        // BÊN PHẢI: CHI TIẾT PHIẾU NHẬP
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(5, 5));
        pnlRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), "CHI TIẾT PHIẾU NHẬP"));

        // Bảng chi tiết
        String[] detailCols = { "ID Sách", "Tên Sách", "Giá Nhập Mới", "Số Lượng", "Thành Tiền" };
        detailModel = new DefaultTableModel(detailCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblImportDetails = new JTable(detailModel);
        tblImportDetails.setRowHeight(30);
        tblImportDetails.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.add(new JScrollPane(tblImportDetails), BorderLayout.CENTER);

        JButton btnRemoveDetail = new JButton("Xóa dòng đã chọn");
        btnRemoveDetail.setForeground(Color.RED);
        btnRemoveDetail.setFocusPainted(false);
        btnRemoveDetail.addActionListener(e -> removeImportDetail());
        pnlTableContainer.add(btnRemoveDetail, BorderLayout.SOUTH);

        pnlRight.add(pnlTableContainer, BorderLayout.CENTER);

        // Khu vực Tổng kết & Lưu (Sửa lại layout cho đẹp)
        JPanel pnlBottomRight = new JPanel(new BorderLayout(0, 10));
        pnlBottomRight.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Panel thông tin (Grid 2 dòng)
        JPanel pnlInfo = new JPanel(new GridLayout(2, 2, 5, 10));

        pnlInfo.add(new JLabel("Nhà cung cấp (*):"));
        cbSupplier = new JComboBox<>();
        pnlInfo.add(cbSupplier);

        pnlInfo.add(new JLabel("TỔNG TIỀN NHẬP:"));
        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalAmount.setForeground(Color.RED);
        pnlInfo.add(lblTotalAmount);

        // Nút Lưu to, rõ ràng
        JButton btnSaveImport = new JButton("LƯU PHIẾU NHẬP & CẬP NHẬT KHO");
        btnSaveImport.setPreferredSize(new Dimension(100, 30));
        btnSaveImport.setBackground(new Color(0, 153, 51));
        btnSaveImport.setForeground(Color.WHITE);
        btnSaveImport.setFont(new Font("Segoe UI", Font.BOLD, 15));
        // --- FIX LỖI HIỂN THỊ NÚT ---
        btnSaveImport.setOpaque(true);
        btnSaveImport.setBorderPainted(false);
        btnSaveImport.setFocusPainted(false);
        // ----------------------------
        btnSaveImport.addActionListener(e -> saveImportReceipt());

        pnlBottomRight.add(pnlInfo, BorderLayout.CENTER);
        pnlBottomRight.add(btnSaveImport, BorderLayout.SOUTH);

        pnlRight.add(pnlBottomRight, BorderLayout.SOUTH);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain, BorderLayout.CENTER);
    }

    private void loadData() {
        // Tải danh sách sách
        for (BookDTO b : bookBUS.getAll()) {
            // Chỉ hiện sách chưa ngừng kinh doanh
            if (!"discontinued".equals(b.getStatus())) {
                bookModel.addRow(
                        new Object[] { b.getBookId(), b.getBookTitle(), b.getImportPrice(), b.getStockQuantity() });
            }
        }

        // Tải danh sách Nhà cung cấp
        for (SupplierDTO s : supplierBUS.getAll()) {
            cbSupplier.addItem(s);
        }
    }

    private void addImportDetail() {
        int row = tblBooks.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ kho bên trái!");
            return;
        }

        int bookId = (int) tblBooks.getValueAt(row, 0);
        String title = (String) tblBooks.getValueAt(row, 1);

        String priceStr = JOptionPane.showInputDialog(this, "Nhập GIÁ NHẬP (Giá vốn) cho mỗi cuốn:");
        if (priceStr == null || priceStr.trim().isEmpty())
            return;

        String qtyStr = JOptionPane.showInputDialog(this, "Nhập SỐ LƯỢNG SÁCH cần nhập vào kho:");
        if (qtyStr == null || qtyStr.trim().isEmpty())
            return;

        try {
            double price = Double.parseDouble(priceStr);
            int qty = Integer.parseInt(qtyStr);

            if (price < 0 || qty <= 0) {
                JOptionPane.showMessageDialog(this, "Giá nhập và số lượng phải lớn hơn 0!");
                return;
            }

            // Gộp dòng nếu sách đã có trong danh sách nhập
            boolean exists = false;
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                if ((int) detailModel.getValueAt(i, 0) == bookId) {
                    int oldQty = (int) detailModel.getValueAt(i, 3);
                    detailModel.setValueAt(oldQty + qty, i, 3); // Cập nhật số lượng
                    detailModel.setValueAt(price, i, 2); // Cập nhật giá mới nhất
                    detailModel.setValueAt((oldQty + qty) * price, i, 4); // Cập nhật thành tiền
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                detailModel.addRow(new Object[] { bookId, title, price, qty, price * qty });
            }
            updateTotal();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        }
    }

    private void removeImportDetail() {
        int row = tblImportDetails.getSelectedRow();
        if (row != -1) {
            detailModel.removeRow(row);
            updateTotal();
        }
    }

    private void updateTotal() {
        currentTotal = 0;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            currentTotal += (double) detailModel.getValueAt(i, 4);
        }
        lblTotalAmount.setText(df.format(currentTotal));
    }

    private void saveImportReceipt() {
        try {
            // 1. GOM DỮ LIỆU TỪ GIAO DIỆN
            boolean hasDetails = detailModel.getRowCount() > 0;

            SupplierDTO supplier = (SupplierDTO) cbSupplier.getSelectedItem();
            int supplierId = (supplier != null) ? supplier.getSupplierId() : 0;

            // Lấy ID nhân viên từ Session đăng nhập (Mặc định là 1 nếu chưa đăng nhập để
            // test)
            int employeeId = config.SessionManager.getCurrentAccount() != null
                    ? config.SessionManager.getCurrentAccount().getEmployeeId()
                    : 1;
            if (config.SessionManager.getCurrentAccount() != null) {
                employeeId = config.SessionManager.getCurrentAccount().getEmployeeId();
            }

            // Đóng gói thành DTO
            ImportReceiptDTO importDTO = new ImportReceiptDTO();
            importDTO.setSupplierId(supplierId);
            importDTO.setEmployeeId(employeeId);
            importDTO.setTotalAmount(currentTotal); // currentTotal đã được tính ở hàm updateTotal()

            BUS.ImportReceiptBUS importBUS = new BUS.ImportReceiptBUS();

            // 2. GỌI BUS KIỂM DUYỆT (Trả về ValidationResult)
            DTO.ValidationResult result = importBUS.addReceipt(importDTO, hasDetails);

            // 3. XỬ LÝ KẾT QUẢ HIỂN THỊ
            if (result.isValid()) {
                // NẾU HỢP LỆ: Tiến hành lưu các sách chi tiết vào DB
                ArrayList<ImportReceiptDetailDTO> listDetails = new ArrayList<>();
                for (int i = 0; i < detailModel.getRowCount(); i++) {
                    int bId = (int) detailModel.getValueAt(i, 0);
                    double price = (double) detailModel.getValueAt(i, 2);
                    int qty = (int) detailModel.getValueAt(i, 3);
                    double subTotal = (double) detailModel.getValueAt(i, 4);

                    // importDTO.getReceiptId() lúc này đã mang ID mới nhất do BUS cập nhật
                    listDetails.add(new ImportReceiptDetailDTO(importDTO.getReceiptId(), bId, qty, price, subTotal));

                    // Cập nhật Số lượng tồn kho & Giá vốn trong bảng Books
                    bookDAO.updateStockAndPrice(bId, qty, price);
                }
                detailDAO.insertBatch(listDetails);

                JOptionPane.showMessageDialog(this,
                        "NHẬP HÀNG THÀNH CÔNG!\nKho sách và Giá vốn đã được hệ thống tự động cập nhật.");
                dispose();
            } else {
                // NẾU CÓ LỖI: Gọi thợ sơn ValidationUI ra tô viền đỏ

                // Dọn sạch màu đỏ của lần bấm lỗi trước (nếu có)
                GUI.util.ValidationUI.resetAll(cbSupplier, tblImportDetails);

                // Dò lỗi và tô đỏ đúng chỗ
                if (result.getError("supplierId") != null) {
                    GUI.util.ValidationUI.setError(cbSupplier, result.getError("supplierId"));
                }
                if (result.getError("details") != null) {
                    GUI.util.ValidationUI.setError(tblImportDetails, result.getError("details"));
                }

                // Hiển thị thông báo tổng
                JOptionPane.showMessageDialog(this, result.getSummary(), "Cảnh báo Lỗi Nhập Liệu",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}