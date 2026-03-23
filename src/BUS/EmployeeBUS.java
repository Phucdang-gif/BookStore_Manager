
package BUS;

import DAO.EmployeeDAO;
import DTO.EmployeeDTO;
import java.util.ArrayList;
import DTO.ValidationResult;

public class EmployeeBUS {

    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private ArrayList<EmployeeDTO> listEmployee;

    public EmployeeBUS() {
        this.listEmployee = employeeDAO.getAll();
    }

    public ArrayList<EmployeeDTO> getAll() {
        return this.listEmployee;
    }
    public ArrayList<EmployeeDTO> refreshData() {
        this.listEmployee = employeeDAO.getAll();
        return this.listEmployee;
    }

    public ValidationResult addEmployee(EmployeeDTO emp) {
        ValidationResult vr = Validator.validateEmployee(emp, this.listEmployee);
        if (!vr.isValid())
            return vr;
        if (employeeDAO.insert(emp)) {
            this.listEmployee = employeeDAO.getAll(); // Refresh RAM
        } else {
            vr.addError("system", "Lỗi hệ thống khi thêm nhân viên");
        }
        return vr;

    }

    public ValidationResult updateEmployee(EmployeeDTO emp) {
        ValidationResult vr = Validator.validateEmployee(emp, listEmployee);
        if (!vr.isValid())
            return vr;
        if (employeeDAO.update(emp)) {
            this.listEmployee = employeeDAO.getAll(); // Refresh RAM
        } else {
            vr.addError("system", "Lỗi hệ thống khi cập nhật nhân viên");
        }
        return vr;
    }

    public ValidationResult deleteEmployee(int employeeId) {
        ValidationResult vr = new ValidationResult();
        boolean isSuccess = employeeDAO.delete(employeeId);
        if (isSuccess) {
            this.listEmployee = employeeDAO.getAll(); // Refresh RAM
        } else {
            vr.addError("system", "Lỗi hệ thống khi xóa nhân viên");
        }
        return vr;
    }

    public ArrayList<EmployeeDTO> search(String text) {
        ArrayList<EmployeeDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();

        if (text.isEmpty())
            return this.listEmployee;

        for (EmployeeDTO emp : listEmployee) {
            if (String.valueOf(emp.getEmployeeId()).contains(text) ||
                    emp.getFullName().toLowerCase().contains(text) ||
                    emp.getPhone().contains(text) ||
                    emp.getPosition().toLowerCase().contains(text)) {
                result.add(emp);
            }
        }
        return result;
    }

    public ArrayList<EmployeeDTO> getUnassignedEmployees() {
        return employeeDAO.getEmployeesWithoutAccount();
    }

    public EmployeeDTO getById(int employeeId) {
        for (EmployeeDTO emp : listEmployee) {
            if (emp.getEmployeeId() == employeeId) {
                return emp;
            }
        }
        return null; // Nếu không tìm thấy
    }
    public String getEmployeeName(int employeeId) {
        EmployeeDTO emp = getById(employeeId);
        return emp != null ? emp.getFullName() : "Nhân viên không xác định";
    }
}
