package DTO;

import java.sql.Date;

public class EmployeeDTO {
    private int employeeId;
    private String fullName;
    private Date dateOfBirth;
    private String gender; // 'male' hoặc 'female'
    private String phone;
    private String address;
    private String position;
    private double salary;
    private Date hireDate;
    private Date terminationDate;
    private String status; // 'active' hoặc 'inactive'
    private String avatar;

    public EmployeeDTO() {}

    public EmployeeDTO(int employeeId, String fullName, Date dateOfBirth, String gender, String phone, String address, String position, double salary, Date hireDate, Date terminationDate, String status, String avatar) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
        this.terminationDate = terminationDate;
        this.status = status;
        this.avatar = avatar;
    }

    // --- GETTER & SETTER ---
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    public Date getTerminationDate() { return terminationDate; }
    public void setTerminationDate(Date terminationDate) { this.terminationDate = terminationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    @Override
    public String toString() {
        // Trả về Tên nhân viên. Nếu thích em có thể nối thêm SĐT cho dễ phân biệt người trùng tên
        // Ví dụ: return this.fullName + " - " + this.phone;
        return this.fullName+" - "+this.phone; // Hiển thị tên và chức vụ để dễ phân biệt khi có nhiều nhân viên trùng tên
    }
}