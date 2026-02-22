package GUI.dialog;

import BUS.ImportReceiptDetailBUS;
import DTO.ImportReceiptDetailDTO;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class ImportDetailDialog extends JDialog {

    private int receiptId;
    private ImportReceiptDetailBUS detailBUS = new ImportReceiptDetailBUS(); 
    
    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,###.## VNĐ");

    public ImportDetailDialog(Frame owner, boolean modal, int receiptId) {
        super(owner, modal);
        this.receiptId = receiptId;

        setTitle("Chi Tiết Phiếu Nhập #" + receiptId);
        setSize(600, 400);
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

        String[] columns = {"Mã Sách", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        
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
                tableModel.addRow(new Object[]{
                    dto.getBookId(), // Sau này có thể dùng BookBUS để đổi thành Tên Sách
                    dto.getQuantity(),
                    df.format(dto.getUnitPrice()),
                    df.format(dto.getSubtotal())
                });
            }
        }
    }
}