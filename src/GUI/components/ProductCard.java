package GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import GUI.util.ImageHelper;
import GUI.util.ThemeColor;

public class ProductCard extends JPanel {
    private String title;
    private String imagePath; // Đã xóa priceOrSubtitle

    // Callbacks cho sự kiện click
    private Runnable onViewClick;
    // Kích thước Card
    private static final int CARD_WIDTH = 200;
    // Vì đã bỏ giá tiền, bạn có thể giảm chiều cao card xuống một chút (VD: 320)
    // cho cân đối
    private static final int CARD_HEIGHT = 320;
    // Kích thước ảnh tràn viền
    private static final int IMG_WIDTH_FULL = CARD_WIDTH;
    private static final int IMG_HEIGHT_FULL = 220;

    // CONSTRUCTOR MỚI: Đã xóa tham số priceOrSubtitle
    public ProductCard(String title, String imagePath, Runnable onViewClick) {
        this.title = title;
        this.imagePath = imagePath;
        this.onViewClick = onViewClick;

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
        JLabel lblImage = new JLabel("Đang tải ảnh...", SwingConstants.CENTER); // Placeholder text
        lblImage.setForeground(Color.GRAY);
        lblImage.setPreferredSize(new Dimension(IMG_WIDTH_FULL, IMG_HEIGHT_FULL));
        lblImage.setOpaque(true);
        lblImage.setBackground(new Color(245, 245, 245));
        add(lblImage, BorderLayout.NORTH);

        // TẢI ẢNH ĐA LUỒNG (TRÁNH ĐƠ GIAO DIỆN)
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // Đọc và resize ảnh ở một luồng ngầm (Background Thread)
                BufferedImage bufImg = ImageHelper.readImage(imagePath);
                if (bufImg != null) {
                    BufferedImage resizedImg = ImageHelper.resize(bufImg, IMG_WIDTH_FULL, IMG_HEIGHT_FULL);
                    return new ImageIcon(resizedImg);
                }
                return null;
            }

            @Override
            protected void done() {
                // Cập nhật lên giao diện khi đã xử lý xong (Main UI Thread)
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        lblImage.setText(""); // Xóa chữ "Đang tải ảnh..."
                        lblImage.setIcon(icon);
                    } else {
                        lblImage.setText("<html><center>NO IMAGE</center></html>");
                    }
                } catch (Exception e) {
                    lblImage.setText("<html><center>NO IMAGE</center></html>");
                }
            }
        };
        worker.execute(); // Bắt đầu chạy ngầm

        // --- 2. PANEL CHỨA INFO VÀ NÚT ---
        JPanel pnlBottomContent = new JPanel(new BorderLayout());
        pnlBottomContent.setBackground(ThemeColor.bgWhite);
        pnlBottomContent.setBorder(new EmptyBorder(10, 10, 15, 10));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBackground(ThemeColor.bgWhite);

        JLabel lblTitle = new JLabel("<html><center>" + title + "</center></html>", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setHorizontalTextPosition(SwingConstants.CENTER);
        lblTitle.setMaximumSize(new Dimension(CARD_WIDTH, 50));

        pnlInfo.add(lblTitle);

        pnlBottomContent.add(pnlInfo, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlButton.setBackground(Color.WHITE);
        pnlButton.setBorder(new EmptyBorder(10, 0, 0, 0));

        RoundedBorderButton btnDetail = new RoundedBorderButton("Xem chi tiết", ThemeColor.textMain, 15);
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
    }
}