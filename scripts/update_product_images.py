#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Update thumbnail và detail images cho products từ file txt
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
    """Parse file DANH_SACH_200_SAN_PHAM.txt để lấy thumbnail URLs"""
    products = {}
    
    with open('../DANH_SACH_200_SAN_PHAM.txt', 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    current_code = None
    current_id = None
    
    for line in lines:
        line = line.strip()
        
        # Tìm dòng bắt đầu với ID:
        if line.startswith('ID:'):
            # Extract ID và Code
            id_match = re.search(r'ID:\s*(\d+)', line)
            code_match = re.search(r'Mã:\s*([\w-]+)', line)
            
            if id_match and code_match:
                current_id = int(id_match.group(1))
                current_code = code_match.group(1)
                
                # Tìm URL trong cùng dòng
                url_match = re.search(r'(https://authentic-shoes\.com[^\s|]+)', line)
                if url_match:
                    products[current_code] = {
                        'id': current_id,
                        'thumbnail': url_match.group(1).strip()
                    }
    
    print(f"✓ Parsed {len(products)} products with thumbnails")
    return products

def parse_detail_images():
    """Parse file Detail_image.txt để lấy detail image URLs"""
    detail_images = {}
    
    with open('../Detail_image.txt', 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    current_code = None
    for line in lines:
        line = line.strip()
        
        # Dòng chứa code sản phẩm (format: "Product Name – CODE")
        if '–' in line and not line.startswith('http'):
            parts = line.split('–')
            if len(parts) == 2:
                current_code = parts[1].strip()
                detail_images[current_code] = []
        
        # Dòng chứa URL detail image
        elif line.startswith('http') and current_code:
            detail_images[current_code].append(line)
    
    print(f"✓ Parsed detail images for {len(detail_images)} products")
    return detail_images

def update_database(products, detail_images):
    """Update database với thumbnail và detail images"""
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    
    updated_count = 0
    
    try:
        for code, data in products.items():
            thumbnail_url = data['thumbnail']
            
            # Update thumbnail
            cursor.execute(
                "UPDATE products SET thumbnail_img = %s WHERE code = %s",
                (thumbnail_url, code)
            )
            
            # Thêm detail images vào product_attachments nếu có
            if code in detail_images and detail_images[code]:
                # Lấy product_id
                cursor.execute("SELECT id FROM products WHERE code = %s", (code,))
                result = cursor.fetchone()
                
                if result:
                    product_id = result[0]
                    
                    # Xóa attachments cũ
                    cursor.execute("DELETE FROM product_attachments WHERE product_id = %s", (product_id,))
                    
                    # Insert detail images mới
                    for img_url in detail_images[code]:
                        cursor.execute(
                            "INSERT INTO product_attachments (product_id, attachment) VALUES (%s, %s)",
                            (product_id, img_url)
                        )
            
            updated_count += 1
            if updated_count % 20 == 0:
                print(f"  Updated {updated_count}/{len(products)} products...")
        
        conn.commit()
        print(f"✓ Successfully updated {updated_count} products!")
        
        # Verify
        cursor.execute("SELECT COUNT(*) FROM products WHERE thumbnail_img LIKE 'https://authentic-shoes.com%'")
        valid_thumbnails = cursor.fetchone()[0]
        print(f"✓ Products with valid thumbnails: {valid_thumbnails}")
        
        cursor.execute("SELECT COUNT(*) FROM product_attachments")
        total_attachments = cursor.fetchone()[0]
        print(f"✓ Total detail images: {total_attachments}")
        
    except Exception as e:
        print(f"✗ Error: {e}")
        conn.rollback()
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    print("🔄 Starting image update process...")
    products = parse_products_file()
    detail_images = parse_detail_images()
    update_database(products, detail_images)
    print("✅ Done!")
