package GUI;

import javax.swing.*;
import java.awt.*;
import GUI.model.MainPanel;
import GUI.model.Sidebar;
import com.formdev.flatlaf.FlatLightLaf;
import GUI.util.ThemeColor;

public class MainFrame extends JFrame {
    private MainPanel content;
    private Sidebar sidebar;

    public MainFrame() {
        setTitle("Quản lý sách");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        applyLightSettings();
        initComponents();
        setupLayout();
        initEvents();
    }

    private void initComponents() {
        content = new MainPanel();
        sidebar = new Sidebar();
        getContentPane().setBackground(ThemeColor.bgPanel);
        content.setOnAvatarUpdated(() -> sidebar.refreshUserProfile());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);
    }

    private void initEvents() {
        sidebar.setMenuListener(e -> {
            String command = e.getActionCommand();
            content.showPanel(command);
        });
        sidebar.addToggleEvent(e -> {
            int currentWidth = sidebar.getWidth();
            if (currentWidth > 100) {
                sidebar.setPreferredSize(new Dimension(60, sidebar.getHeight()));
            } else {
                sidebar.setPreferredSize(new Dimension(240, sidebar.getHeight()));
            }
            sidebar.revalidate();
            sidebar.repaint();
        });
    }

    private void applyLightSettings() {
        try {
            ThemeColor.applyTheme(false); // Luôn gọi theme sáng
            UIManager.setLookAndFeel(new FlatLightLaf());

            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}