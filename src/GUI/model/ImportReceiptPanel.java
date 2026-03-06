package GUI.model;

import BUS.ImportReceiptBUS;
import DTO.ImportReceiptDTO;
import GUI.dialog.CreateImportDialog; // SỬA LỖI 1: Thêm dòng import này
import GUI.dialog.ImportDetailDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat; // SỬA LỖI 3: Import công cụ định dạng ngày

public class ImportReceiptPanel extends JPanel implements FeatureControllerInterface {

    private ImportReceiptBUS importBUS = new ImportReceiptBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // Định dạng ngày

    public ImportReceiptPanel() {
        initUI();
        loadDataToTable(importBUS.getAll());
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "Mã Phiếu", "Mã NCC", "Mã NV", "Ngày Nhập", "Tổng Tiền", "Trạng Thái" };
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

    private void loadDataToTable(ArrayList<ImportReceiptDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (ImportReceiptDTO dto : list) {
                // SỬA LỖI 4: Viết ngược lại để chống lỗi NULL
                String statusStr = "Cancelled".equalsIgnoreCase(dto.getStatus()) ? "Đã Hủy" : "Hoàn Thành";

                // SỬA LỖI 3: Format ngày tháng
                String dateStr = (dto.getReceiptDate() != null) ? sdf.format(dto.getReceiptDate()) : "";

                tableModel.addRow(new Object[] {
                        dto.getReceiptId(),
                        dto.getSupplierId(),
                        dto.getEmployeeId(),
                        dateStr, // Dùng chuỗi ngày đã format
                        df.format(dto.getTotalAmount()),
                        statusStr
                });
            }
        }
    }

    // ==========================================
    // CÁC LỆNH TỪ HEADER TRUYỀN XUỐNG
    // ==========================================

    @Override
    public void onAdd() {
        // Mở cửa sổ Lập Phiếu Nhập
        CreateImportDialog dialog = new CreateImportDialog(null, true);
        dialog.setVisible(true);

        // Làm mới lại bảng danh sách phiếu nhập
        onRefresh();
    }

    @Override
    public void onEdit() {
        JOptionPane.showMessageDialog(this, "Nghiệp vụ không cho phép sửa phiếu nhập đã lưu!");
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Phiếu nhập cần hủy!");
            return;
        }

        String currentStatus = (String) table.getValueAt(row, 5);
        if (currentStatus.equals("Đã Hủy")) {
            JOptionPane.showMessageDialog(this, "Phiếu này đã bị hủy từ trước!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn HỦY phiếu nhập này?", "Cảnh báo",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int importId = (int) table.getValueAt(row, 0);
            boolean isSuccess = importBUS.cancelReceipt(importId); // Đảm bảo BUS có hàm này
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Hủy phiếu nhập thành công!");
                onRefresh();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể hủy phiếu nhập!");
            }
        }
    }

    @Override
    public void onDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Phiếu nhập để xem chi tiết!");
            return;
        }
        int importId = (int) table.getValueAt(row, 0); // Sửa biến receiptId thành importId cho đồng bộ

        // Gọi Dialog xem chi tiết
        ImportDetailDialog dialog = new ImportDetailDialog(null, true, importId);
        dialog.setVisible(true);
    }

    @Override
    public void onSearch(String text) {
        // Có thể gọi hàm search của BUS ở đây
        // ArrayList<ImportReceiptDTO> result = importBUS.search(text);
        // loadDataToTable(result);
        JOptionPane.showMessageDialog(this, "Tìm kiếm phiếu nhập: " + text);
    }

    @Override
    public void onRefresh() {
        importBUS.refreshData();
        loadDataToTable(importBUS.getAll());
    }

    @Override
    public void onExportExcel() {
    }

    @Override
    public void onImportExcel() {
    }

    @Override
    public boolean[] getButtonConfig() {
        if (config.SessionManager.getCurrentAccount() == null) {
            return new boolean[] { false, false, false, false, false, false };
        }

        // Thay mã 452 bằng đúng function_id của Hóa Đơn trong DB
        boolean canAdd = config.SessionManager.hasPermission(454, "Thêm");
        boolean canDelete = config.SessionManager.hasPermission(454, "Xóa");

        return new boolean[] { canAdd, false, canDelete, true, false, false };
    }
}