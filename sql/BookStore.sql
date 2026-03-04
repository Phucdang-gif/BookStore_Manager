-- =============================================
-- BOOKSTORE DATABASE - FULL SETUP SCRIPT
-- Có thể chạy trên máy bất kỳ (tự tạo DB mới)
-- =============================================
-- Quy hoạch dải ID (mỗi bảng 150 ID):
--   employees               :    1 –  150
--   permission_groups       :  151 –  300
--   accounts                :  301 –  450
--   functions               :  451 –  600
--   permission_details      :  601 –  750
--   customers               :  751 –  900
--   point_redemption_history:  901 – 1050
--   publishers              : 1051 – 1200
--   categories              : 1201 – 1350
--   books                   : 1351 – 1500
--   authors                 : 1501 – 1650
--   book_authors            : 1651 – 1800
--   invoices                : 1801 – 1950
--   invoice_details         : 1951 – 2100
--   discount_services       : 2101 – 2250
--   invoice_services        : 2251 – 2400
--   suppliers               : 2401 – 2550
--   import_receipts         : 2551 – 2700
--   import_receipt_details  : 2701 – 2850
-- =============================================

DROP DATABASE IF EXISTS bookstore_db;
CREATE DATABASE bookstore_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore_db;

-- =============================================
-- PHẦN 1: TẠO CÁC BẢNG
-- =============================================

CREATE TABLE employees (
    employee_id    INT AUTO_INCREMENT PRIMARY KEY,
    full_name      VARCHAR(100) NOT NULL,
    date_of_birth  DATE,
    gender         ENUM('male', 'female', 'other') DEFAULT 'other',
    phone          VARCHAR(15),
    address        VARCHAR(255),
    position       VARCHAR(50),
    salary         DECIMAL(15,2),
    hire_date      DATE,
    termination_date DATE,
    status         ENUM('active', 'inactive') DEFAULT 'active',
    avatar         VARCHAR(255)
) ENGINE=InnoDB AUTO_INCREMENT=1;

CREATE TABLE permission_groups (
    permission_group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name          VARCHAR(100) NOT NULL,
    status              ENUM('active', 'inactive') DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=151;

CREATE TABLE accounts (
    account_id          INT AUTO_INCREMENT PRIMARY KEY,
    employee_id         INT NOT NULL UNIQUE,
    permission_group_id INT NOT NULL,
    username            VARCHAR(50) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    status              ENUM('active', 'locked') DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id)         REFERENCES employees(employee_id),
    FOREIGN KEY (permission_group_id) REFERENCES permission_groups(permission_group_id)
) ENGINE=InnoDB AUTO_INCREMENT=301;

CREATE TABLE functions (
    function_id          INT AUTO_INCREMENT PRIMARY KEY,
    function_name        VARCHAR(100) NOT NULL,
    system_function_code VARCHAR(50) UNIQUE,
    function_group       VARCHAR(100)
) ENGINE=InnoDB AUTO_INCREMENT=451;

CREATE TABLE permission_details (
    detail_id           INT AUTO_INCREMENT PRIMARY KEY,
    permission_group_id INT NOT NULL,
    function_id         INT NOT NULL,
    actions             VARCHAR(255),
    assigned_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (permission_group_id) REFERENCES permission_groups(permission_group_id),
    FOREIGN KEY (function_id)         REFERENCES functions(function_id)
) ENGINE=InnoDB AUTO_INCREMENT=601;

CREATE TABLE customers (
    customer_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name         VARCHAR(100) NOT NULL,
    phone             VARCHAR(15),
    loyalty_points    INT DEFAULT 0,
    registration_date DATE DEFAULT (CURRENT_DATE)
) ENGINE=InnoDB AUTO_INCREMENT=751;

CREATE TABLE point_redemption_history (
    history_id      INT AUTO_INCREMENT PRIMARY KEY,
    customer_id     INT NOT NULL,
    points_redeemed INT NOT NULL,
    value_received  DECIMAL(15,2) NOT NULL,
    redemption_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    redemption_type VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
) ENGINE=InnoDB AUTO_INCREMENT=901;

CREATE TABLE publishers (
    publisher_id   INT AUTO_INCREMENT PRIMARY KEY,
    publisher_name VARCHAR(100) NOT NULL,
    phone          VARCHAR(15),
    status         ENUM('active', 'inactive') DEFAULT 'active'
) ENGINE=InnoDB AUTO_INCREMENT=1051;

CREATE TABLE categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    display_order INT,
    status        ENUM('active', 'inactive') DEFAULT 'active'
) ENGINE=InnoDB AUTO_INCREMENT=1201;

CREATE TABLE books (
    book_id          INT AUTO_INCREMENT PRIMARY KEY,
    publisher_id     INT,
    category_id      INT,
    isbn             VARCHAR(20),
    book_title       VARCHAR(255) NOT NULL,
    page_count       INT,
    language         VARCHAR(50),
    publication_year INT,
    cover_type       VARCHAR(50),
    import_price     DECIMAL(15,2),
    selling_price    DECIMAL(15,2),
    stock_quantity   INT DEFAULT 0,
    minimum_stock    INT DEFAULT 0,
    image            VARCHAR(255),
    status           ENUM('in_stock', 'out_of_stock', 'discontinued') DEFAULT 'in_stock',
    added_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publishers(publisher_id),
    FOREIGN KEY (category_id)  REFERENCES categories(category_id)
) ENGINE=InnoDB AUTO_INCREMENT=1351;

CREATE TABLE authors (
    author_id   INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(100) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1501;

CREATE TABLE book_authors (
    book_author_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id        INT NOT NULL,
    author_id      INT NOT NULL,
    display_order  INT,
    FOREIGN KEY (book_id)   REFERENCES books(book_id),
    FOREIGN KEY (author_id) REFERENCES authors(author_id)
) ENGINE=InnoDB AUTO_INCREMENT=1651;

CREATE TABLE invoices (
    invoice_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id    INT,
    employee_id    INT NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount   DECIMAL(15,2),
    total_discount DECIMAL(15,2),
    points_used    DECIMAL(15,2),
    points_value   DECIMAL(15,2),
    final_amount   DECIMAL(15,2),
    payment_method VARCHAR(50),
    status         VARCHAR(50),
    points_earned  INT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
) ENGINE=InnoDB AUTO_INCREMENT=1801;

CREATE TABLE invoice_details (
    detail_id  INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL,
    book_id    INT NOT NULL,
    quantity   INT NOT NULL,
    unit_price DECIMAL(15,2),
    discount   DECIMAL(15,2),
    subtotal   DECIMAL(15,2),
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (book_id)    REFERENCES books(book_id)
) ENGINE=InnoDB AUTO_INCREMENT=1951;

CREATE TABLE discount_services (
    service_id       INT AUTO_INCREMENT PRIMARY KEY,
    service_name     VARCHAR(100) NOT NULL,
    discount_type    VARCHAR(50),
    discount_value   DECIMAL(15,2),
    minimum_value    DECIMAL(15,2),
    maximum_discount DECIMAL(15,2),
    start_date       DATETIME,
    end_date         DATETIME,
    status           ENUM('active', 'inactive') DEFAULT 'active',
    description      TEXT
) ENGINE=InnoDB AUTO_INCREMENT=2101;

CREATE TABLE invoice_services (
    invoice_service_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id         INT NOT NULL,
    service_id         INT NOT NULL,
    service_type       VARCHAR(50),
    discount_value     DECIMAL(15,2),
    description        TEXT,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (service_id) REFERENCES discount_services(service_id)
) ENGINE=InnoDB AUTO_INCREMENT=2251;

CREATE TABLE suppliers (
    supplier_id   INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    phone         VARCHAR(15),
    status        ENUM('active', 'inactive') DEFAULT 'active'
) ENGINE=InnoDB AUTO_INCREMENT=2401;

CREATE TABLE import_receipts (
    receipt_id   INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id  INT NOT NULL,
    employee_id  INT NOT NULL,
    import_date  DATETIME,
    receipt_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(15,2),
    status       VARCHAR(50),
    notes        TEXT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
) ENGINE=InnoDB AUTO_INCREMENT=2551;

CREATE TABLE import_receipt_details (
    detail_id   INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id  INT NOT NULL,
    book_id     INT NOT NULL,
    quantity    INT NOT NULL,
    unit_price  DECIMAL(15,2),
    subtotal    DECIMAL(15,2),
    expiry_date DATE,
    FOREIGN KEY (receipt_id) REFERENCES import_receipts(receipt_id),
    FOREIGN KEY (book_id)    REFERENCES books(book_id)
) ENGINE=InnoDB AUTO_INCREMENT=2701;

CREATE TABLE system_parameters (
    parameter_code  VARCHAR(50) PRIMARY KEY,
    parameter_value VARCHAR(255),
    description     VARCHAR(255)
) ENGINE=InnoDB;

-- =============================================
-- INDEXES
-- =============================================

CREATE INDEX idx_employee_phone ON employees(phone);
CREATE INDEX idx_customer_phone ON customers(phone);
CREATE INDEX idx_book_title      ON books(book_title);
CREATE INDEX idx_invoice_date    ON invoices(created_at);
CREATE INDEX idx_receipt_date    ON import_receipts(import_date);

-- =============================================
-- PHẦN 2: DỮ LIỆU MẪU
-- =============================================

-- 1. NHÂN VIÊN  (1–150)
INSERT INTO employees (employee_id, full_name, date_of_birth, gender, phone, address, position, salary, hire_date, status) VALUES
(1, 'Nguyễn Văn An',  '1990-05-15', 'male',   '0901234567', '123 Lê Lợi, Quận 1, TP.HCM',        'Quản lý',             15000000, '2020-01-10', 'active'),
(2, 'Trần Thị Bình',  '1995-08-20', 'female', '0912345678', '456 Trần Hưng Đạo, Quận 5, TP.HCM', 'Nhân viên bán hàng',   8000000, '2021-03-15', 'active'),
(3, 'Lê Hoàng Châu',  '1992-12-10', 'male',   '0923456789', '789 Nguyễn Huệ, Quận 1, TP.HCM',    'Thủ kho',              9000000, '2021-06-20', 'active'),
(4, 'Phạm Thị Dung',  '1988-03-25', 'female', '0934567890', '321 Võ Văn Tần, Quận 3, TP.HCM',    'Kế toán',             12000000, '2019-11-05', 'active');

-- 2. NHÓM QUYỀN  (151–300)
INSERT INTO permission_groups (permission_group_id, group_name, status) VALUES
(151, 'Quản trị viên',      'active'),
(152, 'Nhân viên bán hàng', 'active'),
(153, 'Thủ kho',            'active'),
(154, 'Kế toán',            'active');

-- 3. TÀI KHOẢN  (301–450)
INSERT INTO accounts (account_id, employee_id, permission_group_id, username, password, status) VALUES
(301, 1, 151, 'admin',    'admin123', 'active'),
(302, 2, 152, 'nvbh01',   'bh123456', 'active'),
(303, 3, 153, 'thukho01', 'tk123456', 'active'),
(304, 4, 154, 'ketoan01', 'kt123456', 'active');

-- 4. CHỨC NĂNG  (451–600)
INSERT INTO functions (function_id, function_name, system_function_code, function_group) VALUES
(451, 'Quản lý sách',       'BOOK',       'Quản lý kho'),
(452, 'Danh mục',           'CATEGORY',   'Quản lý kho'),
(453, 'Quản lý khách hàng', 'CUSTOMER',   'Bán hàng'),
(454, 'Quản lý nhập hàng',  'IMPORT',     'Quản lý kho'),
(455, 'Quản lý hóa đơn',    'INVOICE',    'Bán hàng'),
(456, 'Khuyến mãi',         'PROMOTION',  'Bán hàng'),
(457, 'Quản lý nhân viên',  'EMPLOYEE',   'Quản lý hệ thống'),
(458, 'Quản lý tài khoản',  'ACCOUNT',    'Quản lý hệ thống'),
(459, 'Phân quyền',         'PERMISSION', 'Quản lý hệ thống');

-- 5. PHÂN QUYỀN  (601–750)
-- Admin (151): toàn quyền
INSERT INTO permission_details (detail_id, permission_group_id, function_id, actions) VALUES
(601, 151, 451, 'Xem,Thêm,Sửa,Xóa'),
(602, 151, 452, 'Xem,Thêm,Sửa,Xóa'),
(603, 151, 453, 'Xem,Thêm,Sửa,Xóa'),
(604, 151, 454, 'Xem,Thêm,Sửa,Xóa'),
(605, 151, 455, 'Xem,Thêm,Sửa,Xóa'),
(606, 151, 456, 'Xem,Thêm,Sửa,Xóa'),
(607, 151, 457, 'Xem,Thêm,Sửa,Xóa'),
(608, 151, 458, 'Xem,Thêm,Sửa,Xóa'),
(609, 151, 459, 'Xem,Thêm,Sửa,Xóa');
-- Nhân viên bán hàng (152)
INSERT INTO permission_details (detail_id, permission_group_id, function_id, actions) VALUES
(610, 152, 451, 'Xem'),
(611, 152, 452, 'Xem'),
(612, 152, 453, 'Xem,Thêm,Sửa'),
(613, 152, 455, 'Xem,Thêm'),
(614, 152, 456, 'Xem');
-- Thủ kho (153)
INSERT INTO permission_details (detail_id, permission_group_id, function_id, actions) VALUES
(615, 153, 451, 'Xem,Thêm,Sửa'),
(616, 153, 452, 'Xem,Thêm,Sửa'),
(617, 153, 454, 'Xem,Thêm,Sửa');
-- Kế toán (154)
INSERT INTO permission_details (detail_id, permission_group_id, function_id, actions) VALUES
(618, 154, 454, 'Xem'),
(619, 154, 455, 'Xem'),
(620, 154, 456, 'Xem,Thêm,Sửa');

-- 6. KHÁCH HÀNG  (751–900)
INSERT INTO customers (customer_id, full_name, phone, loyalty_points, registration_date) VALUES
(751, 'Võ Minh Tuấn',  '0945678901', 150, '2023-01-15'),
(752, 'Hoàng Thị Mai', '0956789012', 320, '2023-03-20'),
(753, 'Đặng Văn Nam',  '0967890123',  80, '2023-06-10'),
(754, 'Bùi Thị Lan',   '0978901234', 500, '2022-12-05');

-- 7. LỊCH SỬ QUY ĐỔI ĐIỂM  (901–1050)
INSERT INTO point_redemption_history (history_id, customer_id, points_redeemed, value_received, redemption_type) VALUES
(901, 752, 100, 10000, 'Giảm giá hóa đơn'),
(902, 754, 200, 20000, 'Giảm giá hóa đơn');

-- 8. NHÀ XUẤT BẢN  (1051–1200)
INSERT INTO publishers (publisher_id, publisher_name, phone, status) VALUES
(1051, 'Nhà xuất bản Trẻ',                     '0283822711', 'active'),
(1052, 'Nhà xuất bản Kim Đồng',                '0283943344', 'active'),
(1053, 'Nhà xuất bản Văn học',                 '0283822211', 'active'),
(1054, 'Nhà xuất bản Thế giới',                '0283825252', 'active'),
(1055, 'Nhà xuất bản Nhã Nam',                 '0283517898', 'active'),
(1056, 'Nhà xuất bản Phụ Nữ',                  '0243825993', 'active'),
(1057, 'Nhà xuất bản Lao Động',                '0243851538', 'active'),
(1058, 'Nhà xuất bản Hội Nhà Văn',             '0243822213', 'active'),
(1059, 'Nhà xuất bản Tổng hợp TP.HCM',         '0283822534', 'active'),
(1060, 'Nhà xuất bản Dân Trí',                 '0243762334', 'active'),
(1061, 'Nhà xuất bản Hồng Đức',                '0243926002', 'active'),
(1062, 'Nhà xuất bản Tri Thức',                '0243944727', 'active'),
(1063, 'Nhà xuất bản Đại học Quốc gia Hà Nội', '0243754773', 'active'),
(1064, 'Nhà xuất bản Công Thương',             '0243934168', 'active');

-- 9. THỂ LOẠI  (1201–1350)
INSERT INTO categories (category_id, category_name, display_order, status) VALUES
(1201, 'Văn học Việt Nam',            1,  'active'),
(1202, 'Văn học nước ngoài',          2,  'active'),
(1203, 'Sách thiếu nhi',              3,  'active'),
(1204, 'Sách kỹ năng sống',           4,  'active'),
(1205, 'Sách Kinh tế',                5,  'active'),
(1206, 'Sách Lịch sử',                6,  'active'),
(1207, 'Truyện Trinh thám - Kinh dị', 7,  'active'),
(1208, 'Tâm lý học',                  8,  'active'),
(1209, 'Tiểu sử - Hồi ký',            9,  'active'),
(1210, 'Khoa học viễn tưởng',         10, 'active'),
(1211, 'Sách Giáo khoa - Tham khảo',  11, 'active'),
(1212, 'Sách Học Ngoại ngữ',          12, 'active'),
(1213, 'Y học - Sức khỏe',            13, 'active'),
(1214, 'Truyện Tranh (Manga/Comic)',   14, 'active');

-- 10. SÁCH  (1351–1500)
INSERT INTO books (book_id, publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status) VALUES
(1351, 1051, 1201, '9786041002345', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh',        368, 'Tiếng Việt', 2018, 'Bìa mềm',  65000,  95000,  50, 10, 'hoa_vang.jpg',         'in_stock'),
(1352, 1052, 1203, '9786042134567', 'Doraemon - Nobita Và Hành Tinh Màu Tím', 196, 'Tiếng Việt', 2023, 'Bìa mềm',  15000,  25000, 100, 20, 'doraemon.jpg',          'in_stock'),
(1353, 1053, 1202, '9786043245678', 'Nhà Giả Kim',                             227, 'Tiếng Việt', 2020, 'Bìa cứng', 45000,  79000,  35, 10, 'nha_gia_kim.jpg',       'in_stock'),
(1354, 1054, 1204, '9786044356789', 'Đắc Nhân Tâm',                            320, 'Tiếng Việt', 2021, 'Bìa mềm',  50000,  86000,   8, 10, 'dac_nhan_tam.jpg',      'in_stock'),
(1355, 1058, 1202, '9786049876541', 'Rừng Na Uy',                              500, 'Tiếng Việt', 2021, 'Bìa mềm',  90000, 150000,  40,  5, 'rung_na_uy.jpg',        'in_stock'),
(1356, 1051, 1203, '9786041123456', 'Harry Potter và Hòn đá Phù thủy',         350, 'Tiếng Việt', 2022, 'Bìa mềm', 110000, 185000, 100, 10, 'harry_potter_1.jpg',    'in_stock'),
(1357, 1053, 1201, '9786048234567', 'Gió Lạnh Đầu Mùa',                        180, 'Tiếng Việt', 2019, 'Bìa mềm',  30000,  55000,  25,  5, 'gio_lanh_dau_mua.jpg',  'in_stock'),
(1358, 1053, 1201, '9786048345678', 'Chí Phèo',                                 200, 'Tiếng Việt', 2020, 'Bìa mềm',  35000,  60000,  30,  5, 'chi_pheo.jpg',          'in_stock'),
(1359, 1053, 1201, '9786048456789', 'Số Đỏ',                                    240, 'Tiếng Việt', 2021, 'Bìa mềm',  40000,  75000,  20,  5, 'so_do.jpg',             'in_stock'),
(1360, 1058, 1207, '9786049567890', 'The Shining - Ngôi Nhà Ma',                600, 'Tiếng Việt', 2022, 'Bìa mềm', 120000, 199000,  15,  3, 'the_shining.jpg',       'in_stock'),
(1361, 1057, 1207, '9786047678901', 'Mật Mã Da Vinci',                          550, 'Tiếng Việt', 2018, 'Bìa mềm', 100000, 169000,  45,  8, 'mat_ma_da_vinci.jpg',   'in_stock'),
(1362, 1055, 1204, '9786045789012', 'Tuổi Trẻ Đáng Giá Bao Nhiêu',             280, 'Tiếng Việt', 2018, 'Bìa mềm',  45000,  80000, 200, 20, 'tuoi_tre_dang_gia.jpg', 'in_stock'),
(1363, 1058, 1208, '9786049890123', 'Đại Dương Đen',                            320, 'Tiếng Việt', 2023, 'Bìa mềm',  95000, 165000,  60, 10, 'dai_duong_den.jpg',     'in_stock'),
(1364, 1060, 1208, '9786046901234', 'Phép Lạ Của Sự Tỉnh Thức',                150, 'Tiếng Việt', 2020, 'Bìa mềm',  30000,  59000,  80, 15, 'phep_la_tinh_thuc.jpg', 'in_stock');

-- 11. TÁC GIẢ  (1501–1650)
INSERT INTO authors (author_id, author_name) VALUES
(1501, 'Nguyễn Nhật Ánh'),
(1502, 'Fujiko F. Fujio'),
(1503, 'Paulo Coelho'),
(1504, 'Dale Carnegie'),
(1505, 'Haruki Murakami'),
(1506, 'J.K. Rowling'),
(1507, 'Thạch Lam'),
(1508, 'Nam Cao'),
(1509, 'Vũ Trọng Phụng'),
(1510, 'Stephen King'),
(1511, 'Dan Brown'),
(1512, 'Rosie Nguyễn'),
(1513, 'Đặng Hoàng Giang'),
(1514, 'Thiền sư Thích Nhất Hạnh');

-- 12. SÁCH - TÁC GIẢ  (1651–1800)
INSERT INTO book_authors (book_author_id, book_id, author_id, display_order) VALUES
(1651, 1351, 1501, 1),
(1652, 1352, 1502, 1),
(1653, 1353, 1503, 1),
(1654, 1354, 1504, 1),
(1655, 1355, 1505, 1),
(1656, 1356, 1506, 1),
(1657, 1357, 1507, 1),
(1658, 1358, 1508, 1),
(1659, 1359, 1509, 1),
(1660, 1360, 1510, 1),
(1661, 1361, 1511, 1),
(1662, 1362, 1512, 1),
(1663, 1363, 1513, 1),
(1664, 1364, 1514, 1);

-- 13. HÓA ĐƠN  (1801–1950)
INSERT INTO invoices (invoice_id, customer_id, employee_id, total_amount, total_discount, final_amount, payment_method, status) VALUES
(1801, 751, 2, 190000, 10000, 180000, 'Tiền mặt',     'Completed'),
(1802, 752, 2,  79000,  5000,  69000, 'Chuyển khoản', 'Completed'),
(1803, 753, 2, 120000,     0, 120000, 'Tiền mặt',     'Completed'),
(1804, 754, 2, 258000, 20000, 238000, 'Thẻ',          'Completed');

-- 14. CHI TIẾT HÓA ĐƠN  (1951–2100)
INSERT INTO invoice_details (detail_id, invoice_id, book_id, quantity, unit_price, subtotal) VALUES
(1951, 1801, 1351, 2,  95000, 190000),
(1952, 1802, 1353, 1,  79000,  79000),
(1953, 1803, 1352, 4,  25000, 100000),
(1954, 1804, 1354, 3,  86000, 258000);

-- 15. DỊCH VỤ GIẢM GIÁ  (2101–2250)
INSERT INTO discount_services (service_id, service_name, discount_type, discount_value, status) VALUES
(2101, 'Giảm giá 10% cho hóa đơn trên 200k', 'Phần trăm',            10,    'active'),
(2102, 'Giảm 20k cho hóa đơn đầu tiên',       'Số tiền cố định',    20000,  'active'),
(2103, 'Giảm 15% cho sách thiếu nhi',         'Phần trăm',            15,    'active'),
(2104, 'Mua 3 tặng 1',                         'Khuyến mãi đặc biệt',  0,    'inactive');

-- 16. NHÀ CUNG CẤP  (2401–2550)
INSERT INTO suppliers (supplier_id, supplier_name, phone, status) VALUES
(2401, 'Công ty Sách Miền Nam',    '0287654321', 'active'),
(2402, 'Công ty Phát hành Fahasa', '0287777888', 'active'),
(2403, 'Công ty Sách Phương Nam',  '0283888999', 'active'),
(2404, 'Công ty Sách Thiên Long',  '0289999000', 'active');

-- 17. PHIẾU NHẬP  (2551–2700)
INSERT INTO import_receipts (receipt_id, supplier_id, employee_id, total_amount, status) VALUES
(2551, 2401, 3, 3250000, 'Completed'),
(2552, 2402, 3, 1500000, 'Completed'),
(2553, 2403, 3, 3600000, 'Completed'),
(2554, 2404, 3, 3000000, 'Completed');

-- 18. CHI TIẾT PHIẾU NHẬP  (2701–2850)
INSERT INTO import_receipt_details (detail_id, receipt_id, book_id, quantity, unit_price, subtotal) VALUES
(2701, 2551, 1351, 50,  65000, 3250000),
(2702, 2552, 1352, 100, 15000, 1500000),
(2703, 2553, 1353, 80,  45000, 3600000),
(2704, 2554, 1354, 60,  50000, 3000000);

-- 19. THAM SỐ HỆ THỐNG
INSERT INTO system_parameters (parameter_code, parameter_value, description) VALUES
('TY_LE_TICH_DIEM',            '10',  'Tích 1 điểm cho mỗi 10,000 VNĐ'),
('TY_LE_QUI_DOI_DIEM',         '100', 'Đổi 1 điểm = 100 VNĐ'),
('SO_LUONG_TOI_THIEU_CANH_BAO','10',  'Cảnh báo khi tồn kho < 10 cuốn'),
('THOI_GIAN_LUU_HOA_DON',      '365', 'Lưu hóa đơn trong 365 ngày');