package GUI.model;

import BUS.BookBUS;
import DTO.BookDTO;
import DTO.ValidationResult;
import GUI.dialog.book.BookDialog;
import GUI.dialog.book.DialogMode;
import GUI.util.ExcelHelper;
import GUI.util.ThemeColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class BookTablePanel extends JPanel implements FeatureControllerInterface {
    private BookBUS bookBUS;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private ArrayList<BookDTO> listOriginal;

    private static final String[] COLUMNS = { "ID", "ISBN", "TÊN SÁCH", "GIÁ NHẬP", "GIÁ BÁN", "TỒN KHO",
            "TỒN KHO TỐI THIỂU", "TRẠNG THÁI" };
    private static final int COL_PRICE_IMPORT = 3;
    private static final int COL_PRICE_SELLING = 4;

    public BookTablePanel(BookBUS bus) {
        this.bookBUS = bus;
        setBackground(ThemeColor.bgPanel);
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadTableData();
    }

    // ===================== KHỞI TẠO =====================

    private void initComponents() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(ThemeColor.bgPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleTable() {
        bookTable.setRowHeight(40);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setShowVerticalLines(true);
        bookTable.setShowHorizontalLines(true);
        bookTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookTable.setBackground(ThemeColor.bgPanel);
        bookTable.setForeground(ThemeColor.tableText);
        bookTable.setGridColor(ThemeColor.gridColor);
        bookTable.setSelectionBackground(ThemeColor.selectionBg);
        bookTable.setSelectionForeground(ThemeColor.selectionText);

        JTableHeader header = bookTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(ThemeColor.tableHeaderBg);
        header.setForeground(ThemeColor.tableHeaderText);
        header.setPreferredSize(new Dimension(0, 45));

        for (int i = 0; i < COLUMNS.length; i++) {
            if (i == COL_PRICE_IMPORT)
                bookTable.getColumnModel().getColumn(i).setCellRenderer(new PriceRenderer(ThemeColor.priceImport));
            else if (i == COL_PRICE_SELLING)
                bookTable.getColumnModel().getColumn(i).setCellRenderer(new PriceRenderer(ThemeColor.priceSelling));
            else if (i == 2)
                bookTable.getColumnModel().getColumn(i).setCellRenderer(new ZebraRenderer(JLabel.LEFT));
            else
                bookTable.getColumnModel().getColumn(i).setCellRenderer(new ZebraRenderer(JLabel.CENTER));
        }

        int[] widths = { 60, 140, 300, 120, 120, 100, 140, 150 };
        TableColumnModel cm = bookTable.getColumnModel();
        for (int i = 0; i < widths.length && i < cm.getColumnCount(); i++)
            cm.getColumn(i).setPreferredWidth(widths[i]);
    }

    // ===================== DỮ LIỆU =====================

    public void loadTableData() {
        listOriginal = bookBUS.getAll();
        setTableData(listOriginal);
    }

    public void setTableData(ArrayList<BookDTO> books) {
        tableModel.setRowCount(0);
        for (BookDTO book : books) {
            tableModel.addRow(new Object[] {
                    book.getBookId(),
                    book.getIsbn(),
                    book.getBookTitle(),
                    book.getFormattedImportPrice(),
                    book.getFormattedSellingPrice(),
                    book.getStockQuantity(),
                    book.getMinimumStock(),
                    book.getStatusVietnamese()
            });
        }
        bookTable.repaint();
    }

    public boolean refreshTable() {
        boolean ok = bookBUS.loadDataFromDB();
        if (ok)
            loadTableData();
        return ok;
    }

    public void filterTable(String keyword) {
        if (listOriginal == null)
            return;
        if (keyword == null || keyword.trim().isEmpty()) {
            setTableData(listOriginal);
            return;
        }
        String key = removeAccent(keyword);
        ArrayList<BookDTO> filtered = new ArrayList<>();
        for (BookDTO book : listOriginal) {
            if (removeAccent(book.getBookTitle()).contains(key) || removeAccent(book.getIsbn()).contains(key))
                filtered.add(book);
        }
        setTableData(filtered);
    }

    public int getSelectedBookId() {
        int row = bookTable.getSelectedRow();
        return row == -1 ? -1 : (int) bookTable.getValueAt(row, 0);
    }

    public JTable getBookTable() {
        return bookTable;
    }

    // ===================== ACTIONS =====================

    @Override
    public void onAdd() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        BookDialog dialog = new BookDialog(parent, null, DialogMode.ADD);
        dialog.setVisible(true);
        if (dialog.isSucceeded())
            refreshTable();
    }

    @Override
    public void onEdit() {
        int id = getSelectedBookId();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần sửa!");
            return;
        }
        BookDTO fullInfo = bookBUS.getBookDetails(id);
        if (fullInfo != null) {
            BookDialog dialog = new BookDialog((JFrame) SwingUtilities.getWindowAncestor(this), fullInfo,
                    DialogMode.EDIT);
            dialog.setVisible(true);
            if (dialog.isSucceeded())
                refreshTable();
        }
    }

    @Override
    public void onDelete() {
        int id = getSelectedBookId();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa ID: " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        ValidationResult vr = bookBUS.deleteBook(id);
        if (vr.isValid()) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDetail() {
        int id = getSelectedBookId();
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách xem chi tiết!");
            return;
        }
        BookDTO fullInfo = bookBUS.getBookDetails(id);
        if (fullInfo != null) {
            BookDialog dialog = new BookDialog((JFrame) SwingUtilities.getWindowAncestor(this), fullInfo,
                    DialogMode.READ);
            dialog.setVisible(true);
        }
    }

    @Override
    public void onSearch(String text) {
        filterTable(text);
    }

    @Override
    public void onRefresh() {
        if (refreshTable())
            JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, "Lỗi: Không thể tải dữ liệu!", "Thất bại", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onExportExcel() {
        java.util.List<BookDTO> list = bookBUS.getAll();
        if (list != null && !list.isEmpty())
            ExcelHelper.exportBooks(list, this);
        else
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
    }

    @Override
    public void onImportExcel() {
    }

    @Override
    public boolean[] getButtonConfig() {
        if (config.SessionManager.getCurrentAccount() == null)
            return new boolean[] { false, false, false, false, false, false };
        boolean canAdd = config.SessionManager.hasPermission(451, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(451, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(451, "Xóa");
        return new boolean[] { canAdd, canEdit, canDelete, true, false, false };
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (bookTable != null && ThemeColor.bgPanel != null)
            styleTable();
    }

    // ===================== UTILS =====================

    public static String removeAccent(String s) {
        if (s == null)
            return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(temp).replaceAll("").toLowerCase().replace("đ", "d");
    }

    // ===================== RENDERERS =====================

    private class ZebraRenderer extends DefaultTableCellRenderer {
        private final int align;

        ZebraRenderer(int align) {
            this.align = align;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(align);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            if (isSelected) {
                setBackground(ThemeColor.selectionBg);
                setForeground(ThemeColor.selectionText);
            } else {
                applyRowColor(table, row, this);
            }
            return this;
        }
    }

    private class PriceRenderer extends DefaultTableCellRenderer {
        private final Color priceColor;

        PriceRenderer(Color priceColor) {
            this.priceColor = priceColor;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            if (isSelected) {
                setBackground(ThemeColor.selectionBg);
                setForeground(ThemeColor.selectionText);
            } else {
                applyRowColor(table, row, this);
                setForeground(priceColor);
            }
            return this;
        }
    }

    /** Logic tô màu dòng dùng chung cho cả 2 renderer */
    private void applyRowColor(JTable table, int row, JLabel label) {
        try {
            int modelRow = table.convertRowIndexToModel(row);
            int stock = Integer.parseInt(table.getModel().getValueAt(modelRow, 5).toString());
            int min = Integer.parseInt(table.getModel().getValueAt(modelRow, 6).toString());
            if (stock == 0) {
                label.setBackground(ThemeColor.outOfStockColor);
                label.setForeground(ThemeColor.outOfStockText);
            } else if (stock <= min) {
                label.setBackground(ThemeColor.warningColor);
                label.setForeground(ThemeColor.warningText);
            } else {
                label.setBackground(row % 2 == 0 ? ThemeColor.rowEven : ThemeColor.rowOdd);
                label.setForeground(ThemeColor.tableText);
            }
        } catch (Exception e) {
            label.setBackground(row % 2 == 0 ? ThemeColor.rowEven : ThemeColor.rowOdd);
            label.setForeground(ThemeColor.tableText);
        }
    }
}