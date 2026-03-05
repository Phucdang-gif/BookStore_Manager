package GUI.dialog.group;

import BUS.CategoryBUS;
import DTO.CategoryDTO;
import DTO.ValidationResult;
import GUI.util.ThemeColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CategoryDialog extends JDialog {
    private JTextField txtName, txtOrder;
    private JComboBox<String> cbStatus;
    private CategoryDTO category;
    private CategoryBUS categoryBUS;
    private boolean isSuccess = false;

    public CategoryDialog(Frame parent, CategoryDTO category) {
        super(parent, category == null ? "Thêm thể loại" : "Sửa thể loại", true);
        this.category = category;
        this.categoryBUS = new CategoryBUS();
        initComponents();
        if (category != null) {
            txtName.setText(category.getName());
            txtOrder.setText(String.valueOf(category.getDisplayOrder()));
            cbStatus.setSelectedItem(category.getStatus());
        }
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridLayout(3, 2, 10, 10));
        body.setBorder(new EmptyBorder(20, 20, 20, 20));
        body.setBackground(Color.WHITE);

        body.add(new JLabel("Tên thể loại:"));
        txtName = new JTextField(20);
        body.add(txtName);

        body.add(new JLabel("Thứ tự hiển thị:"));
        txtOrder = new JTextField(20);
        body.add(txtOrder);

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
        String status = cbStatus.getSelectedItem().toString();
        int order = parseOrder(txtOrder.getText().trim());

        CategoryDTO dto = (category == null)
                ? new CategoryDTO(0, name, order, status)
                : new CategoryDTO(category.getId(), name, order, status);

        ValidationResult vr = (category == null)
                ? categoryBUS.add(dto)
                : categoryBUS.update(dto);

        if (vr.isValid()) {
            isSuccess = true;
            JOptionPane.showMessageDialog(this, category == null ? "Thêm thành công!" : "Cập nhật thành công!");
            dispose();
        } else {
            GUI.util.ValidationUI.resetAll(txtName, txtOrder);
            if (vr.getError("name") != null)
                GUI.util.ValidationUI.setError(txtName, vr.getError("name"));
            if (vr.getError("displayOrder") != null)
                GUI.util.ValidationUI.setError(txtOrder, vr.getError("displayOrder"));
            JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int parseOrder(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}