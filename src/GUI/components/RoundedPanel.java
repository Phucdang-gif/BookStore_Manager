package GUI.components;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel với các góc bo tròn
 * Dùng cho các card, container cần thiết kế hiện đại
 */
public class RoundedPanel extends JPanel {

    private static final int DEFAULT_RADIUS = 20;

    private Color backgroundColor;
    private int radius;

    public RoundedPanel() {
        this(DEFAULT_RADIUS, Color.WHITE);
    }

    public RoundedPanel(int radius) {
        this(radius, Color.WHITE);
    }

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.radius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false); // Để vẽ trong suốt các góc bo
    }

    public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
        super(layout);
        this.radius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension arcs = new Dimension(radius, radius);
        int width = getWidth();
        int height = getHeight();

        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền bo góc
        Color bgColor = (backgroundColor != null) ? backgroundColor : getBackground();
        graphics.setColor(bgColor);
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
    }

    /**
     * Set màu nền cho panel
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    /**
     * Set độ bo góc
     */
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    /**
     * Get độ bo góc hiện tại
     */
    public int getRadius() {
        return radius;
    }
}