package GUI.model;

import javax.swing.*;
import java.awt.*;
import GUI.util.UIConstants;

public class MainPanel extends JPanel {
    private Header header;
    private BookTablePanel content;
    // private FilterPanel filter;

    public MainPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        initComponents();

    }

    private void initComponents() {
        header = new Header();
        content = new BookTablePanel();
        header.setPanelTable(content);
        // filter = new FilterPanel();
        add(header, BorderLayout.NORTH);
        // add(filter, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }
}