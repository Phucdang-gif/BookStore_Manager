package GUI.model;

import BUS.InvoiceBUS;
import DTO.InvoiceDTO;
import GUI.util.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InvoiceTablePanel extends JPanel {
    private InvoiceBUS invoiceBUS;
    private JTable table;
    private DefaultTableModel model;
    private List<InvoiceDTO> listInvoices;

    public InvoiceTablePanel() {
        this.invoiceBUS = new InvoiceBUS();
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);

        initTable();
        loadData();
    }

    private void initTable() {
        // Các cột hiển thị
        String[] headers = { "Mã HĐ", "Nhân viên", "Khách hàng", "Ngày lập", "Tổng tiền", "Trạng thái" };
        model = new DefaultTableModel(headers, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(35);

        // Style cho bảng
        try {
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.getTableHeader().setBackground(UIConstants.TABLE_HEADER_BACKGROUND);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        } catch (Exception e) {
            // Bỏ qua nếu font lỗi
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void loadData() {
        listInvoices = invoiceBUS.getAll();
        model.setRowCount(0);
        for (InvoiceDTO inv : listInvoices) {
            model.addRow(new Object[] {
                    inv.getInvoiceId(),
                    inv.getEmployeeId(), // Sau này nâng cấp sẽ hiện Tên NV thay vì ID
                    inv.getCustomerId() == 0 ? "Khách lẻ" : inv.getCustomerId(),
                    inv.getCreatedAt(),
                    String.format("%,.0f", inv.getFinalAmount()),
                    inv.getStatus()
            });
        }
    }

    public int getSelectedInvoiceId() {
        int row = table.getSelectedRow();
        if (row == -1)
            return -1;
        return (int) table.getValueAt(row, 0);
    }
}