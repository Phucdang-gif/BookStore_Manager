package BUS;

import DAO.ImportReceiptDAO;
import DTO.ImportReceiptDTO;
import DTO.ValidationResult;

import java.util.ArrayList;

public class ImportReceiptBUS {

    private ImportReceiptDAO dao = new ImportReceiptDAO();

    public ArrayList<ImportReceiptDTO> getAll() {
        return dao.getAll();
    }

    public int addReceipt(ImportReceiptDTO receipt) {
        return dao.insert(receipt);
    }

    public ImportReceiptDTO findById(int id) {
        ArrayList<ImportReceiptDTO> list = dao.getAll();
        for (ImportReceiptDTO r : list) {
            if (r.getReceiptId() == id) {
                return r;
            }
        }
        return null;
    }

    public boolean cancelReceipt(int id) {
        return dao.delete(id);
    }
    public ArrayList<ImportReceiptDTO> refreshData() {
        return dao.getAll();
    }

    public ArrayList<ImportReceiptDTO> searchByStatus(String status) {
        ArrayList<ImportReceiptDTO> result = new ArrayList<>();
        ArrayList<ImportReceiptDTO> list = dao.getAll();
        for (ImportReceiptDTO r : list) {
            if (r.getStatus().equalsIgnoreCase(status)) {
                result.add(r);
            }
        }
        return result;
    }
    public ValidationResult addReceipt(ImportReceiptDTO receipt, boolean hasDetails) {
        // 1. Gọi cảnh sát kiểm tra
        ValidationResult vr = Validator.validateImportReceipt(receipt, hasDetails);
        
        // 2. Nếu có lỗi (isValid == false), trả biên bản về ngay lập tức
        if (!vr.isValid()) {
            return vr; 
        }

        // 3. Nếu an toàn, mới gọi DAO lưu xuống DB
        int newId = dao.insert(receipt);
        if (newId <= 0) {
            vr.addError("system", "Lỗi CSDL: Không thể tạo Phiếu nhập (Kiểm tra lại khóa ngoại hoặc cấu trúc DB)!");
        } else {
            // (Tùy chọn) Em có thể gán ID mới vào DTO nếu cần dùng tiếp
            receipt.setReceiptId(newId);
        }
        
        return vr;
    }
}