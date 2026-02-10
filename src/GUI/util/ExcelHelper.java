package GUI.util;

import DTO.BookDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

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

    public static File selectExcelFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập dữ liệu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx", "xls"));

        int userSelection = fileChooser.showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    // 2. Hàm đọc dữ liệu từ Excel trả về List
    public static List<BookDTO> importBooksFromExcel(File file) {
        List<BookDTO> listBooks = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua dòng tiêu đề
            if (rowIterator.hasNext())
                rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isRowEmpty(row))
                    continue;

                BookDTO book = new BookDTO();

                // Đọc đúng thứ tự cột trong file new1.xlsx
                // 0:ID | 1:ISBN | 2:Tên | 3:Tác giả | 4:NXB | 5:Thể loại
                // 6:Giá Nhập | 7:Giá Bán | 8:Tồn | 9:Min | 10:Trạng thái | 11:Ảnh

                String idStr = getCellValue(row.getCell(0));
                book.setBookId(idStr.isEmpty() ? 0 : (int) Double.parseDouble(idStr));

                book.setIsbn(getCellValue(row.getCell(1)));

                String title = getCellValue(row.getCell(2));
                if (title.isEmpty())
                    continue; // Bắt buộc có tên
                book.setBookTitle(title);

                // Tách tác giả
                String authorsStr = getCellValue(row.getCell(3));
                List<String> authorNames = new ArrayList<>();
                if (!authorsStr.isEmpty()) {
                    String[] arr = authorsStr.split(",");
                    for (String s : arr)
                        authorNames.add(s.trim());
                }
                book.setAuthorNames(authorNames);

                book.setPublisherName(getCellValue(row.getCell(4)));
                book.setCategoryName(getCellValue(row.getCell(5)));

                book.setImportPrice(parseCurrency(getCellValue(row.getCell(6))));
                book.setSellingPrice(parseCurrency(getCellValue(row.getCell(7))));
                book.setStockQuantity((int) parseCurrency(getCellValue(row.getCell(8))));
                book.setMinimumStock((int) parseCurrency(getCellValue(row.getCell(9))));

                // Xử lý trạng thái (Dùng hàm static bên BookDTO)
                String statusVN = getCellValue(row.getCell(10));
                book.setStatus(BookDTO.getStatusFromVietnamese(statusVN));

                String img = getCellValue(row.getCell(11));
                book.setImage(img.isEmpty() ? null : img);

                listBooks.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi đọc file: " + e.getMessage());
        }
        return listBooks;
    }

    // --- CÁC HÀM BỔ TRỢ ---
    private static double parseCurrency(String val) {
        try {
            return Double.parseDouble(val.replace(",", "").replace("_", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return cell.getDateCellValue().toString();
                double val = cell.getNumericCellValue();
                return (val == (long) val) ? String.format("%d", (long) val) : String.valueOf(val);
            default:
                return "";
        }
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null)
            return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell).isEmpty())
                return false;
        }
        return true;
    }
}