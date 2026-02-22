package GUI.model;

import BUS.InvoiceBUS;
import DTO.InvoiceDTO;
import GUI.dialog.InvoiceDetailDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class InvoicePanel extends JPanel implements FeatureControllerInterface {

    private InvoiceBUS invoiceBUS = new InvoiceBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public InvoicePanel() {
        initUI();
        loadDataToTable(invoiceBUS.getAll());
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Bảng dữ liệu hóa đơn tổng quát
        String[] columns = {"Mã Hóa Đơn", "Khách Hàng", "Nhân Viên", "Ngày Lập", "Phương Thức", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDataToTable(ArrayList<InvoiceDTO> list) {
        tableModel.setRowCount(0); 
        if (list != null) {
            for (InvoiceDTO dto : list) {
                String statusStr = dto.getStatus().equals("Completed") ? "Hoàn Thành" : "Đã Hủy";
                tableModel.addRow(new Object[]{
                    dto.getInvoiceId(), 
                    dto.getCustomerId(), // Sau này dùng CustomerBUS để lấy Tên
                    dto.getEmployeeId(), // Sau này dùng EmployeeBUS để lấy Tên
                    dto.getCreatedAt(), 
                    dto.getPaymentMethod(),
                    df.format(dto.getFinalAmount()), 
                    statusStr
                });
            }
        }
    }

    // ==========================================
    // XỬ LÝ SỰ KIỆN TỪ HEADER
    // ==========================================

    @Override
    public void onAdd() {
        // Nút Thêm ở đây sẽ dùng để mở giao diện Bán Hàng (POS - Point of Sale)
        JOptionPane.showMessageDialog(this, "Chức năng mở giao diện BÁN HÀNG sẽ được tích hợp tại đây!");
    }

    @Override
    public void onEdit() {
        // Không cho phép sửa hóa đơn
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Hóa đơn cần hủy!");
            return;
        }
        
        String currentStatus = (String) table.getValueAt(row, 6);
        if (currentStatus.equals("Đã Hủy")) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã bị hủy từ trước!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn HỦY hóa đơn này?\n(Hành động này không thể hoàn tác)", "Cảnh báo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int invoiceId = (int) table.getValueAt(row, 0);
            boolean isSuccess = invoiceBUS.cancelInvoice(invoiceId);
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Hủy hóa đơn thành công!");
                onRefresh();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể hủy hóa đơn!");
            }
        }
    }

    @Override
    public void onDetail() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Hóa đơn để xem chi tiết!");
            return;
        }
        int invoiceId = (int) table.getValueAt(row, 0);
        
        // Mở Dialog xem chi tiết các cuốn sách trong hóa đơn
        InvoiceDetailDialog dialog = new InvoiceDetailDialog(null, true, invoiceId);
        dialog.setVisible(true);
    }

    @Override
    public void onSearch(String text) {
        loadDataToTable(invoiceBUS.search(text));
    }

    @Override
    public void onRefresh() {
        loadDataToTable(invoiceBUS.getAll());
    }

    @Override
    public void onExportExcel() { }

    @Override
    public void onImportExcel() { }

    @Override
    public boolean[] getButtonConfig() {
        // Bật nút: Add (Bán hàng), Delete (Hủy HĐ), Detail (Xem chi tiết)
        // Tắt nút: Edit, Export, Import
        return new boolean[]{true, false, true, true, false, false}; 
    }
}