package BUS;

import DAO.InvoiceDetailDAO;
import DTO.InvoiceDetailDTO;
import java.util.ArrayList;

public class InvoiceDetailBUS {

    private InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();

    // Lấy chi tiết sách của 1 hóa đơn cụ thể
    public ArrayList<InvoiceDetailDTO> getDetailsByInvoiceId(int invoiceId) {
        return detailDAO.getByInvoiceId(invoiceId);
    }

    // Lưu danh sách sản phẩm khi thanh toán thành công
    public boolean saveAllDetails(ArrayList<InvoiceDetailDTO> details) {
        if (details == null || details.isEmpty()) return false;
        return detailDAO.insertBatch(details);
    }
}
