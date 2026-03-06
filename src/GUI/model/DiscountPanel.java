package GUI.model;

import BUS.DiscountServiceBUS;
import DTO.DiscountServiceDTO;
import GUI.dialog.DiscountDialog; // Lát nữa ta sẽ tạo file này
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class DiscountPanel extends JPanel implements FeatureControllerInterface {

    private DiscountServiceBUS discountBUS = new DiscountServiceBUS();
    private JTable table;
    private DefaultTableModel tableModel;

    private DecimalFormat df = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public DiscountPanel() {
        initUI();
        loadDataToTable(discountBUS.getAll());
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "Mã KM", "Tên Chương Trình", "Giá Trị", "Đơn Tối Thiểu", "Giảm Tối Đa", "Ngày Bắt Đầu",
                "Ngày Kết Thúc", "Trạng Thái" };
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

    private void loadDataToTable(ArrayList<DiscountServiceDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (DiscountServiceDTO d : list) {
                // Xử lý hiển thị phần trăm hoặc tiền mặt
                String typeSign = ("Phần trăm".equals(d.getDiscountType())) ? "%" : " VNĐ";
                String discountValStr = df.format(d.getDiscountValue()) + typeSign;

                String minAmountStr = df.format(d.getMinimumAmount()) + " VNĐ";
                String maxDiscountStr = df.format(d.getMaximumDiscount()) + " VNĐ";

                String startDateStr = (d.getStartDate() != null) ? sdf.format(d.getStartDate()) : "";
                String endDateStr = (d.getEndDate() != null) ? sdf.format(d.getEndDate()) : "";

                String statusStr = (d.getStatus() != null && d.getStatus().equals("active")) ? "Hoạt động" : "Tạm dừng";

                tableModel.addRow(new Object[] {
                        d.getServiceId(),
                        d.getServiceName(),
                        discountValStr,
                        minAmountStr,
                        maxDiscountStr,
                        startDateStr,
                        endDateStr,
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
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        DiscountDialog dialog = new DiscountDialog(parentFrame, true, "add", null, discountBUS);
        dialog.setVisible(true);

        onRefresh();
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Chương trình khuyến mãi cần sửa!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int serviceId = (int) table.getValueAt(row, 0);
        DiscountServiceDTO selectedDiscount = null;
        for (DiscountServiceDTO d : discountBUS.getAll()) {
            if (d.getServiceId() == serviceId) {
                selectedDiscount = d;
                break;
            }
        }

        if (selectedDiscount != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            DiscountDialog dialog = new DiscountDialog(parentFrame, true, "update", selectedDiscount, discountBUS);
            dialog.setVisible(true);
            onRefresh();
        }
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Chương trình cần dừng!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn TẠM DỪNG chương trình khuyến mãi này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int serviceId = (int) table.getValueAt(row, 0);
            boolean isSuccess = discountBUS.deleteDiscount(serviceId);
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đã tạm dừng chương trình khuyến mãi!");
                onRefresh();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể thực hiện thao tác này!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDetail() {
    }

    @Override
    public void onSearch(String text) {
        ArrayList<DiscountServiceDTO> result = discountBUS.search(text);
        loadDataToTable(result);
    }

    @Override
    public void onRefresh() {
        loadDataToTable(discountBUS.getAll());
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
        boolean canAdd = config.SessionManager.hasPermission(456, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(456, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(456, "Xóa");

        return new boolean[] { canAdd, canEdit, canDelete, false, false, false };
    }
}