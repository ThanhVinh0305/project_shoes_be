#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Import đầy đủ dữ liệu sản phẩm từ DANH_SACH_200_SAN_PHAM.txt vào database
"""

import pymysql
import re

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'tuanhung1999',
    'database': 'e-commerce',
    'charset': 'utf8mb4'
}

def parse_products_file():
    """Parse file DANH_SACH_200_SAN_PHAM.txt đầy đủ"""
    products = []
    
    with open('../DANH_SACH_200_SAN_PHAM.txt', 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Split by ID:
    blocks = re.split(r'\n(?=ID:)', content)
    
    for block in blocks:
        if not block.strip() or not block.startswith('ID:'):
            continue
        
        lines = block.strip().split('\n')
        first_line = lines[0]
        
        # Extract từ dòng đầu tiên
        id_match = re.search(r'ID:\s*(\d+)', first_line)
        name_match = re.search(r'Tên:\s*([^|]+)', first_line)
        code_match = re.search(r'Mã:\s*([\w-]+)', first_line)
        gender_match = re.search(r'Giới tính:\s*(\d+)', first_line)
        color_match = re.search(r'Màu:\s*([^|]+)', first_line)
        price_match = re.search(r'Giá:\s*(\d+)', first_line)
        url_match = re.search(r'(https://authentic-shoes\.com[^\s|]+)', first_line)
        
        if not (id_match and code_match):
            continue
        
        product_id = int(id_match.group(1))
        code = code_match.group(1).strip()
        name = name_match.group(1).strip() if name_match else code
        
        # Gender mapping cho PRODUCTS: 0=Nữ, 1=Nam, 2=Unisex
        gender_id = int(gender_match.group(1)) if gender_match else 2
        
        # Color - lấy phần sau "Màu:" và trước "|" hoặc "Giá:"
        color = None
        if color_match:
            color = color_match.group(1).strip()
        elif gender_match:
            # Nếu không có "Màu:" explicit, lấy text giữa gender và price
            temp = re.search(r'Giới tính:\s*\d+\s*\|\s*([^|]+?)\s*\|?\s*Giá:', first_line)
            if temp:
                color = temp.group(1).strip()
        
        if not color or color == '':
            color = 'Unknown'
        
        price = int(price_match.group(1)) if price_match else 0
        thumbnail_url = url_match.group(1).strip() if url_match else None
        
        # Description là các dòng còn lại (bỏ dòng đầu)
        description_lines = []
        for line in lines[1:]:
            line = line.strip()
            if line and not line.startswith('ID:'):
                description_lines.append(line)
        
        description = ' '.join(description_lines).strip()
        if not description:
            description = f"Giày thể thao {name}"
        
        # Limit description length
        if len(description) > 2000:
            description = description[:1997] + '...'
        
        products.append({
            'id': product_id,
            'code': code,
            'name': name,
            'gender_id': gender_id,
            'color': color,
            'price': price,
            'thumbnail_url': thumbnail_url,
            'description': description
        })
    
    print(f"✓ Parsed {len(products)} products")
    return products

def update_database(products):
    """Update database với thông tin đầy đủ"""
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    
    updated_count = 0
    not_found = []
    
    try:
        for product in products:
            # Check if product exists
            cursor.execute("SELECT id FROM products WHERE code = %s", (product['code'],))
            result = cursor.fetchone()
            
            if not result:
                not_found.append(product['code'])
                continue
            
            # Update product info
            sql = """
                UPDATE products 
                SET name = %s, 
                    color = %s, 
                    gender_id = %s, 
                    price = %s, 
                    description = %s
                WHERE code = %s
            """
            
            cursor.execute(sql, (
                product['name'],
                product['color'],
                product['gender_id'],
                product['price'],
                product['description'],
                product['code']
            ))
            
            updated_count += 1
            
            if updated_count % 20 == 0:
                print(f"  Updated {updated_count}/{len(products)} products...")
        
        conn.commit()
        print(f"✓ Successfully updated {updated_count} products!")
        
        if not_found:
            print(f"⚠ {len(not_found)} products not found in database: {not_found[:10]}")
        
        # Verify some products
        cursor.execute("""
            SELECT code, name, color, gender_id, price 
            FROM products 
            WHERE code IN ('NIKE-AM90-001', 'NIKE-AF1-001', 'ADIDAS-UB-001')
        """)
        
        print("\n✓ Sample verification:")
        for row in cursor.fetchall():
            print(f"  {row[0]}: {row[1]}, Color={row[2]}, Gender={row[3]}, Price={row[4]}")
        
    except Exception as e:
        print(f"✗ Error: {e}")
        conn.rollback()
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    print("🔄 Starting full product data import...")
    products = parse_products_file()
    
    # Show first product as sample
    if products:
        p = products[0]
        print(f"\n📋 Sample product (ID={p['id']}):")
        print(f"  Code: {p['code']}")
        print(f"  Name: {p['name']}")
        print(f"  Color: {p['color']}")
        print(f"  Gender: {p['gender_id']}")
        print(f"  Price: {p['price']:,} VNĐ")
        print(f"  Description: {p['description'][:100]}...")
    
    print("\n🔄 Updating database...")
    update_database(products)
    print("✅ Done!")
