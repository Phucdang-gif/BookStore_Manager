package GUI.components;

import GUI.util.IconHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import GUI.util.ThemeColor;

public class UserProfilePanel extends JPanel {

    public UserProfilePanel(String userName, String role) {

        setLayout(new BorderLayout(10, 0));
        setBackground(ThemeColor.bgPanel);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0,
                        ThemeColor.borderColor), // Đường kẻ mờ bên dưới
                new EmptyBorder(10, 20, 10, 10)));
        setPreferredSize(new Dimension(240, 80));
        JLabel lblAvatar = new JLabel();
        IconHelper.setIcon(lblAvatar, "GUI/icon/avatar.svg", 45, 45);

        add(lblAvatar, BorderLayout.WEST);

        // 3. Thông tin Text (Ở Giữa)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(2, 1, 0, 2)); // 2 dòng: Tên và Chức vụ
        textPanel.setOpaque(false);

        JLabel lblName = new JLabel(userName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(ThemeColor.textMain);
        lblName.setVerticalAlignment(SwingConstants.BOTTOM);

        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(ThemeColor.textSecondary);

        textPanel.add(lblName);
        textPanel.add(lblRole);

        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // Kiểm tra null để tránh lỗi khi khởi tạo lần đầu
        if (ThemeColor.bgPanel != null) {
            setBackground(ThemeColor.bgPanel);
            // Cập nhật lại border với màu mới
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.borderColor),
                    new EmptyBorder(10, 20, 10, 10)));
        }
        // Cập nhật màu chữ cho các label con
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel lbl = (JLabel) innerComp;
                        // Mẹo nhỏ: Phân biệt title và subtitle dựa vào font hoặc text
                        if (lbl.getFont().isBold()) {
                            lbl.setForeground(ThemeColor.textMain);
                        } else {
                            lbl.setForeground(ThemeColor.textSecondary);
                        }
                    }
                }
            }
        }
    }
}