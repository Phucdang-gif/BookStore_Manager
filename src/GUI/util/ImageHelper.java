package GUI.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
 * Tiện ích xử lý ảnh thuần (BufferedImage)
 * Không chứa các thành phần liên quan đến Swing Icon hay Component
 */
public class ImageHelper {

    /**
     * Đọc ảnh từ Resource (trong file JAR) hoặc File hệ thống
     * 
     * @return BufferedImage hoặc null nếu không tìm thấy
     */
    public static BufferedImage readImage(String path) {
        try {
            URL res = ImageHelper.class.getResource(path);
            if (res != null)
                return ImageIO.read(res);

            File f = new File(path);
            if (f.exists())
                return ImageIO.read(f);
        } catch (Exception e) {
            System.err.println("❌ Lỗi đọc ảnh: " + path);
        }
        return null;
    }

    /**
     * Thay đổi kích thước ảnh chất lượng cao
     */
    public static BufferedImage resize(BufferedImage img, int w, int h) {
        if (img == null)
            return null;
        BufferedImage dimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dimg.createGraphics();

        // Thiết lập chất lượng khử răng cưa và nội suy
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(img, 0, 0, w, h, null);
        g2.dispose();
        return dimg;
    }

    /**
     * Cắt ảnh thành hình tròn (Không viền)
     */
    public static BufferedImage makeCircle(BufferedImage img, int size) {
        if (img == null)
            return null;
        BufferedImage circleImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Tạo vùng cắt hình tròn
        Ellipse2D.Float area = new Ellipse2D.Float(0, 0, size, size);
        g2.setClip(area);
        g2.drawImage(img, 0, 0, size, size, null);

        g2.dispose();
        return circleImg;
    }
}