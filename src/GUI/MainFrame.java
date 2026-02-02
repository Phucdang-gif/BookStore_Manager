package GUI;

import javax.swing.*;
import java.awt.*;
import GUI.model.MainPanel;
import GUI.model.Sidebar;

public class MainFrame extends JFrame {
    private MainPanel content;
    private Sidebar sidebar;

    public MainFrame() {
        setTitle("Quản lý sách");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#121212"));
        initComponents();
        setupLayout();
        initEvents();
    }

    private void initComponents() {
        content = new MainPanel();
        sidebar = new Sidebar();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);
    }

    private void initEvents() {
        // Lắng nghe sự kiện click từ Sidebar
        sidebar.setMenuListener(e -> {
            String command = e.getActionCommand(); // "BOOK", "SALES"...
            content.showPanel(command); // Bảo MainPanel chuyển tab
        });
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup(); // Giao diện sáng (khuyên dùng)

        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}