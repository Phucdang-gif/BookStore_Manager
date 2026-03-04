package GUI.dialog;

import BUS.EmployeeBUS;
import DTO.EmployeeDTO;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;

public class EmployeeDialog extends JDialog {

    private String mode;
    private EmployeeDTO currentEmp;
    private EmployeeBUS employeeBUS;

    private JTextField txtName, txtDob, txtPhone, txtAddress, txtSalary, txtHireDate;
    private JComboBox<String> cbGender, cbPosition;
    private JButton btnSave, btnCancel;

    // Định dạng ngày tháng Việt Nam
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public EmployeeDialog(Frame owner, boolean modal, String mode, EmployeeDTO emp, EmployeeBUS bus) {
        super(owner, modal);
        this.mode = mode;
        this.currentEmp = emp;
        this.employeeBUS = bus;

        // Cấu hình định dạng ngày chặt chẽ (không chấp nhận ngày 30/02...)
        sdf.setLenient(false);

        // 1. Cấu hình Tiêu đề theo chế độ
        if (mode.equals("add")) {
            setTitle("Thêm Nhân Viên Mới");
        } else if (mode.equals("update")) {
            setTitle("Cập Nhật Thông Tin");
        } else if (mode.equals("detail")) {
            setTitle("Chi Tiết Nhân Viên");
        }

        setSize(450, 520);
        setLocationRelativeTo(null);
        initUI();
        loadData();

        // 2. Xử lý riêng cho chế độ Xem Chi Tiết
        if (mode.equals("detail")) {
            disableForm(); // Khóa toàn bộ ô nhập liệu
            btnSave.setVisible(false); // Ẩn nút Lưu
            btnCancel.setText("Đóng"); // Đổi tên nút Hủy thành Đóng
            btnCancel.setBackground(new Color(108, 117, 125)); // Màu xám cho nút Đóng
            btnCancel.setForeground(Color.WHITE);
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // --- Panel Form nhập liệu ---
        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 10, 15));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Họ và Tên:"));
        pnlForm.add(txtName = new JTextField());

        pnlForm.add(new JLabel("Ngày Sinh (dd-MM-yyyy):"));
        pnlForm.add(txtDob = new JTextField());

        pnlForm.add(new JLabel("Giới Tính:"));
        pnlForm.add(cbGender = new JComboBox<>(new String[] { "Nam", "Nữ" }));

        pnlForm.add(new JLabel("Số Điện Thoại:"));
        pnlForm.add(txtPhone = new JTextField());

        pnlForm.add(new JLabel("Địa Chỉ:"));
        pnlForm.add(txtAddress = new JTextField());

        pnlForm.add(new JLabel("Chức Vụ:"));
        pnlForm.add(
                cbPosition = new JComboBox<>(new String[] { "Quản lý", "Nhân viên bán hàng", "Thủ kho", "Kế toán" }));

        pnlForm.add(new JLabel("Lương (VNĐ):"));
        pnlForm.add(txtSalary = new JTextField());

        pnlForm.add(new JLabel("Ngày Thuê (dd-MM-yyyy):"));
        pnlForm.add(txtHireDate = new JTextField());

        add(pnlForm, BorderLayout.CENTER);

        // --- Panel Nút bấm ---
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.WHITE);

        btnCancel = new JButton("Hủy");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());

        pnlBtns.add(btnSave);
        pnlBtns.add(btnCancel);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    // Hàm load dữ liệu lên form (Dùng cho cả Update và Detail)
    private void loadData() {
        if ((mode.equals("update") || mode.equals("detail")) && currentEmp != null) {
            txtName.setText(currentEmp.getFullName());

            // Format ngày sinh
            if (currentEmp.getDateOfBirth() != null) {
                txtDob.setText(sdf.format(currentEmp.getDateOfBirth()));
            }

            cbGender.setSelectedItem(currentEmp.getGender().equals("male") ? "Nam" : "Nữ");
            txtPhone.setText(currentEmp.getPhone());
            txtAddress.setText(currentEmp.getAddress());
            cbPosition.setSelectedItem(currentEmp.getPosition());
            txtSalary.setText(String.format("%.0f", currentEmp.getSalary()));

            // Format ngày thuê
            if (currentEmp.getHireDate() != null) {
                txtHireDate.setText(sdf.format(currentEmp.getHireDate()));
            }
        }
    }

    // Hàm khóa form (chỉ dùng cho chế độ detail)
    private void disableForm() {
        txtName.setEditable(false);
        txtDob.setEditable(false);
        txtPhone.setEditable(false);
        txtAddress.setEditable(false);
        txtSalary.setEditable(false);
        txtHireDate.setEditable(false);
        cbGender.setEnabled(false);
        cbPosition.setEnabled(false);

        // Tô màu nền xám nhẹ để người dùng biết không sửa được
        Color readOnlyColor = new Color(245, 245, 245);
        txtName.setBackground(readOnlyColor);
        txtDob.setBackground(readOnlyColor);
        txtPhone.setBackground(readOnlyColor);
        txtAddress.setBackground(readOnlyColor);
        txtSalary.setBackground(readOnlyColor);
        txtHireDate.setBackground(readOnlyColor);
    }

    private void save() {
        try {
            // Validate sơ bộ
            String name = txtName.getText().trim();
            String dobStr = txtDob.getText().trim();
            String hireStr = txtHireDate.getText().trim();
            String salaryStr = txtSalary.getText().trim();

            if (name.isEmpty() || dobStr.isEmpty() || hireStr.isEmpty() || salaryStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Chuyển đổi Ngày tháng (dd-MM-yyyy -> SQL Date)
            java.util.Date parsedDob = sdf.parse(dobStr);
            Date sqlDob = new Date(parsedDob.getTime());

            java.util.Date parsedHire = sdf.parse(hireStr);
            Date sqlHire = new Date(parsedHire.getTime());

            // Lấy các thông tin khác
            String gender = cbGender.getSelectedItem().equals("Nam") ? "male" : "female";
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String pos = cbPosition.getSelectedItem().toString();
            double salary = Double.parseDouble(salaryStr);

            // Tạo DTO
            EmployeeDTO emp = new EmployeeDTO(
                    mode.equals("add") ? 0 : currentEmp.getEmployeeId(),
                    name, sqlDob, gender, phone, address, pos, salary, sqlHire, null,
                    "active", null);

            // Gọi BUS xử lý
            boolean success = mode.equals("add") ? employeeBUS.addEmployee(emp) : employeeBUS.updateEmployee(emp);

            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lương phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày tháng phải nhập đúng định dạng dd-MM-yyyy (VD: 15-05-1990)",
                    "Lỗi ngày tháng", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}