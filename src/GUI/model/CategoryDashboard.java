package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import GUI.components.DashboardCard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategoryDashboard extends JPanel {
    private ActionListener onItemSelected;

    public CategoryDashboard(ActionListener onItemSelected) {
        this.onItemSelected = onItemSelected;
        initStyle();
        initComponents();
    }

    private void initStyle() {
        setLayout(new BorderLayout(20, 20)); // Gap giữa các phần
        setBackground(new Color(245, 245, 245)); // Màu nền xám rất nhạt giống web
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Margin xung quanh
    }

    private void initComponents() {
        // 1. PHẦN TOP: CÁC CARD THỐNG KÊ (Giống hình mẫu)
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0)); // 1 dòng, 3 cột, khoảng cách 20
        pnlCards.setOpaque(false);

        // Card 1: Tác giả (Màu Xanh Dương)
        pnlCards.add(new DashboardCard(
                "Tổng Tác Giả",
                "150",
                "GUI/icon/author.svg",
                new Color(65, 105, 225), // Royal Blue
                () -> sendCommand("AUTHOR")));

        // Card 2: Nhà Xuất Bản (Màu Tím)
        pnlCards.add(new DashboardCard(
                "Nhà Xuất Bản",
                "45",
                "GUI/icon/publisher.svg",
                new Color(147, 112, 219), // Medium Purple
                () -> sendCommand("PUBLISHER")));

        // Card 3: Thể Loại (Màu Vàng Cam)
        pnlCards.add(new DashboardCard(
                "Thể Loại Sách",
                "12",
                "GUI/icon/genre.svg",
                new Color(255, 165, 0), // Orange
                () -> sendCommand("GENRE")));

        add(pnlCards, BorderLayout.NORTH);

        // 2. PHẦN CENTER: BẢNG DỮ LIỆU (Standard Table Design)
        JPanel pnlTableSection = new JPanel(new BorderLayout());
        pnlTableSection.setBackground(Color.WHITE);
        pnlTableSection.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hiệu ứng đổ bóng nhẹ hoặc bo góc cho Panel chứa bảng (tùy chọn)
        // Tiêu đề bảng
        JLabel lblTableTitle = new JLabel("Hoạt động gần đây");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pnlTableSection.add(lblTableTitle, BorderLayout.NORTH);

        // Tạo bảng giả lập
        String[] columns = { "ID", "Tên Mục", "Loại", "Ngày tạo", "Trạng thái" };
        Object[][] data = {
                { "TG001", "Nguyễn Nhật Ánh", "Tác giả", "10/02/2026", "Hoạt động" },
                { "NXB02", "NXB Trẻ", "NXB", "09/02/2026", "Hoạt động" },
                { "TL05", "Khoa học viễn tưởng", "Thể loại", "08/02/2026", "Tạm khóa" },
                { "TG002", "J.K. Rowling", "Tác giả", "05/02/2026", "Hoạt động" },
        };

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền scrollpane
        scrollPane.getViewport().setBackground(Color.WHITE);

        pnlTableSection.add(scrollPane, BorderLayout.CENTER);

        add(pnlTableSection, BorderLayout.CENTER);
    }

    private void sendCommand(String command) {
        if (onItemSelected != null) {
            // Tạo ActionEvent giả để gửi command đi
            onItemSelected.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command));
        }
    }
}