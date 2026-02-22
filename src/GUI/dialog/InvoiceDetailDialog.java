package GUI.dialog;

import BUS.InvoiceDetailBUS;
import DTO.InvoiceDetailDTO;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class InvoiceDetailDialog extends JDialog {

    private int invoiceId;
    private InvoiceDetailBUS detailBUS = new InvoiceDetailBUS(); 
    
    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public InvoiceDetailDialog(Frame owner, boolean modal, int invoiceId) {
        super(owner, modal);
        this.invoiceId = invoiceId;

        setTitle("Chi Tiết Hóa Đơn #" + invoiceId);
        setSize(700, 450);
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

        String[] columns = {"Mã Sách", "Số Lượng", "Đơn Giá", "Giảm Giá", "Thành Tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Chỉ xem, không cho sửa
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
        ArrayList<InvoiceDetailDTO> list = detailBUS.getDetailsByInvoiceId(invoiceId);
        if (list != null) {
            for (InvoiceDetailDTO dto : list) {
                tableModel.addRow(new Object[]{
                    dto.getBookId(), // Tương lai ráp BookBUS vào lấy Tên Sách
                    dto.getQuantity(),
                    df.format(dto.getUnitPrice()),
                    df.format(dto.getDiscount()),
                    df.format(dto.getSubtotal())
                });
            }
        }
    }
}