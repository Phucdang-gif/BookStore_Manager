package BUS;

import DAO.ImportReceiptDetailDAO;
import DTO.ImportReceiptDetailDTO;
import java.util.ArrayList;

public class ImportReceiptDetailBUS {

    private ImportReceiptDetailDAO dao = new ImportReceiptDetailDAO();

    // Lấy chi tiết của 1 phiếu nhập cụ thể
    public ArrayList<ImportReceiptDetailDTO> getDetailsByReceiptId(int receiptId) {
        return dao.getByReceiptId(receiptId);
    }

    // Lưu danh sách các chi tiết khi vừa tạo phiếu nhập xong
    public boolean saveAllDetails(ArrayList<ImportReceiptDetailDTO> details) {
        if (details == null || details.isEmpty())
            return false;
        return dao.insertBatch(details);
    }

    public double calculateTotalByReceiptId(int receiptId) {
        ArrayList<ImportReceiptDetailDTO> details = getDetailsByReceiptId(receiptId);
        double total = 0;
        for (ImportReceiptDetailDTO ct : details) {
            total += ct.getSubtotal();
        }
        return total;
    }

    public boolean insertBatch(ArrayList<ImportReceiptDetailDTO> details) {
        if (details == null || details.isEmpty())
            return false;
        return dao.insertBatch(details);
    }
}