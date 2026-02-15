package GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import GUI.util.ImageHelper;
import GUI.util.ThemeColor; // Đảm bảo đã import ThemeColor

public class ProductCard extends JPanel {
    private String title;
    private String priceOrSubtitle;
    private String imagePath;

    // Callbacks cho sự kiện click
    private Runnable onViewClick;

    // Kích thước Card
    private static final int CARD_WIDTH = 220;
    private static final int CARD_HEIGHT = 360;

    // Kích thước ảnh tràn viền
    private static final int IMG_WIDTH_FULL = CARD_WIDTH;
    private static final int IMG_HEIGHT_FULL = 220; // Giảm chiều cao ảnh chút để nhường chỗ cho nút

    // Constructor giữ nguyên signature cũ để tương thích với GroupDashboard
    public ProductCard(String title, String priceOrSubtitle, String imagePath, Runnable onViewClick) {
        this.title = title;
        this.priceOrSubtitle = priceOrSubtitle;
        this.imagePath = imagePath;
        this.onViewClick = onViewClick;
        // Tạm thời chưa xử lý nút Add ở constructor này, có thể mở rộng sau

        initStyle();
        initComponents();
    }

    private void initStyle() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void initComponents() {
        // --- 1. IMAGE AREA (Top) ---
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(IMG_WIDTH_FULL, IMG_HEIGHT_FULL));
        lblImage.setOpaque(true);
        lblImage.setBackground(Color.WHITE);

        try {
            BufferedImage bufImg = ImageHelper.readImage(imagePath);
            if (bufImg != null) {
                BufferedImage resizedImg = ImageHelper.resize(bufImg, IMG_WIDTH_FULL, IMG_HEIGHT_FULL);
                lblImage.setIcon(new ImageIcon(resizedImg));
            } else {
                throw new Exception("Image not found");
            }
        } catch (Exception e) {
            lblImage.setText("<html><center>NO IMAGE</center></html>");
            lblImage.setForeground(Color.GRAY);
            lblImage.setBackground(new Color(245, 245, 245));
        }
        add(lblImage, BorderLayout.NORTH);

        // --- PANEL CHỨA INFO VÀ NÚT ---
        JPanel pnlBottomContent = new JPanel(new BorderLayout());
        pnlBottomContent.setBackground(Color.WHITE);
        pnlBottomContent.setBorder(new EmptyBorder(10, 10, 15, 10)); // Padding: Trên, Trái, Dưới, Phải

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setPreferredSize(new Dimension(CARD_WIDTH - 20, 20));

        JLabel lblSub = new JLabel(priceOrSubtitle);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(new Color(138, 43, 226)); // Màu tím
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlInfo.add(lblTitle);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblSub);

        pnlBottomContent.add(pnlInfo, BorderLayout.CENTER);
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlButton.setBackground(Color.WHITE);
        pnlButton.setBorder(new EmptyBorder(15, 0, 0, 0));

        RoundedBorderButton btnDetail = new RoundedBorderButton(
                "Xem",
                ThemeColor.textMain,
                15 // Độ bo góc
        );
        // Chỉnh lại kích thước nút cho vừa card
        btnDetail.setPreferredSize(new Dimension(120, 30));
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnDetail.addActionListener(e -> {
            if (onViewClick != null)
                onViewClick.run();
        });

        pnlButton.add(btnDetail);
        pnlBottomContent.add(pnlButton, BorderLayout.SOUTH);
        add(pnlBottomContent, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Nếu muốn vẽ thêm hiệu ứng hover cho cả card thì code ở đây
    }
}