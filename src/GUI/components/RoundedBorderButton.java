package GUI.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import GUI.util.IconHelper;

public class RoundedBorderButton extends JButton {

    private Color borderColor;
    private int radius;
    private Color hoverColor;

    // --- CONSTRUCTOR 1: Có Icon ---
    public RoundedBorderButton(String text, String iconPath, Color borderColor, int radius) {
        super(text);
        this.borderColor = borderColor;
        this.radius = radius;
        IconHelper.setIcon(this, iconPath, 24, 24);
        initStyle();
    }

    // --- CONSTRUCTOR 2: Chỉ có Text ---
    public RoundedBorderButton(String text, Color borderColor, int radius) {
        super(text);
        this.borderColor = borderColor;
        this.radius = radius;
        initStyle();
    }

    private void initStyle() {
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);
        if (getIcon() != null)
            setHorizontalTextPosition(SwingConstants.RIGHT);

        setForeground(borderColor);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setBorder(new RoundedBorder());

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hoverColor = getBackground().darker();
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoverColor = null;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền bo tròn — dùng hoverColor nếu đang hover
        g2.setColor(hoverColor != null ? hoverColor : getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();

        super.paintComponent(g); // Chỉ vẽ text/icon vì setContentAreaFilled(false)
    }

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
            return new Insets(5, 15, 5, 15);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}