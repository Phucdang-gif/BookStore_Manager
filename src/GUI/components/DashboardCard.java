package GUI.components;

import GUI.util.IconHelper; // Giả sử bạn có class này, nếu không thì dùng ImageIcon
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardCard extends JPanel {
    private Color color;

    public DashboardCard(String title, String value, String iconPath, Color color, Runnable onClick) {
        this.color = color;
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 120)); // Kích thước thẻ
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- ICON (Trái) ---
        JLabel lblIcon = new JLabel();
        // Nếu bạn chưa có IconHelper, dùng: new ImageIcon(iconPath)
        IconHelper.setIcon(lblIcon, iconPath, 40, 40);
        lblIcon.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // --- TEXT INFO (Phải) ---
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);

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
                // Hiệu ứng hover nhẹ (tối đi 1 chút)
                DashboardCard.this.color = color.darker();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                DashboardCard.this.color = color;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ hình chữ nhật bo tròn với màu phẳng
        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bo góc 20px

        super.paintComponent(g);
    }
}