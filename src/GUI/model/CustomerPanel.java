package GUI.model;

import BUS.CustomerBUS;
import DTO.CustomerDTO;
import GUI.dialog.CustomerDialog;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;

public class CustomerPanel extends JPanel implements FeatureControllerInterface {

    private CustomerBUS customerBUS = new CustomerBUS();
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerPanel() {
        initUI();
        loadDataToTable(customerBUS.getAll());
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm Tích Lũy", "Ngày Đăng Ký" };
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
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDataToTable(ArrayList<CustomerDTO> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tableModel.setRowCount(0);
        if (list != null) {
            for (CustomerDTO cus : list) {
                String dateStr = "";
                if (cus.getRegistrationDate() != null) {
                    dateStr = dateFormat.format(cus.getRegistrationDate());
                }
                tableModel.addRow(new Object[] {
                        cus.getCustomerId(), cus.getFullName(), cus.getPhone(),
                        cus.getLoyaltyPoints(), dateStr
                });
            }
        }
    }

    @Override
    public void onAdd() {
        CustomerDialog dialog = new CustomerDialog(null, true, "add", null, customerBUS);
        dialog.setVisible(true);
        onRefresh();
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        CustomerDTO selectedCus = customerBUS.getAll().stream().filter(c -> c.getCustomerId() == id).findFirst()
                .orElse(null);

        if (selectedCus != null) {
            CustomerDialog dialog = new CustomerDialog(null, true, "update", selectedCus, customerBUS);
            dialog.setVisible(true);
            onRefresh();
        }
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa khách hàng này?\n(Không thể xóa nếu khách đã từng mua hàng)", "Cảnh báo",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) table.getValueAt(row, 0);
            DTO.ValidationResult vr = customerBUS.deleteCustomer(id);

            if (vr.isValid()) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                onRefresh();
            } else {
                // Hiển thị thông báo lỗi cụ thể từ hệ thống hoặc ràng buộc dữ liệu
                JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDetail() {
    }

    @Override
    public void onSearch(String text) {
        loadDataToTable(customerBUS.search(text));
    }

    @Override
    public void onRefresh() {
        customerBUS.refreshData();
        loadDataToTable(customerBUS.getAll());
    }

    @Override
    public void onExportExcel() {
        GUI.util.ExcelExporter.exportJTableToExcel(table, "DanhSachKhachHang");
    }

    @Override
    public void onImportExcel() {
        try {
            String msg = "File Excel cần có 2 cột (Dữ liệu bắt đầu từ dòng 2):\n1. Họ Tên Khách Hàng\n2. Số Điện Thoại";
            if (JOptionPane.showConfirmDialog(this, msg, "Hướng dẫn", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.FileInputStream fis = new java.io.FileInputStream(file);
                org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(fis);
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

                int success = 0, fail = 0;
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                    if (row == null) continue;
                    try {
                        String name = row.getCell(0).getStringCellValue();
                        // Xử lý cột SĐT (nếu Excel tự hiểu là số)
                        org.apache.poi.ss.usermodel.Cell phoneCell = row.getCell(1);
                        String phone = phoneCell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC 
                                     ? "0" + (long) phoneCell.getNumericCellValue() 
                                     : phoneCell.getStringCellValue();

                        CustomerDTO cus = new CustomerDTO();
                        cus.setFullName(name);
                        cus.setPhone(phone);
                        cus.setLoyaltyPoints(0); // Khách mới mặc định 0 điểm

                        if (customerBUS.addCustomer(cus).isValid()) success++;
                        else fail++;
                    } catch (Exception e) {
                        fail++; 
                    }
                }
                workbook.close(); fis.close();
                onRefresh();
                JOptionPane.showMessageDialog(this, "Import hoàn tất!\n- Thành công: " + success + "\n- Lỗi/Trùng SĐT: " + fail);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file Excel!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean[] getButtonConfig() {
        if (config.SessionManager.getCurrentAccount() == null) {
            return new boolean[] { false, false, false, false, false, false };
        }

        boolean canAdd = config.SessionManager.hasPermission(453, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(453, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(453, "Xóa");

        return new boolean[] { canAdd, canEdit, canDelete, false, true, canAdd };
    }
}