package GUI;

import javax.swing.*;
import java.awt.*;

import GUI.model.MainPanel;
import GUI.model.Sidebar;

import com.formdev.flatlaf.FlatDarkLaf;
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
        setTheme(false); // true: dark theme, false: light theme
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
        sidebar.setMenuListener(e -> {
            String command = e.getActionCommand();
            content.showPanel(command);
        });
    }

    public void setTheme(boolean isDark) {
        try {
            ThemeColor.applyTheme(isDark);
            if (isDark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }

            // 3. Nền JFrame
            ((JPanel) getContentPane()).setBackground(ThemeColor.bgMain);

            // 4. Vẽ lại toàn cây
            SwingUtilities.updateComponentTreeUI(this);
            this.revalidate();
            this.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}