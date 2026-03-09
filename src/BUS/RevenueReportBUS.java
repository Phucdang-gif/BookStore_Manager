package BUS;

import DAO.RevenueReportDAO;
import DTO.BookRevenueDTO;
import DTO.CustomerRevenueDTO;
import DTO.EmployeeRevenueDTO;
import DTO.RevenueReportDTO;
import DTO.UnitsInStockDTO;

import java.sql.Date;
import java.util.ArrayList;

public class RevenueReportBUS {
    private RevenueReportDAO dao = new RevenueReportDAO();

    // Thống kê khách hàng theo ngày bắt đầu và kết thúc
    public ArrayList<CustomerRevenueDTO> getCustomerReport(Date startDate, Date endDate) {
        return dao.CustomerReport(startDate, endDate);
    }

    // Thống kê theo nhân viên theo ngày bắt đầu và kết thúc
    public ArrayList<EmployeeRevenueDTO> getEmployeeReport(Date startDate, Date endDate) {
        return dao.EmployeeReport(startDate, endDate);
    }

    // Thống kê sách theo ngày bắt đầu và kết thúc
    public ArrayList<BookRevenueDTO> getBookReport(Date startDate, Date endDate) {
        return dao.BookReport(startDate, endDate);
    }

    // Thống kê doanh thu theo tháng
    public ArrayList<RevenueReportDTO> getRevenueReport(int year) {
        return dao.RevenueReport(year);
    }

    // Thống kê đơn vị tồn kho
    public ArrayList<UnitsInStockDTO> getUnitsInStockReport() {
        return dao.UnitsInStockReport();
    }
}
