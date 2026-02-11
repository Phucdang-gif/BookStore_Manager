package GUI.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import BUS.AuthorBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import DTO.AuthorDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import GUI.components.DashboardCard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GroupDashboard extends JPanel {
    private ActionListener onItemSelected;
    private AuthorBUS authorBUS;
    private CategoryBUS categoryBUS;
    private PublisherBUS publisherBUS;

    private DashboardCard cardAuthor;
    private DashboardCard cardPublisher;
    private DashboardCard cardCategory;
    private DefaultTableModel tableModel;

    public GroupDashboard(ActionListener onItemSelected) {
        this.onItemSelected = onItemSelected;
        authorBUS = new AuthorBUS();
        categoryBUS = new CategoryBUS();
        publisherBUS = new PublisherBUS();
        initStyle();
        initComponents();
        refreshData();
    }

    private void initStyle() {
        setLayout(new BorderLayout(20, 20)); // Gap giữa các phần
        setBackground(new Color(245, 245, 245)); // Màu nền xám rất nhạt giống web
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Margin xung quanh
    }

    private void initComponents() {
        // 1. PHẦN TOP: CÁC CARD THỐNG KÊ
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);

        // Card 1: Tác giả
        // Lưu tham chiếu vào biến instance để lát nữa set text
        cardAuthor = new DashboardCard(
                "Tổng Tác Giả",
                "0", // Giá trị mặc định
                "GUI/icon/author.svg",
                new Color(65, 105, 225),
                () -> sendCommand("AUTHOR"));
        pnlCards.add(cardAuthor);

        // Card 2: Nhà Xuất Bản
        cardPublisher = new DashboardCard(
                "Nhà Xuất Bản",
                "0",
                "GUI/icon/publisher.svg",
                new Color(147, 112, 219),
                () -> sendCommand("PUBLISHER"));
        pnlCards.add(cardPublisher);

        // Card 3: Thể Loại
        cardCategory = new DashboardCard(
                "Thể Loại Sách",
                "0",
                "GUI/icon/genre.svg",
                new Color(255, 165, 0),
                () -> sendCommand("CATEGORY"));
        pnlCards.add(cardCategory);

        add(pnlCards, BorderLayout.NORTH);

        // 2. PHẦN CENTER: BẢNG DỮ LIỆU
        JPanel pnlTableSection = new JPanel(new BorderLayout());
        pnlTableSection.setBackground(Color.WHITE);
        pnlTableSection.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTableTitle = new JLabel("Dữ liệu mới nhất");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pnlTableSection.add(lblTableTitle, BorderLayout.NORTH);

        // Cấu hình bảng
        // Lưu ý: DTO của bạn không có trường "Ngày tạo", nên ta sẽ bỏ cột đó hoặc để
        // trống
        String[] columns = { "ID", "Tên Mục", "Loại", "Trạng thái" };
        tableModel = new DefaultTableModel(null, columns);

        JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        pnlTableSection.add(scrollPane, BorderLayout.CENTER);

        add(pnlTableSection, BorderLayout.CENTER);
    }

    public void refreshData() {
        // 1. Reload data từ DB vào BUS
        authorBUS.loadData();
        categoryBUS.loadData();
        publisherBUS.loadData();

        updateCardValue(cardAuthor, String.valueOf(authorBUS.getAll().size()));
        updateCardValue(cardPublisher, String.valueOf(publisherBUS.getAll().size()));
        updateCardValue(cardCategory, String.valueOf(categoryBUS.getAll().size()));

        tableModel.setRowCount(0); // Xóa hết dữ liệu cũ

        ArrayList<AuthorDTO> authors = authorBUS.getAll();
        ArrayList<PublisherDTO> publishers = publisherBUS.getAll();
        ArrayList<CategoryDTO> categories = categoryBUS.getAll();

        // Thêm vài tác giả mới nhất
        int limit = 3;
        for (int i = 0; i < Math.min(limit, authors.size()); i++) {
            AuthorDTO a = authors.get(i);
            tableModel.addRow(new Object[] {
                    "TG-" + a.getAuthorId(),
                    a.getAuthorName(),
                    "Tác giả",
                    "Hoạt động"
            });
        }

        // Thêm vài NXB
        for (int i = 0; i < Math.min(limit, publishers.size()); i++) {
            PublisherDTO p = publishers.get(i);
            tableModel.addRow(new Object[] {
                    "NXB-" + p.getId(),
                    p.getName(),
                    "NXB",
                    p.getStatus()
            });
        }

        // Thêm vài Thể loại
        for (int i = 0; i < Math.min(limit, categories.size()); i++) {
            CategoryDTO c = categories.get(i);
            tableModel.addRow(new Object[] {
                    "TL-" + c.getId(),
                    c.getName(),
                    "Thể loại",
                    c.getStatus()
            });
        }
    }

    private void updateCardValue(DashboardCard card, String value) {
        try {
            Component[] comps = card.getComponents();
            card.setValue(value);
        } catch (Exception e) {
            System.err.println("Vui lòng thêm hàm setValue() vào class DashboardCard để update số liệu.");
        }
    }

    private void sendCommand(String command) {
        if (onItemSelected != null) {
            // Tạo ActionEvent giả để gửi command đi
            onItemSelected.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command));
        }
    }
}