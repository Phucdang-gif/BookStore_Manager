package GUI.dialog;

import BUS.EmployeeBUS;
import DTO.EmployeeDTO;
import GUI.util.IconHelper;
import GUI.util.ImageHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Date;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import DTO.ValidationResult;

public class EmployeeDialog extends JDialog {

    private String mode;
    private EmployeeDTO currentEmp;
    private EmployeeBUS employeeBUS;

    private JTextField txtName, txtPhone, txtAddress, txtSalary;
    private JDateChooser dcDob, dcHireDate;
    private JComboBox<String> cbGender, cbPosition;
    private JButton btnSave, btnCancel;

    // --- AVATAR ---
    private JLabel lblAvatarPreview;
    private JButton btnChooseAvatar;
    private String selectedAvatarFileName = null;
    private File tempAvatarFile = null;
    private JFileChooser fileChooser;
    // Kích thước avatar hiển thị trong dialog
    private static final int AVATAR_SIZE = 80;

    public EmployeeDialog(Frame owner, boolean modal, String mode, EmployeeDTO emp, EmployeeBUS bus) {
        super(owner, modal);
        this.mode = mode;
        this.currentEmp = emp;
        this.employeeBUS = bus;

        if (mode.equals("add")) {
            setTitle("Thêm Nhân Viên Mới");
        } else if (mode.equals("update")) {
            setTitle("Cập Nhật Thông Tin");
        } else if (mode.equals("detail")) {
            setTitle("Chi Tiết Nhân Viên");
        }

        initUI();
        loadData();

        if (mode.equals("detail")) {
            disableForm();
            btnSave.setVisible(false);
            btnCancel.setText("Đóng");
            btnCancel.setBackground(new Color(108, 117, 125));
            btnCancel.setForeground(Color.WHITE);
        }

        setSize(500, 600);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // ===== PHẦN TRÊN: AVATAR =====
        JPanel pnlAvatar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlAvatar.setBackground(new Color(245, 245, 245));
        pnlAvatar.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        lblAvatarPreview = new JLabel();
        lblAvatarPreview.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        lblAvatarPreview.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        lblAvatarPreview.setHorizontalAlignment(SwingConstants.CENTER);
        // Hiển thị icon mặc định
        setDefaultAvatar();

        btnChooseAvatar = new JButton("Chọn ảnh...");
        btnChooseAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnChooseAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChooseAvatar.addActionListener(e -> chooseAvatar());

        pnlAvatar.add(lblAvatarPreview);
        pnlAvatar.add(btnChooseAvatar);

        add(pnlAvatar, BorderLayout.NORTH);

        // ===== PHẦN GIỮA: FORM =====
        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 10, 15));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        pnlForm.add(new JLabel("Họ và Tên:"));
        pnlForm.add(txtName = new JTextField());

        pnlForm.add(new JLabel("Ngày Sinh:"));
        dcDob = new JDateChooser();
        dcDob.setDateFormatString("dd-MM-yyyy");
        pnlForm.add(dcDob);

        pnlForm.add(new JLabel("Giới Tính:"));
        pnlForm.add(cbGender = new JComboBox<>(new String[] { "Nam", "Nữ" }));

        pnlForm.add(new JLabel("Số Điện Thoại:"));
        pnlForm.add(txtPhone = new JTextField());

        pnlForm.add(new JLabel("Địa Chỉ:"));
        pnlForm.add(txtAddress = new JTextField());

        pnlForm.add(new JLabel("Chức Vụ:"));
        pnlForm.add(cbPosition = new JComboBox<>(
                new String[] { "Quản lý", "Nhân viên bán hàng", "Thủ kho", "Kế toán" }));

        pnlForm.add(new JLabel("Lương (VNĐ):"));
        pnlForm.add(txtSalary = new JTextField());

        pnlForm.add(new JLabel("Ngày Thuê:"));
        dcHireDate = new JDateChooser();
        dcHireDate.setDateFormatString("dd-MM-yyyy");
        pnlForm.add(dcHireDate);

        add(pnlForm, BorderLayout.CENTER);

        // ===== PHẦN DƯỚI: NÚT =====
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

    // ===== LOGIC CHỌN AVATAR =====

    /**
     * Mở file chooser, cho phép chọn ảnh PNG/JPG/SVG.
     * Sau khi chọn: copy vào src/image, lưu tên file, cập nhật preview.
     */
    private void chooseAvatar() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh đại diện");
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                    "Ảnh (PNG, JPG, JPEG, SVG)", "png", "jpg", "jpeg", "svg"));
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            this.tempAvatarFile = selectedFile;

            String ext = getExtension(selectedFile.getName()).toLowerCase();

            if (ext.equals("svg")) {
                IconHelper.setIconFromFile(lblAvatarPreview, selectedFile, AVATAR_SIZE, AVATAR_SIZE);
            } else {
                BufferedImage img = ImageHelper.readImage(selectedFile.getAbsolutePath());
                if (img != null) {
                    BufferedImage circle = ImageHelper.makeCircle(
                            ImageHelper.resize(img, AVATAR_SIZE, AVATAR_SIZE), AVATAR_SIZE);
                    lblAvatarPreview.setIcon(new ImageIcon(circle));
                    lblAvatarPreview.setText("");
                }
            }
        }
    }

    /**
     * Hiển thị avatar theo tên file đã lưu trong DB.
     * Hỗ trợ cả PNG/JPG (bo tròn) và SVG (icon thẳng).
     */
    private void loadAvatarPreview(String avatarFileName) {
        if (avatarFileName == null || avatarFileName.isEmpty()) {
            setDefaultAvatar();
            return;
        }

        String ext = getExtension(avatarFileName).toLowerCase();
        String path = "image/" + avatarFileName;

        if (ext.equals("svg")) {
            IconHelper.setIcon(lblAvatarPreview, path, AVATAR_SIZE, AVATAR_SIZE);
            lblAvatarPreview.setText("");
        } else {
            BufferedImage img = ImageHelper.readImage(path);
            if (img != null) {
                BufferedImage circle = ImageHelper.makeCircle(
                        ImageHelper.resize(img, AVATAR_SIZE, AVATAR_SIZE), AVATAR_SIZE);
                lblAvatarPreview.setIcon(new ImageIcon(circle));
                lblAvatarPreview.setText("");
            } else {
                setDefaultAvatar();
            }
        }
    }

    /** Icon mặc định khi chưa có avatar */
    private void setDefaultAvatar() {
        lblAvatarPreview.setIcon(null);
        lblAvatarPreview.setText("No Image");
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot >= 0) ? fileName.substring(dot + 1) : "";
    }

    // ===== LOAD / SAVE DỮ LIỆU =====

    private void loadData() {
        if ((mode.equals("update") || mode.equals("detail")) && currentEmp != null) {
            txtName.setText(currentEmp.getFullName());
            cbGender.setSelectedItem(currentEmp.getGender().equals("male") ? "Nam" : "Nữ");
            txtPhone.setText(currentEmp.getPhone());
            txtAddress.setText(currentEmp.getAddress());
            cbPosition.setSelectedItem(currentEmp.getPosition());
            txtSalary.setText(String.format("%.0f", currentEmp.getSalary()));
            dcHireDate.setEnabled(false);

            if (currentEmp.getDateOfBirth() != null)
                dcDob.setDate(currentEmp.getDateOfBirth());
            if (currentEmp.getHireDate() != null)
                dcHireDate.setDate(currentEmp.getHireDate());

            // Load avatar hiện tại
            selectedAvatarFileName = currentEmp.getAvatar();
            loadAvatarPreview(currentEmp.getAvatar());
        }
    }

    private void disableForm() {
        txtName.setEditable(false);
        txtPhone.setEditable(false);
        txtAddress.setEditable(false);
        txtSalary.setEditable(false);
        cbGender.setEnabled(false);
        cbPosition.setEnabled(false);
        dcDob.setEnabled(false);
        dcHireDate.setEnabled(false);
        btnChooseAvatar.setVisible(false); // Ẩn nút chọn ảnh ở chế độ xem

        Color readOnlyColor = new Color(245, 245, 245);
        txtName.setBackground(readOnlyColor);
        txtPhone.setBackground(readOnlyColor);
        txtAddress.setBackground(readOnlyColor);
        txtSalary.setBackground(readOnlyColor);
    }

    private void save() {
        try {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String pos = cbPosition.getSelectedItem().toString();
            String gender = cbGender.getSelectedItem().equals("Nam") ? "male" : "female";
            String avatar = selectedAvatarFileName;
            if (tempAvatarFile != null) {
                String savedName = ImageHelper.saveImageToProject(tempAvatarFile);
                if (savedName != null) {
                    avatar = savedName;
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi lưu ảnh vào dự án!");
                    return; // Dừng lại nếu copy lỗi
                }
            }

            String salaryStr = txtSalary.getText().trim();
            double salary = salaryStr.isEmpty() ? -1 : Double.parseDouble(salaryStr);

            Date sqlDob = null;
            if (dcDob.getDate() != null)
                sqlDob = new Date(dcDob.getDate().getTime());

            Date sqlHire = null;
            if (dcHireDate.getDate() != null)
                sqlHire = new Date(dcHireDate.getDate().getTime());

            EmployeeDTO emp = new EmployeeDTO(
                    mode.equals("add") ? 0 : currentEmp.getEmployeeId(),
                    name, sqlDob, gender, phone, address, pos, salary, sqlHire,
                    null, "active",
                    avatar); // <-- Truyền tên file avatar

            ValidationResult result = mode.equals("add")
                    ? employeeBUS.addEmployee(emp)
                    : employeeBUS.updateEmployee(emp);

            if (result.isValid()) {
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, result.getSummary(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lương phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}