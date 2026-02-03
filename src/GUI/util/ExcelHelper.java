package GUI.util;

import DTO.BookDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {

    /**
     * Xuất danh sách sách ra file Excel
     * 
     * @param listBooks       Danh sách gốc (List<BookDTO>) cần xuất
     * @param parentComponent Component cha để hiển thị Dialog
     */
    public static void exportBooks(List<BookDTO> listBooks, Component parentComponent) {
        // 1. Mở hộp thoại chọn nơi lưu file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(parentComponent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return; // Người dùng nhấn Cancel
        }

        File fileToSave = fileChooser.getSelectedFile();
        // Tự động thêm đuôi .xlsx nếu người dùng quên
        if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xlsx");
        }

        // 2. Tạo Workbook và Sheet
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách Sách");

            // --- TẠO HEADER (TIÊU ĐỀ CỘT) ---
            // Thêm các cột chi tiết có trong BookDTO
            String[] headers = {
                    "ID", "ISBN", "Tên sách", "Tác giả", "Nhà xuất bản", "Thể loại",
                    "Giá nhập", "Giá bán", "Tồn kho", "Tồn tối thiểu", "Trạng thái", "Hình ảnh"
            };

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- ĐỔ DỮ LIỆU TỪ LIST VÀO ---
            int rowIdx = 1;
            for (BookDTO book : listBooks) {
                Row row = sheet.createRow(rowIdx++);

                // Cột 0: ID
                row.createCell(0).setCellValue(book.getBookId());

                // Cột 1: ISBN
                row.createCell(1).setCellValue(book.getIsbn() != null ? book.getIsbn() : "");

                // Cột 2: Tên sách
                row.createCell(2).setCellValue(book.getBookTitle());

                // Cột 3: Tác giả (List<String> -> String)
                String authors = "";
                if (book.getAuthorNames() != null && !book.getAuthorNames().isEmpty()) {
                    authors = String.join(", ", book.getAuthorNames());
                }
                row.createCell(3).setCellValue(authors);

                // Cột 4: Nhà xuất bản
                row.createCell(4).setCellValue(book.getPublisherName() != null ? book.getPublisherName() : "");

                // Cột 5: Thể loại
                row.createCell(5).setCellValue(book.getCategoryName() != null ? book.getCategoryName() : "");

                // Cột 6: Giá nhập (double)
                row.createCell(6).setCellValue(book.getImportPrice());

                // Cột 7: Giá bán (double)
                row.createCell(7).setCellValue(book.getSellingPrice());

                // Cột 8: Tồn kho (int)
                row.createCell(8).setCellValue(book.getStockQuantity());

                // Cột 9: Tồn tối thiểu (int)
                row.createCell(9).setCellValue(book.getMinimumStock());

                // Cột 10: Trạng thái (Lấy tiếng Việt cho dễ đọc)
                row.createCell(10).setCellValue(book.getStatusVietnamese());

                // Cột 11: Hình ảnh
                row.createCell(11).setCellValue(book.getImage() != null ? book.getImage() : "");
            }

            // --- AUTO RESIZE COLUMN ---
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 3. Ghi file ra ổ cứng
            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                workbook.write(out);
            }

            JOptionPane.showMessageDialog(parentComponent,
                    "Xuất file Excel thành công!\nĐường dẫn: " + fileToSave.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent,
                    "Lỗi khi ghi file: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm tạo style cho header
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }
}