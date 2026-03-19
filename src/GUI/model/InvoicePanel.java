package GUI.model;

import BUS.InvoiceBUS;
import DTO.InvoiceDTO;
import GUI.dialog.InvoiceDetailDialog;
import GUI.dialog.CreateInvoiceDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class InvoicePanel extends JPanel implements FeatureControllerInterface {

    private InvoiceBUS invoiceBUS = new InvoiceBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbSearchType;
    private JTextField txtSearch;
    private com.toedter.calendar.JDateChooser dateStart;
    private com.toedter.calendar.JDateChooser dateEnd;
    private JButton btnSearch;
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
        String[] columns = { "Mã Hóa Đơn", "Khách Hàng", "Nhân Viên", "Ngày Lập", "Phương Thức", "Tổng Tiền",
                "Trạng Thái" };
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
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        // ===== PANEL TÌM KIẾM =====
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
        String[] searchTypes = { "Invoice ID", "Customer ID", "Employee ID" };
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
        this.add(scrollPane, BorderLayout.CENTER);

    }

    private void loadDataToTable(ArrayList<InvoiceDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (InvoiceDTO dto : list) {
                String statusStr = dto.getStatus().equals("Completed") ? "Hoàn Thành" : "Đã Hủy";
                tableModel.addRow(new Object[] {
                        dto.getInvoiceId(),
                        dto.getCustomerId(),
                        dto.getEmployeeId(),
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
        // Mở form tạo hóa đơn mới (CreateInvoiceDialog)
        CreateInvoiceDialog dialog = new CreateInvoiceDialog(null, true);
        dialog.setVisible(true);

        loadDataToTable(invoiceBUS.getAll());
    }

    @Override
    public void onEdit() {

    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Hóa đơn cần hủy!");
            return;
        }

        String currentStatus = (String) table.getValueAt(row, 6);
        if (currentStatus.equals("Đã Hủy")) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã bị hủy từ trước!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn HỦY hóa đơn này?\n(Hành động này không thể hoàn tác)", "Cảnh báo",
                JOptionPane.YES_NO_OPTION);
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
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Hóa đơn để xem chi tiết!");
            return;
        }
        int invoiceId = (int) table.getValueAt(row, 0);

        // Mở Dialog xem chi tiết các cuốn sách trong hóa đơn
        InvoiceDetailDialog dialog = new InvoiceDetailDialog(null, true, invoiceId);
        dialog.setVisible(true);
    }

    // Tim kiem theo ID, ngay gio
    private void executeSearch() {
        String text = txtSearch.getText().trim();

        Date s = dateStart.getDate();
        Date eDate = dateEnd.getDate();
        String type = (String) cbSearchType.getSelectedItem();

        // Chuyển đổi sang java.sql.Date để làm việc với DB
        java.sql.Date sqlStart = (s != null) ? new java.sql.Date(s.getTime()) : null;
        java.sql.Date sqlEnd = (eDate != null) ? new java.sql.Date(eDate.getTime()) : null;

        ArrayList<InvoiceDTO> result;

        if (txtSearch.getText().trim().isEmpty()) {
            // Nếu ID trống, lọc theo ngày (nếu có chọn ngày)
            if (s != null && eDate != null) {
                result = invoiceBUS.searchByDate(sqlStart, sqlEnd);
            } else {
                result = invoiceBUS.getAll();
            }
        } else {
            int ID = Integer.valueOf(text);
            // Tùy theo lựa chọn trong ComboBox mà gọi hàm tương ứng
            switch (type) {
                case "Customer ID":
                    result = invoiceBUS.searchByCustomerID(ID, sqlStart, sqlEnd);
                    break;
                case "Employee ID":
                    result = invoiceBUS.searchByemployeeID(ID, sqlStart, sqlEnd);
                    break;
                default: // Mặc định là Invoice ID
                    result = invoiceBUS.searchByInvoiceID(ID, sqlStart, sqlEnd);
                    break;
            }
        }
        loadDataToTable(result);
    }

    @Override
    public void onSearch(String text) {
        loadDataToTable(invoiceBUS.search(text));
    }

    @Override
    public void onRefresh() {
        InvoiceBUS invoiceBUS = new InvoiceBUS();
        loadDataToTable(invoiceBUS.getAll());
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu!");
    }

    @Override
    public void onExportExcel() {
        GUI.util.ExcelExporter.exportJTableToExcel(table, "DanhSachHoaDon");
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
        boolean canAdd = config.SessionManager.hasPermission(455, "Thêm");

        boolean canDelete = config.SessionManager.hasPermission(455, "Xóa");

        return new boolean[] { canAdd, false, canDelete, true, true, false };
    }

    @Override
    public boolean hasSearch() {
        return false;
    }
}