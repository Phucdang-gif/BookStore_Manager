package GUI.model;

import GUI.util.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Sidebar extends JPanel {
    private JButton btnSelected; // Lưu nút đang được chọn

    // --- CẤU HÌNH KÍCH THƯỚC (Tùy chỉnh độ rộng tại đây) ---
    private final int SIDEBAR_WIDTH = 200; // Sidebar rộng hơn (300px) để nhìn sang trọng

    public Sidebar() {
        initComponents();
    }

    private void initComponents() {
        // Màu nền Sidebar (Xanh Navy đậm)
        setBackground(UIConstants.CYAN_BACKGROUND);
        setLayout(new BorderLayout());
        // 1. Thiết lập chiều rộng cố định cho Sidebar
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        // --- PHẦN MENU BUTTONS ---
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // Tạo các nút menu
        JButton btn1 = createMenuButton("QUẢN LÝ SÁCH", "/icon/book.svg");
        JButton btn2 = createMenuButton("DANH MỤC", "/icon/category.svg");
        JButton btn3 = createMenuButton("PHIẾU NHẬP", "/icon/import.svg");
        JButton btn4 = createMenuButton("PHIẾU XUẤT", "/icon/export.svg");

        menuPanel.add(btn1);
        menuPanel.add(Box.createVerticalStrut(10)); // Khoảng cách giữa các nút
        menuPanel.add(btn2);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btn3);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btn4);

        // Chọn mặc định nút đầu tiên
        setActiveButton(btn1);
        add(menuPanel, BorderLayout.CENTER);
    }

    // --- HÀM TẠO NÚT MENU ---
    private JButton createMenuButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(UIConstants.CYAN_BACKGROUND); // Cùng màu nền

        // Padding trong nút: Trên/Dưới 14px, Trái 40px (cho rộng)
        btn.setBorder(new EmptyBorder(14, 40, 14, 20));

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 60)); // Chiều rộng full, cao 60px
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sự kiện click
        btn.addActionListener(e -> setActiveButton(btn));
        return btn;
    }

    // --- HÀM XỬ LÝ HIGHLIGHT KHI CHỌN NÚT ---
    private void setActiveButton(JButton btn) {
        if (btnSelected != null) {
            // Trả lại trạng thái cũ cho nút trước đó
            btnSelected.setBackground(UIConstants.CYAN_BACKGROUND);
            btnSelected.setBorder(new EmptyBorder(14, 40, 14, 20));
        }
        btnSelected = btn;

        // Màu nền khi được chọn (Sáng hơn nền sidebar một chút)
        btnSelected.setBackground(UIConstants.YELLOW_BACKGROUND);

        // Tạo đường kẻ màu xanh bên trái (Indicator)
        btnSelected.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, new Color(179, 188, 204)), // Vạch 6px
                new EmptyBorder(14, 34, 14, 20) // Padding trái giảm đi 6px để bù vào vạch
        ));
    }
}