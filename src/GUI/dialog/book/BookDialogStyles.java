package GUI.dialog.book;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class BookDialogStyles {
    public static final Color PRIMARY_COLOR = new Color(15, 108, 189);
    public static final Color BG_COLOR = new Color(245, 248, 250);
    public static final Color BORDER_COLOR = new Color(180, 180, 180);
    public static final int LABEL_WIDTH = 115;

    public static void styleControl(JComponent c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBorder(new LineBorder(BORDER_COLOR));
        c.setPreferredSize(new Dimension(10, 30));
        if (c instanceof JComboBox)
            ((JComboBox<?>) c).setBackground(Color.WHITE);
    }
}