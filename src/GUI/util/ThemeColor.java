package GUI.util;

import java.awt.Color;
import javax.swing.UIManager;

public class ThemeColor {

    // ============================================================
    // LIGHT — "Warm Brown"
    // Nền: nâu ấm nhạt (giấy cũ, cà phê loãng)
    // Chữ: đen xám đậm → contrast cao
    // Accent: nâu cam đất — hợp tông, nổi bật
    // ============================================================
    private static final Color LIGHT_BG_MAIN = Color.decode("#EDE5DC"); // nền ngoài: nâu ấm
    private static final Color LIGHT_BG_PANEL = Color.decode("#FAF7F4"); // nền panel: kem trắng nhạt
    private static final Color LIGHT_TEXT_MAIN = Color.decode("#2C1810"); // chữ: nâu đen đậm
    private static final Color LIGHT_TEXT_SECONDARY = Color.decode("#6B5344"); // chữ phụ: nâu xám
    private static final Color LIGHT_BORDER = Color.decode("#D9CFC4"); // viền: nâu nhạt

    // Accent: nâu cam đất
    private static final Color LIGHT_ACCENT = Color.decode("#B5651D"); // nâu cam đất đậm
    private static final Color LIGHT_ACCENT_BG = Color.decode("#F5EBE0"); // nền highlight nâu nhạt
    private static final Color LIGHT_SELECTION_BG = Color.decode("#C2703A"); // nâu cam đậm

    // ============================================================
    // DARK — giữ nguyên
    // ============================================================
    private static final Color DARK_BG_MAIN = Color.decode("#18191A");
    private static final Color DARK_BG_PANEL = Color.decode("#242526");
    private static final Color DARK_TEXT_MAIN = Color.decode("#E4E6EB");
    private static final Color DARK_TEXT_SECONDARY = Color.decode("#B0B3B8");
    private static final Color DARK_BORDER = Color.decode("#3E4042");
    private static final Color DARK_ACCENT = Color.decode("#D4915A"); // nâu cam sáng hơn cho dark
    private static final Color DARK_ACCENT_BG = Color.decode("#3A2E24");
    private static final Color DARK_SELECTION_BG = Color.decode("#D4915A"); // dark dùng cùng accent đã sáng

    // ============================================================
    // BIẾN ĐỘNG
    // ============================================================
    public static Color bgMain = LIGHT_BG_MAIN;
    public static Color bgPanel = LIGHT_BG_PANEL;
    public static Color textMain = LIGHT_TEXT_MAIN;
    public static Color textSecondary = LIGHT_TEXT_SECONDARY;
    public static Color borderColor = LIGHT_BORDER;
    public static Color ACCENT_COLOR = LIGHT_ACCENT;
    public static Color accentBg = LIGHT_ACCENT_BG;
    public static Color selectionBg = LIGHT_SELECTION_BG;

    // ============================================================
    public static void applyTheme(boolean isDark) {
        if (isDark) {
            bgMain = DARK_BG_MAIN;
            bgPanel = DARK_BG_PANEL;
            textMain = DARK_TEXT_MAIN;
            textSecondary = DARK_TEXT_SECONDARY;
            borderColor = DARK_BORDER;
            ACCENT_COLOR = DARK_ACCENT;
            accentBg = DARK_ACCENT_BG;
            selectionBg = DARK_SELECTION_BG;
        } else {
            bgMain = LIGHT_BG_MAIN;
            bgPanel = LIGHT_BG_PANEL;
            textMain = LIGHT_TEXT_MAIN;
            textSecondary = LIGHT_TEXT_SECONDARY;
            borderColor = LIGHT_BORDER;
            ACCENT_COLOR = LIGHT_ACCENT;
            accentBg = LIGHT_ACCENT_BG;
            selectionBg = LIGHT_SELECTION_BG;
        }
        applyUIManager();
    }

    public static void applyUIManager() {
        // Nền
        UIManager.put("Panel.background", bgPanel);
        UIManager.put("ScrollPane.background", bgPanel);
        UIManager.put("Table.background", bgPanel);
        UIManager.put("Table.alternatingRowBackground", bgMain);

        // Chữ
        UIManager.put("Panel.foreground", textMain);
        UIManager.put("Table.foreground", textMain);

        // Viền / grid
        UIManager.put("Table.gridColor", borderColor);

        // Table header
        UIManager.put("TableHeader.background", accentBg);
        UIManager.put("TableHeader.foreground", ACCENT_COLOR);

        // Selection — dùng selectionBg riêng (đậm hơn accent)
        UIManager.put("Table.selectionBackground", selectionBg);
        UIManager.put("Table.selectionForeground", Color.WHITE);

        // Button
        UIManager.put("Button.background", bgPanel);
        UIManager.put("Button.foreground", textMain);
    }
}