package GUI.model;

import BUS.RoleBUS;
import DTO.RoleDTO;
import GUI.dialog.RoleDialog;
import GUI.dialog.PermissionDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RolePanel extends JPanel implements FeatureControllerInterface {

    private RoleBUS roleBUS = new RoleBUS(); 
    private JTable table;
    private DefaultTableModel tableModel;

    public RolePanel() {
        initUI();
        loadDataToTable(roleBUS.getAll());
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID Nhóm Quyền", "Tên Nhóm Quyền", "Mô Tả"};
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

    private void loadDataToTable(ArrayList<RoleDTO> list) {
        tableModel.setRowCount(0); 
        if (list != null) {
            for (RoleDTO role : list) {
                tableModel.addRow(new Object[]{
                    role.getRoleId(), role.getRoleName(), role.getDescription()
                });
            }
        }
    }

    // ==========================================
    // CÁC LỆNH TỪ HEADER TRUYỀN XUỐNG
    // ==========================================

    @Override
    public void onAdd() {
        RoleDialog dialog = new RoleDialog(null, true, "add", null);
        dialog.setVisible(true);
        onRefresh(); // Refresh sau khi đóng dialog
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhóm quyền cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Chỉ lấy đúng cái ID từ bảng
        int roleId = (int) table.getValueAt(row, 0);

        // 2. Gọi BUS chọc xuống DB lấy FULL cục DTO lên (đảm bảo không rớt mất chữ nào)
        // (Hàm getRoleDTO này em đã tạo trong RoleBUS ở các bước trước rồi)
        RoleDTO selectedRole = roleBUS.getRoleDTO(roleId);

        if (selectedRole == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy dữ liệu nhóm quyền này trong CSDL!");
            return;
        }

        // 3. Ném cục DTO đầy đủ 5 tham số này sang Dialog
        RoleDialog dialog = new RoleDialog(null, true, "update", selectedRole);
        dialog.setVisible(true);
        
        // 4. Refresh bảng sau khi tắt Dialog
        onRefresh();
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhóm quyền cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Chắc chắn muốn xóa nhóm quyền này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int roleId = (int) table.getValueAt(row, 0);
            // Giả sử có hàm delete trong RoleBUS
            // String msg = roleBUS.deleteRole(roleId); 
            // JOptionPane.showMessageDialog(this, msg);
            JOptionPane.showMessageDialog(this, "Đã gọi hàm Xóa cho ID: " + roleId);
            onRefresh();
        }
    }

    @Override
    public void onDetail() {
        // NÚT CHI TIẾT SẼ DÙNG ĐỂ MỞ BẢNG TÍCH XANH QUYỀN HẠN
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhóm quyền để cấu hình chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int roleId = (int) table.getValueAt(row, 0);
        String roleName = (String) table.getValueAt(row, 1);
        
        PermissionDialog pDialog = new PermissionDialog(null, true, roleId, roleName);
        pDialog.setVisible(true);
    }

    @Override
    public void onSearch(String text) {
        // Gọi hàm search của RoleBUS (Tạm thời anh để trống, em tự gắn hàm BUS nhé)
        JOptionPane.showMessageDialog(this, "Tìm kiếm nhóm quyền: " + text);
    }

    @Override
    public void onRefresh() {
        loadDataToTable(roleBUS.getAll());
    }

    @Override
    public void onExportExcel() { }

    @Override
    public void onImportExcel() { }

    @Override
    public boolean[] getButtonConfig() {
        // Mở Add, Edit, Delete, Detail. Tắt Export, Import
        return new boolean[]{true, true, true, true, false, false}; 
    }
}