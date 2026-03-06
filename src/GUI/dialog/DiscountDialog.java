package GUI.dialog;

import BUS.DiscountServiceBUS;
import DTO.DiscountServiceDTO;
import java.awt.*;
import java.sql.Timestamp;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.util.Date;

public class DiscountDialog extends JDialog {

    private String mode;
    private DiscountServiceDTO currentDiscount;
    private DiscountServiceBUS discountBUS;
    private JTextField txtName, txtValue, txtMinAmount, txtMaxAmount, txtDescription;
    private JComboBox<String> cbType;
    private JDateChooser dcStartDate, dcEndDate;

    public DiscountDialog(Frame owner, boolean modal, String mode, DiscountServiceDTO discount,
            DiscountServiceBUS bus) {
        super(owner, modal);
        this.mode = mode;
        this.currentDiscount = discount;
        this.discountBUS = bus;

        setTitle(mode.equals("add") ? "Thêm Chương Trình Khuyến Mãi Mới" : "Cập Nhật Khuyến Mãi");
        setSize(450, 550);
        setLocationRelativeTo(null);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // --- FORM NHẬP LIỆU ---
        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 10, 15));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Tên Chương Trình (*):"));
        pnlForm.add(txtName = new JTextField());

        pnlForm.add(new JLabel("Loại Giảm Giá:"));
        cbType = new JComboBox<>(new String[] { "Phần trăm (%)", "Tiền mặt (VNĐ)" });
        pnlForm.add(cbType);

        pnlForm.add(new JLabel("Giá Trị Giảm (*):"));
        pnlForm.add(txtValue = new JTextField());

        pnlForm.add(new JLabel("Đơn Tối Thiểu (VNĐ):"));
        pnlForm.add(txtMinAmount = new JTextField("0"));

        pnlForm.add(new JLabel("Giảm Tối Đa (VNĐ):"));
        pnlForm.add(txtMaxAmount = new JTextField("0"));

        pnlForm.add(new JLabel("Từ ngày:"));
        dcStartDate = new JDateChooser();
        dcStartDate.setDateFormatString("dd-MM-yyyy");
        pnlForm.add(dcStartDate);

        pnlForm.add(new JLabel("Đến ngày:"));
        dcEndDate = new JDateChooser();
        dcEndDate.setDateFormatString("dd-MM-yyyy");
        pnlForm.add(dcEndDate);

        pnlForm.add(new JLabel("Mô tả thêm:"));
        pnlForm.add(txtDescription = new JTextField());

        add(pnlForm, BorderLayout.CENTER);

        // --- NÚT BẤM ---
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu Dữ Liệu");
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Hủy Bỏ");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());

        pnlBtns.add(btnSave);
        pnlBtns.add(btnCancel);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (mode.equals("update") && currentDiscount != null) {
            txtName.setText(currentDiscount.getServiceName());
            cbType.setSelectedItem(
                    "Phần trăm".equals(currentDiscount.getDiscountType()) ? "Phần trăm (%)" : "Tiền mặt (VNĐ)");

            txtValue.setText(String.format("%.0f", currentDiscount.getDiscountValue()));
            txtMinAmount.setText(String.format("%.0f", currentDiscount.getMinimumAmount()));
            txtMaxAmount.setText(String.format("%.0f", currentDiscount.getMaximumDiscount()));

            // 3. Set trực tiếp Object Timestamp vào JDateChooser
            if (currentDiscount.getStartDate() != null) {
                dcStartDate.setDate(currentDiscount.getStartDate());
            }
            if (currentDiscount.getEndDate() != null) {
                dcEndDate.setDate(currentDiscount.getEndDate());
            }

            txtDescription.setText(currentDiscount.getDescription());
        }
    }

    private void save() {
        try {
            // 1. Lấy dữ liệu Text từ UI
            String name = txtName.getText().trim();
            String type = cbType.getSelectedIndex() == 0 ? "Phần trăm" : "Số tiền cố định";
            String valStr = txtValue.getText().trim();
            String minStr = txtMinAmount.getText().trim();
            String maxStr = txtMaxAmount.getText().trim();
            String desc = txtDescription.getText().trim();

            // 2. Lấy Object Date trực tiếp từ JDateChooser
            Date dateStart = dcStartDate.getDate();
            Date dateEnd = dcEndDate.getDate();

            // 3. Validate cơ bản (Kiểm tra rỗng và chưa chọn ngày)
            if (name.isEmpty() || valStr.isEmpty() || dateStart == null || dateEnd == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (*), Ngày tháng!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4. Parse số
            double value = Double.parseDouble(valStr);
            double min = Double.parseDouble(minStr.isEmpty() ? "0" : minStr);
            double max = Double.parseDouble(maxStr.isEmpty() ? "0" : maxStr);

            // 5. Chuyển đổi java.util.Date sang java.sql.Timestamp
            Timestamp startDate = new Timestamp(dateStart.getTime());

            // Xử lý ngày kết thúc: Cộng thêm gần 1 ngày để bao trọn ngày đó (23:59:59)
            long endTime = dateEnd.getTime() + (24 * 60 * 60 * 1000) - 1000;
            Timestamp endDate = new Timestamp(endTime);

            // Kiểm tra logic ngày tháng
            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Lỗi logic",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 6. Gom vào DTO
            DiscountServiceDTO dto = new DiscountServiceDTO(
                    mode.equals("add") ? 0 : currentDiscount.getServiceId(),
                    name, type, value, min, max, startDate, endDate,
                    mode.equals("add") ? "active" : currentDiscount.getStatus(),
                    desc);

            // 7. Gọi BUS xử lý
            boolean isSuccess = mode.equals("add") ? discountBUS.addDiscount(dto) : discountBUS.updateDiscount(dto);

            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Lưu chương trình khuyến mãi thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá trị số không hợp lệ!",
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}