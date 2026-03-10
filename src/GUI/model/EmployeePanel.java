package GUI.model;

import BUS.EmployeeBUS;
import DTO.EmployeeDTO;
import DTO.ValidationResult;
import GUI.dialog.EmployeeDialog;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

public class EmployeePanel extends JPanel implements FeatureControllerInterface {

    private EmployeeBUS employeeBUS = new EmployeeBUS();
    private JTable table;
    private DefaultTableModel tableModel;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private Runnable onAvatarUpdated;

    public void setOnAvatarUpdated(Runnable callback) {
        this.onAvatarUpdated = callback;
    }

    public EmployeePanel() {
        initUI();
        loadDataToTable(employeeBUS.getAll());
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "Mã NV", "Họ Tên", "Giới Tính", "Số Điện Thoại", "Chức Vụ", "Lương", "Trạng Thái",
                "Avatar" };

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

    private void loadDataToTable(ArrayList<EmployeeDTO> list) {
        tableModel.setRowCount(0);
        if (list == null)
            return;

        for (EmployeeDTO emp : list) {
            String avatarText = (emp.getAvatar() != null && !emp.getAvatar().isEmpty())
                    ? emp.getAvatar()
                    : "";

            String genderStr = "male".equals(emp.getGender()) ? "Nam" : "Nữ";
            String statusStr = emp.getStatus() != null && "active".equals(emp.getStatus())
                    ? "Đang làm"
                    : "Đã nghỉ";

            tableModel.addRow(new Object[] {
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    genderStr,
                    emp.getPhone(),
                    emp.getPosition(),
                    df.format(emp.getSalary()),
                    statusStr,
                    avatarText
            });
        }
    }

    // ===== ACTIONS =====

    @Override
    public void onAdd() {
        EmployeeDialog dialog = new EmployeeDialog(null, true, "add", null, employeeBUS);
        dialog.setVisible(true);
        onRefresh();
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0); // Cột 0 là Mã NV
        EmployeeDTO selectedEmp = employeeBUS.getAll().stream()
                .filter(e -> e.getEmployeeId() == id).findFirst().orElse(null);

        if (selectedEmp != null) {
            EmployeeDialog dialog = new EmployeeDialog(null, true, "update", selectedEmp, employeeBUS);
            dialog.setVisible(true);
            onRefresh();
            int currentEmpId = config.SessionManager.getCurrentAccount().getEmployeeId();
            if (id == currentEmpId && onAvatarUpdated != null) {
                onAvatarUpdated.run();
            }
        }
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để cho nghỉ việc!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận cho nhân viên này nghỉ việc?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) table.getValueAt(row, 0);
            ValidationResult vr = employeeBUS.deleteEmployee(id);
            if (vr.isValid()) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                onRefresh();
            } else {
                JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xem chi tiết!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        EmployeeDTO selectedEmp = employeeBUS.getById(id);

        if (selectedEmp != null) {
            EmployeeDialog dialog = new EmployeeDialog(null, true, "detail", selectedEmp, employeeBUS);
            dialog.setVisible(true);
        }
    }

    @Override
    public void onSearch(String text) {
        loadDataToTable(employeeBUS.search(text));
    }

    @Override
    public void onRefresh() {
        employeeBUS.refreshData();
        loadDataToTable(employeeBUS.getAll());
    }

   @Override
    public void onExportExcel() {
        GUI.util.ExcelExporter.exportJTableToExcel(table, "DanhSachNhanVien");
    }

    @Override
    public void onImportExcel() {
        try {
            String msg = "File Excel cần 6 cột:\n1. Họ Tên\n2. Giới tính (Nam/Nữ)\n3. SĐT\n4. Địa chỉ\n5. Chức vụ\n6. Lương (Số)";
            if (JOptionPane.showConfirmDialog(this, msg, "Hướng dẫn", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

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
                        EmployeeDTO emp = new EmployeeDTO();
                        emp.setFullName(row.getCell(0).getStringCellValue());
                        
                        String gender = row.getCell(1).getStringCellValue();
                        emp.setGender(gender.equalsIgnoreCase("Nam") ? "male" : "female");
                        
                        org.apache.poi.ss.usermodel.Cell phoneCell = row.getCell(2);
                        emp.setPhone(phoneCell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC ? "0" + (long) phoneCell.getNumericCellValue() : phoneCell.getStringCellValue());
                        
                        emp.setAddress(row.getCell(3).getStringCellValue());
                        emp.setPosition(row.getCell(4).getStringCellValue());
                        emp.setSalary(row.getCell(5).getNumericCellValue());
                        emp.setStatus("active");
                        // Tạm set ngày sinh mặc định, có thể update sau
                        emp.setDateOfBirth(new java.sql.Date(System.currentTimeMillis())); 

                        if (employeeBUS.addEmployee(emp).isValid()) success++;
                        else fail++;
                    } catch (Exception e) {
                        fail++; 
                    }
                }
                wb.close(); fis.close();
                onRefresh();
                JOptionPane.showMessageDialog(this, "Import hoàn tất!\n- Thành công: " + success + "\n- Lỗi/Trùng lặp: " + fail);
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
        boolean canAdd = config.SessionManager.hasPermission(457, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(457, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(457, "Xóa");
        return new boolean[] { canAdd, canEdit, canDelete, true, true, canAdd };
    }
}