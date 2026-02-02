package BUS;

import DAO.BookDAO;
import DAO.InvoiceDAO;
import DAO.InvoiceDetailDAO;
import DTO.InvoiceDTO;

import DTO.InvoiceDetailDTO;
import java.util.List;

public class InvoiceBUS {
    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();
    private BookDAO bookDAO = new BookDAO();

    // Hàm quan trọng nhất: THANH TOÁN
    public boolean createInvoice(InvoiceDTO invoice, List<InvoiceDetailDTO> details) {
        // 1. Lưu Hóa Đơn trước để lấy ID
        int invoiceId = invoiceDAO.insert(invoice);

        if (invoiceId == -1) {
            return false; // Lưu hóa đơn thất bại -> Dừng luôn
        }

        // 2. Có ID rồi, bắt đầu lưu từng chi tiết
        for (InvoiceDetailDTO detail : details) {
            detail.setInvoiceId(invoiceId); // Gán ID hóa đơn vào chi tiết

            // Lưu chi tiết vào DB
            detailDAO.insert(detail);

            // 3. Trừ tồn kho
            bookDAO.updateQuantity(detail.getBookId(), detail.getQuantity());
        }

        return true; // Thành công tất cả
    }

    public List<InvoiceDTO> getAll() {
        return invoiceDAO.selectAll();
    }

    public InvoiceDTO getInvoiceById(int id) {
        return invoiceDAO.selectById(id);
    }
}