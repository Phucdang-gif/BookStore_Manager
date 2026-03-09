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
    // THONG KE KHACH HANG theo ngay bat dau va ngay ket thuc
    // (STT.ID,NAME,TOTALINVOICE,TOTALAMOUNT)
    public ArrayList<CustomerRevenueDTO> CustomerReport(Date startDate, Date endDate) {
        ArrayList<CustomerRevenueDTO> list = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.full_name, "
                + "COUNT(i.invoice_id) AS totalinvoice, "
                + "COALESCE(SUM(i.final_amount), 0) AS totalamount "
                + "FROM customers c "
                + "LEFT JOIN invoices i ON c.customer_id = i.customer_id "
                + "AND (? IS NULL OR i.created_at >= ?) "
                + "AND (? IS NULL OR i.created_at <= ?) "
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
                    dto.setOrdinalnumber(number);
                    dto.setTotalinvoices(rs.getInt("totalinvoice"));
                    dto.setTotalamount(rs.getDouble("totalamount"));
                    list.add(dto);
                    number++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // THONG KE THEO NHAN VIEN theo ngay bat dau ngay ket thuc
    public ArrayList<EmployeeRevenueDTO> EmployeeReport(Date startDate, Date endDate) {
        ArrayList<EmployeeRevenueDTO> list = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.full_name, "
                + "COUNT(i.invoice_id) AS totalinvoices, "
                + "COALESCE(SUM(i.final_amount), 0) AS totalrevenue "
                + "FROM employees e "
                + "LEFT JOIN invoices i ON e.employee_id = i.employee_id "
                + "AND (? IS NULL OR i.created_at >= ?) "
                + "AND (? IS NULL OR i.created_at <= ?) "
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
                    dto.setOrdinalnumber(number);
                    list.add(dto);
                    number++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // THONG KE SACH THEO NGAY BAT DAU VA NGAY KET THUC
    public ArrayList<BookRevenueDTO> BookReport(Date startDate, Date endDate) {
        ArrayList<BookRevenueDTO> list = new ArrayList<>();
        String sql = "SELECT b.book_id, b.book_title, "
                + "SUM(id.quantity) AS total_quantity, "
                + "SUM(id.subtotal) AS total_revenue "
                + "FROM books b "
                + "JOIN invoice_details id ON b.book_id = id.book_id "
                + "JOIN invoices i ON id.invoice_id = i.invoice_id "
                + "WHERE (? IS NULL OR DATE(i.created_at) >= ?) "
                + "AND (? IS NULL OR DATE(i.created_at) <= ?) "
                + "GROUP BY b.book_id, b.book_title "
                + "ORDER BY total_quantity DESC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setDate(4, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                int number = 1;
                while (rs.next()) {
                    BookRevenueDTO dto = new BookRevenueDTO();
                    dto.setBookID(rs.getInt("book_id"));
                    dto.setBookTitle(rs.getString("book_title"));
                    dto.setTotalSold(rs.getInt("total_quantity"));
                    dto.setTotalRevenue(rs.getDouble("total_revenue"));
                    dto.setOrdinalNumber(number);
                    list.add(dto);
                    number++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // THONG KE DOANH THU THEO THANG
    public ArrayList<RevenueReportDTO> RevenueReport(int year) {

        ArrayList<RevenueReportDTO> list = new ArrayList<>();

        String sql = "SELECT YEAR(i.created_at) AS year, "
                + "MONTH(i.created_at) AS month, "
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

    public ArrayList<UnitsInStockDTO> UnitsInStockReport() {

        ArrayList<UnitsInStockDTO> list = new ArrayList<>();

        // SQL MỚI:
        // 1. Dùng GROUP_CONCAT để gộp tên các tác giả (vì một sách có thể nhiều tác
        // giả)
        // 2. JOIN bảng categories để lấy tên thể loại
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

                // Xử lý trường hợp sách chưa có tác giả hoặc thể loại (tránh lỗi null)
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
}