package GUI.components;

import BUS.AuthorBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import DTO.AuthorDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class StatisticFilterPanel extends JPanel {

    // --- Thời gian ---
    private JComboBox<String> cbTimeRange;
    private JComboBox<String> cbQuarter;
    private JComboBox<String> cbMonth;
    private JComboBox<String> cbYear;
    private JComboBox<String> cbViewMode;

    // JDateChooser cho chọn ngày tùy chỉnh
    private JDateChooser dcStart;
    private JDateChooser dcEnd;

    // --- Sách ---
    private JComboBox<FilterItem> cbCategory;
    private JComboBox<FilterItem> cbAuthor;
    private JComboBox<FilterItem> cbPublisher;

    // --- Nút Làm Mới ---
    private JButton btnReset;

    private boolean isUpdating = false;
    private FilterListener listener;
    private boolean showBookFilters;

    public interface FilterListener {
        void onFilterApplied(Date startDate, Date endDate, int categoryId, int authorId, int publisherId);
    }

    // Constructor 3 tham số (backward compatible)
    public StatisticFilterPanel(boolean showTimeFilter, boolean showDetailedTime, boolean showBookFilters,
            FilterListener listener) {
        this(showTimeFilter, showDetailedTime, showBookFilters, false, listener);
    }

    // Constructor đầy đủ
    public StatisticFilterPanel(boolean showTimeFilter, boolean showDetailedTime, boolean showBookFilters,
            boolean showViewMode,
            FilterListener listener) {
        this.listener = listener;
        this.showBookFilters = showBookFilters;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Bộ Lọc Dữ Liệu", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        contentPanel.setBackground(Color.WHITE);

        // ----------------------------------------------------------------
        // FILTER XEM THEO (NGÀY / THÁNG / NĂM)
        // ----------------------------------------------------------------
        if (showViewMode) {
            cbViewMode = new JComboBox<>(new String[] { "Theo Ngày", "Theo Tháng", "Theo Năm" });
            cbViewMode.setPreferredSize(new Dimension(105, 30));
            cbViewMode.addActionListener(e -> executeFilter());
            contentPanel.add(new JLabel("Xem theo:"));
            contentPanel.add(cbViewMode);
            contentPanel.add(makeSeparator());
        }

        // ----------------------------------------------------------------
        // FILTER THỜI GIAN
        // ----------------------------------------------------------------
        if (showTimeFilter) {
            String[] times = { "Tất cả", "3 ngày qua", "7 ngày qua",
                    "Tùy chỉnh..." };
            cbTimeRange = new JComboBox<>(times);
            cbTimeRange.setPreferredSize(new Dimension(115, 30));

            // --- Quý ---
            cbQuarter = new JComboBox<>(new String[] { "Chọn Quý", "Quý 1", "Quý 2", "Quý 3", "Quý 4" });
            cbQuarter.setPreferredSize(new Dimension(90, 30));

            // --- Tháng ---
            cbMonth = new JComboBox<>(new String[] {
                    "Chọn Tháng",
                    "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                    "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
                    "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            });
            cbMonth.setPreferredSize(new Dimension(105, 30));

            // --- Năm ---
            int currentYear = LocalDate.now().getYear();
            cbYear = new JComboBox<>();
            cbYear.addItem("Chọn Năm"); // <-- THÊM BASE VÀO ĐÂY
            for (int i = currentYear; i >= 2020; i--)
                cbYear.addItem(String.valueOf(i));
            cbYear.setPreferredSize(new Dimension(95, 30)); // Kéo rộng thêm để hiện đủ chữ

            // --- JDateChooser Từ / Đến (ẩn khi không dùng Tùy chỉnh) ---
            dcStart = new JDateChooser();
            dcStart.setPreferredSize(new Dimension(120, 30));
            dcStart.setDateFormatString("dd/MM/yyyy");
            dcStart.setEnabled(false);

            dcEnd = new JDateChooser();
            dcEnd.setPreferredSize(new Dimension(120, 30));
            dcEnd.setDateFormatString("dd/MM/yyyy");
            dcEnd.setEnabled(false);

            // Listener JDateChooser — chỉ trigger khi đang enabled (chế độ Tùy chỉnh)
            PropertyChangeListener dateChangeListener = evt -> {
                if ("date".equals(evt.getPropertyName()) && !isUpdating
                        && dcStart.isEnabled() && dcEnd.isEnabled()) {
                    executeFilter();
                }
            };
            dcStart.addPropertyChangeListener(dateChangeListener);
            dcEnd.addPropertyChangeListener(dateChangeListener);

            // --- Listener cbTimeRange ---
            cbTimeRange.addActionListener(e -> {
                if (isUpdating)
                    return;
                isUpdating = true;

                String selected = (String) cbTimeRange.getSelectedItem();
                boolean isCustom = "Tùy chỉnh...".equals(selected);

                dcStart.setEnabled(isCustom);
                dcEnd.setEnabled(isCustom);

                if (!isCustom) {
                    cbQuarter.setSelectedIndex(0);
                    cbMonth.setSelectedIndex(0);
                    autoFillDates(selected);
                    isUpdating = false;
                    executeFilter();
                } else {
                    isUpdating = false;
                }
            });

            // --- Listener cbQuarter (TÁCH RIÊNG) ---
            cbQuarter.addActionListener(e -> {
                if (isUpdating)
                    return;
                String q = (String) cbQuarter.getSelectedItem();
                if (q == null || "Chọn Quý".equals(q))
                    return;

                isUpdating = true;
                cbMonth.setSelectedIndex(0); // reset tháng khi chọn quý

                // Nếu chưa chọn năm, tự động gán năm hiện tại để không bị lỗi
                String selectedYear = (String) cbYear.getSelectedItem();
                int year;
                if ("Chọn Năm".equals(selectedYear)) {
                    year = LocalDate.now().getYear();
                    cbYear.setSelectedItem(String.valueOf(year)); // Set lên UI
                } else {
                    year = Integer.parseInt(selectedYear);
                }

                fillQuarterDates(q, year);
                cbTimeRange.setSelectedItem("Tùy chỉnh...");
                dcStart.setEnabled(true);
                dcEnd.setEnabled(true);
                isUpdating = false;
                executeFilter();
            });

            // --- Listener cbMonth (TÁCH RIÊNG) ---
            cbMonth.addActionListener(e -> {
                if (isUpdating)
                    return;
                String m = (String) cbMonth.getSelectedItem();
                if (m == null || "Chọn Tháng".equals(m))
                    return;

                isUpdating = true;
                cbQuarter.setSelectedIndex(0); // reset quý khi chọn tháng

                // Nếu chưa chọn năm, tự động gán năm hiện tại để không bị lỗi
                String selectedYear = (String) cbYear.getSelectedItem();
                int year;
                if ("Chọn Năm".equals(selectedYear)) {
                    year = LocalDate.now().getYear();
                    cbYear.setSelectedItem(String.valueOf(year)); // Set lên UI
                } else {
                    year = Integer.parseInt(selectedYear);
                }

                fillMonthDates(cbMonth.getSelectedIndex(), year);
                cbTimeRange.setSelectedItem("Tùy chỉnh...");
                dcStart.setEnabled(true);
                dcEnd.setEnabled(true);
                isUpdating = false;
                executeFilter();
            });

            // --- Listener cbYear (TÁCH RIÊNG) ---
            cbYear.addActionListener(e -> {
                if (isUpdating)
                    return;
                String selectedYearStr = (String) cbYear.getSelectedItem();
                if (selectedYearStr == null || "Chọn Năm".equals(selectedYearStr))
                    return; // Nếu chọn lại base thì bỏ qua

                int year = Integer.parseInt(selectedYearStr);
                String q = (String) cbQuarter.getSelectedItem();
                String m = (String) cbMonth.getSelectedItem();

                isUpdating = true;
                if (q != null && !"Chọn Quý".equals(q)) {
                    // Đang ở Quý → cập nhật lại ngày theo năm mới
                    fillQuarterDates(q, year);
                    cbTimeRange.setSelectedItem("Tùy chỉnh...");
                    dcStart.setEnabled(true);
                    dcEnd.setEnabled(true);
                } else if (m != null && !"Chọn Tháng".equals(m)) {
                    // Đang ở Tháng → cập nhật lại ngày theo năm mới
                    fillMonthDates(cbMonth.getSelectedIndex(), year);
                    cbTimeRange.setSelectedItem("Tùy chỉnh...");
                    dcStart.setEnabled(true);
                    dcEnd.setEnabled(true);
                } else {
                    // Không ở Quý/Tháng → filter cả năm (01/01 → 31/12)
                    setDcDates(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
                    cbTimeRange.setSelectedItem("Tùy chỉnh...");
                    dcStart.setEnabled(true);
                    dcEnd.setEnabled(true);
                }
                isUpdating = false;
                executeFilter();
            });

            contentPanel.add(new JLabel("Thời gian:"));
            contentPanel.add(cbTimeRange);
            contentPanel.add(makeSeparator());
            if (showDetailedTime) {
                contentPanel.add(new JLabel("Quý:"));
                contentPanel.add(cbQuarter);
                contentPanel.add(new JLabel("Tháng:"));
                contentPanel.add(cbMonth);
                contentPanel.add(new JLabel("Năm:"));
                contentPanel.add(cbYear);
                contentPanel.add(makeSeparator());
            }
            contentPanel.add(new JLabel("Từ:"));
            contentPanel.add(dcStart);
            contentPanel.add(new JLabel("Đến:"));
            contentPanel.add(dcEnd);

            // Mặc định Tất cả
            cbTimeRange.setSelectedItem("Tất cả");
        }

        // ----------------------------------------------------------------
        // FILTER SÁCH
        // ----------------------------------------------------------------
        if (showBookFilters) {
            contentPanel.add(makeSeparator());

            cbCategory = new JComboBox<>();
            cbCategory.addItem(new FilterItem(0, "Tất cả Thể loại"));
            cbCategory.setPreferredSize(new Dimension(140, 30));

            cbAuthor = new JComboBox<>();
            cbAuthor.addItem(new FilterItem(0, "Tất cả Tác giả"));
            cbAuthor.setPreferredSize(new Dimension(140, 30));

            cbPublisher = new JComboBox<>();
            cbPublisher.addItem(new FilterItem(0, "Tất cả NXB"));
            cbPublisher.setPreferredSize(new Dimension(130, 30));

            loadFilterData();

            cbCategory.addActionListener(e -> executeFilter());
            cbAuthor.addActionListener(e -> executeFilter());
            cbPublisher.addActionListener(e -> executeFilter());

            contentPanel.add(cbCategory);
            contentPanel.add(cbAuthor);
            contentPanel.add(cbPublisher);
        }

        // ----------------------------------------------------------------
        // NÚT LÀM MỚI
        // ----------------------------------------------------------------
        btnReset = new JButton("Làm Mới");
        btnReset.setBackground(new Color(15, 108, 189));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.setPreferredSize(new Dimension(85, 30));

        btnReset.addActionListener(e -> {
            isUpdating = true;

            if (showViewMode && cbViewMode != null)
                cbViewMode.setSelectedIndex(0);

            if (showTimeFilter) {
                cbTimeRange.setSelectedItem("Tất cả");
                cbQuarter.setSelectedIndex(0);
                cbMonth.setSelectedIndex(0);
                cbYear.setSelectedIndex(0); // <-- RESET VỀ "Chọn Năm"
                dcStart.setEnabled(false);
                dcEnd.setEnabled(false);
                autoFillDates("Tất cả");
            }

            if (showBookFilters) {
                if (cbCategory != null)
                    cbCategory.setSelectedIndex(0);
                if (cbAuthor != null)
                    cbAuthor.setSelectedIndex(0);
                if (cbPublisher != null)
                    cbPublisher.setSelectedIndex(0);
            }

            isUpdating = false;
            executeFilter();
        });

        contentPanel.add(btnReset);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, 55));

        add(scrollPane, BorderLayout.CENTER);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 26));
        sep.setForeground(new Color(200, 200, 200));
        return sep;
    }

    private void setDcDates(LocalDate start, LocalDate end) {
        dcStart.setDate(java.util.Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dcEnd.setDate(java.util.Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void fillQuarterDates(String quarter, int year) {
        switch (quarter) {
            case "Quý 1":
                setDcDates(LocalDate.of(year, 1, 1), LocalDate.of(year, 3, 31));
                break;
            case "Quý 2":
                setDcDates(LocalDate.of(year, 4, 1), LocalDate.of(year, 6, 30));
                break;
            case "Quý 3":
                setDcDates(LocalDate.of(year, 7, 1), LocalDate.of(year, 9, 30));
                break;
            case "Quý 4":
                setDcDates(LocalDate.of(year, 10, 1), LocalDate.of(year, 12, 31));
                break;
        }
    }

    // month = 1–12 (index của cbMonth khớp trực tiếp với số tháng)
    private void fillMonthDates(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        setDcDates(start, start.withDayOfMonth(start.lengthOfMonth()));
    }

    private void autoFillDates(String mode) {
        LocalDate today = LocalDate.now();
        LocalDate start, end = today;
        switch (mode) {
            case "Tất cả":
                start = LocalDate.of(2000, 1, 1);
                break;
            case "3 ngày qua":
                start = today.minusDays(3);
                break;
            case "7 ngày qua":
                start = today.minusDays(7);
                break;
            case "30 ngày qua":
                start = today.minusDays(30);
                break;
            case "3 tháng qua":
                start = today.minusMonths(3);
                break;
            case "Năm nay":
                start = LocalDate.of(today.getYear(), 1, 1);
                break;
            default:
                return;
        }
        if (dcStart != null && dcEnd != null)
            setDcDates(start, end);
    }

    // ----------------------------------------------------------------
    // Thực thi filter
    // ----------------------------------------------------------------
    private void executeFilter() {
        if (isUpdating)
            return;
        try {
            Date start = (dcStart != null && dcStart.getDate() != null)
                    ? new Date(dcStart.getDate().getTime())
                    : null;
            Date end = (dcEnd != null && dcEnd.getDate() != null)
                    ? new Date(dcEnd.getDate().getTime())
                    : null;

            int catId = (showBookFilters && cbCategory != null && cbCategory.getSelectedItem() != null)
                    ? ((FilterItem) cbCategory.getSelectedItem()).id
                    : 0;
            int auId = (showBookFilters && cbAuthor != null && cbAuthor.getSelectedItem() != null)
                    ? ((FilterItem) cbAuthor.getSelectedItem()).id
                    : 0;
            int pubId = (showBookFilters && cbPublisher != null && cbPublisher.getSelectedItem() != null)
                    ? ((FilterItem) cbPublisher.getSelectedItem()).id
                    : 0;

            if (listener != null)
                listener.onFilterApplied(start, end, catId, auId, pubId);
        } catch (Exception ex) {
            // silent
        }
    }

    // ----------------------------------------------------------------
    // Public APIs
    // ----------------------------------------------------------------
    public String getViewMode() {
        return (cbViewMode != null) ? cbViewMode.getSelectedItem().toString() : "Theo Ngày";
    }

    public void triggerFilter() {
        executeFilter();
    }

    // ----------------------------------------------------------------
    // Load dữ liệu dropdown sách
    // ----------------------------------------------------------------
    private void loadFilterData() {
        try {
            for (CategoryDTO c : new CategoryBUS().getAll())
                cbCategory.addItem(new FilterItem(c.getId(), c.getName()));
            for (AuthorDTO a : new AuthorBUS().getAll())
                cbAuthor.addItem(new FilterItem(a.getAuthorId(), a.getAuthorName()));
            for (PublisherDTO p : new PublisherBUS().getAll())
                cbPublisher.addItem(new FilterItem(p.getId(), p.getName()));
        } catch (Exception e) {
            // silent
        }
    }

    // ----------------------------------------------------------------
    // Inner class FilterItem
    // ----------------------------------------------------------------
    class FilterItem {
        int id;
        String name;

        FilterItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}