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
        // --- ĐỔI TÊN ĐỘNG THEO TÀI KHOẢN ĐĂNG NHẬP ---
        DTO.AccountDTO currentUser = config.SessionManager.getCurrentAccount();
        String displayName = (currentUser != null) ? currentUser.getUsername() : "Chưa đăng nhập";

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Hiển thị tên thật lên Sidebar
        UserProfilePanel userPanel = new UserProfilePanel(displayName, "hé lô "+displayName+" nhá");
        
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

        // --- DANH SÁCH CHỨC NĂNG (ĐÃ TÍCH HỢP PHÂN QUYỀN RAM) ---
        ArrayList<SidebarModel> items = new ArrayList<>();
        
        

        if (config.SessionManager.hasPermission(451, "Xem")) {
            items.add(new SidebarModel("QUẢN LÝ SÁCH", "GUI/icon/book.svg", "BOOK"));
        }
        if (config.SessionManager.hasPermission(455, "Xem")) {
            items.add(new SidebarModel("DANH MỤC", "GUI/icon/category.svg", "GROUP"));
        }
        if (config.SessionManager.hasPermission(456, "Xem")) {
            items.add(new SidebarModel("QL KHÁCH HÀNG", "GUI/icon/customer.svg", "CUSTOMER"));
        }
        if (config.SessionManager.hasPermission(454, "Xem")) {
            items.add(new SidebarModel("QL NHẬP HÀNG", "GUI/icon/import.svg", "IMPORT"));
        }
        if (config.SessionManager.hasPermission(453, "Xem")) {
            items.add(new SidebarModel("QL HÓA ĐƠN", "GUI/icon/invoice.svg", "INVOICE"));
        }
        if (config.SessionManager.hasPermission(457, "Xem")) {
            items.add(new SidebarModel("KHUYẾN MÃI", "GUI/icon/discount.svg", "DISCOUNT"));
        }
        if (config.SessionManager.hasPermission(452, "Xem")) {
            items.add(new SidebarModel("QL NHÂN VIÊN", "GUI/icon/employee.svg", "EMPLOYEE"));
        }
        if (config.SessionManager.hasPermission(458, "Xem")) {
            items.add(new SidebarModel("QL TÀI KHOẢN", "GUI/icon/account.svg", "ACCOUNT")); 
        }
        if (config.SessionManager.hasPermission(459, "Xem")) {
            items.add(new SidebarModel("PHÂN QUYỀN", "GUI/icon/role.svg", "PERMISSION_GROUP")); 
        }

        // Render ra giao diện
        for (SidebarModel item : items) {
            JButton btn = createSidebarButton(item);
            menuContainer.add(btn);
            menuContainer.add(Box.createVerticalStrut(5)); // Khoảng cách
            listButtons.add(btn);

            // Mặc định chọn nút đầu tiên xuất hiện
            if (btnSelected == null) {
                setActiveButton(btn);
            }
        }

        add(pnlHeader, BorderLayout.NORTH);
        add(menuContainer, BorderLayout.CENTER);
         // --- THÊM NÚT ĐĂNG XUẤT Ở DƯỚI CÙNG ---
        JButton btnLogout = new JButton("ĐĂNG XUẤT");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setIconTextGap(15);
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(true);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(new EmptyBorder(15, 25, 15, 20));
        
        // Đổi màu chữ sang đỏ cho nổi bật
        btnLogout.setForeground(new Color(220, 53, 69)); 
        
        // Nếu em có icon logout.svg thì dùng dòng dưới, không thì comment lại nhé
        // GUI.util.IconHelper.setIcon(btnLogout, "GUI/icon/logout.svg", 20, 20); 

        // Hiệu ứng Hover cho nút Đăng xuất
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(new Color(255, 235, 238)); // Nền đỏ nhạt khi di chuột
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(ThemeColor.bgPanel);
            }
        });

        // Bắt sự kiện Click để Đăng xuất
        btnLogout.addActionListener(e -> processLogout());

        // Đặt nút này ở dưới đáy (SOUTH) của Sidebar
        add(btnLogout, BorderLayout.SOUTH);
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
    // Hàm xử lý nghiệp vụ đăng xuất
    private void processLogout() {
        // 1. Hỏi lại cho chắc chắn
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", 
            "Xác nhận đăng xuất", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // 2. Xóa sạch dữ liệu tài khoản và quyền trên RAM
            config.SessionManager.logout();
            
            // 3. Đóng cửa sổ MainFrame hiện tại
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            
            // 4. Mở lại màn hình Login
            SwingUtilities.invokeLater(() -> new GUI.Login().setVisible(true));
        }
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