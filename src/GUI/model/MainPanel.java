package GUI.model;

import javax.swing.*;
import java.awt.*;

import GUI.util.UIConstants;
import BUS.BookBUS;

public class MainPanel extends JPanel {
    private Header header;
    private BookTablePanel content;
    // private FilterPanel filter;
    private BookBUS bookBUS;

    public MainPanel() {
        bookBUS = new BookBUS();
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
        initComponents();
    }

    private void initComponents() {
        header = new Header(bookBUS);
        content = new BookTablePanel(bookBUS);
        header.setPanelTable(content);
        // filter = new FilterPanel();
        add(header, BorderLayout.NORTH);
        // add(filter, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }
}