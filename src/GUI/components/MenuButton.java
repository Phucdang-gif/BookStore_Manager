package GUI.components;

import GUI.util.IconHelper;
import GUI.util.ThemeColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuButton extends JButton {
    private final Color activeColor = ThemeColor.btnActiveBg; // Màu nền khi chọn
    private final Color activeText = ThemeColor.btnActiveText; // Màu chữ khi chọn
    private final Color normalColor = ThemeColor.bgPanel; // Màu nền thường
    private final Color normalText = ThemeColor.textMain; // Màu chữ thường

    public MenuButton(String text, String iconPath) {
        super(text);
        // Setup Icon
        if (iconPath != null) {
            IconHelper.setIcon(this, iconPath, 20, 20);
        }
        initStyle();
    }

    private void initStyle() {
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setIconTextGap(15);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mặc định
        setBackground(normalColor);
        setForeground(normalText);
        setInactiveStyle(); // Set padding mặc định

        // Hiệu ứng hover nhẹ (tùy chọn)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!getBackground().equals(activeColor)) { // Chỉ hover khi chưa active
                    setBackground(new Color(240, 240, 240)); // Màu xám rất nhạt
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!getBackground().equals(activeColor)) {
                    setBackground(normalColor);
                }
            }
        });
    }

    // Hàm quan trọng: Chuyển đổi trạng thái Active
    public void setActive(boolean isActive) {
        if (isActive) {
            setBackground(activeColor);
            setForeground(activeText);
            // Border có vạch xanh bên trái
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 5, 0, 0, activeText),
                    new EmptyBorder(12, 20, 12, 20) // Tổng padding trái = 25
            ));
        } else {
            setInactiveStyle();
        }
    }

    private void setInactiveStyle() {
        setBackground(normalColor);
        setForeground(normalText);
        // Border rỗng, padding trái 25
        setBorder(new EmptyBorder(12, 25, 12, 20));
    }
}