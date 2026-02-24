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
    private ImportReceiptDAO importDAO = new ImportReceiptDAO();
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
        setLayout(new BorderLayout(10, 10));
        JPanel pnlMain = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ==========================================
        // BÊN TRÁI: DANH SÁCH SÁCH TRONG HỆ THỐNG
        // ==========================================
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("KHO SÁCH HIỆN CÓ"));
        
        String[] bookCols = {"ID", "Tên Sách", "Giá Nhập HT", "Tồn Kho"};
        bookModel = new DefaultTableModel(bookCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBooks = new JTable(bookModel);
        tblBooks.setRowHeight(30);
        pnlLeft.add(new JScrollPane(tblBooks), BorderLayout.CENTER);

        JButton btnAddDetail = new JButton("Đưa vào Phiếu Nhập >>");
        btnAddDetail.setBackground(new Color(15, 108, 189));
        btnAddDetail.setForeground(Color.WHITE);
        btnAddDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddDetail.addActionListener(e -> addImportDetail());
        pnlLeft.add(btnAddDetail, BorderLayout.SOUTH);

        // ==========================================
        // BÊN PHẢI: CHI TIẾT PHIẾU NHẬP
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(5, 5));
        pnlRight.setBorder(BorderFactory.createTitledBorder("CHI TIẾT PHIẾU NHẬP"));

        // Bảng chi tiết
        String[] detailCols = {"ID Sách", "Tên Sách", "Giá Nhập Mới", "Số Lượng", "Thành Tiền"};
        detailModel = new DefaultTableModel(detailCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblImportDetails = new JTable(detailModel);
        tblImportDetails.setRowHeight(30);
        
        JPanel pnlDetails = new JPanel(new BorderLayout());
        pnlDetails.add(new JScrollPane(tblImportDetails), BorderLayout.CENTER);
        
        JButton btnRemoveDetail = new JButton("Xóa dòng");
        btnRemoveDetail.addActionListener(e -> removeImportDetail());
        pnlDetails.add(btnRemoveDetail, BorderLayout.SOUTH);
        pnlRight.add(pnlDetails, BorderLayout.CENTER);

        // Khu vực Tổng kết & Lưu
        JPanel pnlSummary = new JPanel(new GridLayout(3, 2, 5, 10));
        pnlSummary.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlSummary.add(new JLabel("Nhà cung cấp (*):"));
        cbSupplier = new JComboBox<>();
        pnlSummary.add(cbSupplier);

        pnlSummary.add(new JLabel("TỔNG TIỀN NHẬP:"));
        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalAmount.setForeground(Color.RED);
        pnlSummary.add(lblTotalAmount);

        JButton btnSaveImport = new JButton("LƯU PHIẾU NHẬP & CẬP NHẬT KHO");
        btnSaveImport.setBackground(new Color(0, 153, 51));
        btnSaveImport.setForeground(Color.WHITE);
        btnSaveImport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSaveImport.addActionListener(e -> saveImportReceipt());
        
        pnlSummary.add(new JLabel("")); // Ô trống cho đẹp layout
        pnlSummary.add(btnSaveImport);

        pnlRight.add(pnlSummary, BorderLayout.SOUTH);

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain, BorderLayout.CENTER);
    }

    private void loadData() {
        // Tải danh sách sách
        for (BookDTO b : bookBUS.getAll()) {
            bookModel.addRow(new Object[]{b.getBookId(), b.getBookTitle(), b.getImportPrice(), b.getStockQuantity()});
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
        if (priceStr == null || priceStr.trim().isEmpty()) return;

        String qtyStr = JOptionPane.showInputDialog(this, "Nhập SỐ LƯỢNG SÁCH cần nhập vào kho:");
        if (qtyStr == null || qtyStr.trim().isEmpty()) return;
        
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
                detailModel.addRow(new Object[]{bookId, title, price, qty, price * qty});
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
        if (detailModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Danh sách phiếu nhập đang trống!");
            return;
        }
        
        if (cbSupplier.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp!");
            return;
        }

        SupplierDTO supplier = (SupplierDTO) cbSupplier.getSelectedItem();

        // 1. Tạo DTO Phiếu Nhập và Lưu
        ImportReceiptDTO importDTO = new ImportReceiptDTO();
        importDTO.setSupplierId(supplier.getSupplierId());
        importDTO.setEmployeeId(1); // Mặc định NV số 1 (sau này thay bằng Session người đăng nhập)
        importDTO.setTotalAmount(currentTotal);
        
        int newImportId = importDAO.insert(importDTO); 

        if (newImportId > 0) {
            // 2. Lưu Chi Tiết và Cập nhật Sách
            ArrayList<ImportReceiptDetailDTO> listDetails = new ArrayList<>();
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                int bId = (int) detailModel.getValueAt(i, 0);
                double price = (double) detailModel.getValueAt(i, 2);
                int qty = (int) detailModel.getValueAt(i, 3);
                double subTotal = (double) detailModel.getValueAt(i, 4);

                listDetails.add(new ImportReceiptDetailDTO(newImportId, bId, qty, price, subTotal));
                
                // Cập nhật Số lượng tồn kho & Giá vốn trung bình trong bảng Books
                bookDAO.updateStockAndPrice(bId, qty, price);
            }
            detailDAO.insertBatch(listDetails);
            
            JOptionPane.showMessageDialog(this, "NHẬP HÀNG THÀNH CÔNG!\nKho sách và Giá vốn đã được hệ thống tự động cập nhật.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu Phiếu nhập vào hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}