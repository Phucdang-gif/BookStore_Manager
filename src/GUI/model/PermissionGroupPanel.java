package GUI.model;

import BUS.PermissionGroupBUS;
import DTO.PermissionGroupDTO;
import GUI.dialog.PermissionGroupDialog;
import GUI.dialog.PermissionDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PermissionGroupPanel extends JPanel implements FeatureControllerInterface {

    private PermissionGroupBUS permissionGroupBUS = new PermissionGroupBUS();
    private JTable table;
    private DefaultTableModel tableModel;

    public PermissionGroupPanel() {
        initUI();
        loadDataToTable(permissionGroupBUS.getAll());
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Đã xóa cột Mô tả, thay bằng Trạng thái cho khớp DB
        String[] columns = { "ID Nhóm Quyền", "Tên Nhóm Quyền", "Trạng Thái" };
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

    private void loadDataToTable(ArrayList<PermissionGroupDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (PermissionGroupDTO group : list) {
                String statusStr = (group.getStatus() != null && group.getStatus().equals("active")) ? "Hoạt động"
                        : "Bị khóa";
                tableModel.addRow(new Object[] {
                        group.getPermissionGroupId(), group.getGroupName(), statusStr
                });
            }
        }
    }

    @Override
    public void onAdd() {
        PermissionGroupDialog dialog = new PermissionGroupDialog(null, true, "add", null);
        dialog.setVisible(true);
        onRefresh();
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhóm quyền cần sửa!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int groupId = (int) table.getValueAt(row, 0);
        PermissionGroupDTO selectedGroup = permissionGroupBUS.getPermissionGroupDTO(groupId);

        if (selectedGroup == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy dữ liệu nhóm quyền này trong CSDL!");
            return;
        }

        PermissionGroupDialog dialog = new PermissionGroupDialog(null, true, "update", selectedGroup);
        dialog.setVisible(true);
        onRefresh();
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhóm quyền cần xóa!");
            return;
        }

        String groupName = (String) table.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hành động này sẽ XÓA VĨNH VIỄN nhóm quyền [" + groupName + "].\nBạn có chắc chắn muốn tiếp tục?",
                "Cảnh báo", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int groupId = (int) table.getValueAt(row, 0);

            // GỌI XUỐNG BUS ĐỂ KIỂM TRA VÀ XÓA
            DTO.ValidationResult result = permissionGroupBUS.deleteGroup(groupId);

            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Đã xóa nhóm quyền thành công!");
                onRefresh(); // Load lại bảng
            } else {
                // Nếu bị chặn (do còn tài khoản dùng), hiển thị câu thông báo lỗi
                JOptionPane.showMessageDialog(this, result.getSummary(), "Từ chối thao tác", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhóm quyền để cấu hình chi tiết!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int groupId = (int) table.getValueAt(row, 0);
        String groupName = (String) table.getValueAt(row, 1);

        PermissionDialog pDialog = new PermissionDialog(null, true, groupId, groupName);
        pDialog.setVisible(true);
    }

    @Override
    public void onSearch(String text) {
        // Xóa dòng hiển thị JOptionPane cũ đi
        // Gọi hàm search từ BUS để lấy danh sách đã lọc
        ArrayList<PermissionGroupDTO> result = permissionGroupBUS.search(text);

        // Đẩy danh sách kết quả lên bảng
        loadDataToTable(result);
    }

    @Override
    public void onRefresh() {
        permissionGroupBUS.refreshData();
        loadDataToTable(permissionGroupBUS.getAll());
    }

    @Override
    public void onExportExcel() {
        GUI.util.ExcelExporter.exportJTableToExcel(table, "DanhSachNhomQuyen");
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
        boolean canAdd = config.SessionManager.hasPermission(458, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(458, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(458, "Xóa");

        return new boolean[] { canAdd, canEdit, canDelete, canEdit, true, false };
    }
}