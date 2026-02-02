package GUI.components;

import GUI.util.IconHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserProfilePanel extends JPanel {

    public UserProfilePanel(String userName, String role) {

        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)), // Đường kẻ mờ bên dưới
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
        lblName.setForeground(new Color(33, 37, 41));
        lblName.setVerticalAlignment(SwingConstants.BOTTOM);

        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(120, 120, 120)); // Màu xám nhạt

        textPanel.add(lblName);
        textPanel.add(lblRole);

        add(textPanel, BorderLayout.CENTER);
    }
}