package GUI.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorderButton extends ActionButton {

    private Color borderColor;
    private int radius;

    public RoundedBorderButton(String text, String iconPath, Color color, int radius) {
        // Gọi constructor của cha (ActionButton) với icon size 24 (vừa vặn)
        super(text, iconPath, 24);
        this.borderColor = color;
        this.radius = radius;
        initStyle();
    }

    private void initStyle() {
        // 1. Chỉnh text nằm ngang bên phải icon
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);

        // 2. Chỉnh màu chữ trùng với màu viền
        setForeground(borderColor);
        setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 3. Set con trỏ chuột
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 4. Set nền trong suốt để vẽ viền bo tròn đè lên
        setContentAreaFilled(false);
        setFocusPainted(false);

        // 5. Tạo viền bo tròn (Logic cũ chuyển vào đây)
        setBorder(new RoundedBorder());
    }

    // Class nội bộ để vẽ viền
    private class RoundedBorder implements Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(borderColor);
            // width-1 và height-1 để viền nằm trọn trong khung
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