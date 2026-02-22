package BUS;

import DAO.SupplierDAO;
import DTO.SupplierDTO;
import java.util.ArrayList;

public class SupplierBUS {

    private SupplierDAO supplierDAO = new SupplierDAO();

    public ArrayList<SupplierDTO> getAll() {
        return supplierDAO.getAll();
    }

    public boolean addSupplier(SupplierDTO dto) {
        return supplierDAO.insert(dto);
    }
}