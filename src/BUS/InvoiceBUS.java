package BUS;

import DAO.InvoiceDAO;
import DTO.InvoiceDTO;
import java.util.ArrayList;

public class InvoiceBUS {

    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private ArrayList<InvoiceDTO> listInvoice;

    public InvoiceBUS() {
        this.listInvoice = invoiceDAO.getAll();
    }

    public ArrayList<InvoiceDTO> getAll() {
        return this.listInvoice;
    }

    public boolean addInvoice(InvoiceDTO dto) {
        boolean success = invoiceDAO.insert(dto);
        if (success) {
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

    public ArrayList<InvoiceDTO> search(String text) {
        ArrayList<InvoiceDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();
        
        if (text.isEmpty()) return this.listInvoice;

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
}