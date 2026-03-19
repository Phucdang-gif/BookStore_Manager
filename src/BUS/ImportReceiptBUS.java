package BUS;

import DAO.ImportReceiptDAO;
import DTO.ImportReceiptDTO;
import DTO.ValidationResult;
import java.sql.Date;

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

    public ArrayList<ImportReceiptDTO> search(String text) {
        ArrayList<ImportReceiptDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();

        if (text.isEmpty())
            return this.getAll();
        for (ImportReceiptDTO r : getAll()) {
            if (String.valueOf(r.getReceiptId()).contains(text) ||
                    String.valueOf(r.getEmployeeId()).contains(text) ||
                    String.valueOf(r.getSupplierId()).contains(text) ||
                    r.getStatus().toLowerCase().contains(text)) {
                result.add(r);
            }
        }
        return result;
    }

    // Cac ham tim kiem theo ngay gio
    public ArrayList<ImportReceiptDTO> searchBySupplierID(int ID, Date s, Date e) {
        try {

            return dao.searchBySupplierID(s, e, ID);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public ArrayList<ImportReceiptDTO> searchByEmployeeID(int ID, Date s, Date e) {
        try {

            return dao.searchByEmployeeID(s, e, ID);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public ArrayList<ImportReceiptDTO> searchByDate(Date s, Date e) {
        return dao.searchByDate(s, e);
    }

    public ArrayList<ImportReceiptDTO> searchByImportID(int ID, Date s, Date e) {
        try {

            return dao.searchByImportID(ID, s, e);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}