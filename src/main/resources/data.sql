-- DATA SEED FOR STOCKFLOW SYSTEM
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE stocktake_details;
TRUNCATE TABLE stocktakes;
TRUNCATE TABLE inventory_transactions;
TRUNCATE TABLE inventory;
TRUNCATE TABLE inbound_details;
TRUNCATE TABLE inbounds;
TRUNCATE TABLE outbound_details;
TRUNCATE TABLE outbounds;
TRUNCATE TABLE lots;
TRUNCATE TABLE storage_locations;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE suppliers;
TRUNCATE TABLE customers;
TRUNCATE TABLE warehouses;
TRUNCATE TABLE users;

-- 1. Users
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (1, 'Nguyen Van Admin', 'admin@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'ADMIN', '0901234567', 1, 0, '2023-01-01 08:00:00', '2023-01-01 08:00:00');
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (2, 'Tran Thi Manager', 'manager@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'MANAGER', '0902345678', 1, 0, '2023-01-15 08:00:00', '2023-01-15 08:00:00');
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (3, 'Le Van Ke Toan', 'accountant@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'ACCOUNTANT', '0903456789', 1, 0, '2023-02-01 08:00:00', '2023-02-01 08:00:00');
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (4, 'Pham Minh Nhan Vien', 'staff@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'STAFF', '0904567890', 1, 0, '2023-02-15 08:00:00', '2023-02-15 08:00:00');
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (5, 'Nguyen Van Hoang', 'hoang@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'STAFF', '0905678901', 1, 0, '2023-03-01 08:00:00', '2023-03-01 08:00:00');
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (6, 'Tran Thi Mai', 'mai@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'MANAGER', '0906789012', 0, 0, '2023-03-15 08:00:00', '2024-01-10 08:00:00');
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (7, 'Le Minh Tuan', 'tuan@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'STAFF', '0907890123', 1, 1, '2023-06-01 08:00:00', '2023-06-01 08:00:00');
INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) VALUES (8, 'Pham Thi Lan', 'lan@stockflow.com', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', 'ACCOUNTANT', '0908901234', 1, 0, '2023-09-01 08:00:00', '2023-09-01 08:00:00');

-- 2. Warehouse
INSERT INTO warehouses (id, code, name, address, created_at, updated_at) VALUES (1, 'KHO_TONG', 'Kho Tổng Miền Nam', 'KCN Tân Bình, TP.HCM', NOW(), NOW());

-- 3. Storage Locations
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (1, 1, 'A', 1, 'A01', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (2, 1, 'A', 2, 'A02', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (3, 1, 'A', 3, 'A03', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (4, 1, 'A', 4, 'A04', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (5, 1, 'A', 5, 'A05', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (6, 1, 'A', 6, 'A06', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (7, 1, 'B', 1, 'B01', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (8, 1, 'B', 2, 'B02', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (9, 1, 'B', 3, 'B03', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (10, 1, 'B', 4, 'B04', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (11, 1, 'B', 5, 'B05', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (12, 1, 'B', 6, 'B06', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (13, 1, 'C', 1, 'C01', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (14, 1, 'C', 2, 'C02', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (15, 1, 'C', 3, 'C03', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (16, 1, 'C', 4, 'C04', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (17, 1, 'C', 5, 'C05', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (18, 1, 'C', 6, 'C06', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (19, 1, 'D', 1, 'D01', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (20, 1, 'D', 2, 'D02', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (21, 1, 'D', 3, 'D03', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (22, 1, 'D', 4, 'D04', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (23, 1, 'D', 5, 'D05', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (24, 1, 'D', 6, 'D06', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (25, 1, 'E', 1, 'E01', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (26, 1, 'E', 2, 'E02', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (27, 1, 'E', 3, 'E03', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (28, 1, 'E', 4, 'E04', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (29, 1, 'E', 5, 'E05', NOW(), NOW());
INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) VALUES (30, 1, 'E', 6, 'E06', NOW(), NOW());

-- 4. Categories
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (1, 'Bia', 'ACTIVE', NULL, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (2, 'Nước Ngọt', 'ACTIVE', NULL, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (3, 'Nước Suối', 'ACTIVE', NULL, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (4, 'Bia Chai', 'ACTIVE', 1, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (5, 'Bia Lon', 'ACTIVE', 1, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (6, 'Nước Ngọt Chai', 'ACTIVE', 2, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (7, 'Nước Ngọt Lon', 'ACTIVE', 2, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (8, 'Sữa', 'ACTIVE', NULL, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (9, 'Nước Tăng Lực', 'ACTIVE', NULL, NOW(), NOW(), 1);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (10, 'Sữa Hộp', 'ACTIVE', 8, NOW(), NOW(), 2);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (11, 'Sữa Tươi', 'ACTIVE', 8, NOW(), NOW(), 2);
INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) VALUES (12, 'Trà', 'INACTIVE', NULL, NOW(), NOW(), 1);

-- 5. Products
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (1, 5, 'Thùng', 'TIGER_N', 'Bia Tiger Nâu (Thùng 24 lon)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (2, 5, 'Thùng', 'TIGER_B', 'Bia Tiger Bạc (Thùng 24 lon)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (3, 5, 'Thùng', 'HNK_L', 'Bia Heineken (Thùng 24 lon)', 30, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (4, 4, 'Thùng', 'HNK_C', 'Bia Heineken (Thùng 20 chai)', 10, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (5, 5, 'Thùng', 'SG_S', 'Bia Saigon Special (Thùng 24 lon)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (6, 5, 'Thùng', 'B333', 'Bia 333 (Thùng 24 lon)', 40, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (7, 7, 'Thùng', 'COCA', 'Coca Cola (Thùng 24 lon)', 40, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (8, 7, 'Thùng', 'PEPSI', 'Pepsi (Thùng 24 lon)', 40, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (9, 7, 'Thùng', '7UP', '7Up (Thùng 24 lon)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (10, 6, 'Thùng', 'STING', 'Sting Dâu (Thùng 24 chai nhựa)', 30, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (11, 3, 'Thùng', 'LAVIE', 'Nước khoáng Lavie (Thùng 24 chai)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (12, 3, 'Thùng', 'AQUA', 'Nước tinh khiết Aquafina (Thùng 24 chai)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (13, 5, 'Thùng', 'BIA_VIET', 'Bia Việt (Thùng 24 lon)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (14, 5, 'Thùng', 'LARUE', 'Bia Larue (Thùng 24 lon)', 30, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (15, 5, 'Thùng', 'SG_L', 'Bia Saigon Lager (Thùng 24 lon)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (16, 9, 'Thùng', 'REDBULL', 'Red Bull (Thùng 24 lon)', 15, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (17, 11, 'Thùng', 'VINA_MILK', 'Vinamilk Sữa Tươi (Thùng 48 hộp)', 25, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (18, 11, 'Thùng', 'TH_MILK', 'TH True Milk (Thùng 48 hộp)', 25, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) VALUES (19, 6, 'Thùng', 'TRAXANH', 'Trà Xanh 0 Độ (Thùng 24 chai)', 20, NOW(), NOW());
INSERT INTO products (id, category_id, unit, code, name, min_stock, status, created_at, updated_at) VALUES (20, 6, 'Thùng', 'REVIVE', 'Revive (Thùng 24 chai)', 15, 'INACTIVE', NOW(), NOW());

-- 6. Suppliers
INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, contact_person, note, created_at, updated_at) VALUES (1, 'SABECO', 'Tổng CTCP Bia Rượu NGK Sài Gòn', '0300583659', '02838294386', 'contact@sabeco.com', '187 Nguyễn Chí Thanh, Q.5, TP.HCM', 'Nguyễn Văn Tùng', 'NCC chiến lược - bia Saigon, Tiger, 333, Bia Việt, Larue', NOW(), NOW());
INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, contact_person, note, created_at, updated_at) VALUES (2, 'HEINEKEN', 'Nhà máy Bia Heineken Việt Nam', '0300111222', '02838220222', 'sales@heineken.com.vn', '550 Kinh Dương Vương, Q.Bình Tân, TP.HCM', 'Trần Minh Đức', 'NCC Heineken, Larue, Tiger', NOW(), NOW());
INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, contact_person, note, created_at, updated_at) VALUES (3, 'PEPSICO', 'Suntory Pepsico Việt Nam', '0310222333', '02838123456', 'order@pepsico.com.vn', 'KCN Biên Hòa 2, Đồng Nai', 'Lê Thị Hương', 'NCC Pepsi, 7Up, Sting, Trà Xanh 0 Độ', NOW(), NOW());
INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, contact_person, note, created_at, updated_at) VALUES (4, 'COCACOLA', 'Coca-Cola Việt Nam', '0310444555', '02839987654', 'supply@cocacola.com.vn', 'Lô A-5, KCN Mỹ Phước 2, Bình Dương', 'Phạm Anh Tuấn', NULL, NOW(), NOW());
INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, contact_person, note, created_at, updated_at) VALUES (5, 'LAVIE', 'La Vie Việt Nam', '0310555666', '02838765432', 'order@lavie.com.vn', 'KCN Long Thành, Đồng Nai', 'Võ Thị Ngọc', 'NCC nước khoáng Lavie, Aquafina', NOW(), NOW());
INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, contact_person, note, created_at, updated_at) VALUES (6, 'VINAMILK', 'Công ty CP Sữa Việt Nam', '0300588569', '02854155555', 'sales@vinamilk.com.vn', '10 Tân Trào, Q.7, TP.HCM', 'Hoàng Minh Long', 'NCC sữa tươi Vinamilk các loại', NOW(), NOW());
INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, contact_person, note, status, created_at, updated_at) VALUES (7, 'REDBULL', 'Red Bull Việt Nam', '0310777888', '02838111222', 'supply@redbull.vn', 'Q.2, TP Thủ Đức, TP.HCM', 'Đặng Quốc Bảo', 'Đã ngừng hợp tác từ 06/2024', 'INACTIVE', NOW(), NOW());

-- 7. Customers
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (1, 'Đại lý Cấp 1 Quận 1', '0988000111', '45 Lê Duẩn, Quận 1, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (2, 'Siêu thị Co.opmart Lý Thường Kiệt', '0988000222', '199 Lý Thường Kiệt, Quận 10, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (3, 'Bách Hóa Xanh Q10', '0988000333', '123 Nguyễn Tri Phương, Quận 10, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (4, 'Tạp Hóa Thành Bình', '0988000444', '78 Cộng Hòa, Quận Tân Bình, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (5, 'Đại lý Bia & NGK Phương Nam', '0988000555', '234 Trần Hưng Đạo, Quận 5, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (6, 'Nhà hàng Hương Biển', '0988000666', '56 Nguyễn Huệ, Quận 1, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (7, 'Quán nhậu Bình Dân Sài Gòn', '0988000777', '89 Phạm Văn Đồng, Thủ Đức, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (8, 'Cửa hàng tiện lợi GS25 Nguyễn Trãi', '0988000888', '301 Nguyễn Trãi, Quận 5, TP.HCM', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, status, created_at, updated_at) VALUES (9, 'Đại lý Sỉ Miền Tây', '0988000999', 'TP Cần Thơ', 'INACTIVE', NOW(), NOW());
INSERT INTO customers (id, name, phone, address, created_at, updated_at) VALUES (10, 'Siêu thị BigC Gò Vấp', '0988001010', '242 Nguyễn Văn Lượng, Gò Vấp, TP.HCM', NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
