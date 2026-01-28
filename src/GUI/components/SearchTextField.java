package GUI.components;

import GUI.util.IconHelper;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * TextField tìm kiếm với icon SVG và placeholder
 */
public class SearchTextField extends JTextField {

    // Constants cho styling
    private static final int ROUND_RADIUS = 10;
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color PLACEHOLDER_COLOR = new Color(173, 181, 189);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final int DEFAULT_ICON_SIZE = 20;
    private static final int ICON_X_POSITION = 10;
    private static final int TEXT_LEFT_PADDING = 35;

    private Icon searchIcon;
    private String placeholder = "Tìm kiếm theo tên, mã...";

    public SearchTextField() {
        this("/GUI/icon/search.svg");
    }

    public SearchTextField(String iconPath) {
        initializeTextField();
        loadSearchIcon(iconPath);
    }

    /**
     * Khởi tạo các thuộc tính cơ bản của TextField
     */
    private void initializeTextField() {
        setFont(DEFAULT_FONT);
        setForeground(TEXT_COLOR);
        setBackground(Color.WHITE);
        setCaretColor(TEXT_COLOR);

        // Padding trái 35px để chừa chỗ cho icon
        setBorder(new EmptyBorder(8, TEXT_LEFT_PADDING, 8, 12));
        setPreferredSize(new Dimension(250, 40));
    }

    /**
     * Load icon SVG cho search field
     */
    private void loadSearchIcon(String iconPath) {
        this.searchIcon = IconHelper.getSVGIcon(iconPath, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE);
        if (this.searchIcon == null) {
            System.err.println("Không thể load search icon: " + iconPath);
        }
    }

    /**
     * Set placeholder text tùy chỉnh
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền trắng
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, ROUND_RADIUS, ROUND_RADIUS));

        // Vẽ viền
        g2.setColor(BORDER_COLOR);
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, ROUND_RADIUS, ROUND_RADIUS));

        // Vẽ icon SVG
        drawSearchIcon(g);

        // Vẽ placeholder
        drawPlaceholder(g2);

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Vẽ icon search
     */
    private void drawSearchIcon(Graphics g) {
        if (searchIcon != null) {
            // Tính toán vị trí Y để icon nằm giữa theo chiều dọc
            int iconY = (getHeight() - searchIcon.getIconHeight()) / 2;
            searchIcon.paintIcon(this, g, ICON_X_POSITION, iconY);
        }
    }

    /**
     * Vẽ placeholder text
     */
    private void drawPlaceholder(Graphics2D g2) {
        if (getText().isEmpty() && !isFocusOwner()) {
            g2.setColor(PLACEHOLDER_COLOR);
            g2.setFont(getFont());

            // Vị trí x=35 khớp với padding trái
            int textY = getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2;
            g2.drawString(placeholder, TEXT_LEFT_PADDING, textY);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Để trống vì đã vẽ trong paintComponent
    }

    /**
     * Set màu viền tùy chỉnh
     */
    public void setBorderColor(Color color) {
        // Có thể lưu vào instance variable và dùng trong paintComponent
        putClientProperty("borderColor", color);
        repaint();
    }
}