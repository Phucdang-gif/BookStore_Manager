package GUI.dialog;

import BUS.AccountBUS;
import BUS.PermissionGroupBUS;
import DTO.AccountDTO;
import DTO.PermissionGroupDTO;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class AccountDialog extends JDialog {

    private AccountBUS accountBUS = new AccountBUS();
    private PermissionGroupBUS permissionGroupBUS = new PermissionGroupBUS();
    
    private String mode; // "add" hoặc "update"
    private AccountDTO currentAccount;

    private JTextField txtEmployeeId;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<PermissionGroupDTO> cbPermissionGroup;
    private JButton btnSave, btnCancel;

    public AccountDialog(Frame owner, boolean modal, String mode, AccountDTO account) {
        super(owner, modal);
        this.mode = mode;
        this.currentAccount = account;
        
        setTitle(mode.equals("add") ? "Thêm Tài Khoản Mới" : "Cập Nhật Tài Khoản");
        setSize(400, 350);
        setLocationRelativeTo(null);
        
        initUI();
        loadPermissionGroups();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // --- Form Nhập Liệu ---
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Mã Nhân Viên:"));
        txtEmployeeId = new JTextField();
        pnlForm.add(txtEmployeeId);

        pnlForm.add(new JLabel("Tên Đăng Nhập:"));
        txtUsername = new JTextField();
        pnlForm.add(txtUsername);

        pnlForm.add(new JLabel("Mật Khẩu:"));
        txtPassword = new JPasswordField();
        pnlForm.add(txtPassword);

        pnlForm.add(new JLabel("Nhóm Quyền:"));
        cbPermissionGroup = new JComboBox<>();
        pnlForm.add(cbPermissionGroup);

        add(pnlForm, BorderLayout.CENTER);

        // --- Nút Bấm ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu Dữ Liệu");
        btnCancel = new JButton("Hủy Bỏ");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveAccount());

        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void loadPermissionGroups() {
        // Đổ danh sách Nhóm Quyền từ DB lên ComboBox
        ArrayList<PermissionGroupDTO> listGroup = permissionGroupBUS.getAll();
        for (PermissionGroupDTO group : listGroup) {
            cbPermissionGroup.addItem(group);
        }
    }

    private void loadData() {
        // Cập nhật điều kiện: Cho phép cả mode "update" và "view" được load dữ liệu
        if ((mode.equals("update") || mode.equals("view")) && currentAccount != null) {
            txtEmployeeId.setText(String.valueOf(currentAccount.getEmployeeId()));
            txtEmployeeId.setEditable(false); 
            
            txtUsername.setText(currentAccount.getUsername());
            txtPassword.setText(currentAccount.getPassword());

            for (int i = 0; i < cbPermissionGroup.getItemCount(); i++) {
                if (cbPermissionGroup.getItemAt(i).getPermissionGroupId() == currentAccount.getPermissionGroupId()) {
                    cbPermissionGroup.setSelectedIndex(i);
                    break;
                }
            }

            // --- THÊM ĐOẠN NÀY ĐỂ XỬ LÝ RIÊNG CHO CHẾ ĐỘ "VIEW" ---
            if (mode.equals("view")) {
                setTitle("Chi Tiết Tài Khoản");
                txtUsername.setEditable(false); // Khóa không cho sửa
                txtPassword.setEditable(false); // Khóa không cho sửa
                cbPermissionGroup.setEnabled(false); // Khóa không cho chọn nhóm khác
                btnSave.setVisible(false); // Ẩn luôn nút Lưu
                btnCancel.setText("Đóng"); // Đổi chữ nút Hủy thành Đóng
            }
        }
    }

    private void saveAccount() {
        try {
            String empIdStr = txtEmployeeId.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            PermissionGroupDTO selectedGroup = (PermissionGroupDTO) cbPermissionGroup.getSelectedItem();

            // Validate cơ bản
            if (empIdStr.isEmpty() || username.isEmpty() || password.isEmpty() || selectedGroup == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int employeeId = Integer.parseInt(empIdStr);

            if (mode.equals("add")) {
                // Thêm mới
                AccountDTO newAcc = new AccountDTO(0, employeeId, selectedGroup.getPermissionGroupId(), username, password, "active", null);
                boolean isSuccess = accountBUS.addAccount(newAcc);
                
                if (isSuccess) {
                    JOptionPane.showMessageDialog(this, "Đã thêm tài khoản thành công!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không thể thêm tài khoản (Có thể Username hoặc Mã NV đã tồn tại)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
            } else {
                // Cập nhật
                currentAccount.setUsername(username);
                currentAccount.setPassword(password);
                currentAccount.setPermissionGroupId(selectedGroup.getPermissionGroupId());
                
                // Lấy index của tài khoản trong list trên RAM
                int index = accountBUS.getAll().indexOf(currentAccount); 
                
                boolean isSuccess = accountBUS.updateAccount(index, currentAccount);
                if (isSuccess) {
                    JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã Nhân Viên phải là một số nguyên hợp lệ!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}