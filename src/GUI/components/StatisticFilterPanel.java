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
    private JComboBox<String> cbTimeRange;
    private JTextField txtStartDate, txtEndDate;
    private JComboBox<FilterItem> cbCategory, cbAuthor, cbPublisher;
    private JButton btnFilter;

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Interface callback để gửi dữ liệu về Panel chính
    public interface FilterListener {
        void onFilterApplied(Date startDate, Date endDate, int categoryId, int authorId, int publisherId);
    }

    public StatisticFilterPanel(boolean showTimeFilter, boolean showBookFilters, FilterListener listener) {
        // 1. SỬ DỤNG BORDERLAYOUT CHO PANEL CHÍNH ĐỂ CHỨA THANH CUỘN
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Bộ Lọc Dữ Liệu", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        // 2. TẠO PANEL CON (CONTENT PANEL) CHỨA CÁC CONTROL LỌC
        // Sử dụng FlowLayout để các item nằm ngang trên 1 dòng
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        contentPanel.setBackground(Color.WHITE);

        // --- FILTER THỜI GIAN ---
        if (showTimeFilter) {
            String[] times = { "Tất cả", "3 ngày qua", "7 ngày qua", "30 ngày qua", "3 tháng qua",
                    "Quý 1", "Quý 2", "Quý 3", "Quý 4", "Năm nay", "Tùy chỉnh..." };
            cbTimeRange = new JComboBox<>(times);
            cbTimeRange.setPreferredSize(new Dimension(130, 30));

            txtStartDate = new JTextField(10);
            txtEndDate = new JTextField(10);
            txtStartDate.setEnabled(false);
            txtEndDate.setEnabled(false);

            cbTimeRange.addActionListener(e -> {
                boolean isCustom = cbTimeRange.getSelectedItem().equals("Tùy chỉnh...");
                txtStartDate.setEnabled(isCustom);
                txtEndDate.setEnabled(isCustom);
                if (!isCustom)
                    autoFillDates(cbTimeRange.getSelectedItem().toString());
            });

            // Add vào Content Panel
            contentPanel.add(new JLabel("Thời gian:"));
            contentPanel.add(cbTimeRange);
            contentPanel.add(new JLabel("Từ:"));
            contentPanel.add(txtStartDate);
            contentPanel.add(new JLabel("Đến:"));
            contentPanel.add(txtEndDate);

            cbTimeRange.setSelectedItem("30 ngày qua"); // Default
        }

        // --- FILTER SÁCH (Tác giả, Thể loại, NXB) ---
        if (showBookFilters) {
            cbCategory = new JComboBox<>();
            cbCategory.addItem(new FilterItem(0, "Tất cả Thể loại"));
            cbAuthor = new JComboBox<>();
            cbAuthor.addItem(new FilterItem(0, "Tất cả Tác giả"));
            cbPublisher = new JComboBox<>();
            cbPublisher.addItem(new FilterItem(0, "Tất cả NXB"));

            loadFilterData(); // Tải dữ liệu từ BUS

            // Gắn sự kiện để tự động lọc khi chọn mục trong Combobox
            cbCategory.addActionListener(e -> {
                if (btnFilter != null)
                    btnFilter.doClick();
            });
            cbAuthor.addActionListener(e -> {
                if (btnFilter != null)
                    btnFilter.doClick();
            });
            cbPublisher.addActionListener(e -> {
                if (btnFilter != null)
                    btnFilter.doClick();
            });

            // Add vào Content Panel
            contentPanel.add(cbCategory);
            contentPanel.add(cbAuthor);
            contentPanel.add(cbPublisher);
        }

        // --- NÚT ÁP DỤNG LỌC ---
        btnFilter = new JButton("Áp Dụng Lọc");
        btnFilter.setBackground(new Color(15, 108, 189));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnFilter.addActionListener(e -> {
            try {
                Date start = txtStartDate.getText().isEmpty() ? null
                        : Date.valueOf(LocalDate.parse(txtStartDate.getText(), fmt));
                Date end = txtEndDate.getText().isEmpty() ? null
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
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ! Vui lòng nhập định dạng dd/MM/yyyy");
            }
        });
        contentPanel.add(btnFilter);

        // 3. BỌC CONTENT PANEL VÀO JSCROLLPANE
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        // Bật thanh cuộn ngang khi cần thiết, tắt thanh cuộn dọc
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null); // Xóa viền của thanh cuộn để giao diện không bị thô
        scrollPane.getViewport().setBackground(Color.WHITE); // Trắng đồng bộ

        // Tăng tốc độ cuộn chuột (hoặc touchpad) cho mượt mà
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        // Thiết lập chiều cao tối thiểu để không bị lẹm khi thanh cuộn hiện lên
        scrollPane.setPreferredSize(new Dimension(0, 65));

        // Thêm thanh cuộn vào giao diện chính
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- Logic tự động tính ngày ---
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
            case "Quý 1":
                start = LocalDate.of(today.getYear(), 1, 1);
                end = LocalDate.of(today.getYear(), 3, 31);
                break;
            case "Quý 2":
                start = LocalDate.of(today.getYear(), 4, 1);
                end = LocalDate.of(today.getYear(), 6, 30);
                break;
            case "Quý 3":
                start = LocalDate.of(today.getYear(), 7, 1);
                end = LocalDate.of(today.getYear(), 9, 30);
                break;
            case "Quý 4":
                start = LocalDate.of(today.getYear(), 10, 1);
                end = LocalDate.of(today.getYear(), 12, 31);
                break;
            case "Năm nay":
                start = LocalDate.of(today.getYear(), 1, 1);
                break;
            case "Tùy chỉnh...":
                return;
        }
        txtStartDate.setText(start.format(fmt));
        txtEndDate.setText(end.format(fmt));
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

    // --- Inner class hỗ trợ lưu ID và Text cho Combobox ---
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
        if (btnFilter != null) {
            btnFilter.doClick();
        }
    }
}