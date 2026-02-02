package GUI.model;

import GUI.components.UserProfilePanel; // Import cái panel profile
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class Sidebar extends JPanel {
    private JButton btnSelected;
    private ActionListener menuListener;
    private final int SIDEBAR_WIDTH = 240; // Rộng hơn chút cho thoáng

    public Sidebar() {
        initComponents();
        this.setBackground(Color.decode("#242526"));
    }

    public void setMenuListener(ActionListener listener) {
        this.menuListener = listener;
    }

    private void initComponents() {
        // QUAN TRỌNG: Đổi nền thành TRẮNG để tệp với Profile
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        // Tạo đường viền mờ bên phải để ngăn cách với nội dung chính
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(230, 230, 230)));

        // --- 1. PHẦN USER PROFILE (Đưa vào đây) ---
        // Profile nằm trên cùng (NORTH)
        UserProfilePanel userPanel = new UserProfilePanel("Đặng Hoàng Phúc", "Quản lý kho");

        // --- 2. PHẦN MENU (CENTER) ---
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false); // Trong suốt để ăn theo nền trắng Sidebar
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Các nút Menu
        JButton btnBook = createMenuButton("QUẢN LÝ SÁCH", "GUI/icon/book.svg", "BOOK");
        JButton btnSales = createMenuButton("BÁN HÀNG", "GUI/icon/product.svg", "SALES");
        JButton btnCategory = createMenuButton("DANH MỤC", "GUI/icon/category.svg", "CATEGORY");

        menuPanel.add(btnBook);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnSales);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnCategory);

        menuPanel.add(Box.createVerticalGlue()); // Đẩy menu lên trên

        add(menuPanel, BorderLayout.CENTER);
        add(userPanel, BorderLayout.NORTH);
        setActiveButton(btnBook); // Chọn mặc định

    }

    private JButton createMenuButton(String text, String iconPath, String command) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Màu sắc: Chữ xám đậm, Nền trắng (khi chưa chọn)
        btn.setForeground(new Color(80, 80, 80));
        btn.setBackground(Color.WHITE);

        btn.setBorder(new EmptyBorder(12, 25, 12, 20)); // Padding trái 25
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 50));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setActionCommand(command);

        btn.addActionListener(e -> {
            setActiveButton(btn);
            if (menuListener != null) {
                menuListener.actionPerformed(e);
            }
        });
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (btnSelected != null) {
            // Reset nút cũ về màu trắng
            btnSelected.setBackground(Color.WHITE);
            btnSelected.setForeground(new Color(80, 80, 80));
            // Xóa border đánh dấu
            btnSelected.setBorder(new EmptyBorder(12, 25, 12, 20));
        }
        btnSelected = btn;

        // Style nút được chọn: Nền xanh nhạt, Chữ xanh dương đậm
        btnSelected.setBackground(new Color(235, 245, 255));
        btnSelected.setForeground(new Color(0, 120, 215));

        // Thêm vạch xanh bên trái đánh dấu
        btnSelected.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(0, 120, 215)),
                new EmptyBorder(12, 20, 12, 20)));
    }
}