package GUI.components;

import GUI.util.IconHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyListener;

public class SearchTextField extends JPanel {

    private JTextField txtSearch;
    private JButton btnSearch;

    public SearchTextField() {

        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);
        setOpaque(false);

        setPreferredSize(new Dimension(250, 40));
        setBorder(new EmptyBorder(5, 10, 5, 10));
        initComponents();
    }

    private void initComponents() {
        // --- 2. Tạo Nút Tìm Kiếm (Bên Trái) ---
        btnSearch = new JButton();
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Làm trong suốt nút
        btnSearch.setBorder(null);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(25, 25));
        IconHelper.setIcon(btnSearch, "GUI/icon/search.svg", 20, 20);

        // --- 3. Tạo TextField nhập liệu (Bên Phải) ---
        txtSearch = new JTextField();
        txtSearch.setBorder(null);
        txtSearch.setOpaque(false);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // --- 4. Add vào Panel ---
        add(btnSearch, BorderLayout.WEST);
        add(txtSearch, BorderLayout.CENTER);
    }

    public String getText() {
        return txtSearch.getText();
    }

    // Gán nội dung text
    public void setText(String t) {
        txtSearch.setText(t);
    }

    // Thêm sự kiện phím (Header gọi cái này để bắt Enter)
    @Override
    public void addKeyListener(KeyListener l) {
        txtSearch.addKeyListener(l);
    }

    // Lấy nút để bắt sự kiện click
    public JButton getBtnSearch() {
        return btnSearch;
    }

    // Lấy text field gốc nếu cần can thiệp sâu
    public JTextField getTxtSearch() {
        return txtSearch;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Khử răng cưa cho mượt
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width - 1, height - 1, 30, 30);
        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(new Color(100, 100, 100));
        g2.drawRoundRect(1, 1, width - 3, height - 3, 30, 30);

        g2.dispose();
    }
}