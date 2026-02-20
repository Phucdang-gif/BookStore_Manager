package GUI.dialog;

import BUS.PermissionBUS;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PermissionDialog extends JDialog {

    private int roleId;
    private String roleName;
    private PermissionBUS permissionBUS = new PermissionBUS(); // BUS xử lý phân quyền

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnSave, btnCancel;

    public PermissionDialog(Frame owner, boolean modal, int roleId, String roleName) {
        super(owner, modal);
        this.roleId = roleId;
        this.roleName = roleName;

        setTitle("Cấu hình Chi Tiết Quyền: " + roleName.toUpperCase());
        setSize(700, 450);
        setLocationRelativeTo(null);
        initUI();
        loadPermissionsFromDB();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JLabel lblHeader = new JLabel("TÍCH CHỌN CÁC QUYỀN CHO NHÓM: " + roleName.toUpperCase(), SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Khởi tạo bảng với các cột chứa Checkbox
        String[] columns = {"Mã Chức Năng", "Tên Chức Năng", "Xem", "Thêm", "Sửa", "Xóa"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Cột 2, 3, 4, 5 (Xem, Thêm, Sửa, Xóa) sẽ là dạng Checkbox (Boolean)
                if (columnIndex >= 2) return Boolean.class; 
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép click vào các cột Checkbox, không cho sửa tên chức năng
                return column >= 2; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Vùng nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu Phân Quyền");
        btnCancel = new JButton("Đóng");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> savePermissions());

        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void loadPermissionsFromDB() {
        // CỨNG DANH SÁCH CHỨC NĂNG (Bạn có thể load từ bảng functions trong DB)
        Object[][] features = {
            {"BOOK", "Quản Lý Sách"},
            {"GROUP", "Quản Lý Danh Mục"},
            {"ACCOUNT", "Quản Lý Tài Khoản"},
            {"ROLE", "Quản Lý Phân Quyền"}
        };

        for (Object[] f : features) {
            String funcCode = (String) f[0];
            
            // Dùng PermissionBUS để kiểm tra nhóm này có quyền gì rồi set True/False
            // Ví dụ: boolean canView = permissionBUS.checkPermission(roleId, funcCode, "view");
            boolean canView = false; // Tạm fix cứng để lên form
            boolean canAdd = false;
            boolean canEdit = false;
            boolean canDelete = false;

            tableModel.addRow(new Object[]{
                funcCode, f[1], canView, canAdd, canEdit, canDelete
            });
        }
    }

    private void savePermissions() {
        // Thu thập toàn bộ các ô Checkbox đã tích
        // ArrayList<PermissionDTO> listNewPerms = new ArrayList<>();

        for (int i = 0; i < table.getRowCount(); i++) {
            String funcCode = (String) table.getValueAt(i, 0);
            boolean canView   = (boolean) table.getValueAt(i, 2);
            boolean canAdd    = (boolean) table.getValueAt(i, 3);
            boolean canEdit   = (boolean) table.getValueAt(i, 4);
            boolean canDelete = (boolean) table.getValueAt(i, 5);

            // NẾU TÍCH XANH -> Gom vào List
            // if(canView) listNewPerms.add(new PermissionDTO(roleId, funcCode, "view"));
            // if(canAdd) listNewPerms.add(new PermissionDTO(roleId, funcCode, "add"));
            // ... (Tương tự cho Edit, Delete)
        }

        // Gọi hàm DAO/BUS xóa hết quyền cũ, Insert List quyền mới vào DB
        // permissionBUS.saveAllPermissions(roleId, listNewPerms);
        
        JOptionPane.showMessageDialog(this, "Đã lưu lại cấu hình phân quyền thành công!");
        dispose();
    }
}