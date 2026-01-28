## 📂 Cấu trúc dự án
Dự án được tổ chức theo mô hình 3 lớp (3-Layer Architecture):

```text
BookStore_Manager
├── 📁 lib/                   # Chứa các thư viện bên ngoài (MySQL Connector, JCalendar...)
├── 📁 src/
│   ├── 📦 bus/               # Business Logic Layer (Xử lý nghiệp vụ)
│   │   ├── BookBUS.java      # Logic kiểm tra, tính toán cho sách
│   │   ├── ...
│   │
│   ├── 📦 dao/               # Data Access Layer (Truy xuất dữ liệu)
│   │   ├── ConnectDB.java    # Cấu hình kết nối MySQL
│   │   ├── BookDAO.java      # Các câu lệnh SQL (SELECT, INSERT...)
│   │   ├── ...
│   │
│   ├── 📦 dto/               # Data Transfer Object (Đối tượng dữ liệu)
│   │   ├── BookDTO.java      # Thực thể Sách (Model)
│   │   ├── ...
│   │
│   ├── 📦 gui/               # Graphical User Interface (Giao diện)
│   │   ├── 📂 components/    # Các thành phần giao diện tái sử dụng (Button, Table...)
│   │   ├── 📂 dialog/        # Các cửa sổ phụ (Thêm, Sửa, Chi tiết)
│   │   │   ├── BookDialog.java
│   │   ├── 📂 model/         # Các Panel chính (Header, TablePanel...)
│   │   ├── MainFrame.java    # Cửa sổ chính của ứng dụng
│   │   ├── Login.java        # Màn hình đăng nhập
│   │
│   └── 📦 util/              # Các tiện ích dùng chung (Format tiền, Xử lý ảnh...)
│
├── 📜 database.sql           # Script tạo Database (Import cái này vào MySQL)
├── 📜 README.md              # Tài liệu hướng dẫn sử dụng
└── 📜 .gitignore             # File cấu hình bỏ qua của Git
```
