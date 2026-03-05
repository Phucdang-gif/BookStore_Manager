package GUI.dialog.group;

import BUS.PublisherBUS;
import DTO.PublisherDTO;
import DTO.ValidationResult;
import GUI.util.ThemeColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PublisherDialog extends JDialog {
    private JTextField txtName, txtPhone;
    private JComboBox<String> cbStatus;
    private PublisherDTO publisher;
    private PublisherBUS publisherBUS;
    private boolean isSuccess = false;

    public PublisherDialog(Frame parent, PublisherDTO publisher) {
        super(parent, publisher == null ? "Thêm NXB" : "Sửa NXB", true);
        this.publisher = publisher;
        this.publisherBUS = new PublisherBUS();
        initComponents();
        if (publisher != null) {
            txtName.setText(publisher.getName());
            txtPhone.setText(publisher.getPhone());
            cbStatus.setSelectedItem(publisher.getStatus());
        }
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridLayout(3, 2, 10, 10));
        body.setBorder(new EmptyBorder(20, 20, 20, 20));
        body.setBackground(Color.WHITE);

        body.add(new JLabel("Tên NXB:"));
        txtName = new JTextField(20);
        body.add(txtName);

        body.add(new JLabel("Số điện thoại:"));
        txtPhone = new JTextField(20);
        body.add(txtPhone);

        body.add(new JLabel("Trạng thái:"));
        cbStatus = new JComboBox<>(new String[] { "active", "inactive" });
        body.add(cbStatus);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        btnSave.setBackground(ThemeColor.btnActiveBg);
        btnSave.setForeground(ThemeColor.btnActiveText);
        btnSave.addActionListener(e -> handleSave());
        footer.add(btnSave);

        add(body, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        pack();
    }

    private void handleSave() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String status = cbStatus.getSelectedItem().toString();

        PublisherDTO dto = (publisher == null)
                ? new PublisherDTO(0, name, phone, status)
                : new PublisherDTO(publisher.getId(), name, phone, status);

        ValidationResult vr = (publisher == null)
                ? publisherBUS.add(dto)
                : publisherBUS.update(dto);

        if (vr.isValid()) {
            isSuccess = true;
            JOptionPane.showMessageDialog(this, publisher == null ? "Thêm thành công!" : "Cập nhật thành công!");
            dispose();
        } else {
            GUI.util.ValidationUI.resetAll(txtName, txtPhone);
            if (vr.getError("name") != null)
                GUI.util.ValidationUI.setError(txtName, vr.getError("name"));
            if (vr.getError("phone") != null)
                GUI.util.ValidationUI.setError(txtPhone, vr.getError("phone"));
            JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}