package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FilterPanel extends JPanel {
    private final Color BG_COLOR = new Color(248, 249, 250);
    private final Color CARD_BG = Color.WHITE;
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color BORDER_COLOR = new Color(222, 226, 230);
    private final Color PRIMARY_COLOR = new Color(13, 110, 253);
    private final Color ACCENT_COLOR = new Color(108, 117, 125);

    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public FilterPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(280, 0));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        headerPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblTitle = new JLabel("BỘ LỌC");
        lblTitle.setFont(TITLE_FONT);
        lblTitle.setForeground(TEXT_COLOR);

        JLabel lblCollapse = new JLabel("▲");
        lblCollapse.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblCollapse.setForeground(ACCENT_COLOR);
        lblCollapse.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblCollapse, BorderLayout.EAST);

        // Content với scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        contentPanel.setBorder(new EmptyBorder(0, 16, 16, 16));

        // Tìm kiếm chung
        contentPanel.add(createSectionLabel("Tìm kiếm chung"));
        contentPanel.add(createSearchField());
        contentPanel.add(Box.createVerticalStrut(16));

        // Nhà xuất bản
        contentPanel.add(createSectionLabel("Nhà xuất bản"));
        contentPanel.add(createComboBox(new String[] { "Tất cả", "NXB Kim Đồng", "NXB Trẻ", "Nhã Nam" }));
        contentPanel.add(Box.createVerticalStrut(16));

        // Danh mục
        contentPanel.add(createSectionLabel("Danh mục"));
        contentPanel.add(createComboBox(new String[] { "Tất cả", "Văn học", "Kinh tế", "Thiếu nhi" }));
        contentPanel.add(Box.createVerticalStrut(16));

        // Tác giả
        contentPanel.add(createSectionLabel("Tác giả"));
        contentPanel.add(createComboBox(new String[] { "Tất cả", "Nguyễn Nhật Ánh", "J.K. Rowling" }));
        contentPanel.add(Box.createVerticalStrut(16));

        // Ngôn ngữ
        contentPanel.add(createSectionLabel("Ngôn ngữ"));
        contentPanel.add(createComboBox(new String[] { "Tất cả", "Tiếng Việt", "Tiếng Anh" }));
        contentPanel.add(Box.createVerticalStrut(16));

        // Năm xuất bản
        contentPanel.add(createSectionLabel("Năm xuất bản"));
        contentPanel.add(createYearRange());
        contentPanel.add(Box.createVerticalStrut(16));

        // Loại bìa
        contentPanel.add(createSectionLabel("Loại bìa"));
        contentPanel.add(createCoverTypePanel());
        contentPanel.add(Box.createVerticalStrut(16));

        // Trạng thái kho
        contentPanel.add(createSectionLabel("Trạng thái"));
        contentPanel.add(createStatusPanel());
        contentPanel.add(Box.createVerticalStrut(16));

        // Tồn kho
        contentPanel.add(createSectionLabel("Tồn kho"));
        contentPanel.add(createRangeSlider(0, 500, "quyển"));
        contentPanel.add(Box.createVerticalStrut(16));

        // Giá bán
        contentPanel.add(createSectionLabel("Giá bán"));
        contentPanel.add(createRangeSlider(0, 1000000, "đ"));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Footer buttons
        JPanel footerPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        footerPanel.setBackground(CARD_BG);
        footerPanel.setBorder(new EmptyBorder(12, 16, 16, 16));

        JButton btnApply = createButton("Áp dụng", PRIMARY_COLOR, Color.WHITE, true);
        JButton btnReset = createButton("Đặt lại", CARD_BG, ACCENT_COLOR, false);

        footerPanel.add(btnApply);
        footerPanel.add(btnReset);

        // Assembly
        JPanel mainCard = new JPanel(new BorderLayout());
        mainCard.setBackground(CARD_BG);
        mainCard.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        mainCard.add(headerPanel, BorderLayout.NORTH);
        mainCard.add(scrollPane, BorderLayout.CENTER);
        mainCard.add(footerPanel, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);
        setBorder(new EmptyBorder(0, 0, 10, 10));
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(ACCENT_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        return lbl;
    }

    private JTextField createSearchField() {
        JTextField txt = new JTextField("Nhập từ khóa...");
        txt.setFont(INPUT_FONT);
        txt.setForeground(new Color(173, 181, 189));
        txt.setPreferredSize(new Dimension(100, 36));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)));
        return txt;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cbo = new JComboBox<>(items);
        cbo.setFont(INPUT_FONT);
        cbo.setBackground(Color.WHITE);
        cbo.setPreferredSize(new Dimension(100, 36));
        cbo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cbo;
    }

    private JPanel createYearRange() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JTextField txtFrom = createRangeField("2000");
        JTextField txtTo = createRangeField("2024");
        JLabel lblSep = new JLabel("~");
        lblSep.setFont(INPUT_FONT);
        lblSep.setBorder(new EmptyBorder(0, 8, 0, 8));

        panel.add(txtFrom);
        panel.add(lblSep);
        panel.add(txtTo);
        return panel;
    }

    private JTextField createRangeField(String value) {
        JTextField txt = new JTextField(value);
        txt.setFont(INPUT_FONT);
        txt.setHorizontalAlignment(JTextField.CENTER);
        txt.setPreferredSize(new Dimension(80, 36));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 8, 6, 8)));
        return txt;
    }

    private JPanel createCoverTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        ButtonGroup group = new ButtonGroup();
        JRadioButton rdoSoft = createRadioButton("Bìa mềm", group, true);
        JRadioButton rdoHard = createRadioButton("Bìa cứng", group, false);

        panel.add(rdoSoft);
        panel.add(Box.createHorizontalStrut(12));
        panel.add(rdoHard);
        return panel;
    }

    private JRadioButton createRadioButton(String text, ButtonGroup group, boolean selected) {
        JRadioButton rdo = new JRadioButton(text);
        rdo.setFont(INPUT_FONT);
        rdo.setBackground(CARD_BG);
        rdo.setFocusPainted(false);
        rdo.setSelected(selected);
        group.add(rdo);
        return rdo;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setBackground(CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        panel.add(createCheckBox("Tất cả", true));
        panel.add(createCheckBox("Sẵn hàng", false));
        panel.add(createCheckBox("Hết hàng", false));
        panel.add(createCheckBox("Sắp hết", false));
        return panel;
    }

    private JCheckBox createCheckBox(String text, boolean selected) {
        JCheckBox chk = new JCheckBox(text);
        chk.setFont(INPUT_FONT);
        chk.setBackground(CARD_BG);
        chk.setFocusPainted(false);
        chk.setSelected(selected);
        return chk;
    }

    private JPanel createRangeSlider(int min, int max, String unit) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JSlider slider = new JSlider(min, max);
        slider.setBackground(CARD_BG);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);

        JPanel valPanel = new JPanel(new BorderLayout());
        valPanel.setBackground(CARD_BG);

        String minStr = min >= 1000 ? String.format("%,d", min) : String.valueOf(min);
        String maxStr = max >= 1000 ? String.format("%,d", max) : String.valueOf(max);

        JLabel lblMin = new JLabel(minStr + unit);
        JLabel lblMax = new JLabel(maxStr + unit);
        lblMin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMax.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMin.setForeground(ACCENT_COLOR);
        lblMax.setForeground(ACCENT_COLOR);

        valPanel.add(lblMin, BorderLayout.WEST);
        valPanel.add(lblMax, BorderLayout.EAST);

        panel.add(slider, BorderLayout.CENTER);
        panel.add(valPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createButton(String text, Color bg, Color fg, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));

        if (!isPrimary) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(9, 16, 9, 16)));
        }

        return btn;
    }
}