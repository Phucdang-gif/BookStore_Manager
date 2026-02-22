package GUI.model;

import BUS.EmployeeBUS;
import DTO.EmployeeDTO;
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

    public EmployeePanel() {
        initUI();
        loadDataToTable(employeeBUS.getAll());
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Mã NV", "Họ Tên", "Giới Tính", "Số Điện Thoại", "Chức Vụ", "Lương", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
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
        if (list != null) {
            for (EmployeeDTO emp : list) {
                String genderStr = emp.getGender().equals("male") ? "Nam" : "Nữ";
                String statusStr = (emp.getStatus() != null && emp.getStatus().equals("active")) ? "Đang làm" : "Đã nghỉ";
                tableModel.addRow(new Object[]{
                    emp.getEmployeeId(), emp.getFullName(), genderStr, 
                    emp.getPhone(), emp.getPosition(), df.format(emp.getSalary()), statusStr
                });
            }
        }
    }

    @Override
    public void onAdd() {
        EmployeeDialog dialog = new EmployeeDialog(null, true, "add", null, employeeBUS);
        dialog.setVisible(true);
        onRefresh();
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!"); return;
        }
        int id = (int) table.getValueAt(row, 0);
        EmployeeDTO selectedEmp = employeeBUS.getAll().stream().filter(e -> e.getEmployeeId() == id).findFirst().orElse(null);
        
        if(selectedEmp != null) {
            EmployeeDialog dialog = new EmployeeDialog(null, true, "update", selectedEmp, employeeBUS);
            dialog.setVisible(true);
            onRefresh();
        }
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để cho nghỉ việc!"); return; }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận cho nhân viên này nghỉ việc?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) table.getValueAt(row, 0);
            if(employeeBUS.deleteEmployee(id)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                onRefresh();
            }
        }
    }

    @Override public void onDetail() {}
    @Override public void onSearch(String text) { loadDataToTable(employeeBUS.search(text)); }
    @Override public void onRefresh() { loadDataToTable(employeeBUS.getAll()); }
    @Override public void onExportExcel() {}
    @Override public void onImportExcel() {}
    @Override public boolean[] getButtonConfig() { return new boolean[]{true, true, true, false, false, false}; }
}
