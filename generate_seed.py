import random
from datetime import datetime, timedelta

def get_random_date(start_date, end_date):
    time_between_dates = end_date - start_date
    days_between_dates = time_between_dates.days
    random_number_of_days = random.randrange(days_between_dates)
    return start_date + timedelta(days=random_number_of_days)

def format_datetime(dt):
    return dt.strftime('%Y-%m-%d %H:%M:%S')

def format_date(dt):
    return dt.strftime('%Y-%m-%d')

with open('dataseed.sql', 'w', encoding='utf-8') as f:
    f.write("-- DATA SEED FOR STOCKFLOW SYSTEM (6 MONTHS DEMO)\n")
    f.write("SET FOREIGN_KEY_CHECKS = 0;\n\n")

    f.write("TRUNCATE TABLE abnormal_stock_details;\n")
    f.write("TRUNCATE TABLE abnormal_stocks;\n")
    f.write("TRUNCATE TABLE stocktake_details;\n")
    f.write("TRUNCATE TABLE stocktakes;\n")
    f.write("TRUNCATE TABLE inventory_transactions;\n")
    f.write("TRUNCATE TABLE inventory;\n")
    f.write("TRUNCATE TABLE inbound_details;\n")
    f.write("TRUNCATE TABLE inbounds;\n")
    f.write("TRUNCATE TABLE outbound_details;\n")
    f.write("TRUNCATE TABLE outbounds;\n")
    f.write("TRUNCATE TABLE lots;\n")
    f.write("TRUNCATE TABLE storage_locations;\n")
    f.write("TRUNCATE TABLE products;\n")
    f.write("TRUNCATE TABLE categories;\n")
    f.write("TRUNCATE TABLE suppliers;\n")
    f.write("TRUNCATE TABLE customers;\n")
    f.write("TRUNCATE TABLE warehouses;\n")
    f.write("TRUNCATE TABLE users;\n\n")

    # 1. Users
    # 123456 bcrypt hash: $2a$10$wY9dF0Yy.eT1D//lH/o0eOzM6y9pM2L4NlH60S6kC/2P1b2m5E/.G
    f.write("-- 1. Users\n")
    users = [
        (1, 'Admin User', 'admin@stockflow.com', 'ADMIN'),
        (2, 'Manager User', 'manager@stockflow.com', 'MANAGER'),
        (3, 'Accountant User', 'accountant@stockflow.com', 'ACCOUNTANT'),
        (4, 'Staff User', 'staff@stockflow.com', 'STAFF')
    ]
    for u in users:
        f.write(f"INSERT INTO users (id, full_name, email, password_hash, role, phone, is_active, must_change_password, created_at, updated_at) "
                f"VALUES ({u[0]}, '{u[1]}', '{u[2]}', '$2a$12$HGkHZAXOuxdxFM5Li/4tk.jirReFdmPQaGIxiPDaYPhdzeCB4upuu', '{u[3]}', '0901234567', 1, 0, NOW(), NOW());\n")
    
    # 2. Warehouse
    f.write("\n-- 2. Warehouse\n")
    f.write("INSERT INTO warehouses (id, code, name, address, created_at, updated_at) "
            "VALUES (1, 'KHO_TONG', 'Kho Tổng Miền Nam', 'KCN Tân Bình, TP.HCM', NOW(), NOW());\n")

    # 3. Storage Locations
    f.write("\n-- 3. Storage Locations\n")
    loc_id = 1
    locations = []
    for row in ['A', 'B', 'C', 'D', 'E']:
        for col in range(1, 11):
            code = f"{row}{col:02d}"
            f.write(f"INSERT INTO storage_locations (id, warehouse_id, row_label, col_index, location_code, created_at, updated_at) "
                    f"VALUES ({loc_id}, 1, '{row}', {col}, '{code}', NOW(), NOW());\n")
            locations.append(loc_id)
            loc_id += 1

    # 4. Categories
    f.write("\n-- 4. Categories\n")
    categories = [
        (1, 'Bia', 'ACTIVE', 'NULL'),
        (2, 'Nước Ngọt', 'ACTIVE', 'NULL'),
        (3, 'Nước Suối', 'ACTIVE', 'NULL'),
        (4, 'Bia Chai', 'ACTIVE', 1),
        (5, 'Bia Lon', 'ACTIVE', 1),
        (6, 'Nước Ngọt Chai', 'ACTIVE', 2),
        (7, 'Nước Ngọt Lon', 'ACTIVE', 2)
    ]
    for c in categories:
        f.write(f"INSERT INTO categories (id, name, status, parent_id, created_at, updated_at, created_by) "
                f"VALUES ({c[0]}, '{c[1]}', '{c[2]}', {c[3]}, NOW(), NOW(), 1);\n")

    # 5. Products
    f.write("\n-- 5. Products\n")
    products = [
        (1, 5, 'Thùng', 'TIGER_N', 'Bia Tiger Nâu (Thùng 24 lon)', 100),
        (2, 5, 'Thùng', 'TIGER_B', 'Bia Tiger Bạc (Thùng 24 lon)', 100),
        (3, 5, 'Thùng', 'HNK_L', 'Bia Heineken (Thùng 24 lon)', 150),
        (4, 4, 'Thùng', 'HNK_C', 'Bia Heineken (Thùng 20 chai)', 50),
        (5, 5, 'Thùng', 'SG_S', 'Bia Saigon Special (Thùng 24 lon)', 100),
        (6, 5, 'Thùng', 'B333', 'Bia 333 (Thùng 24 lon)', 200),
        (7, 7, 'Thùng', 'COCA', 'Coca Cola (Thùng 24 lon)', 200),
        (8, 7, 'Thùng', 'PEPSI', 'Pepsi (Thùng 24 lon)', 200),
        (9, 7, 'Thùng', '7UP', '7Up (Thùng 24 lon)', 100),
        (10, 6, 'Thùng', 'STING', 'Sting Dâu (Thùng 24 chai nhựa)', 150),
        (11, 3, 'Thùng', 'LAVIE', 'Nước khoáng Lavie (Thùng 24 chai)', 100),
        (12, 3, 'Thùng', 'AQUA', 'Nước tinh khiết Aquafina (Thùng 24 chai)', 100)
    ]
    for p in products:
        f.write(f"INSERT INTO products (id, category_id, unit, code, name, min_stock, created_at, updated_at) "
                f"VALUES ({p[0]}, {p[1]}, '{p[2]}', '{p[3]}', '{p[4]}', {p[5]}, NOW(), NOW());\n")

    # 6. Suppliers & Customers
    f.write("\n-- 6. Suppliers & Customers\n")
    suppliers = [
        (1, 'SABECO', 'Tổng CTCP Bia Rượu NGK Sài Gòn', '0300583659', '1200'),
        (2, 'HEINEKEN', 'Nhà máy Bia Heineken Việt Nam', '0300111222', '1201'),
        (3, 'PEPSICO', 'Suntory Pepsico Việt Nam', '0310222333', '1202'),
        (4, 'COCACOLA', 'Coca-Cola Việt Nam', '0310444555', '1203')
    ]
    for s in suppliers:
        f.write(f"INSERT INTO suppliers (id, code, name, tax_code, phone, email, address, created_at, updated_at) "
                f"VALUES ({s[0]}, '{s[1]}', '{s[2]}', '{s[3]}', '{s[4]}', 'contact@{s[1].lower()}.com', 'TP.HCM', NOW(), NOW());\n")

    customers = [
        (1, 'Đại lý Cấp 1 Quận 1', '0988000111', 'Quận 1, TP.HCM'),
        (2, 'Siêu thị Co.opmart', '0988000222', 'Quận 3, TP.HCM'),
        (3, 'Bách Hóa Xanh Q10', '0988000333', 'Quận 10, TP.HCM'),
        (4, 'Tạp Hóa Thành Bình', '0988000444', 'Quận Tân Bình, TP.HCM'),
        (5, 'Đại lý Bia & NGK Phương Nam', '0988000555', 'Quận 5, TP.HCM')
    ]
    for c in customers:
        f.write(f"INSERT INTO customers (id, name, phone, address, created_at, updated_at) "
                f"VALUES ({c[0]}, '{c[1]}', '{c[2]}', '{c[3]}', NOW(), NOW());\n")

    # 7. Generate Data for the last 6 months
    f.write("\n-- 7. Lots, Inbounds, Outbounds, Inventory (6 Months Data)\n")
    
    start_date = datetime(2026, 2, 1)
    end_date = datetime(2026, 8, 11)
    
    current_date = start_date
    
    lot_id = 1
    inbound_id = 1
    outbound_id = 1
    inbound_detail_id = 1
    outbound_detail_id = 1
    inv_trans_id = 1
    next_inv_id = 1
    
    inventory = {} # key: (product_id, lot_id, loc_id) -> quantity
    inventory_ids = {} # key: (product_id, lot_id, loc_id) -> inv_id
    
    sql_lots = []
    sql_inbounds = []
    sql_inbound_details = []
    sql_outbounds = []
    sql_outbound_details = []
    sql_inventory = []
    sql_inventory_transactions = []
    
    available_locs = set(locations)
    
    while current_date < end_date:
        if random.random() < 0.05:
            num_products = random.randint(2, 4)
            sup_id = random.choice([1, 2, 3, 4])
            code = f"INB-{current_date.strftime('%Y%m%d')}-{inbound_id:03d}"
            
            sql_inbounds.append(f"INSERT INTO inbounds (id, code, supplier_id, warehouse_id, created_by, created_at) "
                                f"VALUES ({inbound_id}, '{code}', {sup_id}, 1, 2, '{format_datetime(current_date)}');\n")
            
            for _ in range(num_products):
                if not available_locs:
                    break
                    
                prod = random.choice(products)
                p_id = prod[0]
                qty = random.randint(50, 300)
                price = random.randint(150, 400) * 1000
                total = qty * price
                
                mfg = current_date - timedelta(days=random.randint(5, 30))
                exp = current_date + timedelta(days=random.randint(180, 365))
                lot_code = f"LOT-{p_id}-{mfg.strftime('%y%m%d')}-{lot_id}"
                
                sql_lots.append(f"INSERT INTO lots (id, product_id, lot_code, mfg_date, exp_date, created_at, updated_at) "
                                f"VALUES ({lot_id}, {p_id}, '{lot_code}', '{format_date(mfg)}', '{format_date(exp)}', '{format_datetime(current_date)}', '{format_datetime(current_date)}');\n")
                
                loc = random.choice(list(available_locs))
                available_locs.remove(loc)
                
                sql_inbound_details.append(f"INSERT INTO inbound_details (id, inbound_id, product_id, lot_id, location_id, quantity, unit_price, total_amount) "
                                           f"VALUES ({inbound_detail_id}, {inbound_id}, {p_id}, {lot_id}, {loc}, {qty}, {price}, {total});\n")
                
                key = (p_id, lot_id, loc)
                if key not in inventory_ids:
                    inventory_ids[key] = next_inv_id
                    next_inv_id += 1
                
                inv_id = inventory_ids[key]
                inventory[key] = inventory.get(key, 0) + qty
                
                sql_inventory_transactions.append(f"INSERT INTO inventory_transactions (id, inventory_id, ref_type, ref_id, quantity_change, balance_after, created_by, created_at) "
                                                  f"VALUES ({inv_trans_id}, {inv_id}, 'INBOUND', {inbound_id}, {qty}, {qty}, 2, '{format_datetime(current_date)}');\n")
                
                inbound_detail_id += 1
                lot_id += 1
                inv_trans_id += 1
                
            inbound_id += 1
            
        if random.random() < 0.2 and len(inventory) > 0:
            num_products = random.randint(1, 3)
            cust_id = random.choice([1, 2, 3, 4, 5])
            code = f"OUT-{current_date.strftime('%Y%m%d')}-{outbound_id:03d}"
            
            sql_outbounds.append(f"INSERT INTO outbounds (id, code, issue_type, customer_id, warehouse_id, created_by, created_at) "
                                 f"VALUES ({outbound_id}, '{code}', 'RETAIL', {cust_id}, 1, 4, '{format_datetime(current_date)}');\n")
            
            available_items = [key for key, q in inventory.items() if q > 0]
            random.shuffle(available_items)
            
            for key in available_items[:num_products]:
                p_id, l_id, loc = key
                q = inventory[key]
                out_qty = random.randint(1, min(50, q))
                price = random.randint(180, 450) * 1000
                total = out_qty * price
                
                sql_outbound_details.append(f"INSERT INTO outbound_details (id, outbound_id, product_id, lot_id, location_id, quantity, unit_price, total_amount) "
                                            f"VALUES ({outbound_detail_id}, {outbound_id}, {p_id}, {l_id}, {loc}, {out_qty}, {price}, {total});\n")
                
                inventory[key] -= out_qty
                inv_id = inventory_ids[key]
                
                sql_inventory_transactions.append(f"INSERT INTO inventory_transactions (id, inventory_id, ref_type, ref_id, quantity_change, balance_after, created_by, created_at) "
                                                  f"VALUES ({inv_trans_id}, {inv_id}, 'OUTBOUND', {outbound_id}, {-out_qty}, {inventory[key]}, 4, '{format_datetime(current_date)}');\n")
                
                if inventory[key] == 0:
                    pass # Do not put location back, due to uk_inventory_location DB design flaw
                
                outbound_detail_id += 1
                inv_trans_id += 1
                
            outbound_id += 1
            
        current_date += timedelta(days=1)
        
    for key, q in inventory.items():
        p_id, l_id, loc = key
        inv_id = inventory_ids[key]
        sql_inventory.append(f"INSERT INTO inventory (id, warehouse_id, product_id, lot_id, location_id, quantity, version, updated_at) "
                             f"VALUES ({inv_id}, 1, {p_id}, {l_id}, {loc}, {q}, 0, NOW());\n")
                             
    for stmt in sql_lots: f.write(stmt)
    for stmt in sql_inventory: f.write(stmt)
    for stmt in sql_inbounds: f.write(stmt)
    for stmt in sql_inbound_details: f.write(stmt)
    for stmt in sql_outbounds: f.write(stmt)
    for stmt in sql_outbound_details: f.write(stmt)
    for stmt in sql_inventory_transactions: f.write(stmt)
            
    f.write("\n-- 9. Stocktakes\n")
    stk_id = 1
    stk_dtl_id = 1
    stk_date1 = end_date - timedelta(days=5)
    stk_date2 = end_date - timedelta(days=15)
    
    f.write(f"INSERT INTO stocktakes (id, code, warehouse_id, created_by, status, version, created_at) "
            f"VALUES ({stk_id}, 'STK-{stk_date1.strftime('%Y%m%d')}-001', 1, 2, 'PENDING', 0, '{format_datetime(stk_date1)}');\n")
    
    available_items = [k for k, q in inventory.items() if q > 0]
    for key in available_items[:5]:
        p_id, l_id, loc = key
        sys_qty = inventory[key]
        act_qty = sys_qty - random.randint(-2, 5)
        if act_qty < 0: act_qty = 0
        diff = act_qty - sys_qty
        f.write(f"INSERT INTO stocktake_details (id, stocktake_id, product_id, lot_id, location_id, system_qty, actual_qty, diff_qty) "
                f"VALUES ({stk_dtl_id}, {stk_id}, {p_id}, {l_id}, {loc}, {sys_qty}, {act_qty}, {diff});\n")
        stk_dtl_id += 1
    stk_id += 1

    f.write(f"INSERT INTO stocktakes (id, code, warehouse_id, created_by, approved_by, status, reject_reason, version, created_at, approved_at) "
            f"VALUES ({stk_id}, 'STK-{stk_date2.strftime('%Y%m%d')}-002', 1, 4, 1, 'REJECTED', 'Sai lệch quá lớn, yêu cầu kiểm lại', 0, '{format_datetime(stk_date2)}', '{format_datetime(stk_date2 + timedelta(hours=2))}');\n")
            
    for key in available_items[5:8]:
        p_id, l_id, loc = key
        sys_qty = inventory[key]
        act_qty = sys_qty - 50
        if act_qty < 0: act_qty = 0
        diff = act_qty - sys_qty
        f.write(f"INSERT INTO stocktake_details (id, stocktake_id, product_id, lot_id, location_id, system_qty, actual_qty, diff_qty) "
                f"VALUES ({stk_dtl_id}, {stk_id}, {p_id}, {l_id}, {loc}, {sys_qty}, {act_qty}, {diff});\n")
        stk_dtl_id += 1
        
    f.write("\n-- 10. Abnormal Stocks\n")
    abn_id = 1
    abn_dtl_id = 1
    abn_date1 = end_date - timedelta(days=3)
    abn_date2 = end_date - timedelta(days=10)
    
    f.write(f"INSERT INTO abnormal_stocks (id, code, warehouse_id, created_by, status, version, created_at) "
            f"VALUES ({abn_id}, 'ABN-{abn_date1.strftime('%Y%m%d')}-001', 1, 4, 'PENDING', 0, '{format_datetime(abn_date1)}');\n")
    for key in available_items[8:10]:
        p_id, l_id, loc = key
        qty = random.randint(1, 5)
        reason = random.choice(['DAMAGED', 'LOST', 'EXPIRED'])
        f.write(f"INSERT INTO abnormal_stock_details (id, abnormal_stock_id, product_id, lot_id, location_id, quantity, reason_type, note) "
                f"VALUES ({abn_dtl_id}, {abn_id}, {p_id}, {l_id}, {loc}, {qty}, '{reason}', 'Phát hiện lúc lấy hàng');\n")
        abn_dtl_id += 1
    abn_id += 1
    
    f.write(f"INSERT INTO abnormal_stocks (id, code, warehouse_id, created_by, approved_by, status, reject_reason, version, created_at, approved_at) "
            f"VALUES ({abn_id}, 'ABN-{abn_date2.strftime('%Y%m%d')}-002', 1, 2, 1, 'REJECTED', 'Không rõ nguyên nhân hư hỏng', 0, '{format_datetime(abn_date2)}', '{format_datetime(abn_date2 + timedelta(hours=1))}');\n")
    for key in available_items[10:12]:
        p_id, l_id, loc = key
        qty = random.randint(1, 3)
        f.write(f"INSERT INTO abnormal_stock_details (id, abnormal_stock_id, product_id, lot_id, location_id, quantity, reason_type, note) "
                f"VALUES ({abn_dtl_id}, {abn_id}, {p_id}, {l_id}, {loc}, {qty}, 'OTHER', 'Khách trả lại');\n")
        abn_dtl_id += 1

    f.write("\nSET FOREIGN_KEY_CHECKS = 1;\n")
