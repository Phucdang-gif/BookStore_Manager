package GUI.components;

import GUI.util.IconHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserProfilePanel extends JPanel {
    public UserProfilePanel(String userName, String role) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        setBackground(Color.BLUE);
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Canh lề cho đẹp

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel lblName = new JLabel(userName.toUpperCase());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(33, 37, 41));
        lblName.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(Color.GRAY);
        lblRole.setAlignmentX(Component.RIGHT_ALIGNMENT);

        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(lblRole);

        // Avatar
        JLabel lblAvatar = new JLabel();
        IconHelper.setIcon(lblAvatar, "GUI/icon/avatar.svg", 50, 50); // Size nhỏ hơn chút cho gọn

        add(infoPanel);
        add(lblAvatar);
    }
}