-- =============================================
-- BOOKSTORE DATABASE - FULL SETUP SCRIPT
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
-- PHẦN 2: DỮ LIỆU MẪU ĐÃ GỘP (20 RECORD/BẢNG)
-- =============================================

-- 1. NHÂN VIÊN (20)
INSERT INTO employees (full_name, date_of_birth, gender, phone, address, position, salary, hire_date, status) VALUES
('Nguyễn Văn An',    '1990-05-15', 'male',   '0901234567', '123 Lê Lợi, Quận 1, TP.HCM',           'Quản lý',              15000000, '2020-01-10', 'active'),
('Trần Thị Bình',    '1995-08-20', 'female', '0912345678', '456 Trần Hưng Đạo, Quận 5, TP.HCM',    'Nhân viên bán hàng',    8000000, '2021-03-15', 'active'),
('Lê Hoàng Châu',    '1992-12-10', 'male',   '0923456789', '789 Nguyễn Huệ, Quận 1, TP.HCM',       'Thủ kho',               9000000, '2021-06-20', 'active'),
('Phạm Thị Dung',    '1988-03-25', 'female', '0934567890', '321 Võ Văn Tần, Quận 3, TP.HCM',       'Kế toán',              12000000, '2019-11-05', 'active'),
('Nguyễn Thị Hoa',    '1993-07-12', 'female', '0911111111', '12 Đinh Tiên Hoàng, Bình Thạnh, TP.HCM',      'Nhân viên bán hàng',  8000000, '2022-02-01', 'active'),
('Trần Văn Kiên',     '1991-04-18', 'male',   '0922222222', '45 Hai Bà Trưng, Quận 3, TP.HCM',             'Nhân viên bán hàng',  8000000, '2022-05-10', 'active'),
('Lê Thị Ngọc',       '1996-09-30', 'female', '0933333333', '78 Lý Thường Kiệt, Quận 10, TP.HCM',          'Nhân viên bán hàng',  8000000, '2023-01-15', 'active'),
('Phạm Văn Đức',      '1989-11-05', 'male',   '0944444444', '90 Nguyễn Đình Chiểu, Quận 3, TP.HCM',        'Thủ kho',             9000000, '2021-08-20', 'active'),
('Hoàng Thị Thảo',    '1994-02-14', 'female', '0955555555', '34 Cách Mạng Tháng 8, Tân Bình, TP.HCM',      'Nhân viên bán hàng',  8000000, '2023-04-01', 'active'),
('Vũ Văn Hùng',       '1987-06-22', 'male',   '0966666666', '56 Pasteur, Quận 3, TP.HCM',                  'Kế toán',            12000000, '2020-09-01', 'active'),
('Đỗ Thị Lan',        '1997-01-08', 'female', '0977777777', '23 Bùi Viện, Quận 1, TP.HCM',                 'Nhân viên bán hàng',  8000000, '2023-07-15', 'active'),
('Bùi Văn Tâm',       '1992-10-25', 'male',   '0988888888', '67 Nguyễn Trãi, Quận 5, TP.HCM',              'Thủ kho',             9000000, '2022-11-01', 'active'),
('Ngô Thị Hương',     '1995-03-17', 'female', '0999999999', '89 Lê Văn Sỹ, Phú Nhuận, TP.HCM',             'Nhân viên bán hàng',  8000000, '2024-01-10', 'active'),
('Đinh Văn Phong',    '1990-08-03', 'male',   '0900000001', '11 Trường Chinh, Tân Bình, TP.HCM',            'Quản lý',            14000000, '2020-03-01', 'active'),
('Lý Thị Bích',       '1993-12-20', 'female', '0900000002', '33 Hoàng Văn Thụ, Phú Nhuận, TP.HCM',         'Nhân viên bán hàng',  8000000, '2024-03-01', 'active'),
('Cao Văn Minh',      '1988-05-09', 'male',   '0900000003', '55 Điện Biên Phủ, Bình Thạnh, TP.HCM',        'Kế toán',            12000000, '2019-06-15', 'active'),
('Tạ Thị Thu',        '1996-07-27', 'female', '0900000004', '77 Ngô Gia Tự, Quận 10, TP.HCM',              'Nhân viên bán hàng',  7500000, '2024-06-01', 'active'),
('Phan Văn Long',     '1991-11-14', 'male',   '0900000005', '99 Sư Vạn Hạnh, Quận 10, TP.HCM',             'Thủ kho',             9000000, '2023-10-01', 'active'),
('Trương Thị Yến',    '1994-04-06', 'female', '0900000006', '22 Lạc Long Quân, Quận 11, TP.HCM',            'Nhân viên bán hàng',  8000000, '2024-09-01', 'active'),
('Mai Văn Sơn',       '1985-02-28', 'male',   '0900000007', '44 Tô Hiến Thành, Quận 10, TP.HCM',           'Quản lý',            14500000, '2018-11-01', 'active');

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

-- 4. CHỨC NĂNG
-- Chỉ định rõ function_id để đảm bảo khớp với mã phân quyền trong code
-- 460 (AUTHOR) và 461 (PUBLISHER) đã được gộp vào 451 (BOOK)
-- 452 (CATEGORY), 460 (AUTHOR), 461 (PUBLISHER) đã gộp vào BOOK(451), không insert riêng
-- AUTO_INCREMENT=451 → ID tăng tuần tự:
-- 451=BOOK, 452=CUSTOMER, 453=IMPORT, 454=INVOICE, 455=PROMOTION
-- 456=EMPLOYEE, 457=ACCOUNT, 458=PERMISSION, 459=SETTING, 460=STATISTIC
INSERT INTO functions (function_name, system_function_code, function_group) VALUES
('Quản lý sách',       'BOOK',        'Quản lý kho'),
('Quản lý khách hàng', 'CUSTOMER',    'Bán hàng'),
('Quản lý nhập hàng',  'IMPORT',      'Quản lý kho'),
('Quản lý hóa đơn',    'INVOICE',     'Bán hàng'),
('Khuyến mãi',         'PROMOTION',   'Bán hàng'),
('Quản lý nhân viên',  'EMPLOYEE',    'Quản lý hệ thống'),
('Quản lý tài khoản',  'ACCOUNT',     'Quản lý hệ thống'),
('Phân quyền',         'PERMISSION',  'Quản lý hệ thống'),
('Cài đặt hệ thống',   'SETTING',     'Quản lý hệ thống'),
('Thống kê báo cáo',   'STATISTIC',   'Báo cáo');

-- 5. PHÂN QUYỀN
-- 451=BOOK, 452=CUSTOMER, 453=IMPORT, 454=INVOICE, 455=PROMOTION
-- 456=EMPLOYEE, 457=ACCOUNT, 458=PERMISSION, 459=SETTING, 460=STATISTIC

-- 5. PHÂN QUYỀN
-- Quản trị viên: Tự động lấy tất cả các chức năng trong bảng functions và gán full quyền
INSERT INTO permission_details (permission_group_id, function_id, actions)
SELECT 
    (SELECT permission_group_id FROM permission_groups WHERE group_name = 'Quản trị viên'),
    function_id, 
    'Xem,Thêm,Sửa,Xóa'
FROM functions;

-- Nhân viên bán hàng
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 451, 'Xem'          FROM permission_groups WHERE group_name='Nhân viên bán hàng'; -- BOOK
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 452, 'Xem,Thêm,Sửa' FROM permission_groups WHERE group_name='Nhân viên bán hàng'; -- CUSTOMER
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 454, 'Xem,Thêm'     FROM permission_groups WHERE group_name='Nhân viên bán hàng'; -- INVOICE
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 455, 'Xem'          FROM permission_groups WHERE group_name='Nhân viên bán hàng'; -- PROMOTION

-- Thủ kho
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 451, 'Xem,Thêm,Sửa' FROM permission_groups WHERE group_name='Thủ kho'; -- BOOK
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 453, 'Xem,Thêm,Sửa' FROM permission_groups WHERE group_name='Thủ kho'; -- IMPORT

-- Kế toán
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 453, 'Xem'          FROM permission_groups WHERE group_name='Kế toán'; -- IMPORT
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 454, 'Xem'          FROM permission_groups WHERE group_name='Kế toán'; -- INVOICE
INSERT INTO permission_details (permission_group_id, function_id, actions) SELECT permission_group_id, 455, 'Xem,Thêm,Sửa' FROM permission_groups WHERE group_name='Kế toán'; -- PROMOTION

-- 6. KHÁCH HÀNG (20)
INSERT INTO customers (full_name, phone, loyalty_points, registration_date) VALUES
('Võ Minh Tuấn',  '0945678901', 150, '2023-01-15'),
('Hoàng Thị Mai', '0956789012', 320, '2023-03-20'),
('Đặng Văn Nam',  '0967890123',  80, '2023-06-10'),
('Bùi Thị Lan',   '0978901234', 500, '2022-12-05'),
('Nguyễn Minh Khoa',   '0911222333',  250, '2023-08-20'),
('Trần Thị Phương',    '0922333444',  180, '2023-09-15'),
('Lê Văn Thịnh',       '0933444555',   90, '2023-10-05'),
('Phạm Thị Thúy',      '0944555666',  420, '2023-11-12'),
('Hoàng Văn Dũng',     '0955666777',   60, '2024-01-08'),
('Vũ Thị Linh',        '0966777888',  310, '2024-02-14'),
('Đặng Minh Quân',     '0977888999',  130, '2024-03-20'),
('Bùi Thị Hằng',       '0988999000',  550, '2024-04-10'),
('Ngô Văn Tùng',       '0909111222',   75, '2024-05-25'),
('Đinh Thị Nga',       '0908222333',  200, '2024-06-18'),
('Lý Văn Bảo',         '0907333444',  360, '2024-07-30'),
('Cao Thị Diễm',       '0906444555',  120, '2024-08-22'),
('Tạ Văn Khải',        '0905555666',   40, '2024-09-14'),
('Phan Thị Bạch',      '0904666777',  280, '2024-10-05'),
('Trương Văn Hiếu',    '0903777888',  160, '2024-11-17'),
('Mai Thị Thơ',        '0902888999',  480, '2024-12-01');

-- 7. LỊCH SỬ QUY ĐỔI ĐIỂM (20)
INSERT INTO point_redemption_history (customer_id, points_redeemed, value_received, redemption_date, redemption_type)
SELECT customer_id, 100, 10000, '2023-08-15 10:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Hoàng Thị Mai' UNION ALL
SELECT customer_id, 200, 20000, '2023-10-12 11:30:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Bùi Thị Lan' UNION ALL
SELECT customer_id, 150, 15000, '2024-01-15 10:30:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Nguyễn Minh Khoa' UNION ALL
SELECT customer_id, 100, 10000, '2024-02-20 14:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Trần Thị Phương' UNION ALL
SELECT customer_id, 200, 20000, '2024-03-10 09:15:00', 'Đổi quà tặng'     FROM customers WHERE full_name='Phạm Thị Thúy' UNION ALL
SELECT customer_id,  50,  5000, '2024-04-05 11:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Võ Minh Tuấn' UNION ALL
SELECT customer_id, 300, 30000, '2024-04-25 16:30:00', 'Đổi quà tặng'     FROM customers WHERE full_name='Bùi Thị Hằng' UNION ALL
SELECT customer_id, 100, 10000, '2024-05-15 13:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Vũ Thị Linh' UNION ALL
SELECT customer_id, 250, 25000, '2024-06-08 10:45:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Lý Văn Bảo' UNION ALL
SELECT customer_id,  75,  7500, '2024-07-20 15:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Đinh Thị Nga' UNION ALL
SELECT customer_id, 200, 20000, '2024-08-12 11:30:00', 'Đổi quà tặng'     FROM customers WHERE full_name='Hoàng Thị Mai' UNION ALL
SELECT customer_id, 150, 15000, '2024-09-05 14:15:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Phan Thị Bạch' UNION ALL
SELECT customer_id, 100, 10000, '2024-10-18 09:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Trương Văn Hiếu' UNION ALL
SELECT customer_id, 400, 40000, '2024-11-25 16:00:00', 'Đổi quà tặng'     FROM customers WHERE full_name='Mai Thị Thơ' UNION ALL
SELECT customer_id,  50,  5000, '2024-12-10 12:30:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Hoàng Văn Dũng' UNION ALL
SELECT customer_id, 120, 12000, '2025-01-08 10:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Đặng Văn Nam' UNION ALL
SELECT customer_id, 200, 20000, '2025-02-14 13:45:00', 'Đổi quà tặng'     FROM customers WHERE full_name='Nguyễn Minh Khoa' UNION ALL
SELECT customer_id,  80,  8000, '2025-03-05 11:00:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Cao Thị Diễm' UNION ALL
SELECT customer_id, 350, 35000, '2025-04-20 14:30:00', 'Đổi quà tặng'     FROM customers WHERE full_name='Bùi Thị Lan' UNION ALL
SELECT customer_id, 150, 15000, '2025-05-15 10:15:00', 'Giảm giá hóa đơn' FROM customers WHERE full_name='Vũ Thị Linh';

-- 8. NHÀ XUẤT BẢN (20)
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
('Nhà xuất bản Công Thương',            '0243934168', 'active'),
('Nhà xuất bản Y học',                  '0243825112', 'active'),
('Nhà xuất bản Khoa học Xã hội',        '0243944311', 'active'),
('Nhà xuất bản Chính trị Quốc gia',     '0243845217', 'active'),
('Nhà xuất bản Giáo dục Việt Nam',      '0243869765', 'active'),
('Nhà xuất bản Thông tin Truyền thông', '0243762543', 'active'),
('Nhà xuất bản Mỹ Thuật',               '0243812456', 'active');

-- 9. THỂ LOẠI / DANH MỤC (20)
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
('Truyện Tranh (Manga/Comic)', 14, 'active'),
('Sách Công nghệ - IT',        15, 'active'),
('Sách Nấu ăn',                16, 'active'),
('Sách Du lịch',               17, 'active'),
('Sách Nghệ thuật',            18, 'active'),
('Sách Tôn giáo - Tâm linh',   19, 'active'),
('Sách Thể thao',              20, 'active');

-- 10. SÁCH (20)
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status)
SELECT p.publisher_id, c.category_id, '9786041002345', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh',         368,'Tiếng Việt',2018,'Bìa mềm', 65000, 95000, 50,10,'hoa_vang.jpg',         'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Trẻ' AND c.category_name='Văn học Việt Nam' UNION ALL
SELECT p.publisher_id, c.category_id, '9786042134567', 'Doraemon - Nobita Và Hành Tinh Màu Tím',  196,'Tiếng Việt',2023,'Bìa mềm', 15000, 25000,100,20,'doraemon.jpg',          'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Kim Đồng' AND c.category_name='Sách thiếu nhi' UNION ALL
SELECT p.publisher_id, c.category_id, '9786043245678', 'Nhà Giả Kim',                              227,'Tiếng Việt',2020,'Bìa cứng',45000, 79000, 35,10,'nha_gia_kim.jpg',       'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học' AND c.category_name='Văn học nước ngoài' UNION ALL
SELECT p.publisher_id, c.category_id, '9786044356789', 'Đắc Nhân Tâm',                             320,'Tiếng Việt',2021,'Bìa mềm', 50000, 86000,  8,10,'dac_nhan_tam.jpg',      'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Thế giới' AND c.category_name='Sách kỹ năng sống' UNION ALL
SELECT p.publisher_id, c.category_id, '9786049876541', 'Rừng Na Uy',                               500,'Tiếng Việt',2021,'Bìa mềm', 90000,150000, 40, 5,'rung_na_uy.jpg',        'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Hội Nhà Văn' AND c.category_name='Văn học nước ngoài' UNION ALL
SELECT p.publisher_id, c.category_id, '9786041123456', 'Harry Potter và Hòn đá Phù thủy',          350,'Tiếng Việt',2022,'Bìa mềm',110000,185000,100,10,'harry_potter_1.jpg',    'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Trẻ' AND c.category_name='Sách thiếu nhi' UNION ALL
SELECT p.publisher_id, c.category_id, '9786048234567', 'Gió Lạnh Đầu Mùa',                         180,'Tiếng Việt',2019,'Bìa mềm', 30000, 55000, 25, 5,'gio_lanh_dau_mua.jpg',  'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học' AND c.category_name='Văn học Việt Nam' UNION ALL
SELECT p.publisher_id, c.category_id, '9786048345678', 'Chí Phèo',                                  200,'Tiếng Việt',2020,'Bìa mềm', 35000, 60000, 30, 5,'chi_pheo.jpg',          'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học' AND c.category_name='Văn học Việt Nam' UNION ALL
SELECT p.publisher_id, c.category_id, '9786048456789', 'Số Đỏ',                                     240,'Tiếng Việt',2021,'Bìa mềm', 40000, 75000, 20, 5,'so_do.jpg',             'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học' AND c.category_name='Văn học Việt Nam' UNION ALL
SELECT p.publisher_id, c.category_id, '9786049567890', 'The Shining - Ngôi Nhà Ma',                 600,'Tiếng Việt',2022,'Bìa mềm',120000,199000, 15, 3,'the_shining.jpg',       'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Hội Nhà Văn' AND c.category_name='Truyện Trinh thám - Kinh dị' UNION ALL
SELECT p.publisher_id, c.category_id, '9786047678901', 'Mật Mã Da Vinci',                           550,'Tiếng Việt',2018,'Bìa mềm',100000,169000, 45, 8,'mat_ma_da_vinci.jpg',   'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Lao Động' AND c.category_name='Truyện Trinh thám - Kinh dị' UNION ALL
SELECT p.publisher_id, c.category_id, '9786045789012', 'Tuổi Trẻ Đáng Giá Bao Nhiêu',              280,'Tiếng Việt',2018,'Bìa mềm', 45000, 80000,200,20,'tuoi_tre_dang_gia.jpg', 'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Nhã Nam' AND c.category_name='Sách kỹ năng sống' UNION ALL
SELECT p.publisher_id, c.category_id, '9786049890123', 'Đại Dương Đen',                             320,'Tiếng Việt',2023,'Bìa mềm', 95000,165000, 60,10,'dai_duong_den.jpg',     'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Hội Nhà Văn' AND c.category_name='Tâm lý học' UNION ALL
SELECT p.publisher_id, c.category_id, '9786046901234', 'Phép Lạ Của Sự Tỉnh Thức',                 150,'Tiếng Việt',2020,'Bìa mềm', 30000, 59000, 80,15,'phep_la_tinh_thuc.jpg', 'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Dân Trí'  AND c.category_name='Tâm lý học' UNION ALL
SELECT p.publisher_id, c.category_id, '9786041234001', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự',320,'Tiếng Việt',2023,'Bìa mềm',  85000, 149000, 80,10,'atomic_habits.jpg',   'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Trẻ' AND c.category_name='Sách kỹ năng sống' UNION ALL
SELECT p.publisher_id, c.category_id, '9786041234002', 'Sapiens - Lược Sử Loài Người',             560,'Tiếng Việt',2022,'Bìa mềm', 130000, 219000, 55, 8,'sapiens.jpg',         'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Thế giới' AND c.category_name='Sách Lịch sử' UNION ALL
SELECT p.publisher_id, c.category_id, '9786041234003', 'Dám Nghĩ Lớn',                             288,'Tiếng Việt',2021,'Bìa mềm',  60000, 105000, 70,10,'dam_nghi_lon.jpg',    'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Lao Động' AND c.category_name='Sách kỹ năng sống' UNION ALL
SELECT p.publisher_id, c.category_id, '9786041234004', 'Kẻ Trộm Sách',                             440,'Tiếng Việt',2020,'Bìa mềm',  95000, 159000, 45, 8,'ke_trom_sach.jpg',    'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Văn học' AND c.category_name='Văn học nước ngoài' UNION ALL
SELECT p.publisher_id, c.category_id, '9786041234005', 'Tư Duy Nhanh Và Chậm',                     580,'Tiếng Việt',2023,'Bìa mềm', 120000, 199000, 38, 5,'tu_duy_nhanh_cham.jpg','in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Nhã Nam' AND c.category_name='Tâm lý học' UNION ALL
SELECT p.publisher_id, c.category_id, '9786041234006', 'Conan - Thám Tử Lừng Danh Tập 1',          192,'Tiếng Việt',2022,'Bìa mềm',  18000,  29000,150,20,'conan_01.jpg',        'in_stock' FROM publishers p, categories c WHERE p.publisher_name='Nhà xuất bản Kim Đồng' AND c.category_name='Truyện Tranh (Manga/Comic)';

-- 11. TÁC GIẢ (20)
INSERT INTO authors (author_name) VALUES
('Nguyễn Nhật Ánh'), ('Fujiko F. Fujio'), ('Paulo Coelho'), ('Dale Carnegie'), ('Haruki Murakami'),
('J.K. Rowling'), ('Thạch Lam'), ('Nam Cao'), ('Vũ Trọng Phụng'), ('Stephen King'),
('Dan Brown'), ('Rosie Nguyễn'), ('Đặng Hoàng Giang'), ('Thiền sư Thích Nhất Hạnh'),
('James Clear'), ('Yuval Noah Harari'), ('David J. Schwartz'), ('Markus Zusak'), ('Daniel Kahneman'), ('Gosho Aoyama');

-- 12. SÁCH - TÁC GIẢ (20)
INSERT INTO book_authors (book_id, author_id, display_order)
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Tôi Thấy Hoa Vàng Trên Cỏ Xanh' AND a.author_name='Nguyễn Nhật Ánh' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Doraemon - Nobita Và Hành Tinh Màu Tím' AND a.author_name='Fujiko F. Fujio' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Nhà Giả Kim' AND a.author_name='Paulo Coelho' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Đắc Nhân Tâm' AND a.author_name='Dale Carnegie' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Rừng Na Uy' AND a.author_name='Haruki Murakami' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Harry Potter và Hòn đá Phù thủy' AND a.author_name='J.K. Rowling' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Gió Lạnh Đầu Mùa' AND a.author_name='Thạch Lam' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Chí Phèo' AND a.author_name='Nam Cao' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Số Đỏ' AND a.author_name='Vũ Trọng Phụng' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='The Shining - Ngôi Nhà Ma' AND a.author_name='Stephen King' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Mật Mã Da Vinci' AND a.author_name='Dan Brown' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Tuổi Trẻ Đáng Giá Bao Nhiêu' AND a.author_name='Rosie Nguyễn' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Đại Dương Đen' AND a.author_name='Đặng Hoàng Giang' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Phép Lạ Của Sự Tỉnh Thức' AND a.author_name='Thiền sư Thích Nhất Hạnh' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự' AND a.author_name='James Clear' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Sapiens - Lược Sử Loài Người' AND a.author_name='Yuval Noah Harari' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Dám Nghĩ Lớn' AND a.author_name='David J. Schwartz' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Kẻ Trộm Sách' AND a.author_name='Markus Zusak' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Tư Duy Nhanh Và Chậm' AND a.author_name='Daniel Kahneman' UNION ALL
SELECT b.book_id, a.author_id, 1 FROM books b, authors a WHERE b.book_title='Conan - Thám Tử Lừng Danh Tập 1' AND a.author_name='Gosho Aoyama';

-- 13. DỊCH VỤ GIẢM GIÁ / KHUYẾN MÃI (20)
INSERT INTO discount_services (service_name, discount_type, discount_value, status) VALUES
('Giảm giá 10% cho hóa đơn trên 200k', 'Phần trăm',         10,    'active'),
('Giảm 20k cho hóa đơn đầu tiên',       'Số tiền cố định',   20000, 'active'),
('Giảm 15% cho sách thiếu nhi',         'Phần trăm',         15,    'active'),
('Mua 3 tặng 1',                         'Khuyến mãi đặc biệt', 0,  'inactive'),
('Giảm 5% cho thành viên mới',         'Phần trăm',            5,   'active'),
('Giảm 50k cho hóa đơn trên 500k',     'Số tiền cố định',      50000, 'active'),
('Giảm 20% ngày sinh nhật khách',      'Phần trăm',            20,  'active'),
('Giảm 10k khi thanh toán online',     'Số tiền cố định',      10000, 'active'),
('Combo 2 sách giảm 15%',              'Phần trăm',            15,  'active'),
('Flash sale cuối tuần giảm 25%',      'Phần trăm',            25,  'inactive'),
('Giảm 30k cho đơn từ 300k',           'Số tiền cố định',      30000, 'active'),
('Khách VIP giảm 12%',                 'Phần trăm',            12,  'active'),
('Tặng bookmark khi mua sách VH',      'Khuyến mãi đặc biệt',   0,  'active'),
('Giảm 8% cho sách học ngoại ngữ',     'Phần trăm',             8,  'active'),
('Giảm 100k cho đơn từ 1 triệu',       'Số tiền cố định',     100000, 'active'),
('Mua 2 tặng 1 sách thiếu nhi',        'Khuyến mãi đặc biệt',   0,  'inactive'),
('Giảm 5k khi đánh giá sách',          'Số tiền cố định',       5000, 'active'),
('Giảm 10% ngày khai trương',          'Phần trăm',             10, 'inactive'),
('Ưu đãi học sinh sinh viên 15%',      'Phần trăm',             15, 'active'),
('Giảm 20k cho lần mua thứ 5',         'Số tiền cố định',      20000, 'active');

-- 14. NHÀ CUNG CẤP (20)
INSERT INTO suppliers (supplier_name, phone, status) VALUES
('Công ty Sách Miền Nam',    '0287654321', 'active'),
('Công ty Phát hành Fahasa', '0287777888', 'active'),
('Công ty Sách Phương Nam',  '0283888999', 'active'),
('Công ty Sách Thiên Long',  '0289999000', 'active'),
('Công ty Sách Cá Chép',          '0281234501', 'active'),
('Công ty Sách Alpha',            '0281234502', 'active'),
('Công ty TNHH Sách Việt',        '0281234503', 'active'),
('Công ty Sách Đinh Tị',          '0281234504', 'active'),
('Công ty Sách Nhân Văn',         '0281234505', 'active'),
('Công ty Sách First News',       '0281234506', 'active'),
('Công ty Phát hành Tổng hợp',    '0281234507', 'active'),
('Công ty Sách Trí Tuệ',          '0281234508', 'active'),
('Công ty Sách Huy Hoàng',        '0281234509', 'active'),
('Công ty Sách Đại Nam',          '0281234510', 'active'),
('Công ty Sách Liên Việt',        '0281234511', 'active'),
('Công ty Sách Bảo Châu',         '0281234512', 'active'),
('Công ty Sách Minh Long',        '0281234513', 'active'),
('Công ty Sách Vạn Hoa',          '0281234514', 'active'),
('Công ty Sách Hoàng Long',       '0281234515', 'active'),
('Công ty Sách Ngọc Hà',          '0281234516', 'active');

-- 15. THAM SỐ HỆ THỐNG
INSERT INTO system_parameters (parameter_code, parameter_value, description) VALUES
('TY_LE_TICH_DIEM',          '10000',  'Tích 1 điểm cho mỗi 10,000 VNĐ'),
('TY_LE_QUI_DOI_DIEM',       '100', 'Đổi 1 điểm = 100 VNĐ'),
('SO_LUONG_TOI_THIEU_CANH_BAO','10','Cảnh báo khi tồn kho < 10 cuốn');


-- ============================================================
-- PHẦN 3: DỮ LIỆU ĐÃ TÍNH TOÁN (PHIẾU NHẬP & HÓA ĐƠN)
-- ============================================================

-- A. PHIẾU NHẬP (Gồm 20 phiếu cũ + 5 phiếu mới của Tháng 3/2026)
INSERT INTO import_receipts (supplier_id, employee_id, receipt_date, total_amount, status) VALUES
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Miền Nam'),    (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2023-11-10 09:00:00', 3250000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Phát hành Fahasa'), (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2023-11-15 10:30:00', 1500000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Phương Nam'),  (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2023-12-05 14:00:00', 3600000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Thiên Long'),  (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2023-12-20 08:30:00', 3000000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Cá Chép'),     (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2024-01-15 09:00:00', 4470000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Alpha'),       (SELECT employee_id FROM employees WHERE full_name='Phạm Văn Đức'),  '2024-02-20 10:30:00', 2780000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty TNHH Sách Việt'),   (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2024-04-10 08:30:00', 5200000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Đinh Tị'),     (SELECT employee_id FROM employees WHERE full_name='Bùi Văn Tâm'),   '2024-05-18 14:00:00', 3610000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Nhân Văn'),    (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2024-07-05 09:15:00', 6310000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách First News'),  (SELECT employee_id FROM employees WHERE full_name='Phạm Văn Đức'),  '2024-08-22 11:00:00', 4060000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Phát hành Tổng hợp'),(SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'),'2024-10-08 08:00:00', 7480000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Trí Tuệ'),     (SELECT employee_id FROM employees WHERE full_name='Bùi Văn Tâm'),   '2024-11-30 13:30:00', 4800000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Huy Hoàng'),   (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2025-01-12 09:00:00', 3810000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Đại Nam'),     (SELECT employee_id FROM employees WHERE full_name='Phan Văn Long'), '2025-02-25 10:00:00', 3180000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Liên Việt'),   (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2025-04-03 08:30:00', 5460000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Bảo Châu'),    (SELECT employee_id FROM employees WHERE full_name='Bùi Văn Tâm'),   '2025-05-20 14:15:00', 3210000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Minh Long'),   (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2025-07-10 09:30:00', 6820000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Vạn Hoa'),     (SELECT employee_id FROM employees WHERE full_name='Phan Văn Long'), '2025-08-28 11:45:00', 3860000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Hoàng Long'),  (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2025-10-05 08:00:00', 7700000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Ngọc Hà'),     (SELECT employee_id FROM employees WHERE full_name='Bùi Văn Tâm'),   '2025-11-18 13:00:00', 6480000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Miền Nam'),    (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2026-03-01 09:15:00', 2600000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Phát hành Fahasa'), (SELECT employee_id FROM employees WHERE full_name='Phạm Văn Đức'),  '2026-03-05 10:30:00', 2550000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Phương Nam'),  (SELECT employee_id FROM employees WHERE full_name='Bùi Văn Tâm'),   '2026-03-10 14:00:00', 3250000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Thiên Long'),  (SELECT employee_id FROM employees WHERE full_name='Lê Hoàng Châu'), '2026-03-15 08:45:00', 1800000, 'Completed'),
((SELECT supplier_id FROM suppliers WHERE supplier_name='Công ty Sách Alpha'),       (SELECT employee_id FROM employees WHERE full_name='Phan Văn Long'), '2026-03-20 15:20:00', 3000000, 'Completed');

-- B. CHI TIẾT PHIẾU NHẬP
INSERT INTO import_receipt_details (receipt_id, book_id, quantity, unit_price, subtotal)
SELECT r.receipt_id, b.book_id, d.qty, b.import_price, (d.qty * b.import_price)
FROM import_receipts r
JOIN (
    SELECT '2023-11-10 09:00:00' as dt, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh' as title, 50 as qty UNION ALL
    SELECT '2023-11-15 10:30:00', 'Doraemon - Nobita Và Hành Tinh Màu Tím', 100 UNION ALL
    SELECT '2023-12-05 14:00:00', 'Nhà Giả Kim', 80 UNION ALL
    SELECT '2023-12-20 08:30:00', 'Đắc Nhân Tâm', 60 UNION ALL
    SELECT '2024-01-15 09:00:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 30 UNION ALL
    SELECT '2024-01-15 09:00:00', 'Tư Duy Nhanh Và Chậm', 16 UNION ALL
    SELECT '2024-02-20 10:30:00', 'Sapiens - Lược Sử Loài Người', 18 UNION ALL
    SELECT '2024-02-20 10:30:00', 'Harry Potter và Hòn đá Phù thủy', 4 UNION ALL
    SELECT '2024-04-10 08:30:00', 'Đắc Nhân Tâm', 50 UNION ALL
    SELECT '2024-04-10 08:30:00', 'Dám Nghĩ Lớn', 45 UNION ALL
    SELECT '2024-05-18 14:00:00', 'Kẻ Trộm Sách', 20 UNION ALL
    SELECT '2024-05-18 14:00:00', 'Rừng Na Uy', 19 UNION ALL
    SELECT '2024-07-05 09:15:00', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 30 UNION ALL
    SELECT '2024-07-05 09:15:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 28 UNION ALL
    SELECT '2024-07-05 09:15:00', 'Rừng Na Uy', 22 UNION ALL
    SELECT '2024-08-22 11:00:00', 'Mật Mã Da Vinci', 25 UNION ALL
    SELECT '2024-08-22 11:00:00', 'The Shining - Ngôi Nhà Ma', 13 UNION ALL
    SELECT '2024-10-08 08:00:00', 'Sapiens - Lược Sử Loài Người', 34 UNION ALL
    SELECT '2024-10-08 08:00:00', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 68 UNION ALL
    SELECT '2024-11-30 13:30:00', 'Harry Potter và Hòn đá Phù thủy', 30 UNION ALL
    SELECT '2024-11-30 13:30:00', 'Doraemon - Nobita Và Hành Tinh Màu Tím', 100 UNION ALL
    SELECT '2025-01-12 09:00:00', 'Đại Dương Đen', 30 UNION ALL
    SELECT '2025-01-12 09:00:00', 'Phép Lạ Của Sự Tỉnh Thức', 32 UNION ALL
    SELECT '2025-02-25 10:00:00', 'Conan - Thám Tử Lừng Danh Tập 1', 100 UNION ALL
    SELECT '2025-02-25 10:00:00', 'Dám Nghĩ Lớn', 23 UNION ALL
    SELECT '2025-04-03 08:30:00', 'Nhà Giả Kim', 60 UNION ALL
    SELECT '2025-04-03 08:30:00', 'Tư Duy Nhanh Và Chậm', 23 UNION ALL
    SELECT '2025-05-20 14:15:00', 'Chí Phèo', 30 UNION ALL
    SELECT '2025-05-20 14:15:00', 'Số Đỏ', 30 UNION ALL
    SELECT '2025-05-20 14:15:00', 'Gió Lạnh Đầu Mùa', 32 UNION ALL
    SELECT '2025-07-10 09:30:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 40 UNION ALL
    SELECT '2025-07-10 09:30:00', 'Kẻ Trộm Sách', 36 UNION ALL
    SELECT '2025-08-28 11:45:00', 'Sapiens - Lược Sử Loài Người', 20 UNION ALL
    SELECT '2025-08-28 11:45:00', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 28 UNION ALL
    SELECT '2025-10-05 08:00:00', 'Harry Potter và Hòn đá Phù thủy', 40 UNION ALL
    SELECT '2025-10-05 08:00:00', 'Doraemon - Nobita Và Hành Tinh Màu Tím', 100 UNION ALL
    SELECT '2025-10-05 08:00:00', 'Conan - Thám Tử Lừng Danh Tập 1', 100 UNION ALL
    SELECT '2025-11-18 13:00:00', 'Đắc Nhân Tâm', 60 UNION ALL
    SELECT '2025-11-18 13:00:00', 'Tư Duy Nhanh Và Chậm', 29 UNION ALL
    SELECT '2026-03-01 09:15:00', 'Sapiens - Lược Sử Loài Người', 20 UNION ALL
    SELECT '2026-03-05 10:30:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 30 UNION ALL
    SELECT '2026-03-10 14:00:00', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 50 UNION ALL
    SELECT '2026-03-15 08:45:00', 'Nhà Giả Kim', 40 UNION ALL
    SELECT '2026-03-20 15:20:00', 'Đắc Nhân Tâm', 60
) d ON r.receipt_date = d.dt
JOIN books b ON b.book_title = d.title;

-- C. HÓA ĐƠN (Gồm 20 hóa đơn cũ + 5 hóa đơn mới của Tháng 3/2026)
INSERT INTO invoices (customer_id, employee_id, created_at, total_amount, total_discount, final_amount, payment_method, status, points_earned) VALUES
((SELECT customer_id FROM customers WHERE full_name='Võ Minh Tuấn'),   (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2023-11-20 10:00:00', 190000, 10000, 180000, 'Tiền mặt', 'Completed', 19),
((SELECT customer_id FROM customers WHERE full_name='Hoàng Thị Mai'),  (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2023-11-25 15:30:00', 79000, 5000, 74000, 'Chuyển khoản', 'Completed', 7),
((SELECT customer_id FROM customers WHERE full_name='Đặng Văn Nam'),   (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2023-12-01 09:00:00', 100000, 0, 100000, 'Tiền mặt', 'Completed', 10),
((SELECT customer_id FROM customers WHERE full_name='Bùi Thị Lan'),    (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2023-12-10 14:00:00', 258000, 20000, 238000, 'Thẻ', 'Completed', 25),
((SELECT customer_id FROM customers WHERE full_name='Nguyễn Minh Khoa'),(SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2024-01-20 10:00:00', 298000, 0, 298000, 'Tiền mặt', 'Completed', 29),
((SELECT customer_id FROM customers WHERE full_name='Trần Thị Phương'),(SELECT employee_id FROM employees WHERE full_name='Nguyễn Thị Hoa'),'2024-02-14 15:30:00', 416000, 20000, 396000, 'Chuyển khoản', 'Completed', 41),
((SELECT customer_id FROM customers WHERE full_name='Lê Văn Thịnh'),   (SELECT employee_id FROM employees WHERE full_name='Trần Văn Kiên'), '2024-04-05 11:00:00', 219000, 0, 219000, 'Thẻ', 'Completed', 21),
((SELECT customer_id FROM customers WHERE full_name='Phạm Thị Thúy'),  (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2024-05-22 14:00:00', 574000, 50000, 524000, 'Chuyển khoản', 'Completed', 57),
((SELECT customer_id FROM customers WHERE full_name='Hoàng Văn Dũng'), (SELECT employee_id FROM employees WHERE full_name='Lê Thị Ngọc'),   '2024-07-10 09:30:00', 149000, 0, 149000, 'Tiền mặt', 'Completed', 14),
((SELECT customer_id FROM customers WHERE full_name='Vũ Thị Linh'),    (SELECT employee_id FROM employees WHERE full_name='Hoàng Thị Thảo'),'2024-08-18 16:00:00', 366000, 0, 366000, 'Tiền mặt', 'Completed', 36),
((SELECT customer_id FROM customers WHERE full_name='Đặng Minh Quân'), (SELECT employee_id FROM employees WHERE full_name='Trần Văn Kiên'), '2024-10-12 10:30:00', 597000, 30000, 567000, 'Thẻ', 'Completed', 59),
((SELECT customer_id FROM customers WHERE full_name='Bùi Thị Hằng'),   (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2024-11-28 13:00:00', 832000, 80000, 752000, 'Chuyển khoản', 'Completed', 83),
((SELECT customer_id FROM customers WHERE full_name='Ngô Văn Tùng'),   (SELECT employee_id FROM employees WHERE full_name='Nguyễn Thị Hoa'),'2025-01-15 11:00:00', 199000, 0, 199000, 'Tiền mặt', 'Completed', 19),
((SELECT customer_id FROM customers WHERE full_name='Đinh Thị Nga'),   (SELECT employee_id FROM employees WHERE full_name='Đỗ Thị Lan'),    '2025-02-20 14:30:00', 435000, 20000, 415000, 'Chuyển khoản', 'Completed', 43),
((SELECT customer_id FROM customers WHERE full_name='Lý Văn Bảo'),     (SELECT employee_id FROM employees WHERE full_name='Trần Văn Kiên'), '2025-04-08 10:00:00', 295000, 0, 295000, 'Tiền mặt', 'Completed', 29),
((SELECT customer_id FROM customers WHERE full_name='Cao Thị Diễm'),   (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2025-05-30 15:00:00', 656000, 50000, 606000, 'Thẻ', 'Completed', 65),
((SELECT customer_id FROM customers WHERE full_name='Tạ Văn Khải'),    (SELECT employee_id FROM employees WHERE full_name='Nguyễn Thị Hoa'),'2025-07-18 09:00:00', 290000, 0, 290000, 'Tiền mặt', 'Completed', 29),
((SELECT customer_id FROM customers WHERE full_name='Phan Thị Bạch'),  (SELECT employee_id FROM employees WHERE full_name='Lê Thị Ngọc'),   '2025-08-25 13:30:00', 437000, 20000, 417000, 'Chuyển khoản', 'Completed', 43),
((SELECT customer_id FROM customers WHERE full_name='Trương Văn Hiếu'),(SELECT employee_id FROM employees WHERE full_name='Trần Văn Kiên'), '2025-10-20 10:00:00', 751000, 30000, 721000, 'Thẻ', 'Completed', 75),
((SELECT customer_id FROM customers WHERE full_name='Mai Thị Thơ'),    (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'), '2025-11-30 16:00:00', 1085000, 100000, 985000, 'Chuyển khoản', 'Completed', 108),
((SELECT customer_id FROM customers WHERE full_name='Võ Minh Tuấn'),   (SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'),  '2026-03-02 10:00:00', 219000, 0, 219000, 'Tiền mặt', 'Completed', 21),
((SELECT customer_id FROM customers WHERE full_name='Hoàng Thị Mai'),  (SELECT employee_id FROM employees WHERE full_name='Nguyễn Thị Hoa'),'2026-03-08 14:30:00', 298000, 0, 298000, 'Chuyển khoản', 'Completed', 29),
((SELECT customer_id FROM customers WHERE full_name='Đặng Văn Nam'),   (SELECT employee_id FROM employees WHERE full_name='Trần Văn Kiên'), '2026-03-12 09:15:00', 174000, 0, 174000, 'Tiền mặt', 'Completed', 17),
((SELECT customer_id FROM customers WHERE full_name='Bùi Thị Lan'),    (SELECT employee_id FROM employees WHERE full_name='Đỗ Thị Lan'),    '2026-03-18 16:45:00', 172000, 20000, 152000, 'Thẻ', 'Completed', 15),
((SELECT customer_id FROM customers WHERE full_name='Nguyễn Minh Khoa'),(SELECT employee_id FROM employees WHERE full_name='Trần Thị Bình'),  '2026-03-25 11:20:00', 447000, 30000, 417000, 'Chuyển khoản', 'Completed', 41);

-- D. CHI TIẾT HÓA ĐƠN
INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, discount, subtotal)
SELECT i.invoice_id, b.book_id, d.qty, b.selling_price, 0, (d.qty * b.selling_price)
FROM invoices i
JOIN (
    SELECT '2023-11-20 10:00:00' as dt, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh' as title, 2 as qty UNION ALL
    SELECT '2023-11-25 15:30:00', 'Nhà Giả Kim', 1 UNION ALL
    SELECT '2023-12-01 09:00:00', 'Doraemon - Nobita Và Hành Tinh Màu Tím', 4 UNION ALL
    SELECT '2023-12-10 14:00:00', 'Đắc Nhân Tâm', 3 UNION ALL
    SELECT '2024-01-20 10:00:00', 'Sapiens - Lược Sử Loài Người', 1 UNION ALL
    SELECT '2024-01-20 10:00:00', 'Nhà Giả Kim', 1 UNION ALL
    SELECT '2024-02-14 15:30:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 2 UNION ALL
    SELECT '2024-02-14 15:30:00', 'Phép Lạ Của Sự Tỉnh Thức', 2 UNION ALL
    SELECT '2024-04-05 11:00:00', 'Sapiens - Lược Sử Loài Người', 1 UNION ALL
    SELECT '2024-05-22 14:00:00', 'Rừng Na Uy', 2 UNION ALL
    SELECT '2024-05-22 14:00:00', 'Mật Mã Da Vinci', 1 UNION ALL
    SELECT '2024-05-22 14:00:00', 'Dám Nghĩ Lớn', 1 UNION ALL
    SELECT '2024-07-10 09:30:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 1 UNION ALL
    SELECT '2024-08-18 16:00:00', 'Harry Potter và Hòn đá Phù thủy', 1 UNION ALL
    SELECT '2024-08-18 16:00:00', 'Đắc Nhân Tâm', 1 UNION ALL
    SELECT '2024-08-18 16:00:00', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 1 UNION ALL
    SELECT '2024-10-12 10:30:00', 'Tư Duy Nhanh Và Chậm', 1 UNION ALL
    SELECT '2024-10-12 10:30:00', 'Kẻ Trộm Sách', 2 UNION ALL
    SELECT '2024-10-12 10:30:00', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 1 UNION ALL
    SELECT '2024-11-28 13:00:00', 'Sapiens - Lược Sử Loài Người', 1 UNION ALL
    SELECT '2024-11-28 13:00:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 2 UNION ALL
    SELECT '2024-11-28 13:00:00', 'Rừng Na Uy', 1 UNION ALL
    SELECT '2024-11-28 13:00:00', 'Đại Dương Đen', 1 UNION ALL
    SELECT '2025-01-15 11:00:00', 'Tư Duy Nhanh Và Chậm', 1 UNION ALL
    SELECT '2025-02-20 14:30:00', 'Kẻ Trộm Sách', 2 UNION ALL
    SELECT '2025-02-20 14:30:00', 'Phép Lạ Của Sự Tỉnh Thức', 1 UNION ALL
    SELECT '2025-02-20 14:30:00', 'Conan - Thám Tử Lừng Danh Tập 1', 2 UNION ALL
    SELECT '2025-04-08 10:00:00', 'Harry Potter và Hòn đá Phù thủy', 1 UNION ALL
    SELECT '2025-04-08 10:00:00', 'Doraemon - Nobita Và Hành Tinh Màu Tím', 2 UNION ALL
    SELECT '2025-04-08 10:00:00', 'Chí Phèo', 1 UNION ALL
    SELECT '2025-05-30 15:00:00', 'Sapiens - Lược Sử Loài Người', 1 UNION ALL
    SELECT '2025-05-30 15:00:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 2 UNION ALL
    SELECT '2025-05-30 15:00:00', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 1 UNION ALL
    SELECT '2025-05-30 15:00:00', 'Phép Lạ Của Sự Tỉnh Thức', 1 UNION ALL
    SELECT '2025-07-18 09:00:00', 'Dám Nghĩ Lớn', 1 UNION ALL
    SELECT '2025-07-18 09:00:00', 'Số Đỏ', 1 UNION ALL
    SELECT '2025-07-18 09:00:00', 'Gió Lạnh Đầu Mùa', 2 UNION ALL
    SELECT '2025-08-25 13:30:00', 'Kẻ Trộm Sách', 1 UNION ALL
    SELECT '2025-08-25 13:30:00', 'Tư Duy Nhanh Và Chậm', 1 UNION ALL
    SELECT '2025-08-25 13:30:00', 'Nhà Giả Kim', 1 UNION ALL
    SELECT '2025-10-20 10:00:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 1 UNION ALL
    SELECT '2025-10-20 10:00:00', 'Harry Potter và Hòn đá Phù thủy', 1 UNION ALL
    SELECT '2025-10-20 10:00:00', 'Đại Dương Đen', 2 UNION ALL
    SELECT '2025-10-20 10:00:00', 'Conan - Thám Tử Lừng Danh Tập 1', 3 UNION ALL
    SELECT '2025-11-30 16:00:00', 'Sapiens - Lược Sử Loài Người', 2 UNION ALL
    SELECT '2025-11-30 16:00:00', 'Tư Duy Nhanh Và Chậm', 1 UNION ALL
    SELECT '2025-11-30 16:00:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 2 UNION ALL
    SELECT '2025-11-30 16:00:00', 'Rừng Na Uy', 1 UNION ALL
    SELECT '2026-03-02 10:00:00', 'Sapiens - Lược Sử Loài Người', 1 UNION ALL
    SELECT '2026-03-08 14:30:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 2 UNION ALL
    SELECT '2026-03-12 09:15:00', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 1 UNION ALL
    SELECT '2026-03-12 09:15:00', 'Nhà Giả Kim', 1 UNION ALL
    SELECT '2026-03-18 16:45:00', 'Đắc Nhân Tâm', 2 UNION ALL
    SELECT '2026-03-25 11:20:00', 'Sapiens - Lược Sử Loài Người', 1 UNION ALL
    SELECT '2026-03-25 11:20:00', 'Atomic Habits - Thay Đổi Tí Hon Hiệu Quả Bự', 1 UNION ALL
    SELECT '2026-03-25 11:20:00', 'Nhà Giả Kim', 1
) d ON i.created_at = d.dt
JOIN books b ON b.book_title = d.title;

-- E. DỊCH VỤ GIẢM GIÁ ÁP DỤNG TRÊN HÓA ĐƠN
INSERT INTO invoice_services (invoice_id, service_id, service_type, discount_value, description)
SELECT i.invoice_id, ds.service_id, 'Giảm giá', d.disc, d.desc
FROM invoices i
JOIN (
    SELECT '2024-02-14 15:30:00' as dt, 'Giảm 20k cho hóa đơn đầu tiên' as s_name, 20000 as disc, 'Giảm 20k cho hóa đơn đầu tiên' as `desc` UNION ALL
    SELECT '2024-05-22 14:00:00', 'Giảm 50k cho hóa đơn trên 500k', 50000, 'Giảm 50k cho hóa đơn trên 500k' UNION ALL
    SELECT '2024-10-12 10:30:00', 'Giảm 30k cho đơn từ 300k', 30000, 'Giảm 30k cho đơn từ 300k' UNION ALL
    SELECT '2024-11-28 13:00:00', 'Khách VIP giảm 12%', 80000, 'Khách VIP giảm 12%' UNION ALL
    SELECT '2025-02-20 14:30:00', 'Giảm 20k cho hóa đơn đầu tiên', 20000, 'Giảm 20k cho hóa đơn đầu tiên' UNION ALL
    SELECT '2025-05-30 15:00:00', 'Giảm 50k cho hóa đơn trên 500k', 50000, 'Giảm 50k cho hóa đơn trên 500k' UNION ALL
    SELECT '2025-08-25 13:30:00', 'Giảm 20k cho hóa đơn đầu tiên', 20000, 'Giảm 20k cho hóa đơn đầu tiên' UNION ALL
    SELECT '2025-10-20 10:00:00', 'Giảm 30k cho đơn từ 300k', 30000, 'Giảm 30k cho đơn từ 300k' UNION ALL
    SELECT '2025-11-30 16:00:00', 'Giảm 100k cho đơn từ 1 triệu', 100000, 'Giảm 100k cho đơn từ 1 triệu' UNION ALL
    SELECT '2026-03-18 16:45:00', 'Giảm 20k cho hóa đơn đầu tiên', 20000, 'Giảm 20k cho hóa đơn đầu tiên' UNION ALL
    SELECT '2026-03-25 11:20:00', 'Giảm 30k cho đơn từ 300k', 30000, 'Giảm 30k cho đơn từ 300k'
) d ON i.created_at = d.dt
JOIN discount_services ds ON ds.service_name = d.s_name;