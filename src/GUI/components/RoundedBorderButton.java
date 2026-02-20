package GUI.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorderButton extends ActionButton {

    private Color borderColor;
    private int radius;

    // --- CONSTRUCTOR 1: Có Icon (Giữ nguyên cũ) ---
    public RoundedBorderButton(String text, String iconPath, Color color, int radius) {
        // Gọi constructor của cha có icon
        super(text, iconPath, 24);
        this.borderColor = color;
        this.radius = radius;
        initStyle();
    }

    // --- CONSTRUCTOR 2: Chỉ có Text (Mới thêm) ---
    public RoundedBorderButton(String text, Color color, int radius) {
        super(text);
        this.borderColor = color;
        this.radius = radius;
        initStyle();
    }

    private void initStyle() {
        // 1. Chỉnh text nằm giữa (quan trọng cho button không icon)
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);

        // Nếu có icon (kiểm tra gián tiếp qua icon gap hoặc logic cũ),
        // muốn icon bên trái text thì dùng:
        if (getIcon() != null) {
            setHorizontalTextPosition(SwingConstants.RIGHT);
        }

        // 2. Chỉnh màu chữ trùng với màu viền
        setForeground(borderColor);
        setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 3. Set con trỏ chuột
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 4. Set nền trong suốt để vẽ viền bo tròn đè lên
        setContentAreaFilled(false);
        setFocusPainted(false);

        // 5. Tạo viền bo tròn
        setBorder(new RoundedBorder());
    }

    // Class nội bộ để vẽ viền (Giữ nguyên)
    private class RoundedBorder implements Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // Padding: Trên 5, Trái 15, Dưới 5, Phải 15
            return new Insets(5, 15, 5, 15);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}