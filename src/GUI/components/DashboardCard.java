package GUI.components;

import GUI.util.IconHelper;
import GUI.util.ImageHelper; // Import thêm nếu cần xử lý ảnh phức tạp
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DashboardCard extends JPanel {
    private Color color;
    private Color originalColor;
    private JLabel lblValue;

    // Kích thước nhỏ gọn hơn (Compact)
    private static final int CARD_WIDTH = 220; // Giảm chiều rộng (cũ 250)
    private static final int CARD_HEIGHT = 90; // Giảm chiều cao (cũ 120)

    public DashboardCard(String title, String value, String iconPath, Color color, Runnable onClick) {
        this.color = color;
        this.originalColor = color;

        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- ICON (Trái) - Thu nhỏ lại ---
        JLabel lblIcon = new JLabel();
        try {
            // Dùng IconHelper hoặc ImageHelper để resize icon nhỏ lại (32x32)
            BufferedImage img = ImageHelper.readImage(iconPath);
            if (img != null) {
                lblIcon.setIcon(new ImageIcon(ImageHelper.resize(img, 32, 32)));
            } else {
                IconHelper.setIcon(lblIcon, iconPath, 32, 32); // Fallback
            }
        } catch (Exception e) {
            lblIcon.setText("ICON");
        }
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10)); // Padding nhỏ lại

        // --- TEXT INFO (Phải) ---
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1)); // 2 dòng: Title và Value
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Font nhỏ hơn (cũ 14)
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setVerticalAlignment(SwingConstants.BOTTOM);

        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Font số nhỏ hơn (cũ 24)
        lblValue.setForeground(Color.WHITE);
        lblValue.setVerticalAlignment(SwingConstants.TOP);

        pnlInfo.add(lblTitle);
        pnlInfo.add(lblValue);

        add(lblIcon, BorderLayout.WEST);
        add(pnlInfo, BorderLayout.CENTER);

        // --- SỰ KIỆN CLICK ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null)
                    onClick.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                DashboardCard.this.color = originalColor.darker();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                DashboardCard.this.color = originalColor;
                repaint();
            }
        });
    }

    public void setValue(String value) {
        this.lblValue.setText(value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ bo tròn
        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        super.paintComponent(g);
    }
}