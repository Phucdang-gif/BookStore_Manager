package GUI.model;

import BUS.RevenueReportBUS;
import DTO.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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
    private int currentYear;

    public StatisticPanel() {
        LocalDate today = LocalDate.now();
        currentYear = today.getYear();
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
        tabbedPane.addTab("🌟 Tổng Quan", createOverviewTab());
        tabbedPane.addTab("📈 Doanh Thu Theo Năm", createRevenueTab());
        tabbedPane.addTab("📦 Tồn Kho", createInventoryTab());
        tabbedPane.addTab("🔥 Sách Bán Chạy", createTopBooksTab());
        tabbedPane.addTab("👥 Khách Hàng", createCustomerTab());
        tabbedPane.addTab("💼 Nhân Viên", createEmployeeTab());

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    // ====================================================================
    // HÀM TIỆN ÍCH CHUNG
    // ====================================================================
    private JPanel createFilterFrame(Component... components) {
        JPanel pnlFilter = new JPanel();
        pnlFilter.setLayout(new BoxLayout(pnlFilter, BoxLayout.Y_AXIS));
        pnlFilter.setBackground(Color.WHITE);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Bộ Lọc Báo Cáo");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10), titledBorder));
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
        chart.getTitle().setFont(titleFont);
        chart.getLegend().setItemFont(mainFont);
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
    // TAB 0: TỔNG QUAN (CÓ BIỂU ĐỒ ĐƯỜNG - CURVE CHART)
    // ====================================================================
    private JPanel createOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Dãy Thẻ (Cards) phía trên
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setBackground(Color.WHITE);
        pnlCards.setPreferredSize(new Dimension(0, 120));

        int totalBooks = 0, totalCustomers = 0, totalEmployees = 0;
        try {
            totalBooks = new BUS.BookBUS().getAll().size();
            totalCustomers = new BUS.CustomerBUS().getAll().size();
            totalEmployees = new BUS.EmployeeBUS().getAll().size();
        } catch (Exception e) {}

        pnlCards.add(createSummaryCard("Sản phẩm hiện có trong kho", String.valueOf(totalBooks), new Color(40, 167, 69)));
        pnlCards.add(createSummaryCard("Khách từ trước đến nay", String.valueOf(totalCustomers), new Color(255, 193, 7)));
        pnlCards.add(createSummaryCard("Nhân viên đang hoạt động", String.valueOf(totalEmployees), new Color(23, 162, 184)));
        panel.add(pnlCards, BorderLayout.NORTH);

       // 2. BIỂU ĐỒ ĐƯỜNG (Line Chart) VỚI DỮ LIỆU THẬT 100%
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        
        // Gọi DB lấy dữ liệu 7 ngày qua
        ArrayList<Object[]> dailyStats = reportBUS.get7DaysRevenue();
        
        if (dailyStats != null && !dailyStats.isEmpty()) {
            for (Object[] row : dailyStats) {
                String fullDate = row[0].toString(); // Định dạng MySQL: yyyy-MM-dd
                
                // Cắt chuỗi để hiện lên Biểu đồ cho đẹp (Ví dụ: 2026-03-10 -> 10/03)
                String displayDate = fullDate;
                String[] parts = fullDate.split("-");
                if(parts.length == 3) {
                    displayDate = parts[2] + "/" + parts[1]; 
                }

                double cost = (double) row[1];
                double revenue = (double) row[2];
                double profit = (double) row[3];

                // Đẩy dữ liệu thật vào Thùng chứa của Biểu đồ
                lineDataset.addValue(revenue, "Doanh Thu", displayDate);
                lineDataset.addValue(cost, "Vốn", displayDate);
                lineDataset.addValue(profit, "Lợi Nhuận", displayDate);
            }
        } else {
            // Nếu Database trống trơn (chưa bán được đơn nào trong 7 ngày qua)
            lineDataset.addValue(0, "Doanh Thu", "Hôm nay");
            lineDataset.addValue(0, "Vốn", "Hôm nay");
            lineDataset.addValue(0, "Lợi Nhuận", "Hôm nay");
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "THỐNG KÊ DOANH THU 8 NGÀY GẦN NHẤT", "Ngày", "Số tiền (VNĐ)",
                lineDataset, PlotOrientation.VERTICAL, true, true, false);
        
        applyChartTheme(lineChart);
        
        // Custom đường kẻ
        CategoryPlot plot = lineChart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(102, 51, 153)); // Tím
        renderer.setSeriesPaint(1, new Color(51, 153, 255)); // Xanh
        renderer.setSeriesPaint(2, new Color(255, 153, 51)); // Cam
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(3.0f));
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        panel.add(chartPanel, BorderLayout.CENTER);

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
    // TAB 1: DOANH THU THEO NĂM (CÓ BIỂU ĐỒ CỘT - BAR CHART)
    // ====================================================================
    private JPanel createRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JComboBox<Integer> cbYear = new JComboBox<>();
        for (int i = 2020; i <= 2030; i++) cbYear.addItem(i);
        cbYear.setSelectedItem(currentYear);
        JButton btnFilter = new JButton("Phân Tích Doanh Thu");
        panel.add(createFilterFrame(new JLabel("Chọn Năm:"), cbYear, btnFilter), BorderLayout.WEST);

        // Khung chia 2 nửa: Trên là Biểu đồ, Dưới là Bảng số liệu
        JPanel pnlCenter = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlCenter.setBackground(Color.WHITE);

        // Chuẩn bị Dataset cho Biểu đồ Cột
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart(
                "BIỂU ĐỒ TÀI CHÍNH NĂM " + currentYear, "Tháng", "Số tiền (VNĐ)",
                barDataset, PlotOrientation.VERTICAL, true, true, false);
        
        applyChartTheme(barChart);
        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 153, 51)); // Vốn - Cam
        renderer.setSeriesPaint(1, new Color(51, 153, 255)); // Doanh Thu - Xanh Dương
        renderer.setSeriesPaint(2, new Color(153, 102, 255)); // Lợi nhuận - Tím
        renderer.setItemMargin(0.0); // Chỉnh khoảng cách giữa các cột
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        pnlCenter.add(chartPanel);

        // Chuẩn bị Bảng
        String[] cols = {"Tháng", "Chi Phí (Vốn)", "Doanh Thu", "Lợi Nhuận"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createCustomTable(model);
        pnlCenter.add(new JScrollPane(table));

        panel.add(pnlCenter, BorderLayout.CENTER);

        // Xử lý sự kiện khi bấm nút Thống Kê
        btnFilter.addActionListener(e -> {
            int year = (int) cbYear.getSelectedItem();
            barChart.setTitle("BIỂU ĐỒ TÀI CHÍNH NĂM " + year);
            
            ArrayList<RevenueReportDTO> list = reportBUS.getRevenueReport(year);
            model.setRowCount(0);
            barDataset.clear(); // Xóa biểu đồ cũ
            
            double[] costs = new double[13], revs = new double[13], profs = new double[13];
            for (RevenueReportDTO dto : list) {
                costs[dto.getMonth()] = dto.getCost();
                revs[dto.getMonth()] = dto.getRevenue();
                profs[dto.getMonth()] = dto.getProfit();
            }
            
            for (int i = 1; i <= 12; i++) {
                String mName = "T" + i;
                // Add vào Bảng
                model.addRow(new Object[]{"Tháng " + i, df.format(costs[i]), df.format(revs[i]), df.format(profs[i])});
                // Add vào Biểu đồ
                barDataset.addValue(costs[i], "Vốn", mName);
                barDataset.addValue(revs[i], "Doanh Thu", mName);
                barDataset.addValue(profs[i], "Lợi Nhuận", mName);
            }
        });
        btnFilter.doClick();

        return panel;
    }

    // ====================================================================
    // CÁC TAB CÒN LẠI (TỒN KHO, SÁCH, KHÁCH, NHÂN VIÊN) GIỮ NGUYÊN BẢNG
    // ====================================================================
    private JPanel createInventoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Tồn Kho", "Vốn/Cuốn", "Tổng Giá Trị Tồn"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = createCustomTable(model);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        ArrayList<UnitsInStockDTO> list = reportBUS.getUnitsInStockReport();
        if (list != null) {
            for (UnitsInStockDTO dto : list) {
                model.addRow(new Object[]{dto.getBookID(), dto.getBookTitle(), dto.getAuthor(), dto.getCategory(), dto.getQuantity() + " Cuốn", df.format(dto.getImportPrice()), df.format(dto.getStockValue())});
            }
        }
        return panel;
    }

    private JPanel createTopBooksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth), txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Sách Bán Chạy");
        panel.add(createFilterFrame(new JLabel("Từ ngày:"), txtStart, new JLabel("Đến ngày:"), txtEnd, btnFilter), BorderLayout.WEST);

        String[] cols = {"Thứ Hạng", "Mã Sách", "Tên Sách", "Số Lượng Đã Bán", "Doanh Thu Thu Về"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = createCustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            try {
                ArrayList<BookRevenueDTO> list = reportBUS.getBookReport(new java.sql.Date(sdf.parse(txtStart.getText()).getTime()), new java.sql.Date(sdf.parse(txtEnd.getText()).getTime()));
                model.setRowCount(0);
                for (BookRevenueDTO dto : list) model.addRow(new Object[]{"Top " + dto.getOrdinalNumber(), dto.getBookID(), dto.getBookTitle(), dto.getTotalSold() + " Cuốn", df.format(dto.getTotalRevenue())});
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!"); }
        });
        btnFilter.doClick(); return panel;
    }

    private JPanel createCustomerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth), txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Khách Hàng");
        panel.add(createFilterFrame(new JLabel("Từ ngày:"), txtStart, new JLabel("Đến ngày:"), txtEnd, btnFilter), BorderLayout.WEST);

        String[] cols = {"Thứ Hạng", "Mã KH", "Tên Khách Hàng", "Số Lần Mua", "Tổng Tiền Đã Chi"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = createCustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            try {
                ArrayList<CustomerRevenueDTO> list = reportBUS.getCustomerReport(new java.sql.Date(sdf.parse(txtStart.getText()).getTime()), new java.sql.Date(sdf.parse(txtEnd.getText()).getTime()));
                model.setRowCount(0);
                for (CustomerRevenueDTO dto : list) model.addRow(new Object[]{"Top " + dto.getOrdinalnumber(), dto.getCustomerID(), dto.getFullname(), dto.getTotalinvoices() + " Lần", df.format(dto.getTotalamount())});
            } catch (Exception ex) {}
        });
        btnFilter.doClick(); return panel;
    }

    private JPanel createEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth), txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Nhân Viên");
        panel.add(createFilterFrame(new JLabel("Từ ngày:"), txtStart, new JLabel("Đến ngày:"), txtEnd, btnFilter), BorderLayout.WEST);

        String[] cols = {"Thứ Hạng", "Mã NV", "Tên Nhân Viên", "Số Hóa Đơn Lập", "Doanh Thu Đem Về"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = createCustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            try {
                ArrayList<EmployeeRevenueDTO> list = reportBUS.getEmployeeReport(new java.sql.Date(sdf.parse(txtStart.getText()).getTime()), new java.sql.Date(sdf.parse(txtEnd.getText()).getTime()));
                model.setRowCount(0);
                for (EmployeeRevenueDTO dto : list) model.addRow(new Object[]{"Top " + dto.getOrdinalnumber(), dto.getEmployeeID(), dto.getFullname(), dto.getTotalInvoice() + " HĐ", df.format(dto.getTotalRevenue())});
            } catch (Exception ex) {}
        });
        btnFilter.doClick(); return panel;
    }

    @Override
    public boolean[] getButtonConfig() { return new boolean[]{false, false, false, false, false, false}; }
    @Override public void onAdd() {} @Override public void onEdit() {} @Override public void onDelete() {}
    @Override public void onDetail() {} @Override public void onSearch(String text) {} @Override public void onRefresh() {}
    @Override public void onExportExcel() {} @Override public void onImportExcel() {}
}