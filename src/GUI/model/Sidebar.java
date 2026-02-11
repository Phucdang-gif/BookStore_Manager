package GUI.model;

import GUI.components.UserProfilePanel;
import GUI.util.IconHelper;
import GUI.util.ThemeColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Sidebar extends JPanel {
    private ActionListener menuListener;
    private final int SIDEBAR_WIDTH = 240;
    private JPanel menuContainer;
    private ArrayList<JButton> listButtons = new ArrayList<>(); // Lưu để reset style
    private JButton btnSelected; // Nút đang active

    public Sidebar() {
        initStyle();
        initComponents();
    }

    private void initStyle() {
        setBackground(ThemeColor.bgPanel);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeColor.borderColor));
    }

    public void setMenuListener(ActionListener listener) {
        this.menuListener = listener;
    }

    private void initComponents() {
        // A. USER PROFILE
        UserProfilePanel userPanel = new UserProfilePanel("Đặng Hoàng Phúc", "Quản lý kho");

        // B. MENU LIST (Dùng mảng để render)
        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(10, 0, 0, 0));

        // --- DANH SÁCH CHỨC NĂNG (SAU NÀY LOAD TỪ DB HOẶC CHECK QUYỀN Ở ĐÂY) ---
        ArrayList<SidebarModel> items = new ArrayList<>();
        items.add(new SidebarModel("QUẢN LÝ SÁCH", "GUI/icon/book.svg", "BOOK"));
        items.add(new SidebarModel("DANH MỤC", "GUI/icon/category.svg", "GROUP"));
        // items.add(new SidebarItem("THỐNG KÊ", "GUI/icon/chart.svg", "STATS"));

        // Render ra giao diện
        for (SidebarModel item : items) {
            JButton btn = createSidebarButton(item);
            menuContainer.add(btn);
            menuContainer.add(Box.createVerticalStrut(5)); // Khoảng cách
            listButtons.add(btn);

            // Mặc định chọn nút đầu tiên
            if (btnSelected == null) {
                setActiveButton(btn);
            }
        }

        add(userPanel, BorderLayout.NORTH);
        add(menuContainer, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(SidebarModel item) {
        JButton btn = new JButton(item.getTitle());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 25, 12, 20));

        // Icon
        IconHelper.setIcon(btn, item.getIconPath(), 20, 20);

        // Action
        btn.setActionCommand(item.getCommand());
        btn.addActionListener(e -> {
            setActiveButton(btn);
            if (menuListener != null) {
                menuListener.actionPerformed(e);
            }
        });

        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != btnSelected)
                    btn.setBackground(new Color(240, 240, 240));
            }

            public void mouseExited(MouseEvent e) {
                if (btn != btnSelected)
                    btn.setBackground(ThemeColor.bgPanel);
            }
        });

        return btn;
    }

    private void setActiveButton(JButton btn) {
        // Reset nút cũ
        if (btnSelected != null) {
            btnSelected.setBackground(ThemeColor.bgPanel);
            btnSelected.setForeground(ThemeColor.textMain);
            btnSelected.setBorder(new EmptyBorder(12, 25, 12, 20));
        }
        // Active nút mới
        btnSelected = btn;
        btnSelected.setBackground(ThemeColor.btnActiveBg); // Màu nền khi chọn
        btnSelected.setForeground(ThemeColor.btnActiveText); // Màu chữ khi chọn
        // Border xanh bên trái
        btnSelected.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, ThemeColor.btnActiveText),
                new EmptyBorder(12, 20, 12, 20)));
    }
}