package GUI.model;

import GUI.components.RoundedBorderButton;
import GUI.util.ThemeColor;

import javax.swing.*;
import java.awt.*;

public class CategoryPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel container;

    // Header Panel chứa nút Back
    private JPanel headerBar;
    private JButton btnBack;
    private JLabel lblTitle;

    // Các màn hình con
    private CategoryDashboard dashboard;
    private AuthorPanel pnlAuthor;

    private MainPanel mainPanelContext; // Để gọi ngược lại setController cho Header chính

    public CategoryPanel(MainPanel mainPanel) {
        this.mainPanelContext = mainPanel;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        // 1. THANH ĐIỀU HƯỚNG (HEADER BAR) - Mặc định ẩn
        headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(Color.WHITE);
        headerBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerBar.setVisible(false); // Chỉ hiện khi vào trang con

        btnBack = new RoundedBorderButton(null, "GUI/icon/left-arrow.svg", ThemeColor.textMain, 10);
        btnBack.setPreferredSize(new Dimension(35, 35));
        btnBack.addActionListener(e -> backToDashboard());

        lblTitle = new JLabel("CHI TIẾT DANH MỤC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(ThemeColor.textMain);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel leftParams = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftParams.setOpaque(false);
        leftParams.add(btnBack);
        leftParams.add(lblTitle);

        headerBar.add(leftParams, BorderLayout.WEST);
        add(headerBar, BorderLayout.NORTH);

        // 2. CONTAINER CHÍNH (CARD LAYOUT)
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setBackground(Color.WHITE);

        // --- KHỞI TẠO CÁC PANEL CON ---

        // A. Dashboard (Truyền listener xử lý khi click vào nút grid)
        dashboard = new CategoryDashboard(e -> {
            String command = e.getActionCommand();
            switchPanel(command);
        });

        // B. Các panel quản lý
        pnlAuthor = new AuthorPanel();
        // pnlPublisher = new PublisherPanel();

        // Add vào Card
        container.add(dashboard, "DASHBOARD");
        container.add(pnlAuthor, "AUTHOR");
        // container.add(pnlPublisher, "PUBLISHER");

        add(container, BorderLayout.CENTER);
    }

    // --- LOGIC CHUYỂN ĐỔI ---

    public void switchPanel(String panelName) {
        // 1. Chuyển Card
        cardLayout.show(container, panelName);

        // 2. Cập nhật giao diện & Header Chính
        if (panelName.equals("DASHBOARD")) {
            headerBar.setVisible(false); // Ẩn nút Back
            mainPanelContext.setHeaderVisible(false);

        } else {
            headerBar.setVisible(true); // Hiện nút Back
            mainPanelContext.setHeaderVisible(true);
            // Cập nhật tiêu đề tương ứng
            if (panelName.equals("AUTHOR")) {
                lblTitle.setText("Quản lý Tác giả");
                // Quan trọng: Báo cho Header chính biết là đang dùng AuthorPanel
                mainPanelContext.getHeader().setController(pnlAuthor);
            } else if (panelName.equals("PUBLISHER")) {
                lblTitle.setText("Quản lý Nhà xuất bản");
                // mainPanelContext.getHeader().setController(pnlPublisher);
            }
        }
    }

    private void backToDashboard() {
        switchPanel("DASHBOARD");
    }

    // Hàm này được MainPanel gọi khi click vào sidebar "DANH MỤC"
    public void resetToDashboard() {
        backToDashboard();
    }
}