/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class LoginThanThien extends JFrame {
    
    public LoginThanThien() {
        setTitle("Đăng Nhập - Giao Diện Thân Thiện");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel với màu pastel nhẹ nhàng
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background màu pastel xanh mint
                g2d.setColor(new Color(240, 253, 250));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Vẽ các chấm tròn trang trí dễ thương
                g2d.setColor(new Color(134, 239, 172, 60));
                for (int i = 0; i < 8; i++) {
                    int x = 50 + i * 120;
                    int y = 30 + (i % 2) * 40;
                    g2d.fillOval(x, y, 40, 40);
                }
                
                // Vẽ các hình tròn lớn ở góc
                g2d.setColor(new Color(167, 243, 208, 40));
                g2d.fillOval(-80, getHeight() - 200, 280, 280);
                g2d.fillOval(getWidth() - 200, -80, 280, 280);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        // Container cho illustration và form
        JPanel containerPanel = new JPanel();
        containerPanel.setOpaque(false);
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
        
        // Left panel - Illustration thân thiện
        JPanel illustrationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ illustration đơn giản: người dùng với laptop
                // Background circle
                g2d.setColor(new Color(134, 239, 172, 100));
                g2d.fillOval(50, 80, 250, 250);
                
                // Laptop
                g2d.setColor(new Color(16, 185, 129));
                g2d.fillRoundRect(100, 200, 150, 90, 10, 10);
                g2d.setColor(new Color(240, 253, 250));
                g2d.fillRect(110, 210, 130, 70);
                
                // User icon (simplified person)
                g2d.setColor(new Color(5, 150, 105));
                g2d.fillOval(155, 140, 40, 40);
                g2d.fillRoundRect(145, 180, 60, 30, 20, 20);
                
                // Decorative elements
                g2d.setColor(new Color(167, 243, 208));
                g2d.fillOval(80, 100, 30, 30);
                g2d.fillOval(270, 150, 25, 25);
                g2d.fillOval(90, 280, 20, 20);
            }
        };
        illustrationPanel.setPreferredSize(new Dimension(350, 500));
        illustrationPanel.setOpaque(false);
        
        // Right panel - Login form
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background với shadow mềm
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, 30, 30);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 30, 30);
            }
        };
        loginPanel.setOpaque(false);
        loginPanel.setPreferredSize(new Dimension(450, 500));
        loginPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 40, 8, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Emoji welcome
        JLabel emojiLabel = new JLabel("👋", SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 40, 10, 40);
        loginPanel.add(emojiLabel, gbc);
        
        // Title thân thiện
        JLabel titleLabel = new JLabel("Xin chào bạn!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(16, 185, 129));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 40, 5, 40);
        loginPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Rất vui được gặp lại bạn 😊", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(115, 115, 115));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 20, 40);
        loginPanel.add(subtitleLabel, gbc);
        
        // Username field với style mềm mại
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 40, 3, 40);
        JLabel userLabel = new JLabel("📧 Email hoặc tên đăng nhập");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(new Color(100, 100, 100));
        loginPanel.add(userLabel, gbc);
        
        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBackground(new Color(249, 250, 251));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(3, 40, 8, 40);
        loginPanel.add(userField, gbc);
        
        // Password field
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 40, 3, 40);
        JLabel passLabel = new JLabel("🔑 Mật khẩu");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLabel.setForeground(new Color(100, 100, 100));
        loginPanel.add(passLabel, gbc);
        
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBackground(new Color(249, 250, 251));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridy = 6;
        gbc.insets = new Insets(3, 40, 8, 40);
        loginPanel.add(passField, gbc);
        
        // Options panel
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        
        JCheckBox rememberCheck = new JCheckBox("Nhớ mật khẩu");
        rememberCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberCheck.setOpaque(false);
        rememberCheck.setForeground(new Color(107, 114, 128));
        optionsPanel.add(rememberCheck, BorderLayout.WEST);
        
        JLabel forgotLabel = new JLabel("<html><u>Quên mật khẩu?</u></html>");
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotLabel.setForeground(new Color(16, 185, 129));
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsPanel.add(forgotLabel, BorderLayout.EAST);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 40, 8, 40);
        loginPanel.add(optionsPanel, gbc);
        
        // Login button với màu xanh mint
        JButton loginBtn = new JButton("Đăng nhập ngay 🚀") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow nhẹ
                g2d.setColor(new Color(16, 185, 129, 40));
                g2d.fillRoundRect(0, 4, getWidth(), getHeight()-4, 12, 12);
                
                // Button background
                g2d.setColor(new Color(16, 185, 129));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight()-4, 12, 12);
                
                // Text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }
        };
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(250, 45));
        gbc.gridy = 8;
        gbc.insets = new Insets(18, 40, 8, 40);
        loginPanel.add(loginBtn, gbc);
        
        // Separator với text
        JPanel separatorPanel = new JPanel(new BorderLayout());
        separatorPanel.setOpaque(false);
        separatorPanel.setPreferredSize(new Dimension(250, 30));
        
        JSeparator leftSep = new JSeparator();
        leftSep.setForeground(new Color(229, 231, 235));
        separatorPanel.add(leftSep, BorderLayout.WEST);
        
        JLabel orLabel = new JLabel(" hoặc ", SwingConstants.CENTER);
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orLabel.setForeground(new Color(156, 163, 175));
        separatorPanel.add(orLabel, BorderLayout.CENTER);
        
        JSeparator rightSep = new JSeparator();
        rightSep.setForeground(new Color(229, 231, 235));
        separatorPanel.add(rightSep, BorderLayout.EAST);
        
        gbc.gridy = 9;
        gbc.insets = new Insets(12, 40, 12, 40);
        loginPanel.add(separatorPanel, gbc);
        
        // Register text
        JLabel registerLabel = new JLabel("<html><center>Bạn mới biết đến chúng tôi?<br/><font color='#10B981'><b>Tạo tài khoản miễn phí</b></font></center></html>", SwingConstants.CENTER);
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 10;
        gbc.insets = new Insets(5, 40, 20, 40);
        loginPanel.add(registerLabel, gbc);
        
        containerPanel.add(illustrationPanel);
        containerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        containerPanel.add(loginPanel);
        
        mainPanel.add(containerPanel);
        add(mainPanel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginThanThien().setVisible(true);
        });
    }
}
