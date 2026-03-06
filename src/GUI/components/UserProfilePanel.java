package GUI.components;

import GUI.util.IconHelper;
import GUI.util.ImageHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import GUI.util.ThemeColor;

public class UserProfilePanel extends JPanel {

    private static final int AVATAR_SIZE = 45;
    private JLabel lblAvatar;

    /**
     * Constructor cơ bản (không có avatar riêng — dùng icon mặc định).
     */
    public UserProfilePanel(String userName, String role) {
        this(userName, role, null);
    }

    /**
     * Constructor đầy đủ — avatarFileName là tên file lưu trong DB (ví dụ:
     * "emp_01.png" hoặc null).
     */
    public UserProfilePanel(String userName, String role, String avatarFileName) {
        setLayout(new BorderLayout(10, 0));
        setBackground(ThemeColor.bgWhite);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.borderColor),
                new EmptyBorder(10, 20, 10, 10)));
        setPreferredSize(new Dimension(240, 80));

        // --- AVATAR ---
        lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        loadAvatar(avatarFileName);
        add(lblAvatar, BorderLayout.WEST);

        // --- TEXT ---
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel lblName = new JLabel(userName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(ThemeColor.ACCENT_COLOR);
        lblName.setVerticalAlignment(SwingConstants.BOTTOM);

        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(ThemeColor.textMain);

        textPanel.add(lblName);
        textPanel.add(lblRole);
        add(textPanel, BorderLayout.CENTER);
    }

    /**
     * Load và hiển thị avatar:
     * - PNG/JPG: bo tròn bằng ImageHelper.makeCircle()
     * - SVG: dùng FlatSVGIcon qua IconHelper
     * - null / không tìm thấy: fallback icon stafff.svg
     */
    private void loadAvatar(String avatarFileName) {
        if (avatarFileName == null || avatarFileName.isEmpty()) {
            setDefaultAvatar();
            return;
        }

        String ext = getExtension(avatarFileName).toLowerCase();
        String path = "image/" + avatarFileName;

        if (ext.equals("svg")) {
            IconHelper.setIcon(lblAvatar, path, AVATAR_SIZE, AVATAR_SIZE);
        } else {
            BufferedImage img = ImageHelper.readImage(path);
            if (img != null) {
                BufferedImage circle = ImageHelper.makeCircle(
                        ImageHelper.resize(img, AVATAR_SIZE, AVATAR_SIZE), AVATAR_SIZE);
                lblAvatar.setIcon(new ImageIcon(circle));
            } else {
                setDefaultAvatar();
            }
        }
    }

    private void setDefaultAvatar() {
        IconHelper.setIcon(lblAvatar, "GUI/icon/stafff.svg", AVATAR_SIZE, AVATAR_SIZE);
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot >= 0) ? fileName.substring(dot + 1) : "";
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (ThemeColor.bgPanel != null) {
            setBackground(ThemeColor.bgWhite);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.borderColor),
                    new EmptyBorder(10, 20, 10, 10)));
        }
    }
}