package GUI.util;

import java.awt.Color;

public class ThemeColor {
    private static final Color WARNING_COLOR = new Color(255, 244, 204); // Màu vàng nhạt
    private static final Color WARNING_TEXT = new Color(180, 100, 0); // Màu chữ cam đậm
    private static final Color COLOR_OUT_OF_STOCK = new Color(255, 228, 225);
    private static final Color TEXT_RED_BOLD = new Color(220, 20, 60);
    // --- BẢNG MÀU CHỾ ĐỔI SÁNG (LIGHT) ---
    private static final Color LIGHT_BG_MAIN = Color.decode("#EEF2F6");
    private static final Color LIGHT_BG_PANEL = new Color(245, 246, 250);
    private static final Color LIGHT_WHITE = Color.WHITE;
    private static final Color LIGHT_TEXT_MAIN = Color.decode("#1E293B");
    private static final Color LIGHT_TEXT_SECONDARY = Color.decode("#64748B");
    private static final Color LIGHT_BORDER = Color.decode("#E2E8F0");

    // Nút Sidebar (giữ nguyên)
    private static final Color LIGHT_BTN_ACTIVE_BG = Color.decode("#EFF6FF");
    private static final Color LIGHT_BTN_ACTIVE_TEXT = Color.decode("#2563EB");

    // Header Bảng
    private static final Color LIGHT_TABLE_HEADER_BG = Color.decode("#2563EB");
    private static final Color LIGHT_TABLE_HEADER_TEXT = Color.decode("#EFF6FF");

    // Nội dung Bảng
    private static final Color LIGHT_TABLE_GRID = Color.decode("#E2E8F0");
    private static final Color LIGHT_TABLE_TEXT = Color.decode("#334155");

    // Zebra Row
    private static final Color LIGHT_ROW_EVEN = Color.decode("#FFFFFF");
    private static final Color LIGHT_ROW_ODD = Color.decode("#F0F7FF");

    // Màu chữ giá tiền
    private static final Color LIGHT_PRICE_IMPORT = Color.decode("#16A34A");
    private static final Color LIGHT_PRICE_SELLING = Color.decode("#2563EB");

    // Selection Row — tách ra riêng, không chạm sidebar
    private static final Color LIGHT_SELECTION_BG = Color.decode("#377671"); // teal-600
    private static final Color LIGHT_SELECTION_TEXT = Color.decode("#FFFFFF"); // trắng

    // --- BẢNG MÀU CHỾ ĐỔI TỐI (DARK) ---
    private static final Color DARK_BG_MAIN = Color.decode("#111827");
    private static final Color DARK_BG_PANEL = Color.decode("#1F2937");
    private static final Color DARK_TEXT_MAIN = Color.decode("#F9FAFB");
    private static final Color DARK_TEXT_SECONDARY = Color.decode("#9CA3AF");
    private static final Color DARK_BORDER = Color.decode("#374151");

    // Nút Sidebar Dark (giữ nguyên)
    private static final Color DARK_BTN_ACTIVE_BG = Color.decode("#374151");
    private static final Color DARK_BTN_ACTIVE_TEXT = Color.decode("#60A5FA");

    // Header Bảng Dark
    private static final Color DARK_TABLE_HEADER_BG = Color.decode("#374151");
    private static final Color DARK_TABLE_HEADER_TEXT = Color.decode("#E5E7EB");

    // Nội dung Bảng Dark
    private static final Color DARK_TABLE_GRID = Color.decode("#374151");
    private static final Color DARK_TABLE_TEXT = Color.decode("#D1D5DB");

    // Zebra Row Dark
    private static final Color DARK_ROW_EVEN = Color.decode("#1F2937");
    private static final Color DARK_ROW_ODD = Color.decode("#243444");

    // Màu chữ giá tiền Dark
    private static final Color DARK_PRICE_IMPORT = Color.decode("#34D399");
    private static final Color DARK_PRICE_SELLING = Color.decode("#60A5FA");

    // Selection Row Dark
    private static final Color DARK_SELECTION_BG = Color.decode("#0F766E"); // teal-700
    private static final Color DARK_SELECTION_TEXT = Color.decode("#FFFFFF");
    private static final Color DARK_WHITE = Color.decode("#1F2937");

    // --- MÀU ĐIỂM NHẤN ---
    public static final Color ACCENT_COLOR = Color.decode("#2563EB");

    // --- CÁC BIẾN MÀU ĐỘNG (Public) ---
    public static Color bgMain = LIGHT_BG_MAIN;
    public static Color bgPanel = LIGHT_BG_PANEL;
    public static Color bgWhite = LIGHT_WHITE;
    public static Color textMain = LIGHT_TEXT_MAIN;
    public static Color textSecondary = LIGHT_TEXT_SECONDARY;
    public static Color borderColor = LIGHT_BORDER;

    public static Color btnActiveBg = LIGHT_BTN_ACTIVE_BG; // Sidebar dùng cái này
    public static Color btnActiveText = LIGHT_BTN_ACTIVE_TEXT;

    public static Color tableHeaderBg = LIGHT_TABLE_HEADER_BG;
    public static Color tableHeaderText = LIGHT_TABLE_HEADER_TEXT;

    public static Color gridColor = LIGHT_TABLE_GRID;
    public static Color tableText = LIGHT_TABLE_TEXT;

    public static Color rowEven = LIGHT_ROW_EVEN;
    public static Color rowOdd = LIGHT_ROW_ODD;
    public static Color priceImport = LIGHT_PRICE_IMPORT;
    public static Color priceSelling = LIGHT_PRICE_SELLING;
    public static Color warningColor = WARNING_COLOR;
    public static Color warningText = WARNING_TEXT;

    // Table selection — riêng cái này
    public static Color selectionBg = LIGHT_SELECTION_BG;
    public static Color selectionText = LIGHT_SELECTION_TEXT;
    public static Color outOfStockColor = COLOR_OUT_OF_STOCK;
    public static Color outOfStockText = TEXT_RED_BOLD;

    public static void applyTheme(boolean isDark) {
        if (isDark) {
            bgMain = DARK_BG_MAIN;
            bgPanel = DARK_BG_PANEL;
            bgWhite = DARK_WHITE;
            textMain = DARK_TEXT_MAIN;
            textSecondary = DARK_TEXT_SECONDARY;
            borderColor = DARK_BORDER;

            btnActiveBg = DARK_BTN_ACTIVE_BG;
            btnActiveText = DARK_BTN_ACTIVE_TEXT;

            tableHeaderBg = DARK_TABLE_HEADER_BG;
            tableHeaderText = DARK_TABLE_HEADER_TEXT;

            gridColor = DARK_TABLE_GRID;
            tableText = DARK_TABLE_TEXT;

            rowEven = DARK_ROW_EVEN;
            rowOdd = DARK_ROW_ODD;
            priceImport = DARK_PRICE_IMPORT;
            priceSelling = DARK_PRICE_SELLING;

            selectionBg = DARK_SELECTION_BG;
            selectionText = DARK_SELECTION_TEXT;
        } else {
            bgMain = LIGHT_BG_MAIN;
            bgPanel = LIGHT_BG_PANEL;
            bgWhite = LIGHT_WHITE;
            textMain = LIGHT_TEXT_MAIN;
            textSecondary = LIGHT_TEXT_SECONDARY;
            borderColor = LIGHT_BORDER;

            btnActiveBg = LIGHT_BTN_ACTIVE_BG;
            btnActiveText = LIGHT_BTN_ACTIVE_TEXT;

            tableHeaderBg = LIGHT_TABLE_HEADER_BG;
            tableHeaderText = LIGHT_TABLE_HEADER_TEXT;

            gridColor = LIGHT_TABLE_GRID;
            tableText = LIGHT_TABLE_TEXT;

            rowEven = LIGHT_ROW_EVEN;
            rowOdd = LIGHT_ROW_ODD;
            priceImport = LIGHT_PRICE_IMPORT;
            priceSelling = LIGHT_PRICE_SELLING;

            selectionBg = LIGHT_SELECTION_BG;
            selectionText = LIGHT_SELECTION_TEXT;
        }
    }
}