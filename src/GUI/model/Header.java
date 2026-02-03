package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import BUS.BookBUS;
import DTO.BookDTO;

import java.awt.*;
import java.util.ArrayList;

import GUI.util.ExcelHelper;
import GUI.components.ActionButton;
import GUI.components.SearchTextField;
import GUI.components.ToolBarPanel;
import GUI.dialog.book.BookDialog;
import GUI.dialog.book.DialogMode;
import GUI.dialog.invoice.InvoiceDialog;
import GUI.components.RoundedBorderButton;
import GUI.util.ThemeColor;

public class Header extends JPanel {
    private ToolBarPanel toolBar;
    private SearchTextField txtSearch;
    private InvoiceTablePanel pnlInvoice;
    private BookTablePanel panelTable; // Tham chiếu đến bảng để refresh
    private BookBUS bookBUS;
    private String currentTab = "BOOK";
    private ActionButton btnRefresh;

    public Header(BookBUS bus) { // Nhận BUS từ bên ngoài vào (Dependency Injection)
        this.bookBUS = bus;
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        initStyle();

        initComponents();
        initEvents();
    }

    private void initComponents() {
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, ThemeColor.borderColor));
        // 1. Tạo Toolbar (Trái)
        ArrayList<ButtonModel> listButtons = new ArrayList<>();
        listButtons.add(new ButtonModel("THÊM", "GUI/icon/add.svg", "ADD"));
        listButtons.add(new ButtonModel("SỬA", "GUI/icon/edit.svg", "EDIT"));
        listButtons.add(new ButtonModel("XÓA", "GUI/icon/delete.svg", "DELETE"));
        listButtons.add(new ButtonModel("CHI TIẾT", "GUI/icon/detail.svg", "DETAIL"));
        listButtons.add(new ButtonModel("XUẤT EXCEL", "GUI/icon/export_excel.svg", "EXPORT"));
        listButtons.add(new ButtonModel("NHẬP EXCEL", "GUI/icon/import_excel.svg", "IMPORT"));
        // --- 3. KHỞI TẠO TOOLBAR PANEL ---
        // Truyền list và listener vào constructor của ToolBarPanel
        toolBar = new ToolBarPanel(listButtons);
        btnRefresh = new RoundedBorderButton(
                "LÀM MỚI",
                "GUI/icon/refresh.svg",
                ThemeColor.textMain, // Màu accent chủ đạo
                20 // Độ bo góc
        );
        // 2. Tạo Search (Giữa)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        centerPanel.setOpaque(false);
        txtSearch = new SearchTextField();
        centerPanel.add(txtSearch);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        rightPanel.setOpaque(false); // Trong suốt

        // 2. Thêm nút vào Panel phụ này
        rightPanel.add(btnRefresh);
        // Gép lại
        add(toolBar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setBorder(new EmptyBorder(0, 0, 0, 20)); // Padding 2 bên
    }

    private void initStyle() {
        // 1. Set nền trắng cho toàn bộ Header
        this.setOpaque(true);
        this.setBackground(ThemeColor.bgPanel);
        this.setPreferredSize(new Dimension(0, 80));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.borderColor));
    }
    // --- CÁC HÀM XỬ LÝ LOGIC (PRIVATE) ---
    // Giúp code dễ đọc, dễ sửa hơn là viết dồn cục trong initevents

    private void onAdd() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        switch (currentTab) {
            case "BOOK":
                // Logic thêm sách
                BookDialog bookDialog = new BookDialog(parent, null, DialogMode.ADD);
                bookDialog.setVisible(true);
                if (bookDialog.isSucceeded())
                    panelTable.refreshTable();
                break;

            case "SALES":
                InvoiceDialog invoiceDialog = new InvoiceDialog(parent);
                invoiceDialog.setVisible(true);
                if (invoiceDialog.isSucceeded()) {
                    pnlInvoice.loadData();
                }
                break;
        }
    }

    private void onEdit() {
        switch (currentTab) {
            case "BOOK":
                int selectedBookId = panelTable.getSelectedBookId();
                if (selectedBookId == -1) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần sửa!");
                    return;
                }
                BookDTO fullInfo = bookBUS.getBookDetails(selectedBookId);
                if (fullInfo != null) {
                    JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                    BookDialog dialog = new BookDialog(parent, fullInfo, DialogMode.EDIT);
                    dialog.setVisible(true);
                    if (dialog.isSucceeded()) {
                        panelTable.refreshTable();
                    }
                }
                break;

            case "SALES":
                JOptionPane.showMessageDialog(this, "Hóa đơn đã xuất không thể sửa! Chỉ có thể xem chi tiết.");
                break;
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
        switch (currentTab) {
            case "BOOK":
                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookBUS.deleteBook(selectedId)) {
                        JOptionPane.showMessageDialog(this, "Xóa thành công!");
                        panelTable.refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa thất bại!");
                    }
                }
                break;

            // case "SALES":
            // if (confirm == JOptionPane.YES_OPTION) {
            // if (invoiceBUS.deleteInvoice(selectedId)) {
            // JOptionPane.showMessageDialog(this, "Xóa thành công!");
            // panelTable.refreshTable();
            // } else {
            // JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            // }
            // }
            // break;
        }
    }

    private void onDetail() {
        int selectedId = panelTable.getSelectedBookId();
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách xem chi tiết!");
            return;
        }
        switch (currentTab) {
            case "BOOK":
                BookDTO fullInfo = bookBUS.getBookDetails(selectedId);
                if (fullInfo != null) {
                    BookDialog dialog = new BookDialog((JFrame) SwingUtilities.getWindowAncestor(this), fullInfo,
                            DialogMode.READ);
                    dialog.setVisible(true);
                }
                break;

            case "SALES":
                break;
        }

    }

    private void onRefresh() {
        boolean isSuccess = false;
        switch (currentTab) {
            case "BOOK":
                if (panelTable != null) {
                    isSuccess = panelTable.refreshTable();
                }
                break;
            case "SALES":
                break;
        }

        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Icon lỗi màu đỏ
            JOptionPane.showMessageDialog(this, "Lỗi: Không thể tải dữ liệu!\nVui lòng kiểm tra lại kết nối Database.",
                    "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExportExcel() {
        switch (currentTab) {
            case "BOOK":
                java.util.List<BookDTO> listToExport = bookBUS.getAll();

                if (listToExport != null && !listToExport.isEmpty()) {
                    ExcelHelper.exportBooks(listToExport, this);
                } else {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
                }
                break;
        }
    }

    private void onImportExcel() {
        switch (currentTab) {
            case "BOOK":
                // Gọi hàm import từ ExcelHelper hoặc BUS
                // Ví dụ:
                // File file = ExcelHelper.chooseFile(this);
                // if (file != null) {
                // boolean success = bookBUS.importFromExcel(file);
                // if(success) panelTable.refreshTable();
                // }

            default:
                JOptionPane.showMessageDialog(this, "Chức năng nhập Excel chưa hỗ trợ tab này!");
        }
    }

    private void onSearch() {
        String text = txtSearch.getText();
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
                    onExportExcel();
                    break;
                case "IMPORT":
                    break;
            }
        });

        // --- B. XỬ LÝ TÌM KIẾM
        txtSearch.getBtnSearch().addActionListener(e -> onSearch());
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    onSearch();
                }
            }
        });
        btnRefresh.addActionListener(e -> onRefresh());
    }

    public void setPanelInvoice(InvoiceTablePanel pnl) {
        this.pnlInvoice = pnl;
    }

    public void setPnlName(String name) {
        this.currentTab = name;
        if (name.equals("SALES")) {
        }
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