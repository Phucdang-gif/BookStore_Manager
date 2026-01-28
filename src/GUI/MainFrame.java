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
        initComponents();
        setupLayout();

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

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}