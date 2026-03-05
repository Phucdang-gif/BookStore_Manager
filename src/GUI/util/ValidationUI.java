package GUI.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ValidationUI {

    // Lấy viền mặc định của giao diện hệ thống hiện tại
    private static final Border DEFAULT_BORDER = UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border");

    // Tạo viền màu đỏ độ dày 2px
    private static final Border ERROR_BORDER = BorderFactory.createLineBorder(Color.RED, 2);

    // * Bôi đỏ viền và hiện thông báo lỗi (Tooltip) khi di chuột vào
    public static void setError(JComponent component, String errorMessage) {
        if (component != null) {
            component.setBorder(ERROR_BORDER);
            component.setToolTipText(errorMessage);

        }
    }

    public static void reset(JComponent component) {
        if (component != null) {
            component.setBorder(DEFAULT_BORDER);
            component.setToolTipText(null);
            // component.setBackground(Color.WHITE);
        }
    }

    /**
     * Hàm tiện ích: Reset nhiều component cùng lúc (Dùng dấu ... để truyền bao
     * nhiêu ô cũng được)
     */
    public static void resetAll(JComponent... components) {
        for (JComponent comp : components) {
            reset(comp);
        }
    }
}