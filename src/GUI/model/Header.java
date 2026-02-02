package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import BUS.BookBUS;
import DTO.BookDTO;
import java.awt.*;
import java.util.ArrayList;

import GUI.components.SearchTextField;
import GUI.components.UserProfilePanel; // Import cái vừa tạo
import GUI.components.ToolBarPanel;
import GUI.dialog.book.BookDialog;
import GUI.dialog.book.DialogMode;
import GUI.dialog.sale.SalesDialog;

import java.awt.event.ActionListener;

public class Header extends JPanel {
    private ToolBarPanel toolBar;
    private SearchTextField txtSearch;
    private UserProfilePanel userProfile; // Dùng cái class mới
    private InvoiceTablePanel pnlInvoice;
    private BookTablePanel panelTable; // Tham chiếu đến bảng để refresh
    private BookBUS bookBUS;

    public Header(BookBUS bus) { // Nhận BUS từ bên ngoài vào (Dependency Injection)
        this.bookBUS = bus;
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        initStyle();

        initComponents();
        initEvents();
    }

    private void initComponents() {
        // 1. Tạo Toolbar (Trái)
        ArrayList<ButtonModel> listButtons = new ArrayList<>();
        listButtons.add(new ButtonModel("THÊM", "GUI/icon/add.svg", "ADD"));
        listButtons.add(new ButtonModel("SỬA", "GUI/icon/edit.svg", "EDIT"));
        listButtons.add(new ButtonModel("XÓA", "GUI/icon/delete.svg", "DELETE"));
        listButtons.add(new ButtonModel("CHI TIẾT", "GUI/icon/detail.svg", "DETAIL"));
        listButtons.add(new ButtonModel("XUẤT EXCEL", "GUI/icon/export_excel.svg", "EXPORT"));

        // --- 3. KHỞI TẠO TOOLBAR PANEL ---
        // Truyền list và listener vào constructor của ToolBarPanel
        toolBar = new ToolBarPanel(listButtons);
        toolBar.setBackground(Color.RED);
        toolBar.setOpaque(true);

        // 2. Tạo Search (Giữa)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        centerPanel.setOpaque(false);
        txtSearch = new SearchTextField();
        centerPanel.add(txtSearch);

        // 3. Tạo Profile (Phải) - Code siêu ngắn gọn nhờ tách file
        userProfile = new UserProfilePanel("Đặng Hoàng Phúc", "Quản lý kho");

        // Gép lại
        add(toolBar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(userProfile, BorderLayout.EAST);

        setBorder(new EmptyBorder(0, 20, 0, 20)); // Padding 2 bên
    }

    private void initStyle() {
        // 1. Set nền trắng cho toàn bộ Header
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(0, 80));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
    }
    // --- CÁC HÀM XỬ LÝ LOGIC (PRIVATE) ---
    // Giúp code dễ đọc, dễ sửa hơn là viết dồn cục trong initevents

    private void onAdd() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (panelTable != null && panelTable.isShowing()) {
            // Đang ở tab Sách -> Bật BookDialog
            BookDialog dialog = new BookDialog(parent, null, DialogMode.ADD);
            dialog.setVisible(true);
            if (dialog.isSucceeded())
                panelTable.refreshTable();

        } else if (pnlInvoice != null && pnlInvoice.isShowing()) {
            // Đang ở tab Hóa đơn -> Bật SalesDialog (Tạo hóa đơn)
            SalesDialog dialog = new SalesDialog(parent);
            dialog.setVisible(true);

            // Nếu bán thành công thì reload lại bảng hóa đơn
            if (dialog.isSucceeded()) {
                pnlInvoice.loadData();
            }
        }
    }

    private void onEdit() {
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
        }
    }

    private void onDelete() {
        int selectedId = panelTable.getSelectedBookId();
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa ID: " + selectedId + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookBUS.deleteBook(selectedId)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                panelTable.refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }

    private void onDetail() {
        int selectedId = panelTable.getSelectedBookId();
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách xem chi tiết!");
            return;
        }
        BookDTO fullInfo = bookBUS.getBookDetails(selectedId);
        if (fullInfo != null) {
            BookDialog dialog = new BookDialog((JFrame) SwingUtilities.getWindowAncestor(this), fullInfo,
                    DialogMode.READ);
            dialog.setVisible(true);
        }
    }

    private void onSearch() {
        // 1. Lấy từ khóa
        String text = txtSearch.getText();

        // 2. Gọi bảng để lọc
        if (panelTable != null) {
            panelTable.filterTable(text);
        }
    }

    public void setPanelTable(BookTablePanel panelTable) {
        this.panelTable = panelTable;
    }

    private void initEvents() {
        toolBar.initEvent(e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "ADD":
                    onAdd();
                    break;
                case "EDIT":
                    onEdit();
                    break;
                case "DELETE":
                    onDelete();
                    break;
                case "DETAIL":
                    onDetail();
                    break;
                case "EXPORT":
                    System.out.println("Xuất Excel...");
                    break;
            }
        });

        // --- B. XỬ LÝ TÌM KIẾM (Đã làm ở bài trước) ---
        txtSearch.getBtnSearch().addActionListener(e -> onSearch());
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    onSearch();
                }
            }
        });
    }

    public void setPanelInvoice(InvoiceTablePanel pnl) {
        this.pnlInvoice = pnl;
    }
}