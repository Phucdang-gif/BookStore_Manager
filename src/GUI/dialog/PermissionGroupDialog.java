package GUI.dialog;

import BUS.PermissionGroupBUS;
import DTO.PermissionGroupDTO;
import java.awt.*;
import javax.swing.*;

public class PermissionGroupDialog extends JDialog {

    private PermissionGroupBUS permissionGroupBUS = new PermissionGroupBUS();
    private String mode; 
    private PermissionGroupDTO currentGroup;

    private JTextField txtGroupName;
    private JButton btnSave, btnCancel;

    public PermissionGroupDialog(Frame owner, boolean modal, String mode, PermissionGroupDTO group) {
        super(owner, modal);
        this.mode = mode;
        this.currentGroup = group;
        
        setTitle(mode.equals("add") ? "Thêm Nhóm Quyền" : "Cập Nhật Nhóm Quyền");
        setSize(400, 200); // Thu nhỏ khung lại vì đã bỏ mô tả
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlForm = new JPanel(new GridLayout(1, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Tên Nhóm Quyền:"));
        txtGroupName = new JTextField();
        pnlForm.add(txtGroupName);

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu Dữ Liệu");
        btnCancel = new JButton("Hủy Bỏ");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveRole());

        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (mode.equals("update") && currentGroup != null) {
            txtGroupName.setText(currentGroup.getGroupName());
        }
    }
private void saveRole() {
        String name = txtGroupName.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhóm quyền không được để trống!");
            return;
        }

        if (mode.equals("add")) {
            // GỌI XUỐNG BUS ĐỂ THÊM VÀO DB THẬT
            boolean isSuccess = permissionGroupBUS.addGroup(new PermissionGroupDTO(0, name));
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đã THÊM nhóm quyền: " + name);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể thêm vào Cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // GỌI XUỐNG BUS ĐỂ CẬP NHẬT DB THẬT
            currentGroup.setGroupName(name);
            boolean isSuccess = permissionGroupBUS.updateGroup(currentGroup);
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đã CẬP NHẬT nhóm quyền: " + name);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể cập nhật vào Cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        dispose(); // Đóng cửa sổ sau khi xong
    }
}