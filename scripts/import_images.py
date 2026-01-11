#!/usr/bin/env python3
"""
Script import ảnh từ folder vào MinIO và cập nhật database
Sử dụng: python3 scripts/import_images.py <folder_path>
"""

import os
import sys
import re
import pymysql
from minio import Minio
from minio.error import S3Error
from pathlib import Path

# Cấu hình
MINIO_URL = "localhost:9000"
MINIO_ACCESS_KEY = "kWu8WEj6n28m6CjqUpd2"
MINIO_SECRET_KEY = "mqrPrpYnSIw8TEP0Je6IicjpfKU5MjcHmOWvQ1LC"
MINIO_BUCKET = "products"
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'tuanhung1999',
    'database': 'e-commerce',
    'charset': 'utf8mb4'
}

def connect_minio():
    """Kết nối MinIO"""
    return Minio(
        MINIO_URL,
        access_key=MINIO_ACCESS_KEY,
        secret_key=MINIO_SECRET_KEY,
        secure=False
    )

def connect_db():
    """Kết nối MySQL"""
    return pymysql.connect(**DB_CONFIG)

def parse_filename(filename):
    """
    Parse tên file để lấy product code và loại ảnh
    Format: 
    - NIKE-AM90-001_thumbnail.jpg -> (NIKE-AM90-001, 'thumbnail')
    - NIKE-AM90-001_1.jpg -> (NIKE-AM90-001, 'detail', 1)
    - NIKE-AM90-001.jpg -> (NIKE-AM90-001, 'detail', 1)
    """
    name_without_ext = Path(filename).stem
    ext = Path(filename).suffix
    
    # Kiểm tra thumbnail
    if name_without_ext.endswith('_thumbnail'):
        product_code = name_without_ext.replace('_thumbnail', '')
        return product_code, 'thumbnail', None
    
    # Kiểm tra ảnh chi tiết có số
    match = re.match(r'^(.+)_(\d+)$', name_without_ext)
    if match:
        product_code = match.group(1)
        image_num = int(match.group(2))
        return product_code, 'detail', image_num
    
    # Chỉ có product code (ảnh đầu tiên)
    return name_without_ext, 'detail', 1

def upload_to_minio(client, file_path, object_name):
    """Upload file vào MinIO"""
    try:
        client.fput_object(MINIO_BUCKET, object_name, file_path)
        url = f"http://{MINIO_URL}/{MINIO_BUCKET}/{object_name}"
        return url
    except S3Error as e:
        print(f"  ✗ Lỗi upload: {e}")
        return None

def update_database(cursor, product_code, image_url, image_type, image_num=None):
    """Cập nhật database"""
    try:
        if image_type == 'thumbnail':
            # Cập nhật thumbnail_img
            sql = "UPDATE products SET thumbnail_img = %s WHERE code = %s"
            cursor.execute(sql, (image_url, product_code))
        else:
            # Thêm vào product_attachments
            # Lấy product_id từ code
            cursor.execute("SELECT id FROM products WHERE code = %s", (product_code,))
            result = cursor.fetchone()
            if result:
                product_id = result[0]
                # Kiểm tra xem đã có ảnh số này chưa
                cursor.execute(
                    "SELECT id FROM product_attachments WHERE product_id = %s ORDER BY id LIMIT 1 OFFSET %s",
                    (product_id, (image_num or 1) - 1)
                )
                existing = cursor.fetchone()
                
                if existing:
                    # Update ảnh hiện có
                    sql = "UPDATE product_attachments SET attachment = %s WHERE id = %s"
                    cursor.execute(sql, (image_url, existing[0]))
                else:
                    # Insert ảnh mới
                    sql = "INSERT INTO product_attachments (product_id, attachment) VALUES (%s, %s)"
                    cursor.execute(sql, (product_id, image_url))
            else:
                print(f"  ⚠ Không tìm thấy product với code: {product_code}")
                return False
        return True
    except Exception as e:
        print(f"  ✗ Lỗi database: {e}")
        return False

def main():
    if len(sys.argv) < 2:
        print("Sử dụng: python3 scripts/import_images.py <folder_path>")
        sys.exit(1)
    
    folder_path = sys.argv[1]
    
    if not os.path.isdir(folder_path):
        print(f"Error: Folder không tồn tại: {folder_path}")
        sys.exit(1)
    
    # Kết nối
    print("=== Kết nối MinIO và Database ===")
    try:
        minio_client = connect_minio()
        db_conn = connect_db()
        db_cursor = db_conn.cursor()
    except Exception as e:
        print(f"Error: Không thể kết nối - {e}")
        sys.exit(1)
    
    # Tìm tất cả file ảnh
    image_extensions = {'.jpg', '.jpeg', '.png', '.webp', '.avif'}
    image_files = [
        f for f in os.listdir(folder_path)
        if Path(f).suffix.lower() in image_extensions
    ]
    
    print(f"\nTìm thấy {len(image_files)} file ảnh\n")
    
    # Xử lý từng file
    success_count = 0
    error_count = 0
    
    for filename in sorted(image_files):
        file_path = os.path.join(folder_path, filename)
        print(f"Đang xử lý: {filename}")
        
        # Parse tên file
        product_code, image_type, image_num = parse_filename(filename)
        print(f"  → Product: {product_code}, Type: {image_type}, Num: {image_num}")
        
        # Upload vào MinIO
        object_name = f"products/{filename}"
        image_url = upload_to_minio(minio_client, file_path, object_name)
        
        if image_url:
            print(f"  ✓ Upload thành công: {image_url}")
            
            # Cập nhật database
            if update_database(db_cursor, product_code, image_url, image_type, image_num):
                print(f"  ✓ Database đã cập nhật")
                success_count += 1
            else:
                error_count += 1
        else:
            error_count += 1
        
        print()
    
    # Commit và đóng kết nối
    db_conn.commit()
    db_cursor.close()
    db_conn.close()
    
    print("=== Hoàn thành ===")
    print(f"Thành công: {success_count} file")
    print(f"Lỗi: {error_count} file")

if __name__ == "__main__":
    main()

