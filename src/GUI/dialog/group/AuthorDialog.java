package GUI.dialog.group;

import BUS.AuthorBUS;
import DTO.AuthorDTO;
import GUI.util.ThemeColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AuthorDialog extends JDialog {
    private JTextField txtAuthorName;
    private AuthorDTO author;
    private AuthorBUS authorBUS; // Tái sử dụng lớp nghiệp vụ
    private boolean isSuccess = false;

    public AuthorDialog(Frame parent, AuthorDTO author) {
        super(parent, author == null ? "Thêm tác giả" : "Sửa tác giả", true);
        this.author = author;
        this.authorBUS = new AuthorBUS(); // Khởi tạo BUS
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
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được để trống");
            return;
        }

        if (author == null) {
            // Nghiệp vụ thêm mới thông qua BUS
            AuthorDTO newAuthor = new AuthorDTO(0, name);
            if (authorBUS.addAuthor(newAuthor)) {
                isSuccess = true;
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                dispose();
            }
        } else {
            // Logic cho chức năng Sửa (Cần bổ sung hàm update vào BUS/DAO)
            author.setAuthorName(name);
            isSuccess = true;
            dispose();
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}