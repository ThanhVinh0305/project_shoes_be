#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Tạo dữ liệu lượt xem và tìm kiếm cho users
- Ngưỡng gợi ý: >= 40 lượt xem và >= 40 lượt tìm kiếm
- Ưu tiên sản phẩm có thumbnail
- Tối ưu theo giới tính user
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

def generate_product_views(conn, user_id, user_gender_id, products_with_thumb, products_without_thumb):
    """Tạo lượt xem cho user, ưu tiên sản phẩm cùng giới tính và có thumbnail"""
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
    
    # Giới hạn số sản phẩm để xử lý nhanh hơn (chỉ lấy 30 sản phẩm đầu)
    selected_products = selected_products[:30]
    
    # Tạo lượt xem: ít nhất 40 lượt cho một số sản phẩm (để đủ ngưỡng gợi ý)
    # Chọn 5-8 sản phẩm để có >= 40 lượt xem
    num_high_view_products = random.randint(5, 8)
    high_view_products = random.sample(selected_products, min(num_high_view_products, len(selected_products)))
    
    views_data = []
    base_date = datetime.now() - timedelta(days=30)
    
    for product in selected_products:
        product_id = product[0]
        
        if product in high_view_products:
            # Sản phẩm có nhiều lượt xem (>= 40)
            view_count = random.randint(40, 100)
        else:
            # Sản phẩm có ít lượt xem (< 40)
            view_count = random.randint(1, 39)
        
        # Tạo nhiều lượt xem với thời gian khác nhau
        for i in range(view_count):
            view_date = base_date + timedelta(
                days=random.randint(0, 30),
                hours=random.randint(0, 23),
                minutes=random.randint(0, 59)
            )
            views_data.append((user_id, product_id, view_date))
    
    # Group by product để tính tổng view_count
    product_view_counts = {}
    for user_id, product_id, view_date in views_data:
        key = (user_id, product_id)
        product_view_counts[key] = product_view_counts.get(key, 0) + 1
    
    # Insert/Update vào database (upsert: nếu đã có thì update view_count)
    for (user_id, product_id), count in product_view_counts.items():
        cur.execute("""
            INSERT INTO product_views (user_id, product_id, view_count, last_viewed_date, created_date)
            VALUES (%s, %s, %s, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                view_count = view_count + %s,
                last_viewed_date = NOW()
        """, (user_id, product_id, count, count))
    
    conn.commit()
    # Chỉ in khi cần thiết để giảm I/O
    # print(f"  - Đã tạo {len(product_view_counts)} lượt xem cho user {user_id}")

def generate_search_histories(conn, user_id, products_with_thumb, products_without_thumb):
    """Tạo lịch sử tìm kiếm cho user"""
    cur = conn.cursor()
    
    # Lấy tên sản phẩm để làm keyword (giới hạn 20 sản phẩm để nhanh hơn)
    keywords = []
    for p in products_with_thumb[:20]:  # Lấy 20 sản phẩm có thumbnail
        product_name = p[1]
        # Tách từ khóa từ tên sản phẩm (ví dụ: "Nike Air Max" -> ["Nike", "Air", "Max"])
        words = product_name.split()
        for word in words[:1]:  # Chỉ lấy 1 từ đầu để giảm số lượng
            if len(word) > 3:  # Bỏ qua từ quá ngắn
                keywords.append(word)
    
    # Thêm một số keyword phổ biến
    common_keywords = ["giày", "sneaker", "thể thao", "chạy bộ", "basketball", "running"]
    keywords.extend(common_keywords)
    keywords = list(set(keywords))  # Remove duplicates
    keywords = keywords[:15]  # Giới hạn tối đa 15 keywords
    
    # Tạo lịch sử tìm kiếm: ít nhất 40 lượt cho một số keyword (để đủ ngưỡng gợi ý)
    num_high_search_keywords = random.randint(3, 5)
    high_search_keywords = random.sample(keywords, min(num_high_search_keywords, len(keywords)))
    
    searches_data = []
    base_date = datetime.now() - timedelta(days=30)
    
    for keyword in keywords:
        if keyword in high_search_keywords:
            # Keyword có nhiều lượt tìm kiếm (>= 40)
            search_count = random.randint(40, 60)
        else:
            # Keyword có ít lượt tìm kiếm (< 40)
            search_count = random.randint(1, 39)
        
        # Tạo batch insert thay vì insert từng dòng
        for i in range(search_count):
            search_date = base_date + timedelta(
                days=random.randint(0, 30),
                hours=random.randint(0, 23),
                minutes=random.randint(0, 59)
            )
            result_count = random.randint(5, 50)
            searches_data.append((user_id, keyword, result_count, search_date))
    
    # Batch insert vào database (nhanh hơn)
    if searches_data:
        cur.executemany("""
            INSERT INTO search_histories (user_id, search_keyword, result_count, created_date)
            VALUES (%s, %s, %s, %s)
        """, searches_data)
    
    conn.commit()
    # Chỉ in khi cần thiết để giảm I/O
    # print(f"  - Đã tạo {len(searches_data)} lượt tìm kiếm cho user {user_id}")

def main():
    print("🚀 Bắt đầu tạo dữ liệu lượt xem và tìm kiếm...")
    
    conn = connect_db()
    
    try:
        # Lấy danh sách users
        users = get_users_with_gender(conn)
        print(f"📊 Tìm thấy {len(users)} users có gender_id")
        
        # Lấy danh sách sản phẩm
        products_with_thumb, products_without_thumb = get_products_with_thumbnail(conn)
        print(f"📦 Tìm thấy {len(products_with_thumb)} sản phẩm có thumbnail, {len(products_without_thumb)} sản phẩm không có thumbnail")
        
        # Xóa dữ liệu cũ (optional - comment nếu muốn giữ lại)
        cur = conn.cursor()
        cur.execute("DELETE FROM product_views")
        cur.execute("DELETE FROM search_histories")
        conn.commit()
        print("🗑️  Đã xóa dữ liệu cũ")
        
        # Tối ưu: Chỉ xử lý 100 users đầu tiên để test nhanh
        # Bỏ comment dòng dưới nếu muốn xử lý tất cả
        # users = users[:100]  # Test với 100 users
        
        # Tạo dữ liệu cho từng user
        for idx, (user_id, user_gender_id) in enumerate(users, 1):
            if idx % 50 == 0:  # In progress mỗi 50 users
                print(f"\n[{idx}/{len(users)}] Đang xử lý...")
            
            # Tạo lượt xem
            generate_product_views(conn, user_id, user_gender_id, products_with_thumb, products_without_thumb)
            
            # Tạo lịch sử tìm kiếm
            generate_search_histories(conn, user_id, products_with_thumb, products_without_thumb)
        
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
            SELECT COUNT(*) 
            FROM product_views 
            WHERE view_count >= 40
        """)
        high_view_count = cur.fetchone()[0]
        print(f"\n✅ Sản phẩm có >= 40 lượt xem: {high_view_count}")
        
        # Kiểm tra số keyword/user có >= 40 lượt tìm kiếm
        cur.execute("""
            SELECT COUNT(*) 
            FROM (
                SELECT user_id, search_keyword, COUNT(*) as cnt
                FROM search_histories
                GROUP BY user_id, search_keyword
                HAVING cnt >= 40
            ) as t
        """)
        high_search_count = cur.fetchone()[0]
        print(f"✅ Keywords có >= 40 lượt tìm kiếm: {high_search_count}")
        
    finally:
        conn.close()

if __name__ == "__main__":
    main()

