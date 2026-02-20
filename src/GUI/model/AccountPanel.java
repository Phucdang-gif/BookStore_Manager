package GUI.model;

import BUS.AccountBUS;
import BUS.RoleBUS;
import DTO.AccountDTO;
import DTO.RoleDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Bắt buộc implements FeatureControllerInterface để kết nối với Header
public class AccountPanel extends JPanel implements FeatureControllerInterface {

    private AccountBUS accountBUS = new AccountBUS();
    private RoleBUS roleBUS = new RoleBUS(); 
    
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

        // Vẽ JTable
        String[] columns = {"ID Tài Khoản", "Mã Nhân Viên", "Username", "Nhóm Quyền", "Trạng Thái"};
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
                String statusStr = (acc.getStatus() != null && acc.getStatus().equals("1")) ? "Hoạt động" : "Bị khóa";
                String roleName = "Chưa phân quyền";
                RoleDTO role = roleBUS.getRoleDTO(acc.getRoleId());
                if (role != null) roleName = role.getRoleName();

                tableModel.addRow(new Object[]{
                    acc.getAccountId(), acc.getEmployeeId(), acc.getUsername(), roleName, statusStr
                });
            }
        }
    }

    // =========================================================================
    // CÁC HÀM CỦA FEATURE CONTROLLER INTERFACE (Nhận lệnh từ Header)
    // =========================================================================

    @Override
    public void onAdd() {
        JOptionPane.showMessageDialog(this, "Mở Dialog Thêm Tài Khoản tại đây!");
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa!");
            return;
        }
        JOptionPane.showMessageDialog(this, "Mở Dialog Sửa Tài Khoản " + table.getValueAt(row, 2));
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Chắc chắn muốn xóa?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int accountId = (int) table.getValueAt(row, 0);
            String msg = accountBUS.deleteAccount(accountId, -1); // Tạm để -1, sẽ ráp account đang đăng nhập sau
            JOptionPane.showMessageDialog(this, msg);
            onRefresh();
        }
    }

    @Override
    public void onDetail() {
        JOptionPane.showMessageDialog(this, "Xem chi tiết tài khoản!");
    }

    @Override
    public void onSearch(String text) {
        ArrayList<AccountDTO> result = accountBUS.search(text, "Tất cả");
        loadDataToTable(result);
    }

    @Override
    public void onRefresh() {
        loadDataToTable(accountBUS.getAll());
    }

    @Override
    public void onExportExcel() {
        JOptionPane.showMessageDialog(this, "Xuất Excel Tài Khoản");
    }

    @Override
    public void onImportExcel() {
        JOptionPane.showMessageDialog(this, "Nhập Excel Tài Khoản");
    }

    @Override
    public boolean[] getButtonConfig() {
        // Cấu hình bật/tắt nút trên Header: {Add, Edit, Delete, Detail, Export, Import}
        return new boolean[]{true, true, true, true, false, false}; 
    }
}