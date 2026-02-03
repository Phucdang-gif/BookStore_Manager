package GUI.model;

import BUS.BookBUS;
import DTO.BookDTO;
import GUI.util.ThemeColor;
import GUI.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class BookTablePanel extends JPanel {
    private BookBUS bookBUS;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private String[] columns = { "ID", "ISBN", "TÊN SÁCH", "GIÁ NHẬP", "GIÁ BÁN", "TỒN KHO", "TỒN KHO TỐI THIỂU",
            "TRẠNG THÁI" };
    private ArrayList<BookDTO> listOriginal;

    private static final int COL_PRICE_IMPORT = 3;
    private static final int COL_PRICE_SELLING = 4;

    public BookTablePanel(BookBUS bus) {
        this.bookBUS = bus;
        setBackground(ThemeColor.bgPanel);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadTableData();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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

    // ================================================================
    // Zebra + align (các cột thường)
    // ================================================================
    private class ZebraCenterRenderer extends DefaultTableCellRenderer {
        private final int horizontalAlignment;

        ZebraCenterRenderer(int alignment) {
            this.horizontalAlignment = alignment;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(horizontalAlignment);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));

            if (isSelected) {
                setBackground(ThemeColor.selectionBg); // ← mới
                setForeground(ThemeColor.selectionText); // ← mới
            } else {
                setBackground(row % 2 == 0 ? ThemeColor.rowEven : ThemeColor.rowOdd);
                setForeground(ThemeColor.tableText);
            }

            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return this;
        }
    }

    // ================================================================
    // Zebra + màu chữ giá tiền
    // ================================================================
    private class PriceRenderer extends DefaultTableCellRenderer {
        private final Color priceColor;

        PriceRenderer(Color priceColor) {
            this.priceColor = priceColor;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 13));

            if (isSelected) {
                setBackground(ThemeColor.selectionBg); // ← mới
                setForeground(ThemeColor.selectionText); // ← mới
            } else {
                setBackground(row % 2 == 0 ? ThemeColor.rowEven : ThemeColor.rowOdd);
                setForeground(priceColor);
            }

            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return this;
        }
    }

    // ================================================================
    private void styleTable() {
        bookTable.setRowHeight(UIConstants.ROW_HEIGHT);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setShowVerticalLines(true);
        bookTable.setShowHorizontalLines(true);

        bookTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookTable.setBackground(ThemeColor.bgPanel);
        bookTable.setForeground(ThemeColor.tableText);
        bookTable.setGridColor(ThemeColor.gridColor);

        // Selection — dùng biến mới
        bookTable.setSelectionBackground(ThemeColor.selectionBg);
        bookTable.setSelectionForeground(ThemeColor.selectionText);

        // --- Header ---
        JTableHeader header = bookTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(ThemeColor.tableHeaderBg);
        header.setForeground(ThemeColor.tableHeaderText);
        header.setPreferredSize(new Dimension(0, 45));

        // --- Renderer từng cột ---
        for (int i = 0; i < columns.length; i++) {
            if (i == COL_PRICE_IMPORT) {
                bookTable.getColumnModel().getColumn(i)
                        .setCellRenderer(new PriceRenderer(ThemeColor.priceImport));
            } else if (i == COL_PRICE_SELLING) {
                bookTable.getColumnModel().getColumn(i)
                        .setCellRenderer(new PriceRenderer(ThemeColor.priceSelling));
            } else if (i == 2) {
                bookTable.getColumnModel().getColumn(i)
                        .setCellRenderer(new ZebraCenterRenderer(JLabel.LEFT));
            } else {
                bookTable.getColumnModel().getColumn(i)
                        .setCellRenderer(new ZebraCenterRenderer(JLabel.CENTER));
            }
        }

        // --- Độ rộng cột ---
        int[] widths = { 60, 140, 300, 120, 120, 100, 140, 150 };
        TableColumnModel columnModel = bookTable.getColumnModel();
        for (int i = 0; i < widths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

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

    public void refreshTableData(ArrayList<BookDTO> listBooks) {
        setTableData(listBooks);
    }

    public int getSelectedBookId() {
        int row = bookTable.getSelectedRow();
        if (row == -1)
            return -1;
        return (int) bookTable.getValueAt(row, 0);
    }

    public boolean refreshTable() {
        boolean isSuccess = bookBUS.loadDataFromDB();
        if (isSuccess) {
            loadTableData();
        }
        return isSuccess;
    }

    public void filterTable(String keyword) {
        if (listOriginal == null)
            return;

        if (keyword == null || keyword.trim().isEmpty()) {
            setTableData(listOriginal);
            return;
        }

        String key = UIConstants.removeAccent(keyword);
        ArrayList<BookDTO> listFiltered = new ArrayList<>();

        for (BookDTO book : listOriginal) {
            String name = UIConstants.removeAccent(book.getBookTitle());
            String isbn = UIConstants.removeAccent(book.getIsbn());
            if (name.contains(key) || isbn.contains(key)) {
                listFiltered.add(book);
            }
        }
        setTableData(listFiltered);
    }

    public JTable getBookTable() {
        return bookTable;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (bookTable != null && ThemeColor.bgPanel != null) {
            styleTable();
        }
    }
}