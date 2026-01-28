package GUI.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class IconHelper {

    public static FlatSVGIcon getSVGIcon(String path, int width, int height) {
        return new FlatSVGIcon(path, width, height);
    }

    /**
     * Lấy ImageIcon từ ảnh Bitmap (Dành cho ảnh bìa sách, ảnh sản phẩm)
     */
    public static ImageIcon getImageIcon(String path, int width, int height) {
        BufferedImage img = ImageHelper.readImage(path);
        if (img != null) {
            return new ImageIcon(ImageHelper.resize(img, width, height));
        }
        return null;
    }

    /**
     * Gán icon cho JLabel/JButton (Tự động nhận diện SVG hoặc Bitmap qua đuôi file)
     */
    public static void setIcon(AbstractButton component, String path, int width, int height) {
        if (path.toLowerCase().endsWith(".svg")) {
            component.setIcon(getSVGIcon(path, width, height));
        } else {
            component.setIcon(getImageIcon(path, width, height));
        }
    }

    public static void setIcon(JLabel label, String path, int width, int height) {
        if (path.toLowerCase().endsWith(".svg")) {
            label.setIcon(getSVGIcon(path, width, height));
        } else {
            label.setIcon(getImageIcon(path, width, height));
        }
    }

    /**
     * Gán Avatar bo tròn không viền cho JLabel
     */
    public static void setCircleAvatar(JLabel label, String path, int size) {
        BufferedImage img = ImageHelper.readImage(path);
        if (img != null) {
            BufferedImage circle = ImageHelper.makeCircle(img, size);
            label.setIcon(new ImageIcon(circle));
            label.setText(""); // Xóa text nếu có
        } else {
            label.setText("No Image");
        }
    }
}