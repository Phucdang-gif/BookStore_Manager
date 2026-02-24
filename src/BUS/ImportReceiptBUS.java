package BUS;

import DAO.ImportReceiptDAO;
import DTO.ImportReceiptDTO;
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
}