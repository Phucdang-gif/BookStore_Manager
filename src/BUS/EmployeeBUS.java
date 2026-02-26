
package BUS;

import DAO.EmployeeDAO;
import DTO.EmployeeDTO;
import java.util.ArrayList;

public class EmployeeBUS {

    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private ArrayList<EmployeeDTO> listEmployee;

    public EmployeeBUS() {
        this.listEmployee = employeeDAO.getAll();
    }

    public ArrayList<EmployeeDTO> getAll() {
        return this.listEmployee;
    }

    public boolean addEmployee(EmployeeDTO emp) {
        boolean isSuccess = employeeDAO.insert(emp);
        if (isSuccess) {
            this.listEmployee = employeeDAO.getAll(); // Refresh RAM
        }
        return isSuccess;
    }

    public boolean updateEmployee(EmployeeDTO emp) {
        boolean isSuccess = employeeDAO.update(emp);
        if (isSuccess) {
            this.listEmployee = employeeDAO.getAll(); // Refresh RAM
        }
        return isSuccess;
    }

    public boolean deleteEmployee(int employeeId) {
        boolean isSuccess = employeeDAO.delete(employeeId);
        if (isSuccess) {
            this.listEmployee = employeeDAO.getAll(); // Refresh RAM
        }
        return isSuccess;
    }

    public ArrayList<EmployeeDTO> search(String text) {
        ArrayList<EmployeeDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();
        
        if (text.isEmpty()) return this.listEmployee;

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
}
