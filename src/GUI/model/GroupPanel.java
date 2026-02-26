package GUI.model;

import GUI.components.RoundedBorderButton;
import GUI.util.ThemeColor;

import javax.swing.*;

import BUS.AuthorBUS;
import BUS.BookBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;

import java.awt.*;

public class GroupPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel container;

    private JPanel headerBar;
    private JButton btnBack;
    private JLabel lblTitle;

    // Các màn hình con
    private GroupDashboard dashboard;
    private AuthorPanel pnlAuthor;
    private CategoryPanel pnlCategory;
    private PublisherPanel pnlPublisher;

    private MainPanel mainPanelContext;

    public GroupPanel(MainPanel mainPanel, BookBUS bookBUS, AuthorBUS authorBUS, CategoryBUS categoryBUS,
            PublisherBUS publisherBUS) {
        this.mainPanelContext = mainPanel;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initComponents(bookBUS, authorBUS, categoryBUS, publisherBUS);
    }

    private void initComponents(BookBUS bookBUS, AuthorBUS authorBUS, CategoryBUS categoryBUS,
            PublisherBUS publisherBUS) {
        // --- GIỮ NGUYÊN PHẦN HEADER BAR ---
        headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(Color.WHITE);
        headerBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerBar.setVisible(false);

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

        // --- CONTAINER CHÍNH ---
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setBackground(Color.WHITE);

        // A. Dashboard
        dashboard = new GroupDashboard(bookBUS, authorBUS, categoryBUS, publisherBUS, e -> {
            String command = e.getActionCommand();
            switchPanel(command);
        });

        // B. Các panel quản lý
        pnlAuthor = new AuthorPanel(authorBUS);
        pnlCategory = new CategoryPanel(categoryBUS);
        pnlPublisher = new PublisherPanel(publisherBUS);

        container.add(dashboard, "DASHBOARD");
        container.add(pnlAuthor, "AUTHOR");
        container.add(pnlCategory, "CATEGORY");
        container.add(pnlPublisher, "PUBLISHER");

        add(container, BorderLayout.CENTER);
    }

    public void switchPanel(String panelName) {
        cardLayout.show(container, panelName);

        if (panelName.equals("DASHBOARD")) {
            headerBar.setVisible(false);
            mainPanelContext.setHeaderVisible(false);

            dashboard.resetFilters();
        } else {
            headerBar.setVisible(true);
            mainPanelContext.setHeaderVisible(true);
            if (panelName.equals("AUTHOR")) {
                lblTitle.setText("Quản lý Tác giả");
                mainPanelContext.getHeader().setController(pnlAuthor);

            } else if (panelName.equals("CATEGORY")) {
                lblTitle.setText("Quản lý thể loại");
                mainPanelContext.getHeader().setController(pnlCategory);

            } else if (panelName.equals("PUBLISHER")) {
                lblTitle.setText("Quản lý nhà xuất bản");
                mainPanelContext.getHeader().setController(pnlPublisher);
            }
        }
    }

    private void backToDashboard() {
        switchPanel("DASHBOARD");
    }

    public void resetToDashboard() {
        backToDashboard();
    }
}