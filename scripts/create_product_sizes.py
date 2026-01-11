#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Tạo sizes cho products theo gender
- Nam (gender_id = 1) & Unisex (gender_id = 2): size 36-45
- Nữ (gender_id = 0): size 35-40
"""

import pymysql

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'tuanhung1999',
    'database': 'e-commerce',
    'charset': 'utf8mb4'
}

def get_products():
    """Lấy danh sách products cần tạo sizes"""
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    
    try:
        # Lấy products từ ID 1-200 chưa có sizes
        sql = """
            SELECT p.id, p.code, p.gender_id 
            FROM products p
            WHERE p.id <= 200 
            AND NOT EXISTS (
                SELECT 1 FROM product_properties pp WHERE pp.product_id = p.id
            )
            ORDER BY p.id
        """
        cursor.execute(sql)
        products = cursor.fetchall()
        return products
    finally:
        cursor.close()
        conn.close()

def create_sizes_for_products(products):
    """Tạo sizes cho từng product theo gender"""
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    
    total_sizes = 0
    
    try:
        for product_id, code, gender_id in products:
            # Xác định size range theo gender
            if gender_id == 0:  # Nữ
                sizes = range(35, 41)  # 35-40
            else:  # Nam (1) hoặc Unisex (2)
                sizes = range(36, 46)  # 36-45
            
            # Insert sizes
            for size in sizes:
                cursor.execute(
                    "INSERT INTO product_properties (product_id, size, is_able) VALUES (%s, %s, 1)",
                    (product_id, size)
                )
                total_sizes += 1
            
            if product_id % 20 == 0:
                print(f"  Processed {product_id} products...")
        
        conn.commit()
        print(f"✓ Successfully created {total_sizes} sizes for {len(products)} products!")
        
        # Verify
        cursor.execute("""
            SELECT p.code, p.gender_id, COUNT(pp.id) as size_count, 
                   MIN(pp.size) as min_size, MAX(pp.size) as max_size
            FROM products p
            JOIN product_properties pp ON p.id = pp.product_id
            WHERE p.id IN (1, 2, 3)
            GROUP BY p.id
        """)
        
        print("\n✓ Sample verification:")
        for row in cursor.fetchall():
            gender_name = "Nữ" if row[1] == 0 else ("Nam" if row[1] == 1 else "Unisex")
            print(f"  {row[0]} ({gender_name}): {row[2]} sizes (from {row[3]} to {row[4]})")
        
    except Exception as e:
        print(f"✗ Error: {e}")
        conn.rollback()
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    print("🔄 Starting size creation...")
    
    products = get_products()
    print(f"✓ Found {len(products)} products without sizes")
    
    # Count by gender
    female = sum(1 for p in products if p[2] == 0)
    male = sum(1 for p in products if p[2] == 1)
    unisex = sum(1 for p in products if p[2] == 2)
    
    print(f"  - Nữ (35-40): {female} products")
    print(f"  - Nam (36-45): {male} products")
    print(f"  - Unisex (36-45): {unisex} products")
    
    print("\n🔄 Creating sizes...")
    create_sizes_for_products(products)
    print("✅ Done!")
