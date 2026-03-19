package GUI.model;

import BUS.ImportReceiptBUS;
import DTO.ImportReceiptDTO;
import GUI.dialog.CreateImportDialog;
import GUI.dialog.ImportDetailDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Date;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ImportReceiptPanel extends JPanel implements FeatureControllerInterface {

    private ImportReceiptBUS importBUS = new ImportReceiptBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbSearchType;
    private JTextField txtSearch;
    private com.toedter.calendar.JDateChooser dateStart;
    private com.toedter.calendar.JDateChooser dateEnd;
    private JButton btnSearch;
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

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class;
                }
                if (columnIndex == 1) {
                    return Integer.class;
                }
                if (columnIndex == 2) {
                    return Integer.class;
                }
                return String.class;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        this.add(scrollPane, BorderLayout.CENTER);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // Tạo sub-panel chứa các thành phần và Label
        JPanel searchContainer = new JPanel(new GridBagLayout());
        searchContainer.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 8, 5, 2); // Khoảng cách giữa Label và Component
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Nhóm ComboBox "Tìm theo"
        gbc.gridx = 0;
        searchContainer.add(new JLabel("Tìm theo:"), gbc);
        String[] searchTypes = { "Import ID", "Supplier ID", "Employee ID" };
        cbSearchType = new JComboBox<>(searchTypes);
        cbSearchType.setPreferredSize(new java.awt.Dimension(120, 30));
        gbc.gridx = 1;
        gbc.insets = new java.awt.Insets(5, 2, 5, 15); // Khoảng cách rộng hơn sau mỗi nhóm
        searchContainer.add(cbSearchType, gbc);

        // 2. Nhóm Ô nhập "ID"
        gbc.gridx = 2;
        gbc.insets = new java.awt.Insets(5, 5, 5, 2);
        searchContainer.add(new JLabel("ID:"), gbc);
        txtSearch = new JTextField(10);
        txtSearch.setPreferredSize(new java.awt.Dimension(100, 30));
        gbc.gridx = 3;
        gbc.insets = new java.awt.Insets(5, 2, 5, 15);
        searchContainer.add(txtSearch, gbc);

        // 3. Nhóm "Từ ngày"
        gbc.gridx = 4;
        gbc.insets = new java.awt.Insets(5, 5, 5, 2);
        searchContainer.add(new JLabel("Từ:"), gbc);
        dateStart = new com.toedter.calendar.JDateChooser();
        dateStart.setPreferredSize(new java.awt.Dimension(130, 30));
        gbc.gridx = 5;
        gbc.insets = new java.awt.Insets(5, 2, 5, 15);
        searchContainer.add(dateStart, gbc);

        // 4. Nhóm "Đến ngày"
        gbc.gridx = 6;
        gbc.insets = new java.awt.Insets(5, 5, 5, 2);
        searchContainer.add(new JLabel("Đến:"), gbc);
        dateEnd = new com.toedter.calendar.JDateChooser();
        dateEnd.setPreferredSize(new java.awt.Dimension(130, 30));
        gbc.gridx = 7;
        gbc.insets = new java.awt.Insets(5, 2, 5, 15);
        searchContainer.add(dateEnd, gbc);

        // 5. Nút tìm
        btnSearch = new JButton("Tìm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new java.awt.Dimension(80, 30));
        btnSearch.addActionListener(e -> executeSearch());
        gbc.gridx = 8;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        searchContainer.add(btnSearch, gbc);

        // Đưa toàn bộ cụm sang bên phải
        topPanel.add(searchContainer, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        this.add(topPanel, BorderLayout.NORTH);
    }

    private void loadDataToTable(ArrayList<ImportReceiptDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (ImportReceiptDTO dto : list) {
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
        ArrayList<ImportReceiptDTO> result = importBUS.search(text);
        loadDataToTable(result);

    }

    private void executeSearch() {
        String text = txtSearch.getText().trim();

        Date s = dateStart.getDate();
        Date eDate = dateEnd.getDate();
        String type = (String) cbSearchType.getSelectedItem();

        // Chuyển đổi sang java.sql.Date để làm việc với DB
        java.sql.Date sqlStart = (s != null) ? new java.sql.Date(s.getTime()) : null;
        java.sql.Date sqlEnd = (eDate != null) ? new java.sql.Date(eDate.getTime()) : null;

        ArrayList<ImportReceiptDTO> result;

        if (txtSearch.getText().trim().isEmpty()) {
            // Nếu ID trống, lọc theo ngày (nếu có chọn ngày)
            if (s != null && eDate != null) {
                result = importBUS.searchByDate(sqlStart, sqlEnd);
            } else {
                result = importBUS.getAll();
            }
        } else {
            int ID = Integer.valueOf(text);
            // Tùy theo lựa chọn trong ComboBox mà gọi hàm tương ứng
            switch (type) {
                case "Employee ID":
                    result = importBUS.searchByEmployeeID(ID, sqlStart, sqlEnd);
                    break;
                case "Supplier ID":
                    result = importBUS.searchBySupplierID(ID, sqlStart, sqlEnd);
                    break;
                default: // Mặc định là Invoice ID
                    result = importBUS.searchByImportID(ID, sqlStart, sqlEnd);
                    break;
            }
        }
        loadDataToTable(result);
    }

    @Override
    public boolean hasSearch() {
        return false;
    }

    @Override
    public void onRefresh() {
        importBUS.refreshData();
        loadDataToTable(importBUS.getAll());
    }

    @Override
    public void onExportExcel() {
        GUI.util.ExcelExporter.exportJTableToExcel(table, "DanhSachPhieuNhap");
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

        return new boolean[] { canAdd, false, canDelete, true, true, false };
    }
}