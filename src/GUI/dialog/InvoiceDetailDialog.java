package GUI.dialog;

import BUS.InvoiceDetailBUS;
import BUS.BookBUS; // Thêm BookBUS
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
    
    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public InvoiceDetailDialog(Frame owner, boolean modal, int invoiceId) {
        super(owner, modal);
        this.invoiceId = invoiceId;

        setTitle("Chi Tiết Hóa Đơn #" + invoiceId);
        setSize(750, 450); // Mở rộng chiều ngang một chút cho tên sách khỏi bị cắt
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
        String[] columns = {"Mã Sách", "Tên Sách", "Số Lượng", "Đơn Giá", "Giảm Giá", "Thành Tiền"};
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
                tableModel.addRow(new Object[]{
                    dto.getBookId(), 
                    bookTitle,        // <--- Hiển thị tên sách 
                    dto.getQuantity(),
                    df.format(dto.getUnitPrice()),
                    df.format(dto.getDiscount()),
                    df.format(dto.getSubtotal())
                });
            }
        }
    }
}