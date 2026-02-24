package GUI.dialog;

import BUS.BookBUS;
import BUS.ImportReceiptDetailBUS;
import DTO.BookDTO;
import DTO.ImportReceiptDetailDTO;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class ImportDetailDialog extends JDialog {

    private int receiptId;
    private ImportReceiptDetailBUS detailBUS = new ImportReceiptDetailBUS(); 
    private BookBUS bookBUS = new BookBUS(); // Thêm BookBUS để lấy Tên Sách
    
    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public ImportDetailDialog(Frame owner, boolean modal, int receiptId) {
        super(owner, modal);
        this.receiptId = receiptId;

        setTitle("Chi Tiết Phiếu Nhập #" + receiptId);
        setSize(750, 450); // Mở rộng Form để hiển thị tên sách thoải mái hơn
        setLocationRelativeTo(null);
        initUI();
        loadDetails();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JLabel lblHeader = new JLabel("DANH SÁCH SẢN PHẨM TRONG PHIẾU NHẬP #" + receiptId, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Bổ sung thêm cột "Tên Sách" vào JTable
        String[] columns = {"Mã Sách", "Tên Sách", "Số Lượng", "Giá Nhập", "Thành Tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        // Căn chỉnh độ rộng từng cột cho cân đối
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Cột tên sách rộng nhất
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        pnlBottom.add(btnClose);
        
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadDetails() {
        ArrayList<ImportReceiptDetailDTO> list = detailBUS.getDetailsByReceiptId(receiptId);
        if (list != null) {
            for (ImportReceiptDetailDTO dto : list) {
                // 1. Dùng BookBUS để tìm Tên Sách dựa vào Mã Sách
                String bookTitle = "Không xác định";
                BookDTO book = bookBUS.getBookDetails(dto.getBookId());
                if (book != null) {
                    bookTitle = book.getBookTitle();
                }

                // 2. Đẩy dữ liệu lên bảng
                tableModel.addRow(new Object[]{
                    dto.getBookId(), 
                    bookTitle,       // Hiển thị tên sách thực tế cực kỳ trực quan
                    dto.getQuantity(),
                    // Lưu ý: Nếu DTO của em dùng hàm getUnitPrice() thay vì getImportPrice(), hãy đổi lại cho khớp nhé!
                    df.format(dto.getUnitPrice()), 
                    df.format(dto.getSubtotal())
                });
            }
        }
    }
}