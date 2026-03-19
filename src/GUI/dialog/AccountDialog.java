package GUI.dialog;

import BUS.AccountBUS;
import BUS.EmployeeBUS;
import BUS.PermissionGroupBUS;
import DTO.AccountDTO;
import DTO.EmployeeDTO;
import DTO.PermissionGroupDTO;
import DTO.ValidationResult; // Import

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class AccountDialog extends JDialog {

    private AccountBUS accountBUS = new AccountBUS();
    private PermissionGroupBUS permissionGroupBUS = new PermissionGroupBUS();
    private EmployeeBUS employeeBUS = new EmployeeBUS();

    private String mode;
    private AccountDTO currentAccount;

    private JComboBox<EmployeeDTO> cbEmployee;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<PermissionGroupDTO> cbPermissionGroup;
    private JButton btnSave, btnCancel;

    public AccountDialog(Frame owner, boolean modal, String mode, AccountDTO account) {
        super(owner, modal);
        this.mode = mode;
        this.currentAccount = account;

        setTitle(mode.equals("add") ? "Thêm Tài Khoản Mới"
                : (mode.equals("update") ? "Cập Nhật Tài Khoản" : "Chi Tiết Tài Khoản"));
        initUI();
        loadComboBoxData();
        loadData();
        setSize(450, 380); // Tăng chiều cao một chút
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Chọn Nhân Viên:"));
        cbEmployee = new JComboBox<>();
        cbEmployee.setMaximumRowCount(4); // Giới hạn số dòng hiển thị
        pnlForm.add(cbEmployee);

        pnlForm.add(new JLabel("Tên Đăng Nhập:"));
        txtUsername = new JTextField();
        pnlForm.add(txtUsername);

        pnlForm.add(new JLabel("Mật Khẩu:"));

        // --- TÍNH NĂNG ẨN/HIỆN MẬT KHẨU ---
        JPanel pnlPassword = new JPanel(new BorderLayout()); // Cái hộp chứa
        txtPassword = new JPasswordField();

        JButton btnTogglePass = new JButton("Hiện"); // Nút bấm
        btnTogglePass.setFocusPainted(false);
        btnTogglePass.setBackground(new Color(240, 240, 240));
        btnTogglePass.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Bắt sự kiện Click để chuyển đổi trạng thái
        btnTogglePass.addActionListener(e -> {
            if (txtPassword.getEchoChar() == (char) 0) {
                // Mật khẩu đang HIỆN -> Đổi thành ẨN
                txtPassword.setEchoChar('•'); // Trả về dấu chấm tròn
                btnTogglePass.setText("Hiện");
            } else {
                // Mật khẩu đang ẨN -> Đổi thành HIỆN
                txtPassword.setEchoChar((char) 0); // Ký tự null để hiển thị text thật
                btnTogglePass.setText("Ẩn");
            }
        });

        // Ráp ô text và nút vào hộp
        pnlPassword.add(txtPassword, BorderLayout.CENTER);
        pnlPassword.add(btnTogglePass, BorderLayout.EAST);

        // Nhét cả hộp vào Form
        pnlForm.add(pnlPassword);
        // ----------------------------------

        pnlForm.add(new JLabel("Nhóm Quyền:"));
        cbPermissionGroup = new JComboBox<>();
        pnlForm.add(cbPermissionGroup);

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu Dữ Liệu");
        btnCancel = new JButton("Hủy Bỏ");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveAccount());

        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void loadComboBoxData() {
        cbPermissionGroup.removeAllItems();
        ArrayList<PermissionGroupDTO> listGroup = permissionGroupBUS.getAll();
        for (PermissionGroupDTO group : listGroup) {
            cbPermissionGroup.addItem(group);
        }

        cbEmployee.removeAllItems();
        if (mode.equals("add")) {
            EmployeeDTO defaultOption = new EmployeeDTO();
            defaultOption.setEmployeeId(-1);
            defaultOption.setFullName("--- Chọn nhân viên ---");
            defaultOption.setPhone("");
            cbEmployee.addItem(defaultOption);
            ArrayList<EmployeeDTO> allEmps = employeeBUS.getAll();
            for (EmployeeDTO emp : allEmps) {
                if ("active".equals(emp.getStatus())) {
                    cbEmployee.addItem(emp);
                }
            }
        } else if (currentAccount != null) {
            EmployeeDTO currentEmp = employeeBUS.getById(currentAccount.getEmployeeId());
            if (currentEmp != null) {
                cbEmployee.addItem(currentEmp);
                cbEmployee.setSelectedItem(currentEmp);
            }
            cbEmployee.setEnabled(false);
        }
    }

    private void loadData() {
        if ((mode.equals("update") || mode.equals("view")) && currentAccount != null) {
            txtUsername.setText(currentAccount.getUsername());
            txtPassword.setText(currentAccount.getPassword());

            for (int i = 0; i < cbPermissionGroup.getItemCount(); i++) {
                if (cbPermissionGroup.getItemAt(i).getPermissionGroupId() == currentAccount.getPermissionGroupId()) {
                    cbPermissionGroup.setSelectedIndex(i);
                    break;
                }
            }

            if (mode.equals("view")) {
                txtUsername.setEditable(false);
                txtPassword.setEditable(false);
                cbPermissionGroup.setEnabled(false);
                btnSave.setVisible(false);
                btnCancel.setText("Đóng");
            }
            if(mode.equals("update")) {
                cbEmployee.setEnabled(false); 
                if(currentAccount.getAccountId()==151||currentAccount.getAccountId()==config.SessionManager.getCurrentAccount().getAccountId()) {
                    cbPermissionGroup.setEnabled(false); // Không cho đổi nhóm quyền khi chỉnh sửa tài khoản Admin mặc định hoặc tài khoản thuộc nhóm quyền chung với tài khoản đang đăng nhập
                }
            }

        }
    }

    private void saveAccount() {
        try {
            EmployeeDTO selectedEmp = (EmployeeDTO) cbEmployee.getSelectedItem();
            PermissionGroupDTO selectedGroup = (PermissionGroupDTO) cbPermissionGroup.getSelectedItem();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            ValidationResult result;

            if (mode.equals("add")) {
                AccountDTO newAcc = new AccountDTO(0, selectedEmp.getEmployeeId(), selectedGroup.getPermissionGroupId(),
                        username, password, "active", null);
                result = accountBUS.addAccount(newAcc);

            } else if (mode.equals("update")) {
                currentAccount.setPassword(password);

                
                       
                
                currentAccount.setPermissionGroupId(selectedGroup.getPermissionGroupId());
                
                currentAccount.setUsername(username);
                result = accountBUS.updateAccount(currentAccount);
            } else {
                return;
            }

            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Thao tác thành công!");
                dispose();
            } else {
                GUI.util.ValidationUI.resetAll(txtUsername, txtPassword, cbPermissionGroup, cbEmployee);
                if (result.getError("username") != null)
                    GUI.util.ValidationUI.setError(txtUsername, result.getError("username"));
                if (result.getError("password") != null)
                    GUI.util.ValidationUI.setError(txtPassword, result.getError("password"));
                if (result.getError("permissionGroupId") != null)
                    GUI.util.ValidationUI.setError(cbPermissionGroup, result.getError("permissionGroupId"));
                if (result.getError("employeeId") != null)
                    GUI.util.ValidationUI.setError(cbEmployee, result.getError("employeeId"));
                JOptionPane.showMessageDialog(this, result.getSummary(), "Lỗi", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống không xác định!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}