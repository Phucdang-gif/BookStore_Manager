package GUI.dialog;

import BUS.PermissionDetailBUS;
import BUS.FunctionBUS;
import DTO.PermissionDetailDTO;
import DTO.FunctionDTO;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PermissionDialog extends JDialog {

    private int permissionGroupId;
    private String groupName;
    
    // Khai báo 2 BUS cần thiết
    private PermissionDetailBUS permissionDetailBUS = new PermissionDetailBUS(); 
    private FunctionBUS functionBUS = new FunctionBUS();

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnSave, btnCancel;

    public PermissionDialog(Frame owner, boolean modal, int permissionGroupId, String groupName) {
        super(owner, modal);
        this.permissionGroupId = permissionGroupId;
        this.groupName = groupName;

        setTitle("Cấu hình Chi Tiết Quyền: " + groupName.toUpperCase());
        setSize(700, 450);
        setLocationRelativeTo(null);
        initUI();
        loadPermissionsFromDB();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JLabel lblHeader = new JLabel("TÍCH CHỌN CÁC QUYỀN CHO NHÓM: " + groupName.toUpperCase(), SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        String[] columns = {"Mã Chức Năng", "Tên Chức Năng", "Xem", "Thêm", "Sửa", "Xóa"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2) return Boolean.class; 
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

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
        // 1. GỌI TRỰC TIẾP TỪ DATABASE LÊN (Danh sách các chức năng)
        ArrayList<FunctionDTO> listFuncs = functionBUS.getAll();

        // Kiểm tra xem bảng functions dưới MySQL có dữ liệu chưa
        if (listFuncs == null || listFuncs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bảng chức năng (functions) trong Database đang trống! Vui lòng thêm dữ liệu vào CSDL trước.");
            return;
        }

        // 2. DUYỆT DANH SÁCH CHỨC NĂNG ĐỂ VẼ RA BẢNG TÍCH XANH
        for (FunctionDTO f : listFuncs) {
            String funcCode = f.getSystemFunctionCode(); 
            String funcName = f.getFunctionName();
            
            // DÙNG TIẾNG VIỆT CHO KHỚP VỚI DATABASE
            boolean canView   = permissionDetailBUS.checkPermission(permissionGroupId, funcCode, "Xem");
            boolean canAdd    = permissionDetailBUS.checkPermission(permissionGroupId, funcCode, "Thêm");
            boolean canEdit   = permissionDetailBUS.checkPermission(permissionGroupId, funcCode, "Sửa"); 
            boolean canDelete = permissionDetailBUS.checkPermission(permissionGroupId, funcCode, "Xóa");

            tableModel.addRow(new Object[]{
                funcCode, funcName, canView, canAdd, canEdit, canDelete
            });
        }
    }

    private void savePermissions() {
        try {
            ArrayList<PermissionDetailDTO> listNewPerms = new ArrayList<>();

            for (int i = 0; i < table.getRowCount(); i++) {
                String funcCode = (String) table.getValueAt(i, 0);
                
                Object viewVal = table.getValueAt(i, 2);
                Object addVal = table.getValueAt(i, 3);
                Object editVal = table.getValueAt(i, 4);
                Object deleteVal = table.getValueAt(i, 5);

                boolean canView = (viewVal != null) ? (boolean) viewVal : false;
                boolean canAdd = (addVal != null) ? (boolean) addVal : false;
                boolean canEdit = (editVal != null) ? (boolean) editVal : false;
                boolean canDelete = (deleteVal != null) ? (boolean) deleteVal : false;

                // GỘP CHUỖI TIẾNG VIỆT CHO KHỚP DATABASE
                ArrayList<String> acts = new ArrayList<>();
                if (canView) acts.add("Xem");
                if (canAdd) acts.add("Thêm"); 
                if (canEdit) acts.add("Sửa");
                if (canDelete) acts.add("Xóa");

                if (!acts.isEmpty()) {
                    String combinedActions = String.join(",", acts); 
                    listNewPerms.add(new PermissionDetailDTO(permissionGroupId, funcCode, combinedActions));
                }
            }

            boolean isSuccess = permissionDetailBUS.saveAllPermissions(permissionGroupId, listNewPerms);
            
            if (isSuccess) {
                // Thay vì chỉ báo "Thành công" cụt lủn, hãy báo như một phần mềm chuyên nghiệp:
                JOptionPane.showMessageDialog(this, 
            "Đã cập nhật phân quyền thành công!\nCác thay đổi sẽ được áp dụng vào lần đăng nhập tiếp theo của tài khoản.", 
            "Thông báo hệ thống", 
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu thất bại! Hàm DAO trả về false.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Phát hiện lỗi code: " + ex.getMessage(), "Lỗi Nghiêm Trọng", JOptionPane.ERROR_MESSAGE);
        }
    }
}