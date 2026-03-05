package GUI;

import GUI.util.ImageHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import GUI.components.RoundedBorderButton;
import GUI.util.ThemeColor;
import GUI.util.IconHelper;

public class Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JToggleButton btnShowHide;
    private RoundedBorderButton btnLogin;

    // Ảnh bên trái
    private BufferedImage sideImage;

    public Login() {
        initGUI();
        addEvents();
    }

    private void initGUI() {
        setTitle("BookStore System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // --- PHẦN BÊN TRÁI: CHỨA ẢNH ---
        sideImage = ImageHelper.readImage("BookStore_Manager\\src\\image\\bookshelf.jpg");
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (sideImage != null) {
                    // Vẽ ảnh full panel bên trái
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(sideImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(new Color(245, 245, 245)); // Màu xám nhẹ nếu ko có ảnh
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        add(leftPanel);

        // --- PHẦN BÊN PHẢI: FORM LOGIN ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE); // Nền trắng sạch sẽ
        add(rightPanel);

        // Thêm các thành phần vào bên phải
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 40, 5, 40);

        // Title
        JLabel lblTitle = new JLabel("HELLO!");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(33, 33, 33));
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy = 1;
        rightPanel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Welcome to Bookstore Management");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 30, 40);
        rightPanel.add(lblSub, gbc);

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 40, 5, 40);
        rightPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 40, 15, 40);

        rightPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 40, 5, 40);
        rightPanel.add(lblPass, gbc);

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setPreferredSize(new Dimension(0, 40)); // Chiều cao cố định cho khung

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));// Padding trái 10px, phải 0
        txtPassword.setBackground(Color.WHITE);
        styleTextField(txtPassword);

        btnShowHide = new JToggleButton();
        IconHelper.setIcon(btnShowHide, "GUI/icon/eye2.svg", 20, 20);
        btnShowHide.setContentAreaFilled(false); // Xóa nền
        btnShowHide.setFocusPainted(false); // Xóa viền focus
        btnShowHide.setBorderPainted(false); // Xóa viền nút
        btnShowHide.setPreferredSize(new Dimension(30, 20));
        btnShowHide.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ chuột

        // Sự kiện click vào mắt
        btnShowHide.addActionListener(e -> {
            if (btnShowHide.isSelected()) {
                txtPassword.setEchoChar((char) 0); // Hiện mật khẩu (ký tự rỗng)
            } else {
                txtPassword.setEchoChar('•'); // Ẩn mật khẩu (dấu chấm tròn)
            }
        });

        // Đặt layout cho JPasswordField để nhét nút vào bên trong
        passPanel.add(txtPassword, BorderLayout.CENTER);
        passPanel.add(btnShowHide, BorderLayout.EAST);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 40, 30, 40);
        rightPanel.add(passPanel, gbc);

        // Button
        btnLogin = new RoundedBorderButton("LOGIN", ThemeColor.ACCENT_COLOR, 30);
        btnLogin.setBackground(ThemeColor.ACCENT_COLOR);
        btnLogin.setForeground(ThemeColor.bgWhite);
        btnLogin.setPreferredSize(new Dimension(0, 45));

        // Hover btnLogin
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(ThemeColor.ACCENT_COLOR_DARK);
                btnLogin.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(ThemeColor.ACCENT_COLOR);
                btnLogin.repaint();
            }
        });
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 40, 0, 40);
        rightPanel.add(btnLogin, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 40)); // Chiều cao 40px
        field.setBackground(Color.WHITE);

        // Tạo border mặc định: Viền xám nhạt + Padding (khoảng cách chữ với viền)
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(5, 10, 5, 10)));

        // Thêm sự kiện: Khi bấm vào (Focus) thì đổi màu viền xanh
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                // Khi được chọn: Viền xanh dương, dày 2px
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 136, 229), 2),
                        // Giảm padding đi 1px để bù cho viền dày lên -> Không bị rung khung hình
                        new EmptyBorder(4, 9, 4, 9)));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Khi không chọn: Trả về viền xám nhạt, dày 1px
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        new EmptyBorder(5, 10, 5, 10)));
            }
        });
    }

    private void addEvents() {
        btnLogin.addActionListener(e -> performLogin());

        // Sự kiện Enter
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    performLogin();
            }
        };
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    txtPassword.requestFocus();
            }
        });
        txtPassword.addKeyListener(enterKey);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Logic đăng nhập giữ nguyên như cũ
        try {
            BUS.AccountBUS accountBUS = new BUS.AccountBUS();
            DTO.AccountDTO loggedInAcc = accountBUS.checkLogin(username, password);

            if (loggedInAcc != null) {
                BUS.PermissionDetailBUS permBUS = new BUS.PermissionDetailBUS();
                HashMap<Integer, String> userPerms = permBUS
                        .getAllPermissionsByGroupId(loggedInAcc.getPermissionGroupId());
                config.SessionManager.login(loggedInAcc, userPerms);

                this.dispose();
                SwingUtilities.invokeLater(() -> new GUI.MainFrame().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Login failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}