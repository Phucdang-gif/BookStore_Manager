package GUI.dialog;

import BUS.DiscountServiceBUS;
import DTO.DiscountServiceDTO;
import java.awt.*;
import java.sql.Timestamp;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class DiscountDialog extends JDialog {

    private String mode;
    private DiscountServiceDTO currentDiscount;
    private DiscountServiceBUS discountBUS;

    private JTextField txtName, txtValue, txtMinAmount, txtMaxAmount, txtStartDate, txtEndDate, txtDescription;
    private JComboBox<String> cbType;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public DiscountDialog(Frame owner, boolean modal, String mode, DiscountServiceDTO discount,
            DiscountServiceBUS bus) {
        super(owner, modal);
        this.mode = mode;
        this.currentDiscount = discount;
        this.discountBUS = bus;
        sdf.setLenient(false);
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

        pnlForm.add(new JLabel("Từ ngày (dd-MM-yyyy):"));
        pnlForm.add(txtStartDate = new JTextField());

        pnlForm.add(new JLabel("Đến ngày (dd-MM-yyyy):"));
        pnlForm.add(txtEndDate = new JTextField());

        pnlForm.add(new JLabel("Mô tả thêm:"));
        pnlForm.add(txtDescription = new JTextField());

        add(pnlForm, BorderLayout.CENTER);

        // --- NÚT BẤM ---
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu Dữ Liệu");
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

            // Ép kiểu bỏ số thập phân .0 cho đẹp
            txtValue.setText(String.format("%.0f", currentDiscount.getDiscountValue()));
            txtMinAmount.setText(String.format("%.0f", currentDiscount.getMinimumAmount()));
            txtMaxAmount.setText(String.format("%.0f", currentDiscount.getMaximumDiscount()));

            // Cắt chuỗi lấy phần yyyy-MM-dd hiển thị lên UI
            if (currentDiscount.getStartDate() != null) {
                txtStartDate.setText(sdf.format(currentDiscount.getStartDate()));
            }
            if (currentDiscount.getEndDate() != null) {
                txtEndDate.setText(sdf.format(currentDiscount.getEndDate()));
            }

            txtDescription.setText(currentDiscount.getDescription());
        }
    }

    private void save() {
        try {
            // 1. Lấy dữ liệu từ UI
            String name = txtName.getText().trim();
            String type = cbType.getSelectedIndex() == 0 ? "Phần trăm" : "Số tiền cố định";
            String valStr = txtValue.getText().trim();
            String minStr = txtMinAmount.getText().trim();
            String maxStr = txtMaxAmount.getText().trim();
            String startStr = txtStartDate.getText().trim();
            String endStr = txtEndDate.getText().trim();
            String desc = txtDescription.getText().trim();

            // 2. Validate cơ bản
            if (name.isEmpty() || valStr.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (*), Ngày tháng!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Parse số
            double value = Double.parseDouble(valStr);
            double min = Double.parseDouble(minStr.isEmpty() ? "0" : minStr);
            double max = Double.parseDouble(maxStr.isEmpty() ? "0" : maxStr);

            // 4. Parse ngày theo định dạng dd-MM-yyyy
            Date dateStart = sdf.parse(startStr);
            Date dateEnd = sdf.parse(endStr);
            Timestamp startDate = new Timestamp(dateStart.getTime());

            // Xử lý ngày kết thúc: Cộng thêm gần 1 ngày để lấy hết ngày đó (23:59:59)
            long endTime = dateEnd.getTime() + (24 * 60 * 60 * 1000) - 1000;
            Timestamp endDate = new Timestamp(endTime);

            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Lỗi logic",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra logic ngày tháng
            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Lỗi logic",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. Gom vào DTO
            DiscountServiceDTO dto = new DiscountServiceDTO(
                    mode.equals("add") ? 0 : currentDiscount.getServiceId(),
                    name, type, value, min, max, startDate, endDate,
                    mode.equals("add") ? "active" : currentDiscount.getStatus(), // Giữ nguyên trạng thái nếu update
                    desc);

            // 6. Gọi BUS xử lý
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

        } catch (ParseException ex) {
            // [SỬA 4] Thông báo lỗi rõ ràng nếu nhập sai định dạng
            JOptionPane.showMessageDialog(this, "Ngày tháng phải đúng định dạng dd-MM-yyyy (VD: 31-12-2023)!",
                    "Lỗi ngày tháng", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}