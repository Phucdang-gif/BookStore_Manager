package BUS;

import DAO.CustomerDAO;
import DTO.CustomerDTO;
import java.util.ArrayList;

import com.kitfox.svg.A;

import DTO.ValidationResult;

public class CustomerBUS {

    private CustomerDAO customerDAO = new CustomerDAO();
    private ArrayList<CustomerDTO> listCustomer;

    public CustomerBUS() {
        this.listCustomer = customerDAO.getAll();
    }

    public ArrayList<CustomerDTO> getAll() {
        return this.listCustomer;
    }

    public ValidationResult addCustomer(CustomerDTO dto) {
        ValidationResult vr = Validator.validateCustomer(dto, this.listCustomer);
        if (!vr.isValid())
            return vr;

        if (customerDAO.insert(dto)) {
            this.listCustomer = customerDAO.getAll();
        } else {
            vr.addError("system", "Lỗi hệ thống khi thêm khách hàng");
        }
        return vr;
    }

    public ValidationResult updateCustomer(CustomerDTO dto) {
        ValidationResult vr = Validator.validateCustomer(dto, this.listCustomer);
        if (!vr.isValid())
            return vr;
        if (customerDAO.update(dto)) {
            this.listCustomer = customerDAO.getAll();
        } else {
            vr.addError("system", "Lỗi hệ thống khi cập nhật khách hàng");
        }
        return vr;
    }

    public ValidationResult deleteCustomer(int customerId) {
        ValidationResult vr = new ValidationResult();
        if (customerDAO.delete(customerId)) {
            this.listCustomer = customerDAO.getAll();
        } else {
            vr.addError("system", "Không thể xóa khách hàng này");
        }
        return vr;
    }

    public ArrayList<CustomerDTO> search(String text) {
        ArrayList<CustomerDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();

        if (text.isEmpty())
            return this.listCustomer;

        for (CustomerDTO cus : listCustomer) {
            if (String.valueOf(cus.getCustomerId()).contains(text) ||
                    cus.getFullName().toLowerCase().contains(text) ||
                    cus.getPhone().contains(text)) {
                result.add(cus);
            }
        }
        return result;
    }
    public ArrayList<CustomerDTO> refreshData() {
        this.listCustomer = customerDAO.getAll();
        return this.listCustomer;
}
}