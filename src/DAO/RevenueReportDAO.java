package DAO;

import config.DatabaseConnection;
import DTO.BookRevenueDTO;
import DTO.CustomerRevenueDTO;
import DTO.EmployeeRevenueDTO;
import DTO.RevenueReportDTO;
import DTO.UnitsInStockDTO;

import java.sql.*;
import java.util.ArrayList;

public class RevenueReportDAO {

    // 1. THỐNG KÊ KHÁCH HÀNG
    public ArrayList<CustomerRevenueDTO> CustomerReport(Date startDate, Date endDate) {
        ArrayList<CustomerRevenueDTO> list = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.full_name, "
                + "COUNT(i.invoice_id) AS totalinvoice, "
                + "COALESCE(SUM(i.final_amount), 0) AS totalamount "
                + "FROM customers c "
                + "LEFT JOIN invoices i ON c.customer_id = i.customer_id "
                + "AND (? IS NULL OR DATE(i.created_at) >= ?) "
                + "AND (? IS NULL OR DATE(i.created_at) <= ?) "
                + "GROUP BY c.customer_id, c.full_name "
                + "ORDER BY totalamount DESC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setDate(4, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                int number = 1;
                while (rs.next()) {
                    CustomerRevenueDTO dto = new CustomerRevenueDTO();
                    dto.setCustomerID(rs.getInt("customer_id"));
                    dto.setFullname(rs.getString("full_name"));
                    dto.setOrdinalnumber(number++);
                    dto.setTotalinvoices(rs.getInt("totalinvoice"));
                    dto.setTotalamount(rs.getDouble("totalamount"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. THỐNG KÊ NHÂN VIÊN
    public ArrayList<EmployeeRevenueDTO> EmployeeReport(Date startDate, Date endDate) {
        ArrayList<EmployeeRevenueDTO> list = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.full_name, "
                + "COUNT(i.invoice_id) AS totalinvoices, "
                + "COALESCE(SUM(i.final_amount), 0) AS totalrevenue "
                + "FROM employees e "
                + "LEFT JOIN invoices i ON e.employee_id = i.employee_id "
                + "AND (? IS NULL OR DATE(i.created_at) >= ?) "
                + "AND (? IS NULL OR DATE(i.created_at) <= ?) "
                + "GROUP BY e.employee_id, e.full_name "
                + "ORDER BY totalrevenue DESC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setDate(4, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                int number = 1;
                while (rs.next()) {
                    EmployeeRevenueDTO dto = new EmployeeRevenueDTO();
                    dto.setEmployeeID(rs.getInt("employee_id"));
                    dto.setFullname(rs.getString("full_name"));
                    dto.setTotalInvoice(rs.getInt("totalinvoices"));
                    dto.setTotalRevenue(rs.getDouble("totalrevenue"));
                    dto.setOrdinalnumber(number++);
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. THỐNG KÊ SÁCH (Cập nhật có lọc Thể loại, NXB, Tác giả bằng EXISTS để tránh
    // nhân đôi)
    public ArrayList<BookRevenueDTO> BookReport(Date startDate, Date endDate, int categoryId, int publisherId,
            int authorId) {
        ArrayList<BookRevenueDTO> list = new ArrayList<>();
        String sql = "SELECT b.book_id, b.book_title, "
                + "SUM(id.quantity) AS total_quantity, "
                + "SUM(id.subtotal) AS total_revenue "
                + "FROM books b "
                + "JOIN invoice_details id ON b.book_id = id.book_id "
                + "JOIN invoices i ON id.invoice_id = i.invoice_id "
                + "WHERE (? IS NULL OR DATE(i.created_at) >= ?) "
                + "AND (? IS NULL OR DATE(i.created_at) <= ?) "
                + "AND (? = 0 OR b.category_id = ?) "
                + "AND (? = 0 OR b.publisher_id = ?) "
                + "AND (? = 0 OR EXISTS ("
                + "    SELECT 1 FROM book_authors ba WHERE ba.book_id = b.book_id AND ba.author_id = ?"
                + ")) "
                + "GROUP BY b.book_id, b.book_title "
                + "ORDER BY total_quantity DESC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            // Set params Ngày
            ps.setDate(1, startDate);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setDate(4, endDate);
            // Set params Category
            ps.setInt(5, categoryId);
            ps.setInt(6, categoryId);
            // Set params Publisher
            ps.setInt(7, publisherId);
            ps.setInt(8, publisherId);
            // Set params Author
            ps.setInt(9, authorId);
            ps.setInt(10, authorId);

            try (ResultSet rs = ps.executeQuery()) {
                int number = 1;
                while (rs.next()) {
                    BookRevenueDTO dto = new BookRevenueDTO();
                    dto.setBookID(rs.getInt("book_id"));
                    dto.setBookTitle(rs.getString("book_title"));
                    dto.setTotalSold(rs.getInt("total_quantity"));
                    dto.setTotalRevenue(rs.getDouble("total_revenue"));
                    dto.setOrdinalNumber(number++);
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 4. THỐNG KÊ DOANH THU THEO NĂM (Các tháng)
    public ArrayList<RevenueReportDTO> RevenueReport(int year) {
        ArrayList<RevenueReportDTO> list = new ArrayList<>();
        String sql = "SELECT YEAR(i.created_at) AS year, MONTH(i.created_at) AS month, "
                + "COALESCE(SUM(b.import_price * id.quantity),0) AS cost, "
                + "COALESCE(SUM(id.subtotal),0) AS revenue, "
                + "COALESCE(SUM(id.subtotal - (b.import_price * id.quantity)),0) AS profit "
                + "FROM invoices i "
                + "JOIN invoice_details id ON i.invoice_id = id.invoice_id "
                + "JOIN books b ON id.book_id = b.book_id "
                + "WHERE YEAR(i.created_at) = ? "
                + "GROUP BY YEAR(i.created_at), MONTH(i.created_at) "
                + "ORDER BY MONTH(i.created_at)";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RevenueReportDTO dto = new RevenueReportDTO();
                    dto.setYear(rs.getInt("year"));
                    dto.setMonth(rs.getInt("month"));
                    dto.setCost(rs.getDouble("cost"));
                    dto.setRevenue(rs.getDouble("revenue"));
                    dto.setProfit(rs.getDouble("profit"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 5. THỐNG KÊ DOANH THU 7 NGÀY QUA (Dành cho Trang Tổng quan)
    public ArrayList<Object[]> get7DaysRevenue() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT DATE(i.created_at) as report_date, "
                + "COALESCE(SUM(id.quantity * b.import_price), 0) as total_cost, "
                + "COALESCE(SUM(id.subtotal), 0) as total_revenue "
                + "FROM invoices i "
                + "JOIN invoice_details id ON i.invoice_id = id.invoice_id "
                + "JOIN books b ON id.book_id = b.book_id "
                + "WHERE i.created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) "
                + "GROUP BY DATE(i.created_at) "
                + "ORDER BY DATE(i.created_at) ASC";

        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("report_date");
                double cost = rs.getDouble("total_cost");
                double revenue = rs.getDouble("total_revenue");
                double profit = revenue - cost;
                list.add(new Object[] { date, cost, revenue, profit });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 6. THỐNG KÊ PHIẾU NHẬP HÀNG (Thay thế Tồn Kho theo yêu cầu)
    // Gom nhóm theo từng ngày nhập hàng
    public ArrayList<Object[]> getImportReceiptReport(Date startDate, Date endDate) {
        ArrayList<Object[]> list = new ArrayList<>();
        // Note: Cột thời gian là receipt_date, bảng là import_receipts &
        // import_receipt_details
        String sql = "SELECT DATE(ir.receipt_date) as import_date, "
                + "COUNT(DISTINCT ir.receipt_id) as total_receipts, "
                + "COALESCE(SUM(ird.quantity), 0) as total_books_imported, "
                + "COALESCE(SUM(ird.subtotal), 0) as total_cost "
                + "FROM import_receipts ir "
                + "LEFT JOIN import_receipt_details ird ON ir.receipt_id = ird.receipt_id "
                + "WHERE (? IS NULL OR DATE(ir.receipt_date) >= ?) "
                + "AND (? IS NULL OR DATE(ir.receipt_date) <= ?) "
                + "GROUP BY DATE(ir.receipt_date) "
                + "ORDER BY DATE(ir.receipt_date) DESC";

        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, startDate);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setDate(4, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("import_date"),
                            rs.getInt("total_receipts"),
                            rs.getInt("total_books_imported"),
                            rs.getDouble("total_cost")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 7. THỐNG KÊ TỒN KHO (Giữ lại nếu bạn vẫn muốn hiển thị ở đâu đó)
    public ArrayList<UnitsInStockDTO> UnitsInStockReport() {
        ArrayList<UnitsInStockDTO> list = new ArrayList<>();
        String sql = "SELECT b.book_id, b.book_title, "
                + "GROUP_CONCAT(a.author_name SEPARATOR ', ') AS author, "
                + "c.category_name AS category, "
                + "b.stock_quantity, b.import_price, "
                + "(b.stock_quantity * b.import_price) AS stock_value, "
                + "b.minimum_stock "
                + "FROM books b "
                + "LEFT JOIN categories c ON b.category_id = c.category_id "
                + "LEFT JOIN book_authors ba ON b.book_id = ba.book_id "
                + "LEFT JOIN authors a ON ba.author_id = a.author_id "
                + "GROUP BY b.book_id, b.book_title, c.category_name, "
                + "b.stock_quantity, b.import_price, b.minimum_stock "
                + "ORDER BY b.stock_quantity ASC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UnitsInStockDTO dto = new UnitsInStockDTO();
                dto.setBookID(rs.getInt("book_id"));
                dto.setBookTitle(rs.getString("book_title"));
                String authorName = rs.getString("author");
                dto.setAuthor(authorName != null ? authorName : "Chưa cập nhật");
                String categoryName = rs.getString("category");
                dto.setCategory(categoryName != null ? categoryName : "Chưa cập nhật");
                dto.setQuantity(rs.getInt("stock_quantity"));
                dto.setImportPrice(rs.getDouble("import_price"));
                dto.setStockValue(rs.getDouble("stock_value"));
                dto.setMinimumStock(rs.getInt("minimum_stock"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy doanh thu gom nhóm theo từng ngày (Dùng cho mọi khoảng thời gian tùy
    // chọn)
    public ArrayList<Object[]> getRevenueByDateRange(Date startDate, Date endDate) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT DATE(i.created_at) as report_date, "
                + "COALESCE(SUM(id.quantity * b.import_price), 0) as total_cost, "
                + "COALESCE(SUM(id.subtotal), 0) as total_revenue "
                + "FROM invoices i "
                + "JOIN invoice_details id ON i.invoice_id = id.invoice_id "
                + "JOIN books b ON id.book_id = b.book_id "
                + "WHERE (? IS NULL OR DATE(i.created_at) >= ?) "
                + "AND (? IS NULL OR DATE(i.created_at) <= ?) "
                + "GROUP BY DATE(i.created_at) ORDER BY DATE(i.created_at) ASC";

        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setDate(4, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] { rs.getString("report_date"), rs.getDouble("total_cost"),
                            rs.getDouble("total_revenue"),
                            rs.getDouble("total_revenue") - rs.getDouble("total_cost") });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}