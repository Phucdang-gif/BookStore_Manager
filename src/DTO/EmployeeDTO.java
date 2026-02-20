package DTO;

import java.sql.Date; // Dùng Date vì trong DB là kiểu 'date' (không có giờ phút)

public class EmployeeDTO {
    // 1. Các thuộc tính chuẩn (Khớp 100% với ảnh bảng NHAN_VIEN)
    private int employeeId;      // ma_nhan_vien
    private String fullName;     // ho_ten
    private Date birthDate;      // ngay_sinh
    private String gender;       // gioi_tinh
    private String phone;        // sdt
    private String address;      // dia_chi
    private String position;     // chuc_vu
    private double salary;       // luong (decimal -> double)
    private Date startDate;      // ngay_vao_lam
    private Date resignDate;     // ngay_nghi_viec (Có thể null)
    private String status;       // trang_thai
    private String image;        // hinh_anh

    // 2. Constructor
    public EmployeeDTO() {}

    public EmployeeDTO(int employeeId, String fullName, Date birthDate, String gender, String phone, 
                       String address, String position, double salary, Date startDate, Date resignDate, 
                       String status, String image) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.position = position;
        this.salary = salary;
        this.startDate = startDate;
        this.resignDate = resignDate;
        this.status = status;
        this.image = image;
    }

    // 3. Getter & Setter
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

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

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getResignDate() { return resignDate; }
    public void setResignDate(Date resignDate) { this.resignDate = resignDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    // toString để debug
    @Override
    public String toString() {
        return fullName + " - " + position;
    }
}