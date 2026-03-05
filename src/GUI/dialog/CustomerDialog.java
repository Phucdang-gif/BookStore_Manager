package GUI.dialog;

import BUS.CustomerBUS;
import DTO.CustomerDTO;
import java.awt.*;
import javax.swing.*;

public class CustomerDialog extends JDialog {

    private String mode;
    private CustomerDTO currentCus;
    private CustomerBUS customerBUS;

    private JTextField txtName, txtPhone, txtPoints;
    private JLabel lblDate;

    public CustomerDialog(Frame owner, boolean modal, String mode, CustomerDTO cus, CustomerBUS bus) {
        super(owner, modal);
        this.mode = mode;
        this.currentCus = cus;
        this.customerBUS = bus;

        setTitle(mode.equals("add") ? "Thêm Khách Hàng Mới" : "Cập Nhật Thông Tin");
        setSize(400, 300);
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Họ và Tên:"));
        pnlForm.add(txtName = new JTextField());

        pnlForm.add(new JLabel("Số Điện Thoại:"));
        pnlForm.add(txtPhone = new JTextField());

        pnlForm.add(new JLabel("Điểm Tích Lũy:"));
        pnlForm.add(txtPoints = new JTextField());
        if (mode.equals("add")) {
            txtPoints.setText("0");
            txtPoints.setEditable(false); // Thêm mới thì điểm mặc định là 0
        }

        pnlForm.add(new JLabel("Ngày Đăng Ký:"));
        pnlForm.add(lblDate = new JLabel(mode.equals("add") ? "Tự động tạo" : ""));
        lblDate.setForeground(Color.GRAY);

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());
        pnlBtns.add(btnSave);
        pnlBtns.add(btnCancel);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (mode.equals("update") && currentCus != null) {
            txtName.setText(currentCus.getFullName());
            txtPhone.setText(currentCus.getPhone());
            txtPoints.setText(String.valueOf(currentCus.getLoyaltyPoints()));
            lblDate.setText(currentCus.getRegistrationDate().toString());
        }
    }

    private void save() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String pointsStr = txtPoints.getText().trim();
        try {
            int points = Integer.parseInt(pointsStr);
            CustomerDTO cus = new CustomerDTO(
                    mode.equals("add") ? 0 : currentCus.getCustomerId(),
                    name, phone, points, null);

            // 2. Gọi BUS và nhận ValidationResult (Thay vì boolean)
            DTO.ValidationResult vr = mode.equals("add") ? customerBUS.addCustomer(cus)
                    : customerBUS.updateCustomer(cus);

            // 3. Kiểm tra tính hợp lệ từ BUS
            if (vr.isValid()) {
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm tích lũy phải là số!", "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}