package GUI.model;

import BUS.AccountBUS;
import BUS.PermissionGroupBUS;
import DTO.AccountDTO;
import DTO.PermissionGroupDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AccountPanel extends JPanel implements FeatureControllerInterface {

    private AccountBUS accountBUS = new AccountBUS();
    private PermissionGroupBUS permissionGroupBUS = new PermissionGroupBUS();

    private JTable table;
    private DefaultTableModel tableModel;

    public AccountPanel() {
        initUI();
        loadDataToTable(accountBUS.getAll());
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "ID Tài Khoản", "Mã Nhân Viên", "Username", "Nhóm Quyền", "Trạng Thái" };
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

    private void loadDataToTable(ArrayList<AccountDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (AccountDTO acc : list) {
                String statusStr = (acc.getStatus() != null && acc.getStatus().equals("active")) ? "Hoạt động"
                        : "Bị khóa";
                String roleName = "Chưa phân quyền";

                // Lấy tên nhóm quyền từ DB
                PermissionGroupDTO group = permissionGroupBUS.getPermissionGroupDTO(acc.getPermissionGroupId());
                if (group != null)
                    roleName = group.getGroupName();

                tableModel.addRow(new Object[] {
                        acc.getAccountId(), acc.getEmployeeId(), acc.getUsername(), roleName, statusStr
                });
            }
        }
    }

    @Override
    public void onAdd() {
        // Mở dialog trống ở chế độ "add"
        GUI.dialog.AccountDialog dialog = new GUI.dialog.AccountDialog(null, true, "add", null);
        dialog.setVisible(true);
        onRefresh(); // Refresh lại bảng sau khi tắt Dialog
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Lấy ID tài khoản từ dòng đang chọn (Cột 0)
        int accountId = (int) table.getValueAt(row, 0);

        // 2. Tìm đối tượng AccountDTO tương ứng trong list của BUS
        AccountDTO selectedAcc = null;
        for (AccountDTO acc : accountBUS.getAll()) {
            if (acc.getAccountId() == accountId) {
                selectedAcc = acc;
                break;
            }
        }

        if (selectedAcc != null) {
            // 3. Truyền cục DTO sang Dialog ở chế độ "update"
            GUI.dialog.AccountDialog dialog = new GUI.dialog.AccountDialog(null, true, "update", selectedAcc);
            dialog.setVisible(true);
            onRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy dữ liệu tài khoản!");
        }
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Chắc chắn muốn xóa tài khoản này?", "Cảnh báo",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int accountId = (int) table.getValueAt(row, 0);

            // Xóa thật (Tạm thời để tham số người đang đăng nhập là -1)
            DTO.ValidationResult result = accountBUS.deleteAccount(accountId, -1);
            if (result.showAlert(this)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công");
                onRefresh();
            }
        }
    }

    @Override
    public void onDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xem chi tiết!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int accountId = (int) table.getValueAt(row, 0);
        AccountDTO selectedAcc = null;
        for (AccountDTO acc : accountBUS.getAll()) {
            if (acc.getAccountId() == accountId) {
                selectedAcc = acc;
                break;
            }
        }

        if (selectedAcc != null) {
            // Gọi Dialog nhưng truyền chữ "view"
            GUI.dialog.AccountDialog dialog = new GUI.dialog.AccountDialog(null, true, "view", selectedAcc);
            dialog.setVisible(true);
        }
    }

    @Override
    public void onSearch(String text) {
        ArrayList<AccountDTO> result = accountBUS.search(text, "Tất cả");
        loadDataToTable(result);
    }

    @Override
    public void onRefresh() {
        loadDataToTable(accountBUS.getAll());
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu");
    }

    @Override
    public void onExportExcel() {
        try {
            // 1. Mở hộp thoại cho người dùng chọn nơi lưu
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
            fileChooser
                    .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                // Tự động thêm đuôi .xlsx nếu người dùng quên gõ
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                // 2. Khởi tạo Workbook và Sheet của Apache POI
                org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("DanhSachTaiKhoan");

                // 3. Tạo dòng tiêu đề (Header) từ tên cột của JTable
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(table.getColumnName(i));

                    // In đậm tiêu đề cho đẹp
                    org.apache.poi.ss.usermodel.CellStyle style = workbook.createCellStyle();
                    org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                    font.setBold(true);
                    style.setFont(font);
                    cell.setCellStyle(style);
                }

                // 4. Quét toàn bộ JTable để chép dữ liệu ra dòng Excel
                for (int i = 0; i < table.getRowCount(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object val = table.getValueAt(i, j);
                        row.createCell(j).setCellValue(val != null ? val.toString() : "");
                    }
                }

                // Tự động căn chỉnh độ rộng các cột
                for (int i = 0; i < table.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // 5. Ghi ra file
                java.io.FileOutputStream out = new java.io.FileOutputStream(filePath);
                workbook.write(out);
                out.close();
                workbook.close();

                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công tại:\n" + filePath, "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel: " + e.getMessage(), "Lỗi Nghiêm Trọng",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onImportExcel() {
        try {
            // Hướng dẫn người dùng cấu trúc file mẫu
            String msg = "Để nhập dữ liệu, file Excel của bạn phải cấu trúc ĐÚNG 4 cột theo thứ tự sau (Bắt đầu từ cột A, dữ liệu từ dòng 2):\n\n"
                    + "1. Mã Nhân Viên (Số)\n"
                    + "2. Tên Đăng Nhập (Username)\n"
                    + "3. Mật Khẩu (Password)\n"
                    + "4. Mã Nhóm Quyền (Số ID của nhóm)\n\n"
                    + "Bạn có muốn tiếp tục chọn file không?";

            int confirm = JOptionPane.showConfirmDialog(this, msg, "Hướng dẫn Nhập Excel", JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION)
                return;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file mẫu Excel để nhập dữ liệu");
            fileChooser
                    .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

            int userSelection = fileChooser.showOpenDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToOpen = fileChooser.getSelectedFile();

                // Đọc file bằng POI
                java.io.FileInputStream fis = new java.io.FileInputStream(fileToOpen);
                org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                        fis);
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

                int successCount = 0;
                int errorCount = 0;

                // Duyệt các dòng (Bỏ qua dòng 0 vì là dòng tiêu đề)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                    if (row == null)
                        continue;

                    try {
                        // Đọc 4 cột
                        int empId = (int) row.getCell(0).getNumericCellValue();
                        String username = row.getCell(1).getStringCellValue();
                        String password = row.getCell(2).getStringCellValue();
                        int groupId = (int) row.getCell(3).getNumericCellValue();

                        // Gọi BUS để thêm vào Database
                        AccountDTO newAcc = new AccountDTO(0, empId, groupId, username, password, "active", null);
                        boolean isAdded = accountBUS.addAccount(newAcc).isValid();

                        if (isAdded)
                            successCount++;
                        else
                            errorCount++;

                    } catch (Exception ex) {
                        errorCount++; // Bỏ qua dòng bị lỗi định dạng
                    }
                }

                workbook.close();
                fis.close();

                onRefresh(); // Làm mới lại JTable

                JOptionPane.showMessageDialog(
                        this, "Nhập file Excel hoàn tất!\n- Thành công: " + successCount
                                + " tài khoản\n- Lỗi / Bỏ qua: " + errorCount + " dòng",
                        "Kết quả Import", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi đọc file Excel! Vui lòng đảm bảo file không bị hỏng và đúng định dạng (.xlsx).", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean[] getButtonConfig() {
        if (config.SessionManager.getCurrentAccount() == null) {
            return new boolean[] { false, false, false, false, false, false };
        }

        // Thay mã 452 bằng đúng function_id của Hóa Đơn trong DB
        boolean canAdd = config.SessionManager.hasPermission(458, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(458, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(458, "Xóa");

        return new boolean[] { canAdd, canEdit, canDelete, true, false, false };
    }
}