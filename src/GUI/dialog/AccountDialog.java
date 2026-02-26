package GUI.dialog;

import BUS.AccountBUS;
import BUS.EmployeeBUS;
import BUS.PermissionGroupBUS;
import DTO.AccountDTO;
import DTO.EmployeeDTO;
import DTO.PermissionGroupDTO;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class AccountDialog extends JDialog {

    private AccountBUS accountBUS = new AccountBUS();
    private PermissionGroupBUS permissionGroupBUS = new PermissionGroupBUS();
    private EmployeeBUS employeeBUS = new EmployeeBUS(); // THAY ĐỔI 1: Thêm BUS Nhân viên
    
    private String mode; // "add", "update", hoặc "view"
    private AccountDTO currentAccount;

    // THAY ĐỔI 2: Đổi JTextField thành JComboBox cho Nhân viên
    private JComboBox<EmployeeDTO> cbEmployee; 
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<PermissionGroupDTO> cbPermissionGroup;
    private JButton btnSave, btnCancel;

    public AccountDialog(Frame owner, boolean modal, String mode, AccountDTO account) {
        super(owner, modal);
        this.mode = mode;
        this.currentAccount = account;
        
        setTitle(mode.equals("add") ? "Thêm Tài Khoản Mới" : (mode.equals("update") ? "Cập Nhật Tài Khoản" : "Chi Tiết Tài Khoản"));
        setSize(450, 350);
        setLocationRelativeTo(null);
        
        initUI();
        loadComboBoxData(); // THAY ĐỔI 3: Gộp chung hàm load dữ liệu cho 2 ComboBox
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Chọn Nhân Viên:"));
        cbEmployee = new JComboBox<>(); // Khởi tạo ComboBox Nhân viên
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
        // 1. Load danh sách Nhóm Quyền
        ArrayList<PermissionGroupDTO> listGroup = permissionGroupBUS.getAll();
        for (PermissionGroupDTO group : listGroup) {
            cbPermissionGroup.addItem(group);
        }

        // 2. Load danh sách Nhân viên tùy theo chế độ (Mode)
        if (mode.equals("add")) {
            // Nếu THÊM MỚI: Chỉ load những nhân viên CHƯA CÓ tài khoản
            ArrayList<EmployeeDTO> unassignedEmps = employeeBUS.getUnassignedEmployees();
            for (EmployeeDTO emp : unassignedEmps) {
                cbEmployee.addItem(emp);
            }
        } else if (currentAccount != null) {
            // Nếu SỬA/XEM: Load đúng nhân viên đang sở hữu tài khoản này và KHÓA LẠI
            // (Giả định em có hàm getById trong EmployeeBUS, nếu không có em tự tạo nhé)
            EmployeeDTO currentEmp = employeeBUS.getById(currentAccount.getEmployeeId());
            if (currentEmp != null) {
                cbEmployee.addItem(currentEmp);
            }
            cbEmployee.setEnabled(false); // Không cho phép đổi chủ sở hữu tài khoản
        }
    }

    private void loadData() {
        if ((mode.equals("update") || mode.equals("view")) && currentAccount != null) {
            
            txtUsername.setText(currentAccount.getUsername());
            txtPassword.setText(currentAccount.getPassword());

            // Chọn đúng Nhóm Quyền hiện tại
            for (int i = 0; i < cbPermissionGroup.getItemCount(); i++) {
                if (cbPermissionGroup.getItemAt(i).getPermissionGroupId() == currentAccount.getPermissionGroupId()) {
                    cbPermissionGroup.setSelectedIndex(i);
                    break;
                }
            }

            // Xử lý khóa Form nếu là chế độ VIEW
            if (mode.equals("view")) {
                txtUsername.setEditable(false); 
                txtPassword.setEditable(false); 
                cbPermissionGroup.setEnabled(false); 
                btnSave.setVisible(false); 
                btnCancel.setText("Đóng"); 
            } else if (mode.equals("update")) {
                txtUsername.setEditable(false); // Thường không cho đổi Username khi đã tạo
            }
        }
    }

    private void saveAccount() {
        try {
            // THAY ĐỔI 4: Lấy thông tin từ 2 ComboBox và validate chặt chẽ
            EmployeeDTO selectedEmp = (EmployeeDTO) cbEmployee.getSelectedItem();
            PermissionGroupDTO selectedGroup = (PermissionGroupDTO) cbPermissionGroup.getSelectedItem();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(this, "Không có nhân viên nào để cấp tài khoản!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (username.isEmpty() || password.isEmpty() || selectedGroup == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (mode.equals("add")) {
                // Kiểm tra trùng lặp Username trước khi Thêm mới
                if (accountBUS.checkDuplicateUsername(username)) {
                    JOptionPane.showMessageDialog(this, "Tên đăng nhập '" + username + "' đã tồn tại. Vui lòng chọn tên khác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                AccountDTO newAcc = new AccountDTO(0, selectedEmp.getEmployeeId(), selectedGroup.getPermissionGroupId(), username, password, "active", null);
                boolean isSuccess = accountBUS.addAccount(newAcc);
                
                if (isSuccess) {
                    JOptionPane.showMessageDialog(this, "Đã thêm tài khoản thành công!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
            } else if (mode.equals("update")) {
                currentAccount.setPassword(password);
                currentAccount.setPermissionGroupId(selectedGroup.getPermissionGroupId());
                
                int index = accountBUS.getAll().indexOf(currentAccount); 
                boolean isSuccess = accountBUS.updateAccount(index, currentAccount);
                
                if (isSuccess) {
                    JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}