package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import BUS.AuthorBUS;
import BUS.BookBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import DTO.AuthorDTO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import GUI.components.DashboardCard;
import GUI.components.ProductCard;
import GUI.dialog.book.BookDialog;
import GUI.dialog.book.DialogMode;
import GUI.components.RoundedBorderButton;
import GUI.util.ThemeColor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GroupDashboard extends JPanel {
    private ActionListener onItemSelected;

    // --- BUS ---
    private BookBUS bookBUS;
    private AuthorBUS authorBUS;
    private CategoryBUS categoryBUS;
    private PublisherBUS publisherBUS;

    // --- COMPONENTS ---
    private DashboardCard cardAuthor;
    private DashboardCard cardPublisher;
    private DashboardCard cardCategory;

    // Khu vực hiển thị lưới sách
    private JPanel pnlListContent;

    // Bộ lọc (Combobox)
    private JComboBox<FilterItem> cbFilterCategory;
    private JComboBox<FilterItem> cbFilterPublisher;
    private JComboBox<FilterItem> cbFilterAuthor;

    // Biến cờ để tránh sự kiện khi đang load dữ liệu
    private boolean isLoadingFilter = false;

    public GroupDashboard(BookBUS bookBUS, AuthorBUS authorBUS, CategoryBUS categoryBUS, PublisherBUS publisherBUS,
            ActionListener onItemSelected) {
        this.onItemSelected = onItemSelected;

        // 1. Khởi tạo BUS
        this.bookBUS = bookBUS;
        this.authorBUS = authorBUS;
        this.categoryBUS = categoryBUS;
        this.publisherBUS = publisherBUS;

        initStyle();
        initComponents();

        // 2. Nạp dữ liệu vào bộ lọc và load danh sách sách ban đầu
        loadFilterData();
        refreshData(null); // Load tất cả
    }

    private void initStyle() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void initComponents() {
        // --- A. PHẦN TOP: CÁC CARD THỐNG KÊ ---
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);

        // --- GIỮ NGUYÊN LOGIC CHUYỂN TAB KHI NHẤN CARD ---
        // Card 1: Tác giả -> Gửi lệnh chuyển sang màn hình Quản lý Tác giả
        cardAuthor = new DashboardCard("Tổng Tác Giả", "0", "GUI/icon/author.svg", new Color(65, 105, 225),
                () -> sendCommand("AUTHOR"));

        // Card 2: NXB -> Gửi lệnh chuyển sang màn hình Quản lý NXB
        cardPublisher = new DashboardCard("Nhà Xuất Bản", "0", "GUI/icon/publisher.svg", new Color(147, 112, 219),
                () -> sendCommand("PUBLISHER"));

        // Card 3: Thể Loại -> Gửi lệnh chuyển sang màn hình Quản lý Thể loại
        cardCategory = new DashboardCard("Thể Loại Sách", "0", "GUI/icon/genre.svg", new Color(255, 165, 0),
                () -> sendCommand("CATEGORY"));

        pnlCards.add(cardAuthor);
        pnlCards.add(cardPublisher);
        pnlCards.add(cardCategory);
        add(pnlCards, BorderLayout.NORTH);

        // --- B. PHẦN CENTER: THANH LỌC + LƯỚI SÁCH ---
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 15));
        pnlCenter.setOpaque(false);

        // 1. Thanh bộ lọc (Filter Bar) - CHỈ LỌC SÁCH Ở DASHBOARD
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel lblFilter = new JLabel("Lọc sách theo:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilter.setForeground(Color.DARK_GRAY);

        cbFilterCategory = createFilterCombo("Tất cả Thể loại");
        cbFilterPublisher = createFilterCombo("Tất cả NXB");
        cbFilterAuthor = createFilterCombo("Tất cả Tác giả");

        RoundedBorderButton btnReset = new RoundedBorderButton("Làm mới / Tất cả", ThemeColor.textMain, 10);
        btnReset.addActionListener(e -> resetFilters());

        pnlFilter.add(lblFilter);
        pnlFilter.add(cbFilterCategory);
        pnlFilter.add(cbFilterPublisher);
        pnlFilter.add(cbFilterAuthor);
        pnlFilter.add(Box.createHorizontalStrut(20));
        pnlFilter.add(btnReset);

        pnlCenter.add(pnlFilter, BorderLayout.NORTH);

        // 2. Lưới hiển thị sách
        pnlListContent = new JPanel(new GridLayout(0, 4, 20, 20));
        pnlListContent.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(pnlListContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(245, 245, 245));
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- C. GÁN SỰ KIỆN LỌC ---
        ActionListener filterAction = e -> {
            if (!isLoadingFilter)
                applyFilters();
        };
        cbFilterCategory.addActionListener(filterAction);
        cbFilterPublisher.addActionListener(filterAction);
        cbFilterAuthor.addActionListener(filterAction);
    }

    // --- CÁC HÀM XỬ LÝ DỮ LIỆU ---

    /**
     * Hàm OVERLOAD để sửa lỗi báo đỏ bên GroupPanel.
     * Khi gọi không tham số -> Tự động load tất cả (truyền null).
     */
    public void refreshData() {
        refreshData(null);
    }

    public void refreshData(ArrayList<BookDTO> dataList) {
        // 1. Cập nhật số liệu trên Cards
        try {
            cardAuthor.setValue(String.valueOf(authorBUS.getAll().size()));
            cardPublisher.setValue(String.valueOf(publisherBUS.getAll().size()));
            cardCategory.setValue(String.valueOf(categoryBUS.getAll().size()));
        } catch (Exception e) {
        }

        // 2. Render lại Grid Sách
        pnlListContent.removeAll();

        ArrayList<BookDTO> books = (dataList == null) ? bookBUS.getAll() : dataList;

        if (books == null || books.isEmpty()) {
            JLabel lblEmpty = new JLabel("Không tìm thấy sách nào phù hợp!", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblEmpty.setForeground(Color.GRAY);
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);
            p.add(lblEmpty);
            pnlListContent.add(p);
        } else {

            for (BookDTO book : books) {

                String imgPath = "src/image/default_book.png";
                if (book.getImage() != null && !book.getImage().isEmpty()) {
                    if (book.getImage().contains(":") || book.getImage().startsWith("/"))
                        imgPath = book.getImage();
                    else
                        imgPath = "src/image/" + book.getImage();
                }

                ProductCard pCard = new ProductCard(
                        book.getBookTitle(),
                        imgPath,
                        () -> showBookDetails(book));
                pnlListContent.add(pCard);
            }
        }
        pnlListContent.revalidate();
        pnlListContent.repaint();
    }

    private void applyFilters() {
        // 1. Lấy các giá trị ID đang chọn từ Combobox
        // Nếu chưa khởi tạo xong hoặc combobox null thì bỏ qua
        if (isLoadingFilter || cbFilterCategory.getSelectedItem() == null)
            return;
        int selectedCatId = ((FilterItem) cbFilterCategory.getSelectedItem()).id;
        int selectedPubId = ((FilterItem) cbFilterPublisher.getSelectedItem()).id;
        int selectedAuId = ((FilterItem) cbFilterAuthor.getSelectedItem()).id;
        // 2. Lấy danh sách gốc (Tất cả sách)
        ArrayList<BookDTO> allBooks = bookBUS.getAll();
        ArrayList<BookDTO> filteredList = new ArrayList<>();
        // 3. Vòng lặp lọc theo logic AND (Thỏa mãn TẤT CẢ tiêu chí)
        for (BookDTO book : allBooks) {
            // Kiểm tra Thể loại (0 là "Tất cả")
            boolean matchCat = (selectedCatId == 0) || (book.getCategoryId() == selectedCatId);
            // Kiểm tra NXB
            boolean matchPub = (selectedPubId == 0) || (book.getPublisherId() == selectedPubId);
            // Kiểm tra Tác giả
            boolean matchAu = false;
            if (selectedAuId == 0) {
                matchAu = true; // chọn tất cả
            } else {
                if (book.getAuthors() != null) {
                    for (AuthorDTO a : book.getAuthors()) {
                        if (a.getAuthorId() == selectedAuId) {
                            matchAu = true;
                            break;
                        }
                    }
                }
            }
            // Nếu thỏa mãn cả 3 thì thêm vào danh sách kết quả
            if (matchCat && matchPub && matchAu) {
                filteredList.add(book);
            }
        }
        // 4. Hiển thị lại dữ liệu đã lọc
        refreshData(filteredList);
    }

    public void resetFilters() {
        isLoadingFilter = true;
        cbFilterCategory.setSelectedIndex(0);
        cbFilterPublisher.setSelectedIndex(0);
        cbFilterAuthor.setSelectedIndex(0);
        isLoadingFilter = false;
        refreshData(null);
    }

    // --- CÁC HÀM HỖ TRỢ KHÁC ---

    private JComboBox<FilterItem> createFilterCombo(String defaultText) {
        JComboBox<FilterItem> cb = new JComboBox<>();
        cb.setPreferredSize(new Dimension(180, 35));
        cb.setBackground(Color.WHITE);
        cb.addItem(new FilterItem(0, defaultText));
        return cb;
    }

    private void loadFilterData() {
        isLoadingFilter = true;
        for (CategoryDTO c : categoryBUS.getAll())
            cbFilterCategory.addItem(new FilterItem(c.getId(), c.getName()));
        for (PublisherDTO p : publisherBUS.getAll())
            cbFilterPublisher.addItem(new FilterItem(p.getId(), p.getName()));
        for (AuthorDTO a : authorBUS.getAll())
            cbFilterAuthor.addItem(new FilterItem(a.getAuthorId(), a.getAuthorName()));
        isLoadingFilter = false;
    }

    private void sendCommand(String command) {
        if (onItemSelected != null) {
            onItemSelected.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command));
        }
    }

    private void showBookDetails(BookDTO book) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JFrame frame = (parent instanceof JFrame) ? (JFrame) parent : null;
        BookDTO fullDetails = bookBUS.getBookDetails(book.getBookId());
        if (fullDetails != null) {
            new BookDialog(frame, fullDetails, DialogMode.READ).setVisible(true);
        }
    }

    class FilterItem {
        int id;
        String name;

        public FilterItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}