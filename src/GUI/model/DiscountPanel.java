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
        discountBUS.refreshData();
        loadDataToTable(discountBUS.getAll());
    }

    @Override
    public void onExportExcel() {
        GUI.util.ExcelExporter.exportJTableToExcel(table, "DanhSachChuongTrinhKhuyenMai");
    }

    @Override
    public void onImportExcel() {
        try {
            String msg = "File Excel cần 5 cột (Dữ liệu từ dòng 2):\n1. Tên chương trình\n2. Loại giảm (Phần trăm/Số tiền cố định)\n3. Giá trị giảm\n4. Đơn hàng tối thiểu\n5. Mức giảm tối đa";
            if (JOptionPane.showConfirmDialog(this, msg, "Hướng dẫn Nhập Excel", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.FileInputStream fis = new java.io.FileInputStream(file);
                org.apache.poi.ss.usermodel.Workbook wb = org.apache.poi.ss.usermodel.WorkbookFactory.create(fis);
                org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);

                int success = 0, fail = 0;
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                    if (row == null) continue;
                    try {
                        DTO.DiscountServiceDTO discount = new DTO.DiscountServiceDTO();
                        discount.setServiceName(row.getCell(0).getStringCellValue());
                        discount.setDiscountType(row.getCell(1).getStringCellValue());
                        discount.setDiscountValue(row.getCell(2).getNumericCellValue());
                        discount.setMinimumAmount(row.getCell(3).getNumericCellValue());
                        discount.setMaximumDiscount(row.getCell(4).getNumericCellValue());
                        discount.setStatus("active");
                        
                        // Set ngày bắt đầu là hôm nay, ngày kết thúc là 30 ngày sau
                        long currentTime = System.currentTimeMillis();
                        discount.setStartDate(new java.sql.Timestamp(currentTime));
                        discount.setEndDate(new java.sql.Timestamp(currentTime + (30L * 24 * 60 * 60 * 1000)));

                        // Giả định hàm thêm là addDiscount trong DiscountServiceBUS
                        if (discountBUS.addDiscount(discount)) success++;
                        else fail++;
                    } catch (Exception e) {
                        fail++; 
                    }
                }
                wb.close(); fis.close();
                onRefresh();
                JOptionPane.showMessageDialog(this, "Import hoàn tất!\n- Thành công: " + success + "\n- Lỗi/Bỏ qua: " + fail);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng file Excel!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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

        return new boolean[] { canAdd, canEdit, canDelete, false, true, canAdd };
    }
}