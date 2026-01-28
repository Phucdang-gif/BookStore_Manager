# 📚 BOOKSTORE MANAGER (PHẦN MỀM QUẢN LÝ NHÀ SÁCH)

![Java](https://img.shields.io/badge/Java-JDK_17+-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java_Swing-red?style=for-the-badge&logo=java)
![FlatLaf](https://img.shields.io/badge/UI-FlatLaf-blue?style=for-the-badge)
---

## 📑 Mục lục
1. [Giới thiệu](#-giới-thiệu)
2. [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
3. [Chức năng chính](#-chức-năng-chính)
4. [Thiết kế Cơ sở dữ liệu (ERD)](#-thiết-kế-cơ-sở-dữ-liệu)
5. [Cấu trúc dự án](#-cấu-trúc-dự-án)
6. [Hướng dẫn cài đặt](#-hướng-dẫn-cài-đặt)
7. [Hình ảnh Demo](#-hình-ảnh-demo)

---

## 📖 Giới thiệu
**BookStore Manager** là giải pháp phần mềm toàn diện dành cho các nhà sách vừa và nhỏ. Ứng dụng giúp tự động hóa quy trình quản lý bán hàng, nhập kho, kiểm soát tồn kho và báo cáo doanh thu, thay thế hoàn toàn sổ sách thủ công.

Dự án được xây dựng theo **Mô hình 3 lớp (3-Layer Architecture)** đảm bảo tính chặt chẽ, dễ bảo trì và mở rộng.

---

## 🛠 Công nghệ sử dụng
| Thành phần | Công nghệ / Thư viện | Mô tả |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java (JDK 17) | Ngôn ngữ lập trình chính |
| **Giao diện** | Java Swing + FlatLaf | FlatLaf giúp giao diện hiện đại, phẳng (Flat UI) |
| **Cơ sở dữ liệu** | MySQL | Hệ quản trị CSDL quan hệ |
| **Kết nối DB** | JDBC (MySQL Connector) | Giao tiếp giữa Java và MySQL |
| **Xử lý hình ảnh** | SVG Salamander | Hỗ trợ hiển thị icon Vector (SVG) sắc nét |
| **Công cụ** | VS Code / IntelliJ IDEA | Môi trường phát triển tích hợp (IDE) |

---

## 🚀 Chức năng chính

### 1. Quản lý Sản phẩm (Sách)
- ✅ Xem danh sách sách, tìm kiếm tác giả có gợi ý.
- ✅ Thêm mới sách với đầy đủ thông tin: Tác giả, NXB, Thể loại, Giá nhập/bán.
- ✅ Upload và hiển thị ảnh bìa sách .
- 🛠️ Xuất danh sách sách ra file Excel.

### 2. Quản lý Kho hàng
- Nhập hàng từ nhà cung cấp (Tạo phiếu nhập).
- Theo dõi tồn kho thực tế.
- Cảnh báo sách sắp hết hàng.

### 3. Bán hàng & Nhân viên
- Tạo hóa đơn bán hàng.
- Quản lý thông tin khách hàng thân thiết.
- Quản lý danh sách nhân viên và quyền truy cập.

### 4. Thống kê & Báo cáo
- Thống kê doanh thu theo ngày, tháng, năm.
- Thống kê sách bán chạy, sách tồn kho lâu.

---

## 🗃 Thiết kế Cơ sở dữ liệu
Dưới đây là **Sơ đồ thực thể liên kết (ERD)** mô tả cấu trúc dữ liệu của hệ thống:

![ERD Diagram](erd/erd_bookstore.png)
*(Hình ảnh được lưu trong thư mục `erd/` của dự án)*

---

## 📂 Cấu trúc dự án
Dự án tuân thủ nghiêm ngặt mô hình 3 lớp:

```text
vscode_project
├── 📁 erd/                   # Tài liệu thiết kế Database
├── 📁 lib/                   # Thư viện (FlatLaf, MySQL Driver,...)
├── 📁 sql/                   # Script SQL
│   ├── BookStore.sql         # <-- Chạy file này để tạo DB
│   └── example_data.sql      # Dữ liệu mẫu để test
├── 📁 src/
│   ├── 📦 BUS/               # Business Logic Layer (Xử lý nghiệp vụ)
│   ├── 📦 DAO/               # Data Access Layer (Truy vấn SQL)
│   ├── 📦 DTO/               # Data Transfer Object (Đối tượng)
│   ├── 📦 GUI/               # Graphical User Interface
│   │   ├── 📂 dialog/        # Các cửa sổ Pop-up (Thêm, Sửa...)
│   │   ├── 📂 model/         # Các Panel chính (Header, Table...)
│   │   └── MainFrame.java    # Màn hình chính
│   └── 📦 config/            # Cấu hình kết nối DB
└── 📜 README.md
