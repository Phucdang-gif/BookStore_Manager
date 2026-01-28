package GUI.model;

import BUS.BookBUS;
import DTO.BookDTO;
import GUI.util.UIConstants; // Giả sử bạn vẫn giữ file này, nếu không hãy thay bằng Color.WHITE/GRAY

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BookTablePanel extends JPanel {
    private BookBUS bookBUS;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private static final int TABLE_WIDTH_CONSTANT = 1200;

    public BookTablePanel() {
        this.bookBUS = new BookBUS();

        // Nếu không có file UIConstants, bạn có thể thay bằng Color.WHITE
        try {
            setBackground(UIConstants.BACKGROUND_COLOR);
        } catch (Exception e) {
            setBackground(Color.WHITE);
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        initComponents();
        loadTableData();
    }

    private void initComponents() {
        // Cấu trúc cột bảng
        String[] columns = { "ID", "ISBN", "TÊN SÁCH", "GIÁ NHẬP", "GIÁ BÁN", "TỒN KHO", "TỒN KHO TỐI THIỂU",
                "TRẠNG THÁI" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ngăn không cho sửa trực tiếp trên bảng
            }
        };

        bookTable = new JTable(tableModel);
        // Thiết lập font chữ cơ bản (fallback nếu UIConstants lỗi)
        try {
            bookTable.setFont(UIConstants.NORMAL_FONT);
        } catch (Exception e) {
            bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        bookTable.setRowHeight(30);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setShowGrid(true);
        bookTable.setGridColor(Color.LIGHT_GRAY);
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Thiết lập Header của bảng
        JTableHeader header = bookTable.getTableHeader();
        try {
            header.setFont(UIConstants.TABLE_HEADER_FONT);
            header.setBackground(UIConstants.GREEN_BACKGROUND);
            header.setForeground(UIConstants.DARK_TEXT);
        } catch (Exception e) {
            header.setFont(new Font("Segoe UI", Font.BOLD, 14));
            header.setBackground(new Color(200, 255, 200));
            header.setForeground(Color.BLACK);
        }
        header.setPreferredSize(new Dimension(0, 35));

        // Căn giữa dữ liệu (Trừ cột tên sách)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < bookTable.getColumnCount(); i++) {
            if (i != 2) { // Cột 2 là Tên sách -> Để mặc định (căn trái)
                bookTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Tính toán độ rộng cột
        float[] columnPercentages = { 5f, 15f, 30f, 12.5f, 12.5f, 10f, 10f, 15f };
        TableColumnModel columnModel = bookTable.getColumnModel();
        for (int i = 0; i < columnPercentages.length; i++) {
            if (i < columnModel.getColumnCount()) {
                int width = Math.round((TABLE_WIDTH_CONSTANT * columnPercentages[i]) / 100f);
                columnModel.getColumn(i).setPreferredWidth(width);
            }
        }

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    // Tải dữ liệu từ BUS lên bảng
    public void loadTableData() {
        ArrayList<BookDTO> books = bookBUS.getAll();
        setTableData(books);
    }

    public void setTableData(ArrayList<BookDTO> books) {
        tableModel.setRowCount(0);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

        for (BookDTO book : books) {
            String statusVN = getStatusVN(book.getStatus());
            tableModel.addRow(new Object[] {
                    book.getBookId(),
                    book.getIsbn(),
                    book.getBookTitle(),
                    currencyFormat.format(book.getImportPrice()),
                    currencyFormat.format(book.getSellingPrice()),
                    book.getStockQuantity(),
                    book.getMinimumStock(),
                    statusVN
            });
        }
        bookTable.repaint();
    }

    public void refreshTableData(ArrayList<BookDTO> listBooks) {
        setTableData(listBooks);
    }

    // Hàm quan trọng: Header sẽ gọi hàm này để lấy ID sách cần xem
    public int getSelectedBookId() {
        int row = bookTable.getSelectedRow();
        if (row == -1)
            return -1;
        // Cột 0 là ID
        return (int) bookTable.getValueAt(row, 0);
    }

    // Làm mới bảng (Gọi lại data từ DB)
    public void refreshTable() {
        // Trong mô hình BUS hiện tại, cần reload lại listBook trước
        bookBUS.loadDataFromDB();
        loadTableData();
    }

    private String getStatusVN(String statusEN) {
        if (statusEN == null)
            return "Còn hàng"; // Mặc định

        switch (statusEN) {
            case "IN_STOCK":
                return "Còn hàng";
            case "OUT_OF_STOCK":
                return "Hết hàng";
            case "SUSPENDED":
                return "Ngừng kinh doanh";
            default:
                return statusEN; // Nếu không khớp case nào thì trả về nguyên gốc
        }
    }
}