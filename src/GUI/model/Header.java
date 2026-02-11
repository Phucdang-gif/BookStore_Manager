package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.ArrayList;
import GUI.components.ActionButton;
import GUI.components.SearchTextField;
import GUI.components.ToolBarPanel;
import GUI.components.RoundedBorderButton;
import GUI.util.ThemeColor;

public class Header extends JPanel {
    private ToolBarPanel toolBar;
    private SearchTextField txtSearch;
    private ActionButton btnRefresh;
    private FeatureControllerInterface currentController;

    public Header() {
        this.setLayout(new BorderLayout());
        initStyle();
        initComponents();
        initEvents();
    }

    public void setController(FeatureControllerInterface controller) {
        this.currentController = controller;
        txtSearch.setText("");
        if (controller != null) {
            boolean[] config = controller.getButtonConfig();
            toolBar.setButtonVisible(config);

            txtSearch.setVisible(controller.hasSearch());
            btnRefresh.setVisible(controller.hasRefresh());
        } else {
            // false hết
            toolBar.setButtonVisible(new boolean[6]);
            txtSearch.setVisible(false);
            btnRefresh.setVisible(false);
        }
    }

    private void initComponents() {
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, ThemeColor.borderColor));
        ArrayList<ButtonModel> listButtons = new ArrayList<>();
        listButtons.add(new ButtonModel("THÊM", "GUI/icon/add.svg", "ADD"));
        listButtons.add(new ButtonModel("SỬA", "GUI/icon/edit.svg", "EDIT"));
        listButtons.add(new ButtonModel("XÓA", "GUI/icon/delete.svg", "DELETE"));
        listButtons.add(new ButtonModel("CHI TIẾT", "GUI/icon/detail.svg", "DETAIL"));
        listButtons.add(new ButtonModel("XUẤT EXCEL", "GUI/icon/export_excel.svg", "EXPORT"));
        listButtons.add(new ButtonModel("NHẬP EXCEL", "GUI/icon/import_excel.svg", "IMPORT"));

        toolBar = new ToolBarPanel(listButtons);
        btnRefresh = new RoundedBorderButton(
                "LÀM MỚI",
                "GUI/icon/refresh.svg",
                ThemeColor.textMain, // Màu accent chủ đạo
                20 // Độ bo góc
        );

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        centerPanel.setOpaque(false);
        txtSearch = new SearchTextField();
        centerPanel.add(txtSearch);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        rightPanel.setOpaque(false); // Trong suốt

        rightPanel.add(btnRefresh);
        add(toolBar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setBorder(new EmptyBorder(0, 0, 0, 20)); // Padding 2 bên
    }

    private void initStyle() {
        // 1. Set nền trắng cho toàn bộ Header
        this.setOpaque(true);
        this.setBackground(ThemeColor.bgWhite);
        this.setPreferredSize(new Dimension(0, 80));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.borderColor));
    }

    private void initEvents() {
        toolBar.initEvent(e -> {
            if (currentController == null)
                return;
            String command = e.getActionCommand();
            switch (command) {
                case "ADD":
                    currentController.onAdd();
                    break;
                case "EDIT":
                    currentController.onEdit();
                    break;
                case "DELETE":
                    currentController.onDelete();
                    break;
                case "DETAIL":
                    currentController.onDetail();
                    break;
                case "EXPORT":
                    currentController.onExportExcel();
                    break;
                case "IMPORT":
                    currentController.onImportExcel();
                    break;
            }
        });

        // --- B. XỬ LÝ TÌM KIẾM
        txtSearch.getBtnSearch().addActionListener(e -> {
            if (currentController != null)
                currentController.onSearch(txtSearch.getText());
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (currentController != null)
                        currentController.onSearch(txtSearch.getText());
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            if (currentController != null)
                currentController.onRefresh();
        });

    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (ThemeColor.bgPanel != null) {
            setBackground(ThemeColor.bgPanel);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.borderColor),
                    new EmptyBorder(0, 20, 0, 20)));
        }
    }
}