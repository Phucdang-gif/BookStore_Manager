package GUI;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private Image backgroundImage;

    public Login() {
        backgroundImage = new ImageIcon("./icon/bookshelf.jpg").getImage();

        setTitle("BookStore System");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.7), (int) (screenSize.height * 0.7));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. Panel nền vẽ ảnh bookshelf phủ kín Frame
        JPanel mainBackgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // Thêm một lớp phủ nhẹ để ảnh nền không làm lóa mắt
                g.setColor(new Color(255, 255, 255, 20));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainBackgroundPanel);

        // 3. Container để đẩy phần Login sang bên phải
        JPanel rightContainer = new JPanel(new GridBagLayout());
        rightContainer.setOpaque(false); // Để lộ ảnh nền phía sau

        // 4. Panel Login "Nổi trội" (Trắng, Bo góc, Đổ bóng)
        JPanel loginCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 0)); // Trắng đục sang trọng
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Bo góc 30px
                g2.dispose();
            }
        };
        loginCard.setPreferredSize(new Dimension(400, 500));
        loginCard.setOpaque(false); // Tắt màu nền mặc định để dùng paintComponent

        // --- CÁC THÀNH PHẦN BÊN TRONG LOGIN CARD ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 40, 10, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề Login đậm và lớn
        JLabel titleLabel = new JLabel("LOGIN", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 40, 10, 40);
        loginCard.add(titleLabel, gbc);

        JLabel subTitle = new JLabel("Welcome back! Please enter your details.", SwingConstants.CENTER);
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 20, 30);
        loginCard.add(subTitle, gbc);

        // Ô nhập Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 40, 5, 40);
        loginCard.add(userLabel, gbc);

        JTextField userField = new JTextField();
        styleTextField(userField);
        gbc.gridy = 3;
        loginCard.add(userField, gbc);

        // Ô nhập Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        loginCard.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField();
        styleTextField(passField);
        gbc.gridy = 5;
        loginCard.add(passField, gbc);

        // Nút Login màu xanh nổi bật
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(41, 98, 255));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(30, 40, 40, 40);
        loginCard.add(loginBtn, gbc);

        // Đưa Login Card vào container bên phải
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.insets = new Insets(0, 0, 0, 45); // Cách lề phải 80px
        centerGbc.anchor = GridBagConstraints.EAST;
        centerGbc.weightx = 1.0;
        centerGbc.weighty = 1.0;
        rightContainer.add(loginCard, centerGbc);

        mainBackgroundPanel.add(rightContainer, BorderLayout.CENTER);
    }

    // Hàm phụ để trang trí Text Field cho hiện đại
    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}