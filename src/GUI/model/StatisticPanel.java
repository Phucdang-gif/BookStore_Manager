package GUI.model;

import BUS.RevenueReportBUS;
import DTO.*;

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

public class StatisticPanel extends JPanel {

    private RevenueReportBUS reportBUS = new RevenueReportBUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private String firstDayOfMonth;
    private String lastDayOfMonth;
    private int currentYear;

    public StatisticPanel() {
        // Lấy ngày tự động
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

        tabbedPane.addTab(" Doanh Thu Theo Năm", createRevenueTab());
        tabbedPane.addTab(" Tồn Kho Hiện Tại", createInventoryTab());
        tabbedPane.addTab(" Top Sách Bán Chạy", createTopBooksTab());
        tabbedPane.addTab(" Khách Hàng VIP", createCustomerTab());
        tabbedPane.addTab(" Hiệu Suất Nhân Viên", createEmployeeTab());

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    // ====================================================================
    // HÀM TIỆN ÍCH: TẠO KHUNG BỘ LỌC BÊN TRÁI
    // ====================================================================
    private JPanel createFilterFrame(Component... components) {
        JPanel pnlFilter = new JPanel();
        pnlFilter.setLayout(new BoxLayout(pnlFilter, BoxLayout.Y_AXIS));
        pnlFilter.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Bộ Lọc Báo Cáo");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 10), titledBorder));

        pnlFilter.setPreferredSize(new Dimension(240, 0));
        pnlFilter.add(Box.createVerticalStrut(10));

        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
                ((JLabel) comp).setFont(new Font("Segoe UI", Font.PLAIN, 13));
                pnlFilter.add(comp);
                pnlFilter.add(Box.createVerticalStrut(5));
            } else if (comp instanceof JTextField || comp instanceof JComboBox) {
                ((JComponent) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
                ((JComponent) comp).setMaximumSize(new Dimension(190, 35));
                pnlFilter.add(comp);
                pnlFilter.add(Box.createVerticalStrut(15));
            } else if (comp instanceof JButton) {
                ((JButton) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
                ((JButton) comp).setMaximumSize(new Dimension(190, 40));
                ((JButton) comp).setBackground(new Color(15, 108, 189));
                ((JButton) comp).setForeground(Color.WHITE);
                ((JButton) comp).setFont(new Font("Segoe UI", Font.BOLD, 13));
                ((JButton) comp).setCursor(new Cursor(Cursor.HAND_CURSOR));
                pnlFilter.add(comp);
                pnlFilter.add(Box.createVerticalStrut(10));
            }
        }
        return pnlFilter;
    }

    // ====================================================================
    // HÀM TIỆN ÍCH: ĐỊNH DẠNG JTABLE CHUẨN ĐẸP
    // ====================================================================
    private JTable createCustomTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.setSelectionBackground(new Color(204, 229, 255));

        // Canh giữa dữ liệu cho cột đầu tiên (Thường là STT hoặc Tháng)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        return table;
    }

    // ====================================================================
    // TAB 1: DOANH THU THEO NĂM
    // ====================================================================
    private JPanel createRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JComboBox<Integer> cbYear = new JComboBox<>();
        for (int i = 2020; i <= 2030; i++)
            cbYear.addItem(i);
        cbYear.setSelectedItem(currentYear);
        JButton btnFilter = new JButton("Thống Kê Doanh Thu");

        panel.add(createFilterFrame(new JLabel("Chọn Năm:"), cbYear, btnFilter), BorderLayout.WEST);

        String[] cols = { "Tháng", "Tổng Vốn Nhập", "Tổng Doanh Thu", "Lợi Nhuận Gộp" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createCustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            int year = (int) cbYear.getSelectedItem();
            ArrayList<RevenueReportDTO> list = reportBUS.getRevenueReport(year);
            model.setRowCount(0);

            double[] costs = new double[13], revs = new double[13], profs = new double[13];
            for (RevenueReportDTO dto : list) {
                costs[dto.getMonth()] = dto.getCost();
                revs[dto.getMonth()] = dto.getRevenue();
                profs[dto.getMonth()] = dto.getProfit();
            }
            for (int i = 1; i <= 12; i++) {
                model.addRow(
                        new Object[] { "Tháng " + i, df.format(costs[i]), df.format(revs[i]), df.format(profs[i]) });
            }
        });
        btnFilter.doClick();
        return panel;
    }

    // ====================================================================
    // TAB 2: TỒN KHO HIỆN TẠI (Không cần bộ lọc ngày)
    // ====================================================================
    private JPanel createInventoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] cols = { "Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Tồn Kho", "Vốn/Cuốn", "Tổng Giá Trị Tồn" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createCustomTable(model);
        // Chỉnh cho cột tên sách rộng ra
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Gọi dữ liệu trực tiếp không qua nút bấm
        ArrayList<UnitsInStockDTO> list = reportBUS.getUnitsInStockReport();
        if (list != null) {
            for (UnitsInStockDTO dto : list) {
                model.addRow(new Object[] {
                        dto.getBookID(), dto.getBookTitle(), dto.getAuthor(), dto.getCategory(),
                        dto.getQuantity() + " Cuốn", df.format(dto.getImportPrice()), df.format(dto.getStockValue())
                });
            }
        }
        return panel;
    }

    // ====================================================================
    // TAB 3: TOP SÁCH BÁN CHẠY
    // ====================================================================
    private JPanel createTopBooksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth);
        JTextField txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Sách Bán Chạy");

        panel.add(createFilterFrame(
                new JLabel("Từ ngày (dd/mm/yyyy):"), txtStart,
                new JLabel("Đến ngày (dd/mm/yyyy):"), txtEnd,
                btnFilter), BorderLayout.WEST);

        String[] cols = { "Thứ Hạng", "Mã Sách", "Tên Sách", "Số Lượng Đã Bán", "Doanh Thu Thu Về" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createCustomTable(model);
        table.getColumnModel().getColumn(2).setPreferredWidth(250); // Tên sách rộng hơn
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            try {
                java.sql.Date sqlStart = new java.sql.Date(sdf.parse(txtStart.getText().trim()).getTime());
                java.sql.Date sqlEnd = new java.sql.Date(sdf.parse(txtEnd.getText().trim()).getTime());
                ArrayList<BookRevenueDTO> list = reportBUS.getBookReport(sqlStart, sqlEnd);
                model.setRowCount(0);
                for (BookRevenueDTO dto : list) {
                    model.addRow(new Object[] {
                            "Top " + dto.getOrdinalNumber(), dto.getBookID(), dto.getBookTitle(),
                            dto.getTotalSold() + " Cuốn", df.format(dto.getTotalRevenue())
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ! Vui lòng nhập đúng định dạng dd/MM/yyyy.");
            }
        });
        btnFilter.doClick();
        return panel;
    }

    // ====================================================================
    // TAB 4: KHÁCH HÀNG VIP
    // ====================================================================
    private JPanel createCustomerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth);
        JTextField txtEnd = new JTextField(lastDayOfMonth);
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
                java.sql.Date sqlStart = new java.sql.Date(sdf.parse(txtStart.getText().trim()).getTime());
                java.sql.Date sqlEnd = new java.sql.Date(sdf.parse(txtEnd.getText().trim()).getTime());
                ArrayList<CustomerRevenueDTO> list = reportBUS.getCustomerReport(sqlStart, sqlEnd);
                model.setRowCount(0);
                for (CustomerRevenueDTO dto : list) {
                    model.addRow(new Object[] {
                            "Top " + dto.getOrdinalnumber(), dto.getCustomerID(), dto.getFullname(),
                            dto.getTotalinvoices() + " Lần", df.format(dto.getTotalamount())
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!");
            }
        });
        btnFilter.doClick();
        return panel;
    }

    // ====================================================================
    // TAB 5: HIỆU SUẤT NHÂN VIÊN
    // ====================================================================
    private JPanel createEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txtStart = new JTextField(firstDayOfMonth);
        JTextField txtEnd = new JTextField(lastDayOfMonth);
        JButton btnFilter = new JButton("Lọc Nhân Viên");

        panel.add(createFilterFrame(new JLabel("Từ ngày:"), txtStart, new JLabel("Đến ngày:"), txtEnd, btnFilter),
                BorderLayout.WEST);

        String[] cols = { "Thứ Hạng", "Mã NV", "Tên Nhân Viên", "Số Hóa Đơn Đã Lập", "Doanh Thu Đem Về" };
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
                java.sql.Date sqlStart = new java.sql.Date(sdf.parse(txtStart.getText().trim()).getTime());
                java.sql.Date sqlEnd = new java.sql.Date(sdf.parse(txtEnd.getText().trim()).getTime());
                ArrayList<EmployeeRevenueDTO> list = reportBUS.getEmployeeReport(sqlStart, sqlEnd);
                model.setRowCount(0);
                for (EmployeeRevenueDTO dto : list) {
                    model.addRow(new Object[] {
                            "Top " + dto.getOrdinalnumber(), dto.getEmployeeID(), dto.getFullname(),
                            dto.getTotalInvoice() + " HĐ", df.format(dto.getTotalRevenue())
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!");
            }
        });
        btnFilter.doClick();
        return panel;
    }

    // ====================================================================
    // IMPLEMENTS FEATURE CONTROLLER (Dọn dẹp Header)
    // ====================================================================
}