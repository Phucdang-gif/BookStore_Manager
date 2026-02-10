package GUI.dialog.book;

import javax.swing.*;
import javax.swing.border.*;

import DTO.AuthorDTO;

import java.awt.*;
import static GUI.dialog.book.BookDialogStyles.*;
import GUI.util.ThemeColor;

public class BookDialogView extends JPanel {
    // --- COMPONENTS ---
    public JTextField txtTitle, txtIsbn, txtYear, txtPage, txtPriceImport, txtPriceExport, txtQuantity, txtLanguage,
            txtMinStock;
    public JComboBox<String> cbCategory, cbPublisher, cbStatus, cbCoverType;

    // UI TÁC GIẢ MỚI
    public JTextField txtAuthorSearch;
    public JButton btnAuthorAdd;
    public JPopupMenu popupAuthorSuggestions;
    public JList<AuthorDTO> listAuthorSuggestions;
    public JPanel pnlAuthorTags;
    public JLabel lblAddNewAuthor;

    public JLabel lblImagePreview, lblTitle;
    public JButton btnUpload, btnSave, btnCancel;

    public BookDialogView() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createLineBorder(ThemeColor.borderColor, 2));
        initComponents();
    }

    private void initComponents() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PRIMARY_COLOR);
        p.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblTitle = new JLabel("Chi Tiết Sách");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Thông tin chi tiết sản phẩm trong hệ thống");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(230, 230, 230));

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblSub, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        p.setBackground(BG_COLOR);
        p.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnCancel = new JButton("Đóng");
        btnSave = new JButton("Lưu thông tin"); // Vẫn giữ UI nhưng sẽ ẩn đi

        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);

        btnSave.setPreferredSize(new Dimension(140, 35));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setFocusPainted(false);

        p.add(btnCancel);
        p.add(btnSave);
        return p;
    }

    private JScrollPane createMainContent() {
        JPanel pnlMain = new JPanel(new BorderLayout(20, 0));
        pnlMain.setBackground(BG_COLOR);
        pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));

        pnlMain.add(createImagePanel(), BorderLayout.WEST);
        pnlMain.add(createFormPanel(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(pnlMain);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel createImagePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(280, 0));

        JPanel pContent = new JPanel(new BorderLayout(0, 15));
        pContent.setOpaque(false);

        lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImagePreview.setOpaque(true);
        lblImagePreview.setBackground(new Color(235, 235, 235));
        lblImagePreview.setPreferredSize(new Dimension(250, 360));
        lblImagePreview.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 1.5f, 6.0f, 3.0f, true));

        btnUpload = new JButton("Tải ảnh lên");
        btnUpload.setBackground(Color.WHITE);
        btnUpload.setFocusPainted(false);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pBtn.setOpaque(false);
        pBtn.add(btnUpload);

        pContent.add(lblImagePreview, BorderLayout.CENTER);
        pContent.add(pBtn, BorderLayout.SOUTH);

        p.add(createSection("4. Ảnh bìa (Image)", pContent), BorderLayout.CENTER);
        return p;
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        p.add(createSection("1. Thông tin cơ bản", createBasicInfoForm()), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 10);
        p.add(createSection("2. Chi tiết sách", createDetailForm()), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        p.add(createSection("3. Kho hàng & Giá", createPriceStockForm()), gbc);

        return p;
    }

    private JPanel createBasicInfoForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        txtTitle = new JTextField();
        txtIsbn = new JTextField();
        cbCategory = new JComboBox<>();
        cbPublisher = new JComboBox<>();

        styleControl(txtTitle);
        styleControl(txtIsbn);
        styleControl(cbCategory);
        styleControl(cbPublisher);

        p.add(createRow("Tên sách (*)", txtTitle));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("ISBN (*)", txtIsbn));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("Danh mục", cbCategory));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("Nhà xuất bản", cbPublisher));
        return p;
    }

    private JPanel createDetailForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        // --- GIAO DIỆN TÁC GIẢ SEARCH & TAG ---
        JLabel lblAuth = new JLabel("Tác giả:");
        lblAuth.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel pSearchBox = new JPanel(new BorderLayout(5, 0));
        pSearchBox.setOpaque(false);
        txtAuthorSearch = new JTextField();
        btnAuthorAdd = new JButton("+");
        btnAuthorAdd.setPreferredSize(new Dimension(40, 30));
        pSearchBox.add(txtAuthorSearch, BorderLayout.CENTER);
        pSearchBox.add(btnAuthorAdd, BorderLayout.EAST);

        popupAuthorSuggestions = new JPopupMenu();
        listAuthorSuggestions = new JList<>();
        popupAuthorSuggestions.add(new JScrollPane(listAuthorSuggestions));

        pnlAuthorTags = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlAuthorTags.setBackground(new Color(245, 245, 245));
        pnlAuthorTags.setBorder(new LineBorder(BORDER_COLOR));
        JScrollPane scrollTags = new JScrollPane(pnlAuthorTags);
        scrollTags.setPreferredSize(new Dimension(200, 70));
        scrollTags.setBorder(null);

        lblAddNewAuthor = new JLabel("<html><u>Thêm tác giả mới...</u></html>");
        lblAddNewAuthor.setForeground(new Color(0, 102, 204));
        lblAddNewAuthor.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel pAuthContainer = new JPanel(new BorderLayout(5, 5));
        pAuthContainer.setOpaque(false);
        pAuthContainer.add(lblAuth, BorderLayout.NORTH);

        JPanel pAuthBody = new JPanel(new BorderLayout(0, 5));
        pAuthBody.setOpaque(false);
        pAuthBody.add(pSearchBox, BorderLayout.NORTH);
        pAuthBody.add(scrollTags, BorderLayout.CENTER);
        pAuthBody.add(lblAddNewAuthor, BorderLayout.SOUTH);
        pAuthContainer.add(pAuthBody, BorderLayout.CENTER);

        p.add(pAuthContainer);
        // -------------------------------------

        p.add(Box.createVerticalStrut(12));
        txtYear = new JTextField();
        txtPage = new JTextField();
        styleControl(txtYear);
        styleControl(txtPage);
        p.add(createDoubleRow("Năm XB", txtYear, "Số trang", txtPage));

        p.add(Box.createVerticalStrut(12));
        cbCoverType = new JComboBox<>(new String[] { "Bìa mềm", "Bìa cứng", "Ebook" });
        styleControl(cbCoverType);
        p.add(createRow("Loại bìa", cbCoverType));
        return p;
    }

    private JPanel createPriceStockForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        txtPriceImport = new JTextField();
        txtPriceExport = new JTextField();
        txtQuantity = new JTextField("0");
        cbStatus = new JComboBox<>(new String[] { "Còn hàng", "Hết hàng", "Ngừng kinh doanh" });
        txtLanguage = new JTextField();
        txtMinStock = new JTextField("0");

        styleControl(txtPriceImport);
        styleControl(txtPriceExport);
        styleControl(txtQuantity);
        styleControl(cbStatus);
        styleControl(txtLanguage);
        styleControl(txtMinStock);

        p.add(createRow("Giá nhập", txtPriceImport));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("Giá bán (*)", txtPriceExport));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("Tồn kho", txtQuantity));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("Trạng thái", cbStatus));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("Ngôn ngữ", txtLanguage));
        p.add(Box.createVerticalStrut(12));
        p.add(createRow("Số lượng tối thiểu", txtMinStock));
        return p;
    }

    // Helpers (giữ nguyên)
    private JPanel createSection(String title, Component content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        TitledBorder border = BorderFactory.createTitledBorder(new LineBorder(BORDER_COLOR), title);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        p.setBorder(new CompoundBorder(border, new EmptyBorder(15, 15, 15, 15)));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel createRow(String label, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(LABEL_WIDTH, 30));
        p.add(lbl, BorderLayout.WEST);
        p.add(input, BorderLayout.CENTER);
        return p;
    }

    private JPanel createDoubleRow(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl1 = new JLabel(l1);
        lbl1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl1.setPreferredSize(new Dimension(60, 30));
        p.add(lbl1, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        p.add(c1, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        p.add(Box.createHorizontalStrut(15), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0;
        JLabel lbl2 = new JLabel(l2);
        lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl2.setPreferredSize(new Dimension(60, 30));
        p.add(lbl2, gbc);
        gbc.gridx = 4;
        gbc.weightx = 0.5;
        p.add(c2, gbc);
        return p;
    }
}