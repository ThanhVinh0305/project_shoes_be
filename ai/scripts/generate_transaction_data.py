#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script để tạo dữ liệu giao dịch cho hệ thống AI Recommendation
- Tạo 10 users mới
- Tạo 1000 giao dịch phân bổ cho các users
- Mỗi giao dịch có 1-3 sản phẩm
"""

import pymysql
import random
from datetime import datetime, timedelta
from faker import Faker

# Cấu hình database
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'tuanhung1999',
    'database': 'e-commerce',
    'charset': 'utf8mb4'
}

# Khởi tạo Faker để tạo dữ liệu giả
fake = Faker('vi_VN')  # Tiếng Việt

def get_db_connection():
    """Kết nối database"""
    return pymysql.connect(**DB_CONFIG)

def create_users(num_users=1000):
    """Tạo users mới"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    user_ids = []
    
    try:
        # Lấy user_id lớn nhất hiện tại
        cursor.execute("SELECT MAX(id) FROM users")
        max_id = cursor.fetchone()[0] or 0
        
        print(f"Đang tạo {num_users} users mới...")
        
        for i in range(1, num_users + 1):
            user_id = max_id + i
            username = f"user_{user_id}"
            email = f"user{user_id}@example.com"
            first_name = fake.first_name()
            last_name = fake.last_name()
            phone = fake.phone_number()[:15]  # Giới hạn 15 ký tự
            address = fake.address()[:500]  # Giới hạn 500 ký tự
            gender_id = random.choice([1, 2, 3])  # 1=Nữ, 2=Nam, 3=Unisex
            
            # Password đã hash (bcrypt của "password123")
            password_hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
            
            sql = """
                INSERT INTO users (id, username, password, email, first_name, last_name, 
                                 phone_number, address, gender_id, active, created_date)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, 1, NOW())
            """
            
            cursor.execute(sql, (user_id, username, password_hash, email, first_name, 
                               last_name, phone, address, gender_id))
            user_ids.append(user_id)
        
        conn.commit()
        print(f"✓ Đã tạo {len(user_ids)} users: {user_ids}")
        return user_ids
        
    except Exception as e:
        conn.rollback()
        print(f"✗ Lỗi khi tạo users: {e}")
        return []
    finally:
        cursor.close()
        conn.close()

def get_available_product_properties():
    """Lấy danh sách product_properties có sẵn"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        sql = """
            SELECT pp.id, pp.product_id, pp.size, p.price, p.gender_id, p.color
            FROM product_properties pp
            JOIN products p ON pp.product_id = p.id
            WHERE pp.is_able = 1
            ORDER BY RAND()
        """
        cursor.execute(sql)
        return cursor.fetchall()
    finally:
        cursor.close()
        conn.close()

def create_transactions(user_ids, num_transactions=1000000):
    """Tạo giao dịch cho các users"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    # Lấy danh sách product_properties
    product_properties = get_available_product_properties()
    if not product_properties:
        print("✗ Không có product_properties nào!")
        return
    
    print(f"✓ Tìm thấy {len(product_properties)} product_properties có sẵn")
    
    # Lấy bill_id lớn nhất
    cursor.execute("SELECT MAX(id) FROM bills")
    max_bill_id = cursor.fetchone()[0] or 0
    
    # Địa chỉ mẫu
    addresses = [
        "123 Nguyễn Văn A, Quận 1, TP.HCM",
        "456 Lê Văn B, Quận 2, TP.HCM",
        "789 Trần Văn C, Quận 3, TP.HCM",
        "321 Phạm Văn D, Quận 4, TP.HCM",
        "654 Hoàng Văn E, Quận 5, TP.HCM",
    ]
    
    # Tạo một số sản phẩm "hot" (sẽ được mua nhiều hơn vượt trội)
    # Top 20-30 sản phẩm đầu sẽ là "hot" với số lượng mua cao hơn đáng kể
    hot_product_count = min(30, len(product_properties))
    hot_products = {}
    for pp in product_properties[:hot_product_count]:
        hot_products[pp[0]] = pp
    
    print(f"✓ Tạo {hot_product_count} sản phẩm 'hot' sẽ được mua nhiều hơn vượt trội")
    
    print(f"Đang tạo {num_transactions} giao dịch...")
    
    created = 0
    start_date = datetime.now() - timedelta(days=90)  # 3 tháng gần đây
    
    try:
        for i in range(num_transactions):
            # Chọn user ngẫu nhiên
            user_id = random.choice(user_ids)
            created_by = user_id
            
            # Tạo ngày ngẫu nhiên trong 3 tháng qua
            days_ago = random.randint(0, 90)
            created_date = start_date + timedelta(days=days_ago, 
                                                 hours=random.randint(0, 23),
                                                 minutes=random.randint(0, 59))
            
            # Status: 80% PURCHASE (1), 15% CREATED (0), 5% CANCEL (2)
            status_rand = random.random()
            if status_rand < 0.8:
                status = 1  # PURCHASE
            elif status_rand < 0.95:
                status = 0  # CREATED
            else:
                status = 2  # CANCEL
            
            # Chọn 1-3 sản phẩm cho mỗi giao dịch
            num_products = random.randint(1, 3)
            
            # 85% khả năng chọn sản phẩm "hot" (tăng từ 70% để tạo sự vượt trội)
            selected_products = []
            for _ in range(num_products):
                if random.random() < 0.85 and hot_products:
                    # Chọn từ hot products (ưu tiên cao)
                    pp_id = random.choice(list(hot_products.keys()))
                    selected_products.append(hot_products[pp_id])
                else:
                    # Chọn ngẫu nhiên từ tất cả sản phẩm
                    selected_products.append(random.choice(product_properties))
            
            # Tính tổng tiền
            total = sum(pp[3] * random.randint(1, 3) for pp in selected_products)  # price * amount
            
            # Tạo bill
            bill_id = max_bill_id + i + 1
            address = random.choice(addresses)
            phone = fake.phone_number()[:15]
            is_online = random.choice([True, False])
            
            sql_bill = """
                INSERT INTO bills (id, user_id, created_by, created_date, address, 
                                 phone_number, is_online_transaction, status, total)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(sql_bill, (bill_id, user_id, created_by, created_date, 
                                     address, phone, is_online, status, total))
            
            # Tạo product_bills
            for pp in selected_products:
                pp_id = pp[0]
                price = pp[3]
                amount = random.randint(1, 3)
                promotion_price = None
                promotion_id = None
                
                # 20% khả năng có giảm giá
                if random.random() < 0.2:
                    discount = random.randint(10, 30)  # 10-30%
                    promotion_price = price * (1 - discount / 100)
                    promotion_id = 1  # Giả sử có promotion_id = 1
                
                sql_pb = """
                    INSERT INTO product_bills (bill_id, product_properties_id, 
                                             amount, price, promotion_price, promotion_id)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """
                cursor.execute(sql_pb, (bill_id, pp_id, amount, price, 
                                       promotion_price, promotion_id))
            
            created += 1
            # Commit mỗi 1000 records để tối ưu performance
            if created % 1000 == 0:
                conn.commit()
                print(f"  Đã tạo {created:,}/{num_transactions:,} giao dịch... ({created*100//num_transactions}%)")
        
        conn.commit()
        print(f"✓ Đã tạo {created} giao dịch thành công!")
        
        # Thống kê
        cursor.execute("""
            SELECT 
                COUNT(*) as total_bills,
                COUNT(DISTINCT user_id) as total_users,
                SUM(total) as total_revenue,
                AVG(total) as avg_bill
            FROM bills
            WHERE id > %s
        """, (max_bill_id,))
        stats = cursor.fetchone()
        print(f"\n📊 Thống kê:")
        print(f"  - Tổng số giao dịch: {stats[0]}")
        print(f"  - Số users: {stats[1]}")
        print(f"  - Tổng doanh thu: {stats[2]:,.0f} VNĐ")
        print(f"  - Trung bình mỗi giao dịch: {stats[3]:,.0f} VNĐ")
        
    except Exception as e:
        conn.rollback()
        print(f"✗ Lỗi khi tạo giao dịch: {e}")
        import traceback
        traceback.print_exc()
    finally:
        cursor.close()
        conn.close()

def main():
    """Hàm chính"""
    print("=" * 60)
    print("TẠO DỮ LIỆU GIAO DỊCH CHO AI RECOMMENDATION SYSTEM")
    print("=" * 60)
    print("⚠️  LƯU Ý: Script này sẽ tạo 1 lần duy nhất:")
    print("   - 1000 users mới")
    print("   - 100,000 giao dịch phân bổ cho 1000 users")
    print("   - Không tạo liên tục, chỉ chạy 1 lần để có dữ liệu đủ lớn")
    print("   - ⚠️  Sẽ mất thời gian (có thể 5-10 phút)")
    print("=" * 60)
    
    confirm = input("\nBạn có chắc muốn tiếp tục? (yes/no): ")
    if confirm.lower() != 'yes':
        print("Đã hủy.")
        return
    
    # Bước 1: Tạo 1000 users
    user_ids = create_users(1000)
    if not user_ids:
        print("✗ Không thể tạo users. Dừng script.")
        return
    
    # Bước 2: Tạo 100,000 giao dịch phân bổ cho 1000 users
    # Mỗi user sẽ có khoảng 100 giao dịch
    create_transactions(user_ids, 100000)
    
    print("\n" + "=" * 60)
    print("HOÀN THÀNH!")
    print("=" * 60)

if __name__ == "__main__":
    main()

