package GUI.dialog;

import BUS.EmployeeBUS;
import DTO.EmployeeDTO;
import java.awt.*;
import java.sql.Date;
import javax.swing.*;

public class EmployeeDialog extends JDialog {

    private String mode;
    private EmployeeDTO currentEmp;
    private EmployeeBUS employeeBUS;

    private JTextField txtName, txtDob, txtPhone, txtAddress, txtSalary, txtHireDate;
    private JComboBox<String> cbGender, cbPosition;

    public EmployeeDialog(Frame owner, boolean modal, String mode, EmployeeDTO emp, EmployeeBUS bus) {
        super(owner, modal);
        this.mode = mode;
        this.currentEmp = emp;
        this.employeeBUS = bus;
        
        setTitle(mode.equals("add") ? "Thêm Nhân Viên Mới" : "Cập Nhật Thông Tin");
        setSize(450, 450);
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Họ và Tên:")); pnlForm.add(txtName = new JTextField());
        pnlForm.add(new JLabel("Ngày Sinh (YYYY-MM-DD):")); pnlForm.add(txtDob = new JTextField());
        pnlForm.add(new JLabel("Giới Tính:")); pnlForm.add(cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"}));
        pnlForm.add(new JLabel("Số Điện Thoại:")); pnlForm.add(txtPhone = new JTextField());
        pnlForm.add(new JLabel("Địa Chỉ:")); pnlForm.add(txtAddress = new JTextField());
        pnlForm.add(new JLabel("Chức Vụ:")); pnlForm.add(cbPosition = new JComboBox<>(new String[]{"Quản lý", "Nhân viên bán hàng", "Thủ kho", "Kế toán"}));
        pnlForm.add(new JLabel("Lương (VNĐ):")); pnlForm.add(txtSalary = new JTextField());
        pnlForm.add(new JLabel("Ngày Thuê (YYYY-MM-DD):")); pnlForm.add(txtHireDate = new JTextField());

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());
        pnlBtns.add(btnSave); pnlBtns.add(btnCancel);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (mode.equals("update") && currentEmp != null) {
            txtName.setText(currentEmp.getFullName());
            txtDob.setText(currentEmp.getDateOfBirth().toString());
            cbGender.setSelectedItem(currentEmp.getGender().equals("male") ? "Nam" : "Nữ");
            txtPhone.setText(currentEmp.getPhone());
            txtAddress.setText(currentEmp.getAddress());
            cbPosition.setSelectedItem(currentEmp.getPosition());
            txtSalary.setText(String.format("%.0f", currentEmp.getSalary()));
            txtHireDate.setText(currentEmp.getHireDate().toString());
        }
    }

    private void save() {
        try {
            String name = txtName.getText();
            Date dob = Date.valueOf(txtDob.getText()); // Yêu cầu đúng định dạng YYYY-MM-DD
            String gender = cbGender.getSelectedItem().equals("Nam") ? "male" : "female";
            String phone = txtPhone.getText();
            String address = txtAddress.getText();
            String pos = cbPosition.getSelectedItem().toString();
            double salary = Double.parseDouble(txtSalary.getText());
            Date hire = Date.valueOf(txtHireDate.getText());

            EmployeeDTO emp = new EmployeeDTO(mode.equals("add") ? 0 : currentEmp.getEmployeeId(), 
                              name, dob, gender, phone, address, pos, salary, hire, null, "active", null);

            boolean success = mode.equals("add") ? employeeBUS.addEmployee(emp) : employeeBUS.updateEmployee(emp);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào DB!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu! Ngày tháng phải là YYYY-MM-DD và Lương phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}