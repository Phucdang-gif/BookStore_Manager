package GUI.model;

import GUI.util.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class Sidebar extends JPanel {
    private JButton btnSelected;
    // Interface để giao tiếp với bên ngoài
    private ActionListener menuListener;

    private final int SIDEBAR_WIDTH = 220;

    public Sidebar() {
        initComponents();
    }

    public void setMenuListener(ActionListener listener) {
        this.menuListener = listener;
    }

    private void initComponents() {
        setBackground(UIConstants.CYAN_BACKGROUND);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // --- DANH SÁCH MENU ---
        // command string ("BOOK", "SALES"...) sẽ được gửi đi khi bấm
        JButton btnBook = createMenuButton("QUẢN LÝ SÁCH", "GUI/icon/book.svg", "BOOK");
        JButton btnSales = createMenuButton("BÁN HÀNG", "GUI/icon/product.svg", "SALES"); // Icon tạm
        JButton btnCategory = createMenuButton("DANH MỤC", "GUI/icon/category.svg", "CATEGORY");

        menuPanel.add(btnBook);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnSales);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnCategory);

        setActiveButton(btnBook);
        add(menuPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text, String iconPath, String command) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(UIConstants.CYAN_BACKGROUND);
        btn.setBorder(new EmptyBorder(14, 40, 14, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 60));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Quan trọng: Đặt ActionCommand để phân biệt nút nào được bấm
        btn.setActionCommand(command);

        btn.addActionListener(e -> {
            setActiveButton(btn);
            if (menuListener != null) {
                menuListener.actionPerformed(e); // Gửi sự kiện ra ngoài
            }
        });
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (btnSelected != null) {
            btnSelected.setBackground(UIConstants.CYAN_BACKGROUND);
            btnSelected.setBorder(new EmptyBorder(14, 40, 14, 20));
        }
        btnSelected = btn;
        btnSelected.setBackground(UIConstants.YELLOW_BACKGROUND);
        btnSelected.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, new Color(179, 188, 204)),
                new EmptyBorder(14, 34, 14, 20)));
    }
}