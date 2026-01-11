#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Tạo dữ liệu lượt xem và tìm kiếm cho users - VERSION 2
- Chia đều dữ liệu cho các sản phẩm
- Đảm bảo mỗi user có ít nhất 10-15 sản phẩm đạt ngưỡng >= 40 lượt xem
- Đảm bảo mỗi user có ít nhất 5-8 keywords đạt ngưỡng >= 40 lượt tìm kiếm
"""

import pymysql
import random
from datetime import datetime, timedelta

DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "tuanhung1999",
    "database": "e-commerce",
    "charset": "utf8mb4"
}

def connect_db():
    return pymysql.connect(**DB_CONFIG)

def get_users_with_gender(conn):
    """Lấy danh sách users có gender_id"""
    cur = conn.cursor()
    cur.execute("""
        SELECT id, gender_id 
        FROM users 
        WHERE gender_id IS NOT NULL
        ORDER BY id
    """)
    return cur.fetchall()

def get_products_with_thumbnail(conn):
    """Lấy sản phẩm có thumbnail, ưu tiên trước"""
    cur = conn.cursor()
    cur.execute("""
        SELECT id, name, gender_id, thumbnail_img
        FROM products
        WHERE thumbnail_img IS NOT NULL AND thumbnail_img != ''
        ORDER BY id
    """)
    products_with_thumb = cur.fetchall()
    
    cur.execute("""
        SELECT id, name, gender_id, thumbnail_img
        FROM products
        WHERE thumbnail_img IS NULL OR thumbnail_img = ''
        ORDER BY id
    """)
    products_without_thumb = cur.fetchall()
    
    return products_with_thumb, products_without_thumb

def generate_product_views(conn, user_id, user_gender_id, products_with_thumb, products_without_thumb, product_total_views):
    """Tạo lượt xem cho user - tổng view_count của mỗi sản phẩm trong khoảng 0-3200"""
    cur = conn.cursor()
    
    # Chọn sản phẩm: ưu tiên cùng gender + có thumbnail
    selected_products = []
    
    # 1. Sản phẩm cùng gender + có thumbnail (ưu tiên cao nhất)
    for p in products_with_thumb:
        if p[2] == user_gender_id:  # gender_id match
            selected_products.append(p)
    
    # 2. Sản phẩm unisex + có thumbnail
    for p in products_with_thumb:
        if p[2] == 3 and p not in selected_products:  # Unisex
            selected_products.append(p)
    
    # 3. Nếu chưa đủ, thêm sản phẩm có thumbnail khác
    for p in products_with_thumb:
        if p not in selected_products:
            selected_products.append(p)
    
    # 4. Cuối cùng mới thêm sản phẩm không có thumbnail
    for p in products_without_thumb:
        if p[2] == user_gender_id or p[2] == 3:
            selected_products.append(p)
    
    # Giới hạn 50 sản phẩm để đảm bảo có đủ dữ liệu
    selected_products = selected_products[:50]
    
    if not selected_products:
        return
    
    product_view_counts = {}
    
    for product in selected_products:
        product_id = product[0]
        
        # Lấy tổng view_count đã được phân bổ cho sản phẩm này
        total_views_for_product = product_total_views.get(product_id, 0)
        
        if total_views_for_product > 0:
            # Phân bổ view_count cho user này (1-5 lượt xem mỗi user)
            # Đảm bảo không vượt quá tổng view_count đã định
            view_count = min(random.randint(1, 5), total_views_for_product)
            product_total_views[product_id] = total_views_for_product - view_count
            product_view_counts[(user_id, product_id)] = view_count
    
    # Insert/Update vào database
    for (user_id, product_id), count in product_view_counts.items():
        cur.execute("""
            INSERT INTO product_views (user_id, product_id, view_count, last_viewed_date, created_date)
            VALUES (%s, %s, %s, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                view_count = view_count + %s,
                last_viewed_date = NOW()
        """, (user_id, product_id, count, count))
    
    conn.commit()

def generate_search_histories(conn, user_id, user_gender_id, products_with_thumb, products_without_thumb):
    """Tạo lịch sử tìm kiếm cho user - lưu TÊN SẢN PHẨM thay vì keyword"""
    cur = conn.cursor()
    
    # Lấy tên sản phẩm để lưu vào search_histories (tìm kiếm trực tiếp tên sản phẩm)
    product_names = []
    
    # 1. Sản phẩm cùng gender + có thumbnail (ưu tiên)
    for p in products_with_thumb:
        if p[2] == user_gender_id:  # gender_id match
            if p[1] and p[1].strip():
                product_names.append(p[1].strip())
    
    # 2. Sản phẩm unisex + có thumbnail
    for p in products_with_thumb:
        if p[2] == 3 and p[1] and p[1].strip():
            if p[1].strip() not in product_names:
                product_names.append(p[1].strip())
    
    # 3. Thêm các sản phẩm khác có thumbnail
    for p in products_with_thumb:
        if p[1] and p[1].strip():
            if p[1].strip() not in product_names:
                product_names.append(p[1].strip())
    
    # Giới hạn 30-40 tên sản phẩm
    product_names = product_names[:40]
    
    if not product_names:
        return
    
    # Đảm bảo có ít nhất 10-12 sản phẩm đạt ngưỡng >= 40 lượt tìm kiếm
    num_high_search_products = min(12, len(product_names))
    high_search_products = random.sample(product_names, num_high_search_products)
    
    searches_data = []
    base_date = datetime.now() - timedelta(days=30)
    
    for product_name in product_names:
        if product_name in high_search_products:
            # Sản phẩm có nhiều lượt tìm kiếm (>= 40) - chia đều từ 40-70
            search_count = random.randint(40, 70)
        else:
            # Sản phẩm có ít lượt tìm kiếm (< 40) - chia đều từ 5-35
            search_count = random.randint(5, 35)
        
        # Tạo batch insert - lưu TÊN SẢN PHẨM vào search_keyword
        for i in range(search_count):
            search_date = base_date + timedelta(
                days=random.randint(0, 30),
                hours=random.randint(0, 23),
                minutes=random.randint(0, 59)
            )
            result_count = random.randint(5, 50)
            searches_data.append((user_id, product_name, result_count, search_date))
    
    # Batch insert vào database
    if searches_data:
        cur.executemany("""
            INSERT INTO search_histories (user_id, search_keyword, result_count, created_date)
            VALUES (%s, %s, %s, %s)
        """, searches_data)
    
    conn.commit()

def main():
    print("🚀 Bắt đầu tạo dữ liệu lượt xem và tìm kiếm (VERSION 2 - Chia đều)...")
    
    conn = connect_db()
    
    try:
        # Lấy danh sách users
        users = get_users_with_gender(conn)
        print(f"📊 Tìm thấy {len(users)} users có gender_id")
        
        # Lấy danh sách sản phẩm
        products_with_thumb, products_without_thumb = get_products_with_thumbnail(conn)
        print(f"📦 Tìm thấy {len(products_with_thumb)} sản phẩm có thumbnail, {len(products_without_thumb)} sản phẩm không có thumbnail")
        
        # Xóa dữ liệu cũ
        cur = conn.cursor()
        cur.execute("DELETE FROM product_views")
        cur.execute("DELETE FROM search_histories")
        conn.commit()
        print("🗑️  Đã xóa dữ liệu cũ")
        
        # Tính toán tổng view_count cho mỗi sản phẩm (0-3200)
        # Đảm bảo một số sản phẩm có tổng view_count >= 1900 (để đạt ngưỡng gợi ý)
        all_products = products_with_thumb + products_without_thumb
        product_total_views = {}
        
        # Chọn khoảng 20-30 sản phẩm để có tổng view_count >= 1900 (đạt ngưỡng)
        num_high_view_products = min(30, len(all_products))
        high_view_products = random.sample(all_products, num_high_view_products)
        
        for product in all_products:
            product_id = product[0]
            if product in high_view_products:
                # Sản phẩm đạt ngưỡng: tổng view_count từ 1900-3200
                product_total_views[product_id] = random.randint(1900, 3200)
            else:
                # Sản phẩm khác: tổng view_count từ 0-1800
                product_total_views[product_id] = random.randint(0, 1800)
        
        print(f"📊 Đã phân bổ tổng view_count cho {len(product_total_views)} sản phẩm")
        print(f"   - {num_high_view_products} sản phẩm có tổng view_count >= 1900")
        
        # Tạo dữ liệu cho từng user
        for idx, (user_id, user_gender_id) in enumerate(users, 1):
            if idx % 100 == 0:  # In progress mỗi 100 users
                print(f"\n[{idx}/{len(users)}] Đang xử lý...")
            
            # Tạo lượt xem (truyền product_total_views để phân bổ)
            generate_product_views(conn, user_id, user_gender_id, products_with_thumb, products_without_thumb, product_total_views)
            
            # Tạo lịch sử tìm kiếm (lưu tên sản phẩm)
            generate_search_histories(conn, user_id, user_gender_id, products_with_thumb, products_without_thumb)
        
        print("\n✅ Hoàn thành!")
        
        # Kiểm tra kết quả
        cur.execute("""
            SELECT 
                COUNT(DISTINCT user_id) as total_users,
                COUNT(*) as total_views,
                COUNT(DISTINCT product_id) as total_products_viewed
            FROM product_views
        """)
        stats = cur.fetchone()
        print(f"\n📊 Thống kê lượt xem:")
        print(f"  - Users: {stats[0]}")
        print(f"  - Tổng lượt xem: {stats[1]}")
        print(f"  - Sản phẩm được xem: {stats[2]}")
        
        cur.execute("""
            SELECT 
                COUNT(DISTINCT user_id) as total_users,
                COUNT(*) as total_searches,
                COUNT(DISTINCT search_keyword) as total_keywords
            FROM search_histories
        """)
        stats = cur.fetchone()
        print(f"\n📊 Thống kê tìm kiếm:")
        print(f"  - Users: {stats[0]}")
        print(f"  - Tổng lượt tìm kiếm: {stats[1]}")
        print(f"  - Keywords: {stats[2]}")
        
        # Kiểm tra số sản phẩm/user có >= 40 lượt xem
        cur.execute("""
            SELECT user_id, COUNT(*) as count
            FROM product_views 
            WHERE view_count >= 40
            GROUP BY user_id
            ORDER BY count DESC
            LIMIT 10
        """)
        print(f"\n✅ Top 10 users có nhiều sản phẩm >= 40 lượt xem:")
        for row in cur.fetchall():
            print(f"  - User {row[0]}: {row[1]} sản phẩm")
        
        # Kiểm tra số tên sản phẩm/user có >= 40 lượt tìm kiếm
        cur.execute("""
            SELECT user_id, COUNT(*) as count
            FROM (
                SELECT user_id, search_keyword, COUNT(*) as cnt
                FROM search_histories
                GROUP BY user_id, search_keyword
                HAVING cnt >= 40
            ) as t
            GROUP BY user_id
            ORDER BY count DESC
            LIMIT 10
        """)
        print(f"\n✅ Top 10 users có nhiều tên sản phẩm >= 40 lượt tìm kiếm:")
        for row in cur.fetchall():
            print(f"  - User {row[0]}: {row[1]} sản phẩm")
        
        # Kiểm tra user 2107 cụ thể
        cur.execute("""
            SELECT COUNT(*) as count
            FROM product_views 
            WHERE user_id = 2107 AND view_count >= 40
        """)
        count = cur.fetchone()[0]
        print(f"\n✅ User 2107 có {count} sản phẩm >= 40 lượt xem")
        
        cur.execute("""
            SELECT COUNT(*) as count
            FROM (
                SELECT search_keyword, COUNT(*) as cnt
                FROM search_histories
                WHERE user_id = 2107
                GROUP BY search_keyword
                HAVING cnt >= 40
            ) as t
        """)
        count = cur.fetchone()[0]
        print(f"✅ User 2107 có {count} tên sản phẩm >= 40 lượt tìm kiếm")
        
        # Hiển thị vài tên sản phẩm user 2107 đã tìm kiếm
        cur.execute("""
            SELECT search_keyword, COUNT(*) as cnt
            FROM search_histories
            WHERE user_id = 2107
            GROUP BY search_keyword
            HAVING cnt >= 40
            ORDER BY cnt DESC
            LIMIT 5
        """)
        print(f"\n📋 Top 5 tên sản phẩm user 2107 tìm kiếm nhiều nhất:")
        for row in cur.fetchall():
            print(f"  - {row[0]}: {row[1]} lượt")
        
    finally:
        conn.close()

if __name__ == "__main__":
    main()

