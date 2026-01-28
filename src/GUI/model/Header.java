package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import BUS.BookBUS;
import DTO.BookDTO;

import java.awt.*;

import GUI.components.ActionButton;
import GUI.util.IconHelper;
import GUI.components.SearchTextField;
import java.util.ArrayList;

import GUI.dialog.book.BookDialog;
import GUI.dialog.book.DialogMode;

public class Header extends JPanel {
    private JButton btnAdd, btnEdit, btnDelete, btnDetail, btnExportExcel;
    private SearchTextField txtSearch;
    private final int AVATAR_SIZE = 70;
    private JPanel toolBar;
    private BookTablePanel panelTable;
    private BookBUS bookBUS;

    public Header() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.bookBUS = new BookBUS();
        setPreferredSize(new Dimension(0, 100));
        initComponents();
        setupLayout();
        initEvents();
    }

    private void initComponents() {
        toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolBar.setBackground(Color.LIGHT_GRAY);
        ArrayList<ButtonModel> dsChucNang = new ArrayList<>();
        dsChucNang.add(new ButtonModel("THÊM", "GUI/icon/add.svg", "ADD"));
        dsChucNang.add(new ButtonModel("SỬA", "GUI/icon/edit.svg", "EDIT"));
        dsChucNang.add(new ButtonModel("XÓA", "GUI/icon/delete.svg", "DELETE"));
        dsChucNang.add(new ButtonModel("CHI TIẾT", "GUI/icon/detail.svg", "READ"));
        dsChucNang.add(new ButtonModel("XUẤT EXCEL", "GUI/icon/export_excel.svg", "EXPORT"));
        for (ButtonModel chucnang : dsChucNang) {
            JButton btn = createToolBar(chucnang);
            toolBar.add(btn);
            if (chucnang.getActionCommand().equals("ADD"))
                btnAdd = btn;
            if (chucnang.getActionCommand().equals("EDIT"))
                btnEdit = btn;
            if (chucnang.getActionCommand().equals("DELETE"))
                btnDelete = btn;
            if (chucnang.getActionCommand().equals("READ"))
                btnDetail = btn;
            if (chucnang.getActionCommand().equals("EXPORT"))
                btnExportExcel = btn;
        }
        txtSearch = new SearchTextField();

    }

    public void initEvents() {
        // --- 1. CHỨC NĂNG THÊM ---
        btnAdd.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Truyền null vì thêm mới chưa có sách, Mode là ADD
            BookDialog dialog = new BookDialog(parent, null, DialogMode.ADD);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                panelTable.refreshTable();
            }
        });
        // --- 2. CHỨC NĂNG XEM CHI TIẾT ---
        this.getBtnDetail().addActionListener(e -> {
            int selectedId = panelTable.getSelectedBookId();
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách để xem!");
                return;
            }

            // Gọi BUS để lấy thông tin chi tiết (bao gồm cả tác giả...)
            BookDTO fullInfo = bookBUS.getBookDetails(selectedId);
            if (fullInfo != null) {
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                BookDialog dialog = new BookDialog(parent, fullInfo, DialogMode.READ);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sách!");
            }
        });

        // --- 3. CHỨC NĂNG SỬA ---
        btnEdit.addActionListener(e -> {
            int selectedId = panelTable.getSelectedBookId();
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần sửa!");
                return;
            }
            BookDTO fullInfo = bookBUS.getBookDetails(selectedId);

            if (fullInfo != null) {
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                BookDialog dialog = new BookDialog(parent, fullInfo, DialogMode.EDIT);
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    panelTable.refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sách!");
            }
        });
        // ... (Code nút Thêm, Sửa giữ nguyên) ...

        // --- XỬ LÝ NÚT XÓA ---
        if (btnDelete != null) {
            btnDelete.addActionListener(e -> {
                // 1. Kiểm tra xem người dùng đã chọn dòng nào chưa
                int selectedId = panelTable.getSelectedBookId();
                if (selectedId == -1) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!");
                    return;
                }
                // 2. Hỏi xác nhận (Tránh lỡ tay bấm nhầm)
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc chắn muốn xóa sách có ID: " + selectedId
                                + " không?\nHành động này không thể hoàn tác!",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 3. Gọi BUS để xóa
                    // nếu sản phẩm này có hóa đơn hoặc phiếu nhập thì cách xóa này database sẽ
                    // không chịu xóa
                    // Thay vì gọi deleteBook, ta gọi updateStatus
                    // Bạn cần viết thêm hàm updateStatus trong BUS/DAO nếu muốn làm cách này
                    // Ví dụ: bookBUS.updateStatus(selectedId, "SUSPENDED");
                    boolean result = bookBUS.deleteBook(selectedId);
                    if (result) {
                        JOptionPane.showMessageDialog(this, "Xóa thành công!");
                        panelTable.refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Xóa thất bại! Có thể sách này đang tồn tại trong hóa đơn.");
                    }
                }
            });
        }
        // --- 5. TÌM KIẾM (Gợi ý thêm) ---
        // txtSearch.addKeyListener(...) -> gọi bookBUS.search...
    }

    private void setupLayout() {
        JPanel cardPanel = new JPanel(new BorderLayout());
        // Panel chứa các nút (Bên trái)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(toolBar);
        // 2. Panel chứa thanh tìm kiếm (Ở giữa)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(txtSearch);
        // Panel chứa profile (Bên phải)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(createProfilePanel());

        cardPanel.add(leftPanel, BorderLayout.WEST);
        cardPanel.add(centerPanel, BorderLayout.CENTER);
        cardPanel.add(rightPanel, BorderLayout.EAST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        profilePanel.setBackground(Color.RED);
        // Avatar
        JLabel lblAvatar = new JLabel();
        IconHelper.setIcon(lblAvatar, "GUI/icon/avatar.svg", AVATAR_SIZE, AVATAR_SIZE);

        // Panel chứa tên và chức vụ
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel lblName = new JLabel("ĐẶNG HOÀNG PHÚC");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(33, 37, 41));
        lblName.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblRole = new JLabel("Quản lý kho hàng");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(108, 117, 125));
        lblRole.setAlignmentX(Component.RIGHT_ALIGNMENT);

        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(lblRole);

        profilePanel.add(infoPanel);
        profilePanel.add(lblAvatar);
        profilePanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        return profilePanel;
    }

    private JButton createToolBar(ButtonModel model) {
        ActionButton btn = new ActionButton(model.getTitle(), model.getIconPath(), 25);
        btn.setActionCommand(model.getActionCommand());
        return btn;
    }

    // Thêm vào cuối file Header.java
    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnEdit() {
        return btnEdit;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnDetail() {
        return btnDetail;
    }

    public JButton getBtnExportExcel() {
        return btnExportExcel;
    }

    public SearchTextField getTxtSearch() {
        return txtSearch;
    }

    public void setPanelTable(BookTablePanel panelTable) {
        this.panelTable = panelTable;
    }
}