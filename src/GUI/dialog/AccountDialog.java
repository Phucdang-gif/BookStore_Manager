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
        setSize(450, 380); // Tăng chiều cao một chút
        setLocationRelativeTo(null);

        initUI();
        loadComboBoxData();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Chọn Nhân Viên:"));
        cbEmployee = new JComboBox<>();
        pnlForm.add(cbEmployee);

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
            ArrayList<EmployeeDTO> unassignedEmps = employeeBUS.getUnassignedEmployees();
            for (EmployeeDTO emp : unassignedEmps) {
                cbEmployee.addItem(emp);
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
            } else if (mode.equals("update")) {
                txtUsername.setEditable(false);
            }
        }
    }

    // ================== HÀM NÀY ĐÃ ĐƯỢC RÚT GỌN ==================
    private void saveAccount() {
        try {
            EmployeeDTO selectedEmp = (EmployeeDTO) cbEmployee.getSelectedItem();
            PermissionGroupDTO selectedGroup = (PermissionGroupDTO) cbPermissionGroup.getSelectedItem();

            // Validator không check được ComboBox null từ GUI, nên check nhẹ ở đây
            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedGroup == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhóm quyền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            ValidationResult result;

            if (mode.equals("add")) {
                AccountDTO newAcc = new AccountDTO(0, selectedEmp.getEmployeeId(), selectedGroup.getPermissionGroupId(),
                        username, password, "active", null);

                // Gọi BUS và nhận về ValidationResult
                result = accountBUS.addAccount(newAcc);

            } else if (mode.equals("update")) {
                currentAccount.setPassword(password);
                currentAccount.setPermissionGroupId(selectedGroup.getPermissionGroupId());

                // Gọi BUS và nhận về ValidationResult
                result = accountBUS.updateAccount(currentAccount);
            } else {
                return;
            }

            // Dùng hàm showAlert để tự động hiện lỗi (nếu có)
            if (result.showAlert(this)) {
                // Nếu không có lỗi (isValid == true) -> Thành công
                JOptionPane.showMessageDialog(this, "Thao tác thành công!");
                dispose();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống không xác định!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}