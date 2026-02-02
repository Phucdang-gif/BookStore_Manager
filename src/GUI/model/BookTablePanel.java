package GUI.model;

import BUS.BookBUS;
import DTO.BookDTO;
import GUI.util.UIConstants; // Giả sử bạn vẫn giữ file này, nếu không hãy thay bằng Color.WHITE/GRAY

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Utilities;

import java.awt.*;
import java.util.ArrayList;

public class BookTablePanel extends JPanel {
    private BookBUS bookBUS;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private String[] columns = { "ID", "ISBN", "TÊN SÁCH", "GIÁ NHẬP", "GIÁ BÁN", "TỒN KHO", "TỒN KHO TỐI THIỂU",
            "TRẠNG THÁI" };
    private ArrayList<BookDTO> listOriginal;

    public BookTablePanel(BookBUS bus) {
        this.bookBUS = bus;
        setBackground(UIConstants.BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadTableData();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(columns, 0) {
            @Override // Không cho phép sửa trực tiếp trên bảng
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // 2. Style cho bảng (Làm đẹp)
        styleTable();

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleTable() {
        // Font chữ
        try {
            bookTable.setFont(UIConstants.NORMAL_FONT);
        } catch (Exception e) {
            bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        bookTable.setRowHeight(UIConstants.ROW_HEIGHT);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setShowVerticalLines(true);
        bookTable.setShowHorizontalLines(true);
        bookTable.setGridColor(new Color(200, 200, 200));

        // Header
        JTableHeader header = bookTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250)); // Màu xám nhẹ
        header.setForeground(new Color(33, 37, 41));
        header.setPreferredSize(new Dimension(0, 45)); // Header cao hơn chút
        header.setBackground(UIConstants.GREEN_BACKGROUND);

        // Căn giữa dữ liệu (Trừ tên sách để căn trái)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Cột ID, ISBN, Giá, Tồn, Trạng thái -> Căn giữa
        for (int i = 0; i < columns.length; i++) {
            if (i == 2)
                continue;
            bookTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Độ rộng cột tương đối
        int[] widths = {
                60, // ID (Nhỏ)
                140, // ISBN
                300, // TÊN SÁCH (Rất rộng để không bị che)
                120, // GIÁ NHẬP
                120, // GIÁ BÁN
                100, // TỒN KHO
                140, // TỒN KHO TỐI THIỂU
                150 // TRẠNG THÁI
        };
        TableColumnModel columnModel = bookTable.getColumnModel();
        for (int i = 0; i < widths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    // Tải dữ liệu từ BUS lên bảng
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
            // Lấy các trường muốn tìm kiếm
            String name = UIConstants.removeAccent(book.getBookTitle());
            String isbn = UIConstants.removeAccent(book.getIsbn());

            // Nếu muốn tìm cả tác giả thì nối chuỗi tác giả vào đây
            // String author = UIConstants.removeAccent(book.getAuthorNames());
            if (name.contains(key) || isbn.contains(key)) {
                listFiltered.add(book);
            }
        }

        // Cập nhật lại bảng với dữ liệu đã lọc
        setTableData(listFiltered);
    }
}