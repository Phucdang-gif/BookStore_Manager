package GUI.components;

import BUS.AuthorBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import DTO.AuthorDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticFilterPanel extends JPanel {
    private JComboBox<String> cbTimeRange, cbQuarter, cbYear, cbViewMode;
    private JTextField txtStartDate, txtEndDate;
    private JComboBox<FilterItem> cbCategory, cbAuthor, cbPublisher;
    private JButton btnReset; // Thêm nút Làm Mới

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean isUpdating = false;
    private FilterListener listener;
    private boolean showBookFilters;

    public interface FilterListener {
        void onFilterApplied(Date startDate, Date endDate, int categoryId, int authorId, int publisherId);
    }

    public StatisticFilterPanel(boolean showTimeFilter, boolean showBookFilters, FilterListener listener) {
        this(showTimeFilter, showBookFilters, false, listener);
    }

    public StatisticFilterPanel(boolean showTimeFilter, boolean showBookFilters, boolean showViewMode,
            FilterListener listener) {
        this.listener = listener;
        this.showBookFilters = showBookFilters;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Bộ Lọc Dữ Liệu", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        contentPanel.setBackground(Color.WHITE);

        // --- FILTER XEM THEO (NGÀY/THÁNG/NĂM) ---
        if (showViewMode) {
            cbViewMode = new JComboBox<>(new String[] { "Theo Ngày", "Theo Tháng", "Theo Năm" });
            cbViewMode.setPreferredSize(new Dimension(100, 30));
            cbViewMode.addActionListener(e -> executeFilter());
            contentPanel.add(new JLabel("Xem theo:"));
            contentPanel.add(cbViewMode);
        }

        // --- FILTER THỜI GIAN ---
        if (showTimeFilter) {
            String[] times = { "Tất cả", "3 ngày qua", "7 ngày qua", "30 ngày qua", "3 tháng qua", "Năm nay",
                    "Tùy chỉnh..." };
            cbTimeRange = new JComboBox<>(times);
            cbTimeRange.setPreferredSize(new Dimension(110, 30));

            cbQuarter = new JComboBox<>(new String[] { "Chọn Quý", "Quý 1", "Quý 2", "Quý 3", "Quý 4" });
            cbQuarter.setPreferredSize(new Dimension(90, 30));

            int currentYear = LocalDate.now().getYear();
            cbYear = new JComboBox<>();
            for (int i = currentYear; i >= 2020; i--) {
                cbYear.addItem(String.valueOf(i));
            }
            cbYear.setPreferredSize(new Dimension(70, 30));

            txtStartDate = new JTextField(9);
            txtEndDate = new JTextField(9);
            txtStartDate.setEnabled(false);
            txtEndDate.setEnabled(false);

            txtStartDate.addActionListener(e -> executeFilter());
            txtEndDate.addActionListener(e -> executeFilter());

            // Đổi mốc thời gian chung
            cbTimeRange.addActionListener(e -> {
                if (isUpdating)
                    return;
                isUpdating = true;
                String selected = cbTimeRange.getSelectedItem().toString();
                boolean isCustom = selected.equals("Tùy chỉnh...");
                txtStartDate.setEnabled(isCustom);
                txtEndDate.setEnabled(isCustom);

                if (!isCustom) {
                    cbQuarter.setSelectedIndex(0);
                    autoFillDates(selected);
                }

                isUpdating = false;

                if (!isCustom) {
                    executeFilter();
                }
            });

            // Đổi Quý hoặc Năm
            java.awt.event.ActionListener quarterListener = e -> {
                if (isUpdating)
                    return;
                String q = cbQuarter.getSelectedItem().toString();
                if (!q.equals("Chọn Quý")) {
                    isUpdating = true;
                    cbTimeRange.setSelectedItem("Tùy chỉnh...");
                    txtStartDate.setEnabled(true);
                    txtEndDate.setEnabled(true);

                    int y = Integer.parseInt(cbYear.getSelectedItem().toString());
                    LocalDate start = null, end = null;
                    switch (q) {
                        case "Quý 1":
                            start = LocalDate.of(y, 1, 1);
                            end = LocalDate.of(y, 3, 31);
                            break;
                        case "Quý 2":
                            start = LocalDate.of(y, 4, 1);
                            end = LocalDate.of(y, 6, 30);
                            break;
                        case "Quý 3":
                            start = LocalDate.of(y, 7, 1);
                            end = LocalDate.of(y, 9, 30);
                            break;
                        case "Quý 4":
                            start = LocalDate.of(y, 10, 1);
                            end = LocalDate.of(y, 12, 31);
                            break;
                    }
                    txtStartDate.setText(start.format(fmt));
                    txtEndDate.setText(end.format(fmt));

                    isUpdating = false;
                    executeFilter();
                }
            };
            cbQuarter.addActionListener(quarterListener);
            cbYear.addActionListener(quarterListener);

            contentPanel.add(new JLabel("Thời gian:"));
            contentPanel.add(cbTimeRange);
            contentPanel.add(new JLabel("Lọc Quý:"));
            contentPanel.add(cbQuarter);
            contentPanel.add(cbYear);
            contentPanel.add(new JLabel("Từ:"));
            contentPanel.add(txtStartDate);
            contentPanel.add(new JLabel("Đến:"));
            contentPanel.add(txtEndDate);

            cbTimeRange.setSelectedItem("30 ngày qua");
        }

        // --- FILTER SÁCH ---
        if (showBookFilters) {
            cbCategory = new JComboBox<>();
            cbCategory.addItem(new FilterItem(0, "Tất cả Thể loại"));
            cbAuthor = new JComboBox<>();
            cbAuthor.addItem(new FilterItem(0, "Tất cả Tác giả"));
            cbPublisher = new JComboBox<>();
            cbPublisher.addItem(new FilterItem(0, "Tất cả NXB"));

            loadFilterData();

            cbCategory.addActionListener(e -> executeFilter());
            cbAuthor.addActionListener(e -> executeFilter());
            cbPublisher.addActionListener(e -> executeFilter());

            contentPanel.add(cbCategory);
            contentPanel.add(cbAuthor);
            contentPanel.add(cbPublisher);
        }

        // --- NÚT LÀM MỚI (RESET) ---
        btnReset = new JButton("Làm Mới");
        btnReset.setBackground(new Color(15, 108, 189));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReset.addActionListener(e -> {
            isUpdating = true; // Khóa auto-filter để tránh trigger liên tục khi đang reset

            // 1. Reset Xem theo
            if (showViewMode && cbViewMode != null) {
                cbViewMode.setSelectedIndex(0);
            }

            // 2. Reset Thời gian
            if (showTimeFilter) {
                cbTimeRange.setSelectedItem("30 ngày qua");
                cbQuarter.setSelectedIndex(0);
                cbYear.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
                txtStartDate.setEnabled(false);
                txtEndDate.setEnabled(false);
                autoFillDates("30 ngày qua");
            }

            // 3. Reset Sách
            if (showBookFilters) {
                if (cbCategory != null)
                    cbCategory.setSelectedIndex(0);
                if (cbAuthor != null)
                    cbAuthor.setSelectedIndex(0);
                if (cbPublisher != null)
                    cbPublisher.setSelectedIndex(0);
            }

            isUpdating = false; // Mở khóa
            executeFilter(); // Chạy bộ lọc 1 lần duy nhất với các giá trị mặc định
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

    private void executeFilter() {
        if (isUpdating)
            return;
        try {
            Date start = (txtStartDate != null && txtStartDate.getText().isEmpty()) ? null
                    : Date.valueOf(LocalDate.parse(txtStartDate.getText(), fmt));
            Date end = (txtEndDate != null && txtEndDate.getText().isEmpty()) ? null
                    : Date.valueOf(LocalDate.parse(txtEndDate.getText(), fmt));

            int catId = (showBookFilters && cbCategory.getSelectedItem() != null)
                    ? ((FilterItem) cbCategory.getSelectedItem()).id
                    : 0;
            int auId = (showBookFilters && cbAuthor.getSelectedItem() != null)
                    ? ((FilterItem) cbAuthor.getSelectedItem()).id
                    : 0;
            int pubId = (showBookFilters && cbPublisher.getSelectedItem() != null)
                    ? ((FilterItem) cbPublisher.getSelectedItem()).id
                    : 0;

            if (listener != null) {
                listener.onFilterApplied(start, end, catId, auId, pubId);
            }
        } catch (Exception ex) {
            if (txtStartDate != null && txtStartDate.isEnabled() && isShowing()) {
                JOptionPane.showMessageDialog(this,
                        "Ngày không hợp lệ! Vui lòng nhập định dạng dd/MM/yyyy và nhấn Enter.", "Lỗi nhập liệu",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public String getViewMode() {
        return (cbViewMode != null) ? cbViewMode.getSelectedItem().toString() : "Theo Ngày";
    }

    private void autoFillDates(String mode) {
        LocalDate today = LocalDate.now();
        LocalDate start = null, end = today;

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
            case "Tùy chỉnh...":
                return;
        }
        if (txtStartDate != null && txtEndDate != null) {
            txtStartDate.setText(start.format(fmt));
            txtEndDate.setText(end.format(fmt));
        }
    }

    private void loadFilterData() {
        try {
            for (CategoryDTO c : new CategoryBUS().getAll())
                cbCategory.addItem(new FilterItem(c.getId(), c.getName()));
            for (AuthorDTO a : new AuthorBUS().getAll())
                cbAuthor.addItem(new FilterItem(a.getAuthorId(), a.getAuthorName()));
            for (PublisherDTO p : new PublisherBUS().getAll())
                cbPublisher.addItem(new FilterItem(p.getId(), p.getName()));
        } catch (Exception e) {
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

    public void triggerFilter() {
        executeFilter();
    }
}