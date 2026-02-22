package BUS;

import DAO.CustomerDAO;
import DTO.CustomerDTO;
import java.util.ArrayList;

public class CustomerBUS {

    private CustomerDAO customerDAO = new CustomerDAO();
    private ArrayList<CustomerDTO> listCustomer;

    public CustomerBUS() {
        this.listCustomer = customerDAO.getAll();
    }

    public ArrayList<CustomerDTO> getAll() {
        return this.listCustomer;
    }

    public boolean addCustomer(CustomerDTO dto) {
        boolean isSuccess = customerDAO.insert(dto);
        if (isSuccess) this.listCustomer = customerDAO.getAll(); // Refresh RAM
        return isSuccess;
    }

    public boolean updateCustomer(CustomerDTO dto) {
        boolean isSuccess = customerDAO.update(dto);
        if (isSuccess) this.listCustomer = customerDAO.getAll(); // Refresh RAM
        return isSuccess;
    }

    public boolean deleteCustomer(int customerId) {
        boolean isSuccess = customerDAO.delete(customerId);
        if (isSuccess) this.listCustomer = customerDAO.getAll(); // Refresh RAM
        return isSuccess;
    }

    public ArrayList<CustomerDTO> search(String text) {
        ArrayList<CustomerDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();
        
        if (text.isEmpty()) return this.listCustomer;

        for (CustomerDTO cus : listCustomer) {
            if (String.valueOf(cus.getCustomerId()).contains(text) || 
                cus.getFullName().toLowerCase().contains(text) || 
                cus.getPhone().contains(text)) {
                result.add(cus);
            }
        }
        return result;
    }
}