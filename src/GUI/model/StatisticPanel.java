package GUI.model;

import BUS.RevenueReportBUS;
import DTO.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StatisticPanel extends JPanel implements FeatureControllerInterface {

    private RevenueReportBUS reportBUS = new RevenueReportBUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private String firstDayOfMonth;
    private String lastDayOfMonth;

    public StatisticPanel() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        firstDayOfMonth = today.withDayOfMonth(1).format(formatter);
        lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth()).format(formatter);

        initUI();
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // THỨ TỰ CÁC TAB
        tabbedPane.addTab(" Tổng Quan", createOverviewTab());
        tabbedPane.addTab(" Doanh Thu", createRevenueTab());
        tabbedPane.addTab(" Sách Bán Chạy", createTopBooksTab());
        tabbedPane.addTab(" Nhập hàng", createImportTab());
        tabbedPane.addTab(" Khách Hàng", createCustomerTab());
        tabbedPane.addTab(" Nhân Viên", createEmployeeTab());

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    // ====================================================================
    // HÀM TIỆN ÍCH CHUNG
    // ====================================================================
    private JPanel createFilterFrame(Component... components) {
        JPanel pnlFilter = new JPanel();
        pnlFilter.setLayout(new BoxLayout(pnlFilter, BoxLayout.Y_AXIS));
        pnlFilter.setBackground(Color.WHITE);
        TitledBorder titledBorder = BorderFactory
                .createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Bộ Lọc Báo Cáo");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlFilter.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10), titledBorder));
        pnlFilter.setPreferredSize(new Dimension(240, 0));
        pnlFilter.add(Box.createVerticalStrut(10));

        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
                ((JLabel) comp).setFont(new Font("Segoe UI", Font.PLAIN, 13));
            } else if (comp instanceof JTextField || comp instanceof JComboBox) {
                ((JComponent) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
                ((JComponent) comp).setMaximumSize(new Dimension(190, 35));
            } else if (comp instanceof JButton) {
                ((JButton) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
                ((JButton) comp).setMaximumSize(new Dimension(190, 40));
                comp.setBackground(new Color(15, 108, 189));
                comp.setForeground(Color.WHITE);
                comp.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
            pnlFilter.add(comp);
            pnlFilter.add(Box.createVerticalStrut(10));
        }
        return pnlFilter;
    }

    private JTable createCustomTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.setSelectionBackground(new Color(204, 229, 255));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        return table;
    }

    private void applyChartTheme(JFreeChart chart) {
        Font mainFont = new Font("Segoe UI", Font.PLAIN, 12);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
        if (chart.getTitle() != null) {
            chart.getTitle().setFont(titleFont);
        }
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(mainFont);
        }
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        plot.getDomainAxis().setTickLabelFont(mainFont);
        plot.getDomainAxis().setLabelFont(mainFont);
        plot.getRangeAxis().setTickLabelFont(mainFont);
        plot.getRangeAxis().setLabelFont(mainFont);
    }

    // ====================================================================
    // TAB TỔNG QUAN (CÓ BIỂU ĐỒ ĐƯỜNG - KẾT HỢP FILTER CHUNG CÓ XEM THEO)
    // ====================================================================
    private JPanel createOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(Color.WHITE);

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setBackground(Color.WHITE);
        pnlCards.setPreferredSize(new Dimension(0, 120));

        int totalBooks = 0, totalCustomers = 0, totalEmployees = 0;
        try {
            totalBooks = new BUS.BookBUS().getAll().size();
            totalCustomers = new BUS.CustomerBUS().getAll().size();
            totalEmployees = new BUS.EmployeeBUS().getAll().size();
        } catch (Exception e) {
        }

        pnlCards.add(
                createSummaryCard("Sản phẩm hiện có trong kho", String.valueOf(totalBooks), new Color(40, 167, 69)));
        pnlCards.add(
                createSummaryCard("Khách từ trước đến nay", String.valueOf(totalCustomers), new Color(255, 193, 7)));
        pnlCards.add(
                createSummaryCard("Nhân viên đang hoạt động", String.valueOf(totalEmployees), new Color(23, 162, 184)));

        topPanel.add(pnlCards, BorderLayout.NORTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Tự động load doanh thu theo từng ngày trong tháng hiện tại (không có filter)
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        java.sql.Date startDate = java.sql.Date.valueOf(firstDay);
        java.sql.Date endDate = java.sql.Date.valueOf(lastDay);

        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();

        String monthTitle = String.format("DOANH THU THÁNG %d/%d (THEO NGÀY)", today.getMonthValue(), today.getYear());
        JFreeChart lineChart = ChartFactory.createLineChart(
                monthTitle, "Ngày", "Số tiền (VNĐ)",
                lineDataset, PlotOrientation.VERTICAL, true, true, true);

        applyChartTheme(lineChart);

        CategoryPlot plot = lineChart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(51, 153, 255));
        renderer.setSeriesPaint(1, new Color(255, 153, 51));
        renderer.setSeriesPaint(2, new Color(102, 51, 153));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(3.0f));
        renderer.setDefaultToolTipGenerator(new org.jfree.chart.labels.StandardCategoryToolTipGenerator());
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setDisplayToolTips(true);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(chartPanel, BorderLayout.CENTER);

        // Load dữ liệu ngay khi khởi tạo
        SwingUtilities.invokeLater(() -> {
            try {
                ArrayList<Object[]> list = reportBUS.getRevenueByDateRange(startDate, endDate);
                lineDataset.clear();
                if (list != null && !list.isEmpty()) {
                    for (Object[] row : list) {
                        String timeLabel = row[0].toString();
                        String[] parts = timeLabel.split("-");
                        if (parts.length == 3)
                            timeLabel = parts[2] + "/" + parts[1]; // VD: 05/03
                        double cost = (double) row[1];
                        double revenue = (double) row[2];
                        double profit = (double) row[3];
                        lineDataset.addValue(revenue, "Doanh Thu", timeLabel);
                        lineDataset.addValue(cost, "Vốn", timeLabel);
                        lineDataset.addValue(profit, "Lợi Nhuận", timeLabel);
                    }
                } else {
                    lineDataset.addValue(0, "Doanh Thu", "Không có dữ liệu");
                    lineDataset.addValue(0, "Vốn", "Không có dữ liệu");
                    lineDataset.addValue(0, "Lợi Nhuận", "Không có dữ liệu");
                }
            } catch (Exception ex) {
                /* silent */ }
        });

        return panel;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 4), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.DARK_GRAY);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        return card;
    }

    // ====================================================================
    // TAB DOANH THU (CÓ BIỂU ĐỒ CỘT - KẾT HỢP FILTER CÓ XEM THEO)
    // ====================================================================
    private JPanel createRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart("DOANH THU THEO THỜI GIAN", "Thời Gian", "VNĐ", barDataset,
                PlotOrientation.VERTICAL, true, true, true); // tooltips = true
        applyChartTheme(barChart);

        String[] cols = { "Thời Gian", "Chi Phí (Vốn)", "Doanh Thu", "Lợi Nhuận" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = createCustomTable(model);

        ChartPanel barChartPanel = new ChartPanel(barChart);
        barChartPanel.setDisplayToolTips(true);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, barChartPanel,
                new JScrollPane(table));
        splitPane.setResizeWeight(0.6);
        panel.add(splitPane, BorderLayout.CENTER);

        final GUI.components.StatisticFilterPanel[] filterHolder = new GUI.components.StatisticFilterPanel[1];
        filterHolder[0] = new GUI.components.StatisticFilterPanel(true, false, false,
                (startDate, endDate, catId, auId, pubId) -> {

                    String mode = filterHolder[0].getViewMode();
                    ArrayList<Object[]> list = new ArrayList<>();

                    if (mode.equals("Theo Ngày")) {
                        list = reportBUS.getRevenueByDateRange(startDate, endDate);
                    } else if (mode.equals("Theo Tháng")) {
                        list = reportBUS.getRevenueByMonthRange(startDate, endDate);
                    } else if (mode.equals("Theo Năm")) {
                        list = reportBUS.getRevenueByYearRange(startDate, endDate);
                    }

                    model.setRowCount(0);
                    barDataset.clear();
                    if (list != null) {
                        for (Object[] row : list) {
                            String timeLabel = row[0].toString();

                            // Format ngày dd/mm/yyyy hiển thị trên bảng cho đẹp
                            if (mode.equals("Theo Ngày")) {
                                String[] parts = timeLabel.split("-");
                                if (parts.length == 3)
                                    timeLabel = parts[2] + "/" + parts[1] + "/" + parts[0];
                            }

                            double cost = (double) row[1], rev = (double) row[2], prof = (double) row[3];
                            model.addRow(new Object[] { timeLabel, df.format(cost), df.format(rev), df.format(prof) });
                            barDataset.addValue(cost, "Vốn", timeLabel);
                            barDataset.addValue(rev, "Doanh Thu", timeLabel);
                            barDataset.addValue(prof, "Lợi Nhuận", timeLabel);
                        }
                    } else {
                        barDataset.addValue(0, "Vốn", "Không có dữ liệu");
                        barDataset.addValue(0, "Doanh Thu", "Không có dữ liệu");
                        barDataset.addValue(0, "Lợi Nhuận", "Không có dữ liệu");
                    }
                });
        panel.add(filterHolder[0], BorderLayout.NORTH);

        SwingUtilities.invokeLater(() -> filterHolder[0].triggerFilter());
        return panel;
    }

    // ====================================================================
    // CÁC TAB CÒN LẠI GIỮ NGUYÊN (Không ảnh hưởng bởi Xem theo)
    // ====================================================================
    private JPanel createImportTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        String[] cols = { "Mã Phiếu", "Ngày Nhập", "Mã NCC", "Mã NV", "Tổng Tiền (VNĐ)", "Trạng Thái" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class; // Mã Phiếu → sort số đúng
                    case 4:
                        return Double.class; // Tổng Tiền → sort số đúng
                    default:
                        return String.class;
                }
            }
        };
        JTable table = createCustomTable(model);
        table.setAutoCreateRowSorter(true);

        // Renderer hiển thị Tổng Tiền dạng #,### VNĐ dù lưu kiểu Double
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value instanceof Number ? df.format(((Number) value).doubleValue()) : "");
                setHorizontalAlignment(JLabel.RIGHT);
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(Color.WHITE);
        JButton btnViewDetail = new JButton("Xem Chi Tiết Phiếu Nhập");
        btnViewDetail.setBackground(new Color(40, 167, 69));
        btnViewDetail.setForeground(Color.WHITE);
        btnViewDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnViewDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlBottom.add(btnViewDetail);

        panel.add(pnlBottom, BorderLayout.SOUTH);

        btnViewDetail.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn một phiếu nhập trên bảng để xem chi tiết!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Convert view index → model index để lấy đúng ID khi đang sort
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int receiptId = (int) model.getValueAt(modelRow, 0);
            Window parentWindow = SwingUtilities.getWindowAncestor(panel);
            Frame parentFrame = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;

            new GUI.dialog.ImportDetailDialog(parentFrame, true, receiptId).setVisible(true);
        });

        // Sử dụng bộ lọc cũ (không có showViewMode -> tự ẩn đi)
        GUI.components.StatisticFilterPanel filterPanel = new GUI.components.StatisticFilterPanel(true, false,
                (startDate, endDate, catId, auId, pubId) -> {

                    BUS.ImportReceiptBUS importBus = new BUS.ImportReceiptBUS();
                    ArrayList<DTO.ImportReceiptDTO> allReceipts = importBus.getAll();

                    model.setRowCount(0);

                    if (allReceipts != null) {
                        for (DTO.ImportReceiptDTO receipt : allReceipts) {
                            java.util.Calendar cal = java.util.Calendar.getInstance();
                            cal.setTime(receipt.getReceiptDate());
                            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            cal.set(java.util.Calendar.MINUTE, 0);
                            cal.set(java.util.Calendar.SECOND, 0);
                            cal.set(java.util.Calendar.MILLISECOND, 0);
                            java.sql.Date receiptDate = new java.sql.Date(cal.getTimeInMillis());

                            boolean passStart = (startDate == null) || !receiptDate.before(startDate);
                            boolean passEnd = (endDate == null) || !receiptDate.after(endDate);

                            if (passStart && passEnd) {
                                model.addRow(new Object[] {
                                        receipt.getReceiptId(),
                                        sdf.format(receipt.getReceiptDate()),
                                        "NCC " + receipt.getSupplierId(),
                                        "NV " + receipt.getEmployeeId(),
                                        receipt.getTotalAmount(), // Double → sort đúng
                                        receipt.getStatus()
                                });
                            }
                        }
                    }
                });
        panel.add(filterPanel, BorderLayout.NORTH);
        SwingUtilities.invokeLater(() -> filterPanel.triggerFilter());

        return panel;
    }

    private JPanel createTopBooksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth), txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Sách Bán Chạy");
        panel.add(createFilterFrame(new JLabel("Từ ngày:"), txtStart, new JLabel("Đến ngày:"), txtEnd, btnFilter),
                BorderLayout.WEST);

        String[] cols = { "Thứ Hạng", "Mã Sách", "Tên Sách", "Số Lượng Đã Bán", "Doanh Thu Thu Về" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createCustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            try {
                ArrayList<BookRevenueDTO> list = reportBUS.getBookReport(
                        new java.sql.Date(sdf.parse(txtStart.getText()).getTime()),
                        new java.sql.Date(sdf.parse(txtEnd.getText()).getTime()));
                model.setRowCount(0);
                for (BookRevenueDTO dto : list)
                    model.addRow(new Object[] { "Top " + dto.getOrdinalNumber(), dto.getBookID(), dto.getBookTitle(),
                            dto.getTotalSold() + " Cuốn", df.format(dto.getTotalRevenue()) });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!");
            }
        });
        btnFilter.doClick();
        return panel;
    }

    private JPanel createCustomerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth), txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Khách Hàng");
        panel.add(createFilterFrame(new JLabel("Từ ngày:"), txtStart, new JLabel("Đến ngày:"), txtEnd, btnFilter),
                BorderLayout.WEST);

        String[] cols = { "Thứ Hạng", "Mã KH", "Tên Khách Hàng", "Số Lần Mua", "Tổng Tiền Đã Chi" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createCustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            try {
                ArrayList<CustomerRevenueDTO> list = reportBUS.getCustomerReport(
                        new java.sql.Date(sdf.parse(txtStart.getText()).getTime()),
                        new java.sql.Date(sdf.parse(txtEnd.getText()).getTime()));
                model.setRowCount(0);
                for (CustomerRevenueDTO dto : list)
                    model.addRow(new Object[] { "Top " + dto.getOrdinalnumber(), dto.getCustomerID(), dto.getFullname(),
                            dto.getTotalinvoices() + " Lần", df.format(dto.getTotalamount()) });
            } catch (Exception ex) {
            }
        });
        btnFilter.doClick();
        return panel;
    }

    private JPanel createEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth), txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Nhân Viên");
        panel.add(createFilterFrame(new JLabel("Từ ngày:"), txtStart, new JLabel("Đến ngày:"), txtEnd, btnFilter),
                BorderLayout.WEST);

        String[] cols = { "Thứ Hạng", "Mã NV", "Tên Nhân Viên", "Số Hóa Đơn Lập", "Doanh Thu Đem Về" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createCustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            try {
                ArrayList<EmployeeRevenueDTO> list = reportBUS.getEmployeeReport(
                        new java.sql.Date(sdf.parse(txtStart.getText()).getTime()),
                        new java.sql.Date(sdf.parse(txtEnd.getText()).getTime()));
                model.setRowCount(0);
                for (EmployeeRevenueDTO dto : list)
                    model.addRow(new Object[] { "Top " + dto.getOrdinalnumber(), dto.getEmployeeID(), dto.getFullname(),
                            dto.getTotalInvoice() + " HĐ", df.format(dto.getTotalRevenue()) });
            } catch (Exception ex) {
            }
        });
        btnFilter.doClick();
        return panel;
    }

    @Override
    public boolean[] getButtonConfig() {
        return new boolean[] { false, false, false, false, false, false };
    }

    @Override
    public boolean hasSearch() {
        return false;
    }

    @Override
    public boolean hasRefresh() {
        return false;
    }

    @Override
    public void onSearch(String text) {
    }

    @Override
    public void onRefresh() {
        this.removeAll();
        this.reportBUS = new RevenueReportBUS();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        firstDayOfMonth = today.withDayOfMonth(1).format(formatter);
        lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth()).format(formatter);

        initUI();

        this.revalidate();
        this.repaint();
    }


    @Override
    public void onAdd() {
    }

    @Override
    public void onEdit() {
    }

    @Override
    public void onDelete() {
    }

    @Override
    public void onDetail() {
    }

    @Override
    public void onExportExcel() {
    }

    @Override
    public void onImportExcel() {
    }
}