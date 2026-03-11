package BUS;

import DAO.InvoiceServiceDAO;
import DTO.InvoiceServiceDTO;

public class InvoiceServiceBUS {
    private InvoiceServiceDAO dao = new InvoiceServiceDAO();

    public boolean insert(InvoiceServiceDTO dto) {
        
        return dao.insert(dto);
    }
}
