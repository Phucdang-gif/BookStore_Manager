package GUI.dialog;

import BUS.RoleBUS;
import DTO.RoleDTO;
import java.awt.*;
import javax.swing.*;

public class RoleDialog extends JDialog {

    private RoleBUS roleBUS = new RoleBUS();
    private String mode; // "add" hoặc "update"
    private RoleDTO currentRole;

    private JTextField txtRoleName;
    private JTextArea txtDescription;
    private JButton btnSave, btnCancel;

    public RoleDialog(Frame owner, boolean modal, String mode, RoleDTO role) {
        super(owner, modal);
        this.mode = mode;
        this.currentRole = role;
        
        setTitle(mode.equals("add") ? "Thêm Nhóm Quyền" : "Cập Nhật Nhóm Quyền");
        setSize(400, 300);
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Tên Nhóm Quyền:"));
        txtRoleName = new JTextField();
        pnlForm.add(txtRoleName);

        pnlForm.add(new JLabel("Mô Tả:"));
        txtDescription = new JTextArea(3, 20);
        txtDescription.setLineWrap(true);
        pnlForm.add(new JScrollPane(txtDescription));

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu Dữ Liệu");
        btnCancel = new JButton("Hủy Lỏ");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveRole());

        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (mode.equals("update") && currentRole != null) {
            txtRoleName.setText(currentRole.getRoleName());
            txtDescription.setText(currentRole.getDescription());
        }
    }

    private void saveRole() {
        String name = txtRoleName.getText().trim();
        String desc = txtDescription.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhóm quyền không được để trống!");
            return;
        }

        if (mode.equals("add")) {
            // roleBUS.addRole(new RoleDTO(0, name, desc));
            JOptionPane.showMessageDialog(this, "Đã THÊM nhóm: " + name);
        } else {
            // roleBUS.updateRole(new RoleDTO(currentRole.getRoleId(), name, desc));
            JOptionPane.showMessageDialog(this, "Đã CẬP NHẬT nhóm: " + name);
        }
        dispose();
    }
}