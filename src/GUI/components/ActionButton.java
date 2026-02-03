package GUI.components;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import GUI.util.ThemeColor;

/**
 * Button với icon nằm phía trên và text nằm phía dưới
 * Thường dùng cho các action button trong header/toolbar
 */
public class ActionButton extends JButton {

    private static final Color DEFAULT_TEXT_COLOR = ThemeColor.textMain;
    private static final Color DEFAULT_HOVER_COLOR = ThemeColor.borderColor;
    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 11);
    private static final int DEFAULT_ICON_SIZE = 35;

    public ActionButton(String text, String iconPath) {
        this(text, iconPath, DEFAULT_ICON_SIZE);
    }

    public ActionButton(String text, String svgPath, int iconSize) {
        super(text);
        initializeButton();
        loadSVGIcon(svgPath, iconSize);
    }

    private void initializeButton() {
        // Font và màu sắc
        setFont(DEFAULT_FONT);
        setForeground(DEFAULT_TEXT_COLOR);
        setBackground(ThemeColor.bgPanel);

        // Hiệu ứng và con trỏ
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sắp xếp icon nằm trên, chữ nằm dưới
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setIconTextGap(8);

        // Border và padding
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentAreaFilled(false);
    }

    private void loadSVGIcon(String svgPath, int iconSize) {
        try {
            FlatSVGIcon icon = new FlatSVGIcon(svgPath, iconSize, iconSize);
            setIcon(icon);
        } catch (Exception e) {
            System.err.println("Không thể load SVG icon: " + svgPath);
            e.printStackTrace();
        }
    }

    /**
     * Vẽ hiệu ứng hover khi di chuột vào button
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isRollover()) {
            g.setColor(DEFAULT_HOVER_COLOR);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        super.paintComponent(g);
    }

    public void setTextColor(Color color) {
        setForeground(color);
    }

    public void setHoverColor(Color color) {
        // Lưu màu hover để dùng trong paintComponent
        putClientProperty("hoverColor", color);
        repaint();
    }

}