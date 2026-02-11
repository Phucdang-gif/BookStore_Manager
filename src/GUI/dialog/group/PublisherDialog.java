package GUI.dialog.group;

import BUS.PublisherBUS;
import DTO.PublisherDTO;
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

        body.add(new JLabel("Tên NXB:"));
        txtName = new JTextField(20);
        body.add(txtName);

        body.add(new JLabel("Số điện thoại:"));
        txtPhone = new JTextField(20);
        body.add(txtPhone);

        body.add(new JLabel("Trạng thái:"));
        cbStatus = new JComboBox<>(new String[] { "active", "inactive" });
        body.add(cbStatus);

        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            // Logic lưu tương tự Category/Author
            isSuccess = true;
            dispose();
        });

        add(body, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
        pack();
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}