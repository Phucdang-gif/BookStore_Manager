package GUI.dialog.group;

import BUS.AuthorBUS;
import DTO.AuthorDTO;
import DTO.ValidationResult;
import GUI.util.ThemeColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AuthorDialog extends JDialog {
    private JTextField txtAuthorName;
    private AuthorDTO author;
    private AuthorBUS authorBUS;
    private boolean isSuccess = false;

    public AuthorDialog(Frame parent, AuthorDTO author) {
        super(parent, author == null ? "Thêm tác giả" : "Sửa tác giả", true);
        this.author = author;
        this.authorBUS = new AuthorBUS();
        initComponents();
        if (author != null)
            txtAuthorName.setText(author.getAuthorName());
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridLayout(2, 1, 5, 5));
        body.setBorder(new EmptyBorder(20, 20, 20, 20));
        body.setBackground(Color.WHITE);
        body.add(new JLabel("Tên tác giả:"));
        txtAuthorName = new JTextField(20);
        body.add(txtAuthorName);

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
        String name = txtAuthorName.getText().trim();

        AuthorDTO dto = (author == null)
                ? new AuthorDTO(0, name)
                : new AuthorDTO(author.getAuthorId(), name);

        ValidationResult vr = (author == null)
                ? authorBUS.addAuthor(dto)
                : authorBUS.updateAuthor(dto);

        if (vr.isValid()) {
            isSuccess = true;
            JOptionPane.showMessageDialog(this, author == null ? "Thêm thành công!" : "Cập nhật thành công!");
            dispose();
        } else {
            GUI.util.ValidationUI.resetAll(txtAuthorName);
            if (vr.getError("authorName") != null) {
                GUI.util.ValidationUI.setError(txtAuthorName, vr.getError("authorName"));
            }
            JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}