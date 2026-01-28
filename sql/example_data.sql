-- =============================================
-- DỮ LIỆU MẪU CHO DATABASE CƠ BẢN (bookstore_db)
-- =============================================

USE bookstore_db;

-- =============================================
-- 1. NHÂN VIÊN (EMPLOYEES)
-- =============================================
INSERT INTO employees (full_name, date_of_birth, gender, phone, address, position, salary, hire_date, status, avatar) VALUES
('Nguyễn Văn An', '1990-05-15', 'Nam', '0901234567', '123 Lê Lợi, Quận 1, TP.HCM', 'Quản lý', 15000000, '2020-01-10', 'Đang làm việc', 'nv_an.jpg'),
('Trần Thị Bình', '1995-08-20', 'Nữ', '0912345678', '456 Trần Hưng Đạo, Quận 5, TP.HCM', 'Nhân viên bán hàng', 8000000, '2021-03-15', 'Đang làm việc', 'nv_binh.jpg'),
('Lê Hoàng Châu', '1992-12-10', 'Nam', '0923456789', '789 Nguyễn Huệ, Quận 1, TP.HCM', 'Thủ kho', 9000000, '2021-06-20', 'Đang làm việc', 'nv_chau.jpg'),
('Phạm Thị Dung', '1988-03-25', 'Nữ', '0934567890', '321 Võ Văn Tần, Quận 3, TP.HCM', 'Kế toán', 12000000, '2019-11-05', 'Đang làm việc', 'nv_dung.jpg');

-- =============================================
-- 2. NHÓM QUYỀN (PERMISSION_GROUPS)
-- =============================================
INSERT INTO permission_groups (group_name, status) VALUES
('Quản trị viên', 'Hoạt động'),
('Nhân viên bán hàng', 'Hoạt động'),
('Thủ kho', 'Hoạt động'),
('Kế toán', 'Hoạt động');

-- =============================================
-- 3. TÀI KHOẢN (ACCOUNTS)
-- =============================================
INSERT INTO accounts (employee_id, permission_group_id, username, password, status) VALUES
(1, 1, 'admin', 'admin123', 'Hoạt động'),
(2, 2, 'nvbh01', 'bh123456', 'Hoạt động'),
(3, 3, 'thukho01', 'tk123456', 'Hoạt động'),
(4, 4, 'ketoan01', 'kt123456', 'Hoạt động');

-- =============================================
-- 4. CHỨC NĂNG (FUNCTIONS)
-- =============================================
INSERT INTO functions (function_name, system_function_code, function_group) VALUES
('Quản lý sách', 'BOOK_MANAGE', 'Quản lý kho'),
('Quản lý nhân viên', 'EMPLOYEE_MANAGE', 'Quản lý hệ thống'),
('Bán hàng', 'SALE_MANAGE', 'Bán hàng'),
('Quản lý nhập hàng', 'IMPORT_MANAGE', 'Quản lý kho');

-- =============================================
-- 5. CHI TIẾT NHÓM QUYỀN (PERMISSION_DETAILS)
-- =============================================
INSERT INTO permission_details (permission_group_id, function_id, actions) VALUES
(1, 1, 'Xem,Thêm,Sửa,Xóa'),
(1, 2, 'Xem,Thêm,Sửa,Xóa'),
(2, 3, 'Xem,Thêm'),
(3, 4, 'Xem,Thêm,Sửa');

-- =============================================
-- 6. KHÁCH HÀNG (CUSTOMERS)
-- =============================================
INSERT INTO customers (full_name, phone, loyalty_points, registration_date) VALUES
('Võ Minh Tuấn', '0945678901', 150, '2023-01-15'),
('Hoàng Thị Mai', '0956789012', 320, '2023-03-20'),
('Đặng Văn Nam', '0967890123', 80, '2023-06-10'),
('Bùi Thị Lan', '0978901234', 500, '2022-12-05');

-- =============================================
-- 7. LỊCH SỬ QUY ĐỔI ĐIỂM (POINT_REDEMPTION_HISTORY)
-- =============================================
INSERT INTO point_redemption_history (customer_id, points_redeemed, value_received, redemption_type) VALUES
(2, 100, 10000, 'Giảm giá hóa đơn'),
(4, 200, 20000, 'Giảm giá hóa đơn'),
(4, 300, 30000, 'Đổi quà tặng'),
(2, 50, 5000, 'Giảm giá hóa đơn');

-- =============================================
-- 8. NHÀ XUẤT BẢN (PUBLISHERS)
-- =============================================
INSERT INTO publishers (publisher_name, phone, status) VALUES
('Nhà xuất bản Trẻ', '0283822711', 'Hoạt động'),
('Nhà xuất bản Kim Đồng', '0283943344', 'Hoạt động'),
('Nhà xuất bản Văn học', '0283822211', 'Hoạt động'),
('Nhà xuất bản Thế giới', '0283825252', 'Hoạt động');

-- =============================================
-- 9. THỂ LOẠI (CATEGORIES)
-- =============================================
INSERT INTO categories (category_name, display_order, status) VALUES
('Văn học Việt Nam', 1, 'Hoạt động'),
('Văn học nước ngoài', 2, 'Hoạt động'),
('Sách thiếu nhi', 3, 'Hoạt động'),
('Sách kỹ năng sống', 4, 'Hoạt động');

-- =============================================
-- 10. SÁCH (BOOKS)
-- =============================================
INSERT INTO books (publisher_id, category_id, isbn, book_title, page_count, language, publication_year, 
                   cover_type, import_price, selling_price, stock_quantity, minimum_stock, image, status) VALUES
(1, 1, '978-604-1-00234-5', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 368, 'Tiếng Việt', 2018, 'Bìa mềm', 65000, 95000, 50, 10, 'hoa_vang.jpg', 'Còn hàng'),
(2, 3, '978-604-2-13456-7', 'Doraemon - Nobita Và Hành Tinh Màu Tím', 196, 'Tiếng Việt', 2023, 'Bìa mềm', 15000, 25000, 100, 20, 'doraemon.jpg', 'Còn hàng'),
(3, 2, '978-604-3-24567-8', 'Nhà Giả Kim', 227, 'Tiếng Việt', 2020, 'Bìa cứng', 45000, 79000, 35, 10, 'nha_gia_kim.jpg', 'Còn hàng'),
(4, 4, '978-604-4-35678-9', 'Đắc Nhân Tâm', 320, 'Tiếng Việt', 2021, 'Bìa mềm', 50000, 86000, 8, 10, 'dac_nhan_tam.jpg', 'Còn hàng');

-- =============================================
-- 11. TÁC GIẢ (AUTHORS)
-- =============================================
INSERT INTO authors (author_name, avatar) VALUES
('Nguyễn Nhật Ánh', 'nna.jpg'),
('Fujiko F. Fujio', 'fujiko.jpg'),
('Paulo Coelho', 'paulo.jpg'),
('Dale Carnegie', 'dale.jpg');

-- =============================================
-- 12. SÁCH - TÁC GIẢ (BOOK_AUTHORS)
-- =============================================
INSERT INTO book_authors (book_id, author_id, role, display_order) VALUES
(1, 1, 'Tác giả chính', 1),
(2, 2, 'Tác giả chính', 1),
(3, 3, 'Tác giả chính', 1),
(4, 4, 'Tác giả chính', 1);

-- =============================================
-- 13. HÓA ĐƠN (INVOICES)
-- =============================================
INSERT INTO invoices (customer_id, employee_id, total_amount, total_discount, points_used, points_value, 
                     final_amount, payment_method, status, points_earned) VALUES
(1, 2, 190000, 10000, 0, 0, 180000, 'Tiền mặt', 'Hoàn thành', 18),
(2, 2, 79000, 5000, 50, 5000, 69000, 'Chuyển khoản', 'Hoàn thành', 7),
(3, 2, 120000, 0, 0, 0, 120000, 'Tiền mặt', 'Hoàn thành', 12),
(4, 2, 250000, 20000, 100, 10000, 220000, 'Thẻ', 'Hoàn thành', 22);

-- =============================================
-- 14. CHI TIẾT HÓA ĐƠN (INVOICE_DETAILS)
-- =============================================
INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, discount, subtotal) VALUES
(1, 1, 2, 95000, 10000, 180000),
(2, 3, 1, 79000, 5000, 74000),
(3, 2, 4, 25000, 0, 100000),
(4, 4, 3, 86000, 20000, 238000);

-- =============================================
-- 15. DỊCH VỤ GIẢM GIÁ (DISCOUNT_SERVICES)
-- =============================================
INSERT INTO discount_services (service_name, discount_type, discount_value, minimum_value, maximum_discount, 
                               start_date, end_date, status, description) VALUES
('Giảm giá 10% cho hóa đơn trên 200k', 'Phần trăm', 10, 200000, 50000, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Hoạt động', 'Áp dụng cho tất cả sách'),
('Giảm 20k cho hóa đơn đầu tiên', 'Số tiền cố định', 20000, 100000, 20000, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Hoạt động', 'Chỉ khách hàng mới'),
('Giảm 15% cho sách thiếu nhi', 'Phần trăm', 15, 50000, 30000, '2024-06-01 00:00:00', '2024-08-31 23:59:59', 'Hoạt động', 'Khuyến mãi hè cho thiếu nhi'),
('Mua 3 tặng 1', 'Khuyến mãi đặc biệt', 0, 150000, 0, '2024-01-01 00:00:00', '2024-03-31 23:59:59', 'Ngừng hoạt động', 'Đã kết thúc');

-- =============================================
-- 16. DỊCH VỤ HÓA ĐƠN (INVOICE_SERVICES)
-- =============================================
INSERT INTO invoice_services (invoice_id, service_id, service_type, discount_value, description) VALUES
(1, 1, 'Giảm giá phần trăm', 10000, 'Giảm 10% cho hóa đơn trên 200k'),
(2, 2, 'Giảm giá cố định', 5000, 'Giảm 20k cho hóa đơn đầu tiên'),
(4, 1, 'Giảm giá phần trăm', 20000, 'Giảm 10% cho hóa đơn trên 200k'),
(3, 3, 'Giảm giá phần trăm', 0, 'Không đủ điều kiện giảm giá');

-- =============================================
-- 17. NHÀ CUNG CẤP (SUPPLIERS)
-- =============================================
INSERT INTO suppliers (supplier_name, phone, status) VALUES
('Công ty Sách Miền Nam', '0287654321', 'Hoạt động'),
('Công ty Phát hành Fahasa', '0287777888', 'Hoạt động'),
('Công ty Sách Phương Nam', '0283888999', 'Hoạt động'),
('Công ty Sách Thiên Long', '0289999000', 'Hoạt động');

-- =============================================
-- 18. PHIẾU NHẬP (IMPORT_RECEIPTS)
-- =============================================
INSERT INTO import_receipts (supplier_id, employee_id, import_date, total_amount, status, notes) VALUES
(1, 3, '2024-01-15 10:30:00', 5000000, 'Hoàn thành', 'Nhập hàng đầu năm 2024'),
(2, 3, '2024-02-20 14:00:00', 8500000, 'Hoàn thành', 'Nhập sách thiếu nhi mới'),
(3, 3, '2024-03-10 09:15:00', 3200000, 'Hoàn thành', 'Bổ sung sách văn học'),
(4, 3, '2024-04-05 16:45:00', 6700000, 'Đang xử lý', 'Đợi nhận hàng từ nhà cung cấp');

-- =============================================
-- 19. CHI TIẾT PHIẾU NHẬP (IMPORT_RECEIPT_DETAILS)
-- =============================================
INSERT INTO import_receipt_details (receipt_id, book_id, quantity, unit_price, subtotal, expiry_date) VALUES
(1, 1, 50, 65000, 3250000, NULL),
(1, 2, 100, 15000, 1500000, NULL),
(2, 3, 80, 45000, 3600000, NULL),
(3, 4, 60, 50000, 3000000, NULL);

-- =============================================
-- 20. THAM SỐ HỆ THỐNG (SYSTEM_PARAMETERS)
-- =============================================
INSERT INTO system_parameters (parameter_code, parameter_value, description) VALUES
('TY_LE_TICH_DIEM', '10', 'Tích 1 điểm cho mỗi 10,000 VNĐ'),
('TY_LE_QUI_DOI_DIEM', '100', 'Đổi 1 điểm = 100 VNĐ'),
('SO_LUONG_TOI_THIEU_CANH_BAO', '10', 'Cảnh báo khi tồn kho < 10 cuốn'),
('THOI_GIAN_LUU_HOA_DON', '365', 'Lưu hóa đơn trong 365 ngày');

-- =============================================
-- HOÀN TẤT - Kiểm tra dữ liệu
-- =============================================
SELECT 'Nhân viên:' as Bang, COUNT(*) as SoLuong FROM employees
UNION ALL
SELECT 'Khách hàng:', COUNT(*) FROM customers
UNION ALL
SELECT 'Sách:', COUNT(*) FROM books
UNION ALL
SELECT 'Hóa đơn:', COUNT(*) FROM invoices
UNION ALL
SELECT 'Phiếu nhập:', COUNT(*) FROM import_receipts;