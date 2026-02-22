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
    private JButton btnToggle; // nút ẩn hiện

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
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        UserProfilePanel userPanel = new UserProfilePanel("Đặng Hoàng Phúc", "Quản lý kho");
        btnToggle = new JButton();
        IconHelper.setIcon(btnToggle, "GUI/icon/menu.svg", 27, 27);
        btnToggle.setPreferredSize(new Dimension(40, 40));
        btnToggle.setBorder(new EmptyBorder(5, 5, 5, 5));
        btnToggle.setBackground(ThemeColor.bgWhite);
        // Add vào Header Panel
        pnlHeader.add(userPanel, BorderLayout.CENTER);
        pnlHeader.add(btnToggle, BorderLayout.EAST);

        // B. MENU LIST (Dùng mảng để render)
        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(10, 0, 0, 0));

        // --- DANH SÁCH CHỨC NĂNG (SAU NÀY LOAD TỪ DB HOẶC CHECK QUYỀN Ở ĐÂY) ---
        ArrayList<SidebarModel> items = new ArrayList<>();
        items.add(new SidebarModel("QUẢN LÝ SÁCH", "GUI/icon/book.svg", "BOOK"));
        items.add(new SidebarModel("DANH MỤC", "GUI/icon/category.svg", "GROUP"));
        items.add(new SidebarModel("QL KHÁCH HÀNG", "GUI/icon/customer.svg", "CUSTOMER"));
        items.add(new SidebarModel("QL NHẬP HÀNG", "GUI/icon/import.svg", "IMPORT"));
        items.add(new SidebarModel("QL HÓA ĐƠN", "GUI/icon/invoice.svg", "INVOICE"));
        items.add(new SidebarModel("QL NHÂN VIÊN", "GUI/icon/employee.svg", "EMPLOYEE"));
        items.add(new SidebarModel("QL TÀI KHOẢN", "GUI/icon/account.svg", "ACCOUNT")); 
        items.add(new SidebarModel("PHÂN QUYỀN", "GUI/icon/role.svg", "PERMISSION_GROUP")); 
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

        add(pnlHeader, BorderLayout.NORTH);
        add(menuContainer, BorderLayout.CENTER);
    }

    public void addToggleEvent(ActionListener event) {
        btnToggle.addActionListener(event);
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

    public boolean isOpen() {
        return this.isVisible();
    }

    public void toggle() {
        // Đảo ngược trạng thái hiển thị
        boolean isShow = !this.isVisible();
        this.setVisible(isShow);

        // Nếu muốn mượt hơn thì dùng Timer để thay đổi width (nâng cao),
        // nhưng setVisible là cách nhanh và ổn định nhất cho BorderLayout.
        this.revalidate();
        this.repaint();
    }
}