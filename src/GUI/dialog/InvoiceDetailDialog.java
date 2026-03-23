package GUI.dialog;

import BUS.InvoiceDetailBUS;
import BUS.BookBUS; // Thêm BookBUS
import BUS.EmployeeBUS;
import BUS.InvoiceBUS;
import DTO.BookDTO; // Thêm BookDTO
import DTO.InvoiceDetailDTO;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class InvoiceDetailDialog extends JDialog {

    private int invoiceId;
    private InvoiceDetailBUS detailBUS = new InvoiceDetailBUS();
    private BookBUS bookBUS = new BookBUS(); // Khai báo BookBUS để lấy Tên Sách
    private EmployeeBUS employeeBUS = new EmployeeBUS(); // Khai báo EmployeeBUS để lấy Tên Nhân Viên

    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public InvoiceDetailDialog(Frame owner, boolean modal, int invoiceId) {
        super(owner, modal);
        this.invoiceId = invoiceId;

        setTitle("Chi Tiết Hóa Đơn #" + invoiceId);
        setSize(750, 450);
        setLocationRelativeTo(null);
        initUI();
        loadDetails();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblHeader = new JLabel("CÁC SẢN PHẨM TRONG HÓA ĐƠN #" + invoiceId, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // THÊM CỘT "TÊN SÁCH" VÀO BẢNG
        String[] columns = { "Mã sách", "Tên sách", "Số lượng", "Đơn giá", "Giảm giá", "Thành tiền" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);

        // Chỉnh độ rộng để tên sách hiển thị thoải mái
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(230); // Tên sách rộng nhất
        table.getColumnModel().getColumn(2).setPreferredWidth(80);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("IN LẠI HÓA ĐƠN");
        btnPrint.setBackground(new Color(0, 123, 255));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrint.setFocusPainted(false);
        btnPrint.addActionListener(e -> printOldInvoice()); // Gọi hàm in
        pnlBottom.add(btnPrint);
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        pnlBottom.add(btnClose);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadDetails() {
        ArrayList<InvoiceDetailDTO> list = detailBUS.getDetailsByInvoiceId(invoiceId);
        if (list != null) {
            for (InvoiceDetailDTO dto : list) {
                // 1. Dùng BookBUS để dịch ID Sách thành Tên Sách
                String bookTitle = "Không xác định";
                BookDTO book = bookBUS.getBookDetails(dto.getBookId());
                if (book != null) {
                    bookTitle = book.getBookTitle();
                }

                // 2. Nạp dữ liệu lên bảng (Có thêm cột Tên Sách)
                tableModel.addRow(new Object[] {
                        dto.getBookId(),
                        bookTitle, // <--- Hiển thị tên sách
                        dto.getQuantity(),
                        dto.getUnitPrice(),
                        dto.getDiscount(),
                        dto.getSubtotal()
                });
            }
        }
    }

    // --- HÀM XỬ LÝ IN LẠI (REPRINT) ---
    private void printOldInvoice() {
        try {
            // 1. Lấy dữ liệu bảng từ JTable đang hiển thị sẵn trên màn hình
            net.sf.jasperreports.engine.data.JRTableModelDataSource dataSource = new net.sf.jasperreports.engine.data.JRTableModelDataSource(
                    tableModel);
            java.util.Map<String, Object> parameters = new java.util.HashMap<>();

            // 2. Query (Truy vấn) lại hóa đơn gốc từ Database
            // Giả sử InvoiceBUS của em có hàm getById(id) để lấy ra InvoiceDTO
            InvoiceBUS invoiceBus = new InvoiceBUS();
            DTO.InvoiceDTO invoice = invoiceBus.getById(this.invoiceId);

            if (invoice == null) {
                JOptionPane.showMessageDialog(this, "Error (Lỗi): Không tìm thấy dữ liệu hóa đơn gốc!");
                return;
            }

            // Gọi CustomerBUS để dịch ID Khách ra Tên thật
            String cusName = "Khách vãng lai";
            if (invoice.getCustomerId() > 0) {
                BUS.CustomerBUS customerBus = new BUS.CustomerBUS();
                DTO.CustomerDTO cus = customerBus.getById(invoice.getCustomerId());
                if (cus != null)
                    cusName = cus.getFullName();
            }

            // Định dạng ngày giờ
            String createdAtStr = "Không xác định";
            if (invoice.getCreatedAt() != null) {
                createdAtStr = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(invoice.getCreatedAt());
            }

            // 3. Đưa dữ liệu vào Parameters (Khớp 100% với file thiết kế)
            parameters.put("invoiceId", String.valueOf(invoice.getInvoiceId()));
            parameters.put("createdAt", createdAtStr);
            parameters.put("customerName", cusName);
            parameters.put("employeeName", employeeBUS.getEmployeeName(invoice.getEmployeeId())); // Em có thể gọi EmployeeBUS để
                                                                                        // lấy tên thật
            parameters.put("paymentMethod", invoice.getPaymentMethod());

            parameters.put("totalAmount", df.format(invoice.getTotalAmount()));
            parameters.put("totalDiscount", df.format(invoice.getTotalDiscount()));
            parameters.put("pointsUsed", String.valueOf(invoice.getPointsUsed()));
            parameters.put("pointsValue", df.format(invoice.getPointsValue()));
            parameters.put("finalAmount", df.format(invoice.getFinalAmount()));

            // 4. Compile & Print (Biên dịch và hiển thị)
            String reportPath = "BookStore_Manager\\src\\reports\\Invoice.jrxml";
            net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager
                    .compileReport(reportPath);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager
                    .fillReport(jasperReport, parameters, dataSource);

            net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi in ấn: " + ex.getMessage());
        }
    }
}