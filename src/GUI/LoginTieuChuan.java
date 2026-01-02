/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;
import javax.swing.*;
import java.awt.*;

public class LoginTieuChuan extends JFrame {
    
    public LoginTieuChuan() {
        setTitle("Đăng Nhập Hệ Thống");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Left panel - Branding area
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Solid professional color
                g2d.setColor(new Color(41, 98, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Company logo area (vẽ một logo đơn giản)
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(50, 80, 80, 80, 20, 20);
                
                g2d.setColor(new Color(41, 98, 255));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
                g2d.drawString("M", 75, 135);
            }
        };
        leftPanel.setPreferredSize(new Dimension(320, 500));
        leftPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 40, 20, 40);
        
        // Company name
        JLabel companyLabel = new JLabel("MANAGEMENT");
        companyLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        companyLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        leftPanel.add(companyLabel, gbc);
        
        JLabel systemLabel = new JLabel("SYSTEM");
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        systemLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        leftPanel.add(systemLabel, gbc);
        
        JLabel versionLabel = new JLabel("Version 2.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versionLabel.setForeground(new Color(200, 220, 255));
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 40, 20, 40);
        leftPanel.add(versionLabel, gbc);
        
        JTextArea descArea = new JTextArea("Hệ thống quản lý doanh nghiệp\ntoàn diện và chuyên nghiệp");
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(new Color(230, 240, 255));
        descArea.setBackground(new Color(41, 98, 255));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 40, 20, 40);
        leftPanel.add(descArea, gbc);
        
        // Right panel - Login form
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.fill = GridBagConstraints.HORIZONTAL;
        loginGbc.insets = new Insets(8, 50, 8, 50);
        
        // Title
        JLabel titleLabel = new JLabel("Đăng nhập");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 33, 33));
        loginGbc.gridx = 0;
        loginGbc.gridy = 0;
        loginGbc.gridwidth = 2;
        loginGbc.insets = new Insets(30, 50, 10, 50);
        rightPanel.add(titleLabel, loginGbc);
        
        JLabel welcomeLabel = new JLabel("Vui lòng nhập thông tin đăng nhập");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(120, 120, 120));
        loginGbc.gridy = 1;
        loginGbc.insets = new Insets(0, 50, 25, 50);
        rightPanel.add(welcomeLabel, loginGbc);
        
        // Username
        loginGbc.gridy = 2;
        loginGbc.gridwidth = 1;
        loginGbc.insets = new Insets(8, 50, 3, 50);
        JLabel userLabel = new JLabel("Tên đăng nhập:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(new Color(70, 70, 70));
        rightPanel.add(userLabel, loginGbc);
        
        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        loginGbc.gridy = 3;
        loginGbc.insets = new Insets(3, 50, 8, 50);
        rightPanel.add(userField, loginGbc);
        
        // Password
        loginGbc.gridy = 4;
        loginGbc.insets = new Insets(8, 50, 3, 50);
        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLabel.setForeground(new Color(70, 70, 70));
        rightPanel.add(passLabel, loginGbc);
        
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        loginGbc.gridy = 5;
        loginGbc.insets = new Insets(3, 50, 8, 50);
        rightPanel.add(passField, loginGbc);
        
        // Remember & Forgot password panel
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBackground(Color.WHITE);
        
        JCheckBox rememberCheck = new JCheckBox("Ghi nhớ đăng nhập");
        rememberCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberCheck.setBackground(Color.WHITE);
        rememberCheck.setForeground(new Color(100, 100, 100));
        optionsPanel.add(rememberCheck, BorderLayout.WEST);
        
        JLabel forgotLabel = new JLabel("<html><u>Quên mật khẩu?</u></html>");
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotLabel.setForeground(new Color(41, 98, 255));
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsPanel.add(forgotLabel, BorderLayout.EAST);
        
        loginGbc.gridy = 6;
        loginGbc.insets = new Insets(5, 50, 8, 50);
        rightPanel.add(optionsPanel, loginGbc);
        
        // Login button
        JButton loginBtn = new JButton("ĐĂNG NHẬP");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(41, 98, 255));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(200, 40));
        loginGbc.gridy = 7;
        loginGbc.insets = new Insets(20, 50, 8, 50);
        rightPanel.add(loginBtn, loginGbc);
        
        // Footer
        JLabel footerLabel = new JLabel("© 2026 Management System. All rights reserved.");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(150, 150, 150));
        loginGbc.gridy = 8;
        loginGbc.insets = new Insets(20, 50, 20, 50);
        rightPanel.add(footerLabel, loginGbc);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginTieuChuan().setVisible(true);
        });
    }
}