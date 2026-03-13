-- =============================================
-- BOOKSTORE DATABASE - FULL SETUP SCRIPT
-- Có thể chạy trên máy bất kỳ (tự tạo DB mới)
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
) ENGINE=InnoDB;

CREATE TABLE permission_groups (
    permission_group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name          VARCHAR(100) NOT NULL,
    status              ENUM('active', 'inactive') DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=151;

CREATE TABLE accounts (
    account_id          INT AUTO_INCREMENT PRIMARY KEY,
    employee_id         INT NOT NULL,
    permission_group_id INT NOT NULL,
    username            VARCHAR(50) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    status              ENUM('active', 'locked') DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id)         REFERENCES employees(employee_id),
    FOREIGN KEY (permission_group_id) REFERENCES permission_groups(permission_group_id)
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;

CREATE TABLE invoice_services (
    invoice_service_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id         INT NOT NULL,
    service_id         INT NOT NULL,
    service_type       VARCHAR(50),
    discount_value     DECIMAL(15,2),
    description        TEXT,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (service_id) REFERENCES discount_services(service_id)
) ENGINE=InnoDB;

CREATE TABLE suppliers (
    supplier_id   INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    phone         VARCHAR(15),
    status        ENUM('active', 'inactive') DEFAULT 'active'
) ENGINE=InnoDB AUTO_INCREMENT=2701;

CREATE TABLE import_receipts (
    receipt_id   INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id  INT NOT NULL,
    employee_id  INT NOT NULL,
    receipt_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(15,2),
    status       VARCHAR(50),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
) ENGINE=InnoDB;

CREATE TABLE import_receipt_details (
    detail_id   INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id  INT NOT NULL,
    book_id     INT NOT NULL,
    quantity    INT NOT NULL,
    unit_price  DECIMAL(15,2),
    subtotal    DECIMAL(15,2),
    FOREIGN KEY (receipt_id) REFERENCES import_receipts(receipt_id),
    FOREIGN KEY (book_id)    REFERENCES books(book_id)
) ENGINE=InnoDB;

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
CREATE INDEX idx_receipt_date    ON import_receipts(receipt_date);

-- =============================================
-- PHẦN 2: DỮ LIỆU MẪU
-- =============================================

-- 1. NHÂN VIÊN
INSERT INTO employees (full_name, date_of_birth, gender, phone, address, position, salary, hire_date, status) VALUES
('Nguyễn Văn An',    '1990-05-15', 'male',   '0901234567', '123 Lê Lợi, Quận 1, TP.HCM',           'Quản lý',              15000000, '2020-01-10', 'active'),
('Trần Thị Bình',    '1995-08-20', 'female', '0912345678', '456 Trần Hưng Đạo, Quận 5, TP.HCM',    'Nhân viên bán hàng',    8000000, '2021-03-15', 'active'),
('Lê Hoàng Châu',    '1992-12-10', 'male',   '0923456789', '789 Nguyễn Huệ, Quận 1, TP.HCM',       'Thủ kho',               9000000, '2021-06-20', 'active'),
('Phạm Thị Dung',    '1988-03-25', 'female', '0934567890', '321 Võ Văn Tần, Quận 3, TP.HCM',       'Kế toán',              12000000, '2019-11-05', 'active');

-- 2. NHÓM QUYỀN
INSERT INTO permission_groups (group_name, status) VALUES
('Quản trị viên',       'active'),
('Nhân viên bán hàng',  'active'),
('Thủ kho',             'active'),
('Kế toán',             'active');

-- 3. TÀI KHOẢN
INSERT INTO accounts (employee_id, permission_group_id, username, password, status)
SELECT e.employee_id, pg.permission_group_id, 'admin',    'admin123', 'active' FROM employees e, permission_groups pg WHERE e.full_name='Nguyễn Văn An'   AND pg.group_name='Quản trị viên';
INSERT INTO accounts (employee_id, permission_group_id, username, password, status)
SELECT e.employee_id, pg.permission_group_id, 'nvbh01',   'bh123456', 'active' FROM employees e, permission_groups pg WHERE e.full_name='Trần Thị Bình'   AND pg.group_name='Nhân viên bán hàng';
INSERT INTO accounts (employee_id, permission_group_id, username, password, status)
SELECT e.employee_id, pg.permission_group_id, 'thukho01', 'tk123456', 'active' FROM employees e, permission_groups pg WHERE e.full_name='Lê Hoàng Châu'   AND pg.group_name='Thủ kho';
INSERT INTO accounts (employee_id, permission_group_id, username, password, status)
SELECT e.employee_id, pg.permission_group_id, 'ketoan01', 'kt123456', 'active' FROM employees e, permission_groups pg WHERE e.full_name='Phạm Thị Dung'   AND pg.group_name='Kế toán';

-- 4. CHỨC NĂNG (Bổ sung Cài đặt hệ thống)
INSERT INTO functions (function_name, system_function_code, function_group) VALUES
('Quản lý sách',       'BOOK',        'Quản lý kho'),
('Danh mục',           'CATEGORY',    'Quản lý kho'),
('Quản lý khách hàng', 'CUSTOMER',    'Bán hàng'),
('Quản lý nhập hàng',  'IMPORT',      'Quản lý kho'),
('Quản lý hóa đơn',    'INVOICE',     'Bán hàng'),
('Khuyến mãi',         'PROMOTION',   'Bán hàng'),
('Quản lý nhân viên',  'EMPLOYEE',    'Quản lý hệ thống'),
('Quản lý tài khoản',  'ACCOUNT',     'Quản lý hệ thống'),
('Phân quyền',         'PERMISSION',  'Quản lý hệ thống'),
('Quản lý tác giả',    'AUTHOR',      'Quản lý kho'),
('Quản lý nhà xuất bản','PUBLISHER',  'Quản lý kho'),
('Cài đặt hệ thống',   'SETTING',     'Quản lý hệ thống');

-- 5. PHÂN QUYỀN

-- Admin: toàn quyền tất cả chức năng, TRỪ Cài đặt hệ thống (chỉ Xem,Sửa)
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Thêm,Sửa,Xóa'
FROM permission_groups pg, functions f
WHERE pg.group_name = 'Quản trị viên' AND f.system_function_code != 'SETTING';

-- Admin: Riêng Cài đặt hệ thống chỉ cấp 'Xem,Sửa' để bảo vệ logic hệ thống
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Sửa'
FROM permission_groups pg, functions f
WHERE pg.group_name = 'Quản trị viên' AND f.system_function_code = 'SETTING';

-- Nhân viên bán hàng (152)
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem'          FROM permission_groups pg, functions f WHERE pg.group_name='Nhân viên bán hàng' AND f.system_function_code='BOOK';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem'          FROM permission_groups pg, functions f WHERE pg.group_name='Nhân viên bán hàng' AND f.system_function_code='CATEGORY';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Thêm,Sửa' FROM permission_groups pg, functions f WHERE pg.group_name='Nhân viên bán hàng' AND f.system_function_code='CUSTOMER';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Thêm'     FROM permission_groups pg, functions f WHERE pg.group_name='Nhân viên bán hàng' AND f.system_function_code='INVOICE';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem'          FROM permission_groups pg, functions f WHERE pg.group_name='Nhân viên bán hàng' AND f.system_function_code='PROMOTION';

-- Thủ kho (153)
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Thêm,Sửa' FROM permission_groups pg, functions f WHERE pg.group_name='Thủ kho' AND f.system_function_code='BOOK';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Thêm,Sửa' FROM permission_groups pg, functions f WHERE pg.group_name='Thủ kho' AND f.system_function_code='CATEGORY';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Thêm,Sửa' FROM permission_groups pg, functions f WHERE pg.group_name='Thủ kho' AND f.system_function_code='IMPORT';

-- Kế toán (154)
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem'          FROM permission_groups pg, functions f WHERE pg.group_name='Kế toán' AND f.system_function_code='IMPORT';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem'          FROM permission_groups pg, functions f WHERE pg.group_name='Kế toán' AND f.system_function_code='INVOICE';
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT pg.permission_group_id, f.function_id, 'Xem,Thêm,Sửa' FROM permission_groups pg, functions f WHERE pg.group_name='Kế toán' AND f.system_function_code='PROMOTION';

-- 6. KHÁCH HÀNG
INSERT INTO customers (full_name, phone, loyalty_points, registration_date) VALUES
('Võ Minh Tuấn',  '0945678901', 150, '2023-01-15'),
('Hoàng Thị Mai', '0956789012', 320, '2023-03-20'),
('Đặng Văn Nam',  '0967890123',  80, '2023-06-10'),
('Bùi Thị Lan',   '0978901234', 500, '2022-12-05');

-- 7. LỊCH SỬ QUY ĐỔI ĐIỂM
INSERT INTO point_redemption_history (customer_id, points_redeemed, value_received, redemption_type)
SELECT customer_id, 100, 10000, 'Giảm giá hóa đơn' FROM customers WHERE full_name='Hoàng Thị Mai';
INSERT INTO point_redemption_history (customer_id, points_redeemed, value_received, redemption_type)
SELECT customer_id, 200, 20000, 'Giảm giá hóa đơn' FROM customers WHERE full_name='Bùi Thị Lan';

-- 8. NHÀ XUẤT BẢN
INSERT INTO publishers (publisher_name, phone, status) VALUES
('Nhà xuất bản Trẻ',                    '0283822711', 'active'),
('Nhà xuất bản Kim Đồng',               '0283943344', 'active'),
('Nhà xuất bản Văn học',                '0283822211', 'active'),
('Nhà xuất bản Thế giới',               '0283825252', 'active'),
('Nhà xuất bản Nhã Nam',                '0283517898', 'active'),
('Nhà xuất bản Phụ Nữ',                 '0243825993', 'active'),
('Nhà xuất bản Lao Động',               '0243851538', 'active'),
('Nhà xuất bản Hội Nhà Văn',            '0243822213', 'active'),
('Nhà xuất bản Tổng hợp TP.HCM',        '0283822534', 'active'),
('Nhà xuất bản Dân Trí',                '0243762334', 'active'),
('Nhà xuất bản Hồng Đức',               '0243926002', 'active'),
('Nhà xuất bản Tri Thức',               '0243944727', 'active'),
('Nhà xuất bản Đại học Quốc gia Hà Nội','0243754773', 'active'),
('Nhà xuất bản Công Thương',            '0243934168', 'active');

-- 9. THỂ LOẠI / DANH MỤC
INSERT INTO categories (category_name, display_order, status) VALUES
('Văn học Việt Nam',           1,  'active'),
('Văn học nước ngoài',         2,  'active'),
('Sách thiếu nhi',             3,  'active'),
('Sách kỹ năng sống',          4,  'active'),
('Sách Kinh tế',               5,  'active'),
('Sách Lịch sử',               6,  'active'),
('Truyện Trinh thám - Kinh dị',7,  'active'),
('Tâm lý học',                 8,  'active'),
('Tiểu sử - Hồi ký',           9,  'active'),
('Khoa học viễn tưởng',        10, 'active'),
('Sách Giáo khoa - Tham khảo', 11, 'active'),
('Sách Học Ngoại ngữ',         12, 'active'),
('Y học - Sức khỏe',           13, 'active'),
('Truyện Tranh (Manga/Comic)',  14, 'active');

-- 10. SÁCH
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786041002345', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh',         368,'Tiếng Việt',2018,'Bìa mềm', 65000, 95000, 50,10,'hoa_vang.jpg',         'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Trẻ'                    AND c.category_name='Văn học Việt Nam';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786042134567', 'Doraemon - Nobita Và Hành Tinh Màu Tím',  196,'Tiếng Việt',2023,'Bìa mềm', 15000, 25000,100,20,'doraemon.jpg',          'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Kim Đồng'                AND c.category_name='Sách thiếu nhi';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786043245678', 'Nhà Giả Kim',                              227,'Tiếng Việt',2020,'Bìa cứng',45000, 79000, 35,10,'nha_gia_kim.jpg',       'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học'                 AND c.category_name='Văn học nước ngoài';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786044356789', 'Đắc Nhân Tâm',                             320,'Tiếng Việt',2021,'Bìa mềm', 50000, 86000,  8,10,'dac_nhan_tam.jpg',      'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Thế giới'                AND c.category_name='Sách kỹ năng sống';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786049876541', 'Rừng Na Uy',                               500,'Tiếng Việt',2021,'Bìa mềm', 90000,150000, 40, 5,'rung_na_uy.jpg',        'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Hội Nhà Văn'             AND c.category_name='Văn học nước ngoài';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786041123456', 'Harry Potter và Hòn đá Phù thủy',          350,'Tiếng Việt',2022,'Bìa mềm',110000,185000,100,10,'harry_potter_1.jpg',    'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Trẻ'                    AND c.category_name='Sách thiếu nhi';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786048234567', 'Gió Lạnh Đầu Mùa',                         180,'Tiếng Việt',2019,'Bìa mềm', 30000, 55000, 25, 5,'gio_lanh_dau_mua.jpg',  'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học'                 AND c.category_name='Văn học Việt Nam';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786048345678', 'Chí Phèo',                                  200,'Tiếng Việt',2020,'Bìa mềm', 35000, 60000, 30, 5,'chi_pheo.jpg',          'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học'                 AND c.category_name='Văn học Việt Nam';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786048456789', 'Số Đỏ',                                     240,'Tiếng Việt',2021,'Bìa mềm', 40000, 75000, 20, 5,'so_do.jpg',             'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học'                 AND c.category_name='Văn học Việt Nam';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786049567890', 'The Shining - Ngôi Nhà Ma',                 600,'Tiếng Việt',2022,'Bìa mềm',120000,199000, 15, 3,'the_shining.jpg',       'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Hội Nhà Văn'             AND c.category_name='Truyện Trinh thám - Kinh dị';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786047678901', 'Mật Mã Da Vinci',                           550,'Tiếng Việt',2018,'Bìa mềm',100000,169000, 45, 8,'mat_ma_da_vinci.jpg',   'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Lao Động'                AND c.category_name='Truyện Trinh thám - Kinh dị';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786045789012', 'Tuổi Trẻ Đáng Giá Bao Nhiêu',              280,'Tiếng Việt',2018,'Bìa mềm', 45000, 80000,200,20,'tuoi_tre_dang_gia.jpg', 'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Nhã Nam'                 AND c.category_name='Sách kỹ năng sống';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786049890123', 'Đại Dương Đen',                             320,'Tiếng Việt',2023,'Bìa mềm', 95000,165000, 60,10,'dai_duong_den.jpg',     'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Hội Nhà Văn'             AND c.category_name='Tâm lý học';
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786046901234', 'Phép Lạ Của Sự Tỉnh Thức',                 150,'Tiếng Việt',2020,'Bìa mềm', 30000, 59000, 80,15,'phep_la_tinh_thuc.jpg', 'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Dân Trí'                  AND c.category_name='Tâm lý học';

-- 11. TÁC GIẢ
INSERT INTO authors (author_name) VALUES
('Nguyễn Nhật Ánh'),
('Fujiko F. Fujio'),
('Paulo Coelho'),
('Dale Carnegie'),
('Haruki Murakami'),
('J.K. Rowling'),
('Thạch Lam'),
('Nam Cao'),
('Vũ Trọng Phụng'),
('Stephen King'),
('Dan Brown'),
('Rosie Nguyễn'),
('Đặng Hoàng Giang'),
('Thiền sư Thích Nhất Hạnh');

-- 12. SÁCH - TÁC GIẢ
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Tôi Thấy Hoa Vàng Trên Cỏ Xanh'        AND a.author_name='Nguyễn Nhật Ánh';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Doraemon - Nobita Và Hành Tinh Màu Tím' AND a.author_name='Fujiko F. Fujio';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Nhà Giả Kim'                            AND a.author_name='Paulo Coelho';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Đắc Nhân Tâm'                           AND a.author_name='Dale Carnegie';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Rừng Na Uy'                             AND a.author_name='Haruki Murakami';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Harry Potter và Hòn đá Phù thủy'        AND a.author_name='J.K. Rowling';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Gió Lạnh Đầu Mùa'                       AND a.author_name='Thạch Lam';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Chí Phèo'                               AND a.author_name='Nam Cao';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Số Đỏ'                                  AND a.author_name='Vũ Trọng Phụng';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='The Shining - Ngôi Nhà Ma'              AND a.author_name='Stephen King';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Mật Mã Da Vinci'                        AND a.author_name='Dan Brown';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Tuổi Trẻ Đáng Giá Bao Nhiêu'           AND a.author_name='Rosie Nguyễn';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Đại Dương Đen'                          AND a.author_name='Đặng Hoàng Giang';
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Phép Lạ Của Sự Tỉnh Thức'              AND a.author_name='Thiền sư Thích Nhất Hạnh';

-- 13. HÓA ĐƠN
INSERT INTO invoices (customer_id, employee_id, total_amount, total_discount, final_amount, payment_method, status)
SELECT c.customer_id, e.employee_id, 190000, 10000, 180000, 'Tiền mặt',    'Completed' FROM customers c, employees e WHERE c.full_name='Võ Minh Tuấn'  AND e.full_name='Trần Thị Bình';
INSERT INTO invoices (customer_id, employee_id, total_amount, total_discount, final_amount, payment_method, status)
SELECT c.customer_id, e.employee_id,  79000,  5000,  69000, 'Chuyển khoản','Completed' FROM customers c, employees e WHERE c.full_name='Hoàng Thị Mai' AND e.full_name='Trần Thị Bình';
INSERT INTO invoices (customer_id, employee_id, total_amount, total_discount, final_amount, payment_method, status)
SELECT c.customer_id, e.employee_id, 120000,     0, 120000, 'Tiền mặt',    'Completed' FROM customers c, employees e WHERE c.full_name='Đặng Văn Nam'  AND e.full_name='Trần Thị Bình';
INSERT INTO invoices (customer_id, employee_id, total_amount, total_discount, final_amount, payment_method, status)
SELECT c.customer_id, e.employee_id, 258000, 20000, 238000, 'Thẻ',         'Completed' FROM customers c, employees e WHERE c.full_name='Bùi Thị Lan'   AND e.full_name='Trần Thị Bình';

-- 14. CHI TIẾT HÓA ĐƠN
INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, subtotal)
SELECT i.invoice_id, b.book_id, 2,  95000, 190000 FROM invoices i, books b, customers c WHERE i.customer_id=c.customer_id AND c.full_name='Võ Minh Tuấn'  AND b.book_title='Tôi Thấy Hoa Vàng Trên Cỏ Xanh';
INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, subtotal)
SELECT i.invoice_id, b.book_id, 1,  79000,  79000 FROM invoices i, books b, customers c WHERE i.customer_id=c.customer_id AND c.full_name='Hoàng Thị Mai' AND b.book_title='Nhà Giả Kim';
INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, subtotal)
SELECT i.invoice_id, b.book_id, 4,  25000, 100000 FROM invoices i, books b, customers c WHERE i.customer_id=c.customer_id AND c.full_name='Đặng Văn Nam'  AND b.book_title='Doraemon - Nobita Và Hành Tinh Màu Tím';
INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, subtotal)
SELECT i.invoice_id, b.book_id, 3,  86000, 258000 FROM invoices i, books b, customers c WHERE i.customer_id=c.customer_id AND c.full_name='Bùi Thị Lan'   AND b.book_title='Đắc Nhân Tâm';

-- 15. DỊCH VỤ GIẢM GIÁ / KHUYẾN MÃI
INSERT INTO discount_services (service_name, discount_type, discount_value, status) VALUES
('Giảm giá 10% cho hóa đơn trên 200k', 'Phần trăm',         10,    'active'),
('Giảm 20k cho hóa đơn đầu tiên',       'Số tiền cố định',   20000, 'active'),
('Giảm 15% cho sách thiếu nhi',         'Phần trăm',         15,    'active'),
('Mua 3 tặng 1',                         'Khuyến mãi đặc biệt', 0,  'inactive');

-- 16. NHÀ CUNG CẤP
INSERT INTO suppliers (supplier_name, phone, status) VALUES
('Công ty Sách Miền Nam',    '0287654321', 'active'),
('Công ty Phát hành Fahasa', '0287777888', 'active'),
('Công ty Sách Phương Nam',  '0283888999', 'active'),
('Công ty Sách Thiên Long',  '0289999000', 'active');

-- 17. PHIẾU NHẬP
INSERT INTO import_receipts (supplier_id, employee_id, total_amount, status)
SELECT s.supplier_id, e.employee_id, 3250000, 'Completed' FROM suppliers s, employees e WHERE s.supplier_name='Công ty Sách Miền Nam'    AND e.full_name='Lê Hoàng Châu';
INSERT INTO import_receipts (supplier_id, employee_id, total_amount, status)
SELECT s.supplier_id, e.employee_id, 1500000, 'Completed' FROM suppliers s, employees e WHERE s.supplier_name='Công ty Phát hành Fahasa' AND e.full_name='Lê Hoàng Châu';
INSERT INTO import_receipts (supplier_id, employee_id, total_amount, status)
SELECT s.supplier_id, e.employee_id, 3600000, 'Completed' FROM suppliers s, employees e WHERE s.supplier_name='Công ty Sách Phương Nam'  AND e.full_name='Lê Hoàng Châu';
INSERT INTO import_receipts (supplier_id, employee_id, total_amount, status)
SELECT s.supplier_id, e.employee_id, 3000000, 'Completed' FROM suppliers s, employees e WHERE s.supplier_name='Công ty Sách Thiên Long'  AND e.full_name='Lê Hoàng Châu';

-- 18. CHI TIẾT PHIẾU NHẬP
INSERT INTO import_receipt_details (receipt_id, book_id, quantity, unit_price, subtotal)
SELECT r.receipt_id, b.book_id, 50,  65000, 3250000 FROM import_receipts r, suppliers s, books b WHERE r.supplier_id=s.supplier_id AND s.supplier_name='Công ty Sách Miền Nam'    AND b.book_title='Tôi Thấy Hoa Vàng Trên Cỏ Xanh';
INSERT INTO import_receipt_details (receipt_id, book_id, quantity, unit_price, subtotal)
SELECT r.receipt_id, b.book_id,100,  15000, 1500000 FROM import_receipts r, suppliers s, books b WHERE r.supplier_id=s.supplier_id AND s.supplier_name='Công ty Phát hành Fahasa' AND b.book_title='Doraemon - Nobita Và Hành Tinh Màu Tím';
INSERT INTO import_receipt_details (receipt_id, book_id, quantity, unit_price, subtotal)
SELECT r.receipt_id, b.book_id, 80,  45000, 3600000 FROM import_receipts r, suppliers s, books b WHERE r.supplier_id=s.supplier_id AND s.supplier_name='Công ty Sách Phương Nam'  AND b.book_title='Nhà Giả Kim';
INSERT INTO import_receipt_details (receipt_id, book_id, quantity, unit_price, subtotal)
SELECT r.receipt_id, b.book_id, 60,  50000, 3000000 FROM import_receipts r, suppliers s, books b WHERE r.supplier_id=s.supplier_id AND s.supplier_name='Công ty Sách Thiên Long'  AND b.book_title='Đắc Nhân Tâm';

-- 19. THAM SỐ HỆ THỐNG
INSERT INTO system_parameters (parameter_code, parameter_value, description) VALUES
('TY_LE_TICH_DIEM',          '10000',  'Tích 1 điểm cho mỗi 10,000 VNĐ'),
('TY_LE_QUI_DOI_DIEM',       '100', 'Đổi 1 điểm = 100 VNĐ'),
('SO_LUONG_TOI_THIEU_CANH_BAO','10','Cảnh báo khi tồn kho < 10 cuốn');