package BUS;

import DAO.InvoiceDAO;
import DTO.CustomerDTO;
import DTO.InvoiceDTO;
import DTO.ValidationResult;

import java.util.ArrayList;

public class InvoiceBUS {

    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private ArrayList<InvoiceDTO> listInvoice;

    public InvoiceBUS() {
        this.listInvoice = invoiceDAO.getAll();
    }

    public void loadDataFromDB() {
        try {
            listInvoice = invoiceDAO.getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<InvoiceDTO> getAll() {
        return invoiceDAO.getAll();
    }

    public int addInvoice(InvoiceDTO dto) {
        int success = invoiceDAO.insert(dto);
        if (success > 0) {
            this.listInvoice = invoiceDAO.getAll(); // Làm mới RAM
        }
        return success;
    }

    public boolean cancelInvoice(int invoiceId) {
        // Hủy hóa đơn -> Trạng thái 'Cancelled'
        boolean success = invoiceDAO.updateStatus(invoiceId, "Cancelled");
        if (success) {
            this.listInvoice = invoiceDAO.getAll(); // Làm mới RAM
        }
        return success;
    }

    public ArrayList<InvoiceDTO> refreshData() {
        return invoiceDAO.getAll();
    }

    public ArrayList<InvoiceDTO> search(String text) {
        ArrayList<InvoiceDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();

        if (text.isEmpty())
            return this.listInvoice;
        for (InvoiceDTO inv : listInvoice) {
            if (String.valueOf(inv.getInvoiceId()).contains(text) ||
                    String.valueOf(inv.getCustomerId()).contains(text) ||
                    inv.getPaymentMethod().toLowerCase().contains(text) ||
                    inv.getStatus().toLowerCase().contains(text)) {
                result.add(inv);
            }
        }
        return result;
    }

    public ValidationResult addInvoice(InvoiceDTO invoice, boolean hasDetails, CustomerDTO customer) {
        ValidationResult vr = Validator.validateInvoice(invoice, hasDetails, customer);

        if (!vr.isValid()) {
            return vr;
        }

        int newId = invoiceDAO.insert(invoice);
        if (newId <= 0) {
            vr.addError("system", "Lỗi CSDL: Không thể tạo HÓA ĐƠN (Kiểm tra lại khóa ngoại hoặc cấu trúc DB)!");
        } else {
            // Gán ID mới vào DTO
            invoice.setInvoiceId(newId);
        }

        return vr;
    }
}