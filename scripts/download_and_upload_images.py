#!/usr/bin/env python3
"""
Script để download ảnh từ external URLs và upload lên MinIO
Sử dụng: python3 scripts/download_and_upload_images.py Detail_image.txt
"""

import sys
import os
import re
import requests
from pathlib import Path
from urllib.parse import urlparse
import pymysql
from minio import Minio
from minio.error import S3Error
import tempfile

# Cấu hình MinIO
MINIO_URL = os.getenv("MINIO_URL", "localhost:9000")
# Thử credentials từ application.properties trước, nếu không thì dùng default
MINIO_ACCESS_KEY = os.getenv("MINIO_ACCESS_KEY", "minioadmin")
MINIO_SECRET_KEY = os.getenv("MINIO_SECRET_KEY", "minioadmin")
MINIO_BUCKET = "products"
MINIO_SECURE = False

# Cấu hình Database
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
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
        secure=MINIO_SECURE
    )

def connect_db():
    """Kết nối Database"""
    return pymysql.connect(**DB_CONFIG)

def ensure_bucket(client, bucket_name):
    """Đảm bảo bucket tồn tại"""
    try:
        if not client.bucket_exists(bucket_name):
            client.make_bucket(bucket_name)
            print(f"✓ Đã tạo bucket: {bucket_name}")
        else:
            print(f"✓ Bucket đã tồn tại: {bucket_name}")
    except S3Error as e:
        print(f"✗ Lỗi tạo bucket: {e}")
        raise

def download_image(url, temp_dir):
    """Download ảnh từ URL"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        response = requests.get(url, headers=headers, timeout=30, stream=True)
        response.raise_for_status()
        
        # Lấy extension từ URL hoặc Content-Type
        parsed_url = urlparse(url)
        filename = os.path.basename(parsed_url.path)
        
        if not filename or '.' not in filename:
            content_type = response.headers.get('Content-Type', '')
            if 'jpeg' in content_type or 'jpg' in content_type:
                ext = '.jpg'
            elif 'png' in content_type:
                ext = '.png'
            elif 'webp' in content_type:
                ext = '.webp'
            else:
                ext = '.jpg'  # default
            filename = f"image{ext}"
        
        file_path = os.path.join(temp_dir, filename)
        
        with open(file_path, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                f.write(chunk)
        
        return file_path, filename
    except Exception as e:
        print(f"  ✗ Lỗi download: {e}")
        return None, None

def upload_to_minio(client, file_path, object_name):
    """Upload file lên MinIO"""
    try:
        client.fput_object(MINIO_BUCKET, object_name, file_path)
        url = f"http://{MINIO_URL}/{MINIO_BUCKET}/{object_name}"
        return url
    except S3Error as e:
        print(f"  ✗ Lỗi upload: {e}")
        return None

def get_product_id(cursor, product_code):
    """Lấy product_id từ product_code"""
    cursor.execute(
        "SELECT id FROM products WHERE code = %s",
        (product_code,)
    )
    result = cursor.fetchone()
    return result[0] if result else None

def update_database(cursor, product_code, image_url, is_thumbnail=False):
    """Cập nhật database"""
    try:
        product_id = get_product_id(cursor, product_code)
        if not product_id:
            print(f"  ⚠ Không tìm thấy sản phẩm: {product_code}")
            return False
        
        if is_thumbnail:
            # Cập nhật thumbnail_img
            cursor.execute(
                "UPDATE products SET thumbnail_img = %s WHERE id = %s",
                (image_url, product_id)
            )
        else:
            # Thêm vào product_attachments
            cursor.execute(
                """INSERT INTO product_attachments (product_id, attachment) 
                   VALUES (%s, %s)
                   ON DUPLICATE KEY UPDATE attachment = %s""",
                (product_id, image_url, image_url)
            )
        return True
    except Exception as e:
        print(f"  ✗ Lỗi cập nhật database: {e}")
        return False

def parse_detail_file(file_path):
    """Parse file Detail_image.txt để lấy product code và URLs"""
    products = []
    current_product = None
    
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            
            # Kiểm tra xem có phải là dòng product code không
            match = re.search(r'([A-Z]+-[A-Z0-9-]+)', line)
            if match:
                # Dòng mới với product code
                if current_product:
                    products.append(current_product)
                current_product = {
                    'code': match.group(1),
                    'name': line.split('–')[0].strip() if '–' in line else line,
                    'urls': []
                }
            elif line.startswith('http'):
                # Dòng URL
                if current_product:
                    current_product['urls'].append(line)
    
    # Thêm product cuối cùng
    if current_product:
        products.append(current_product)
    
    return products

def main():
    if len(sys.argv) < 2:
        print("Sử dụng: python3 scripts/download_and_upload_images.py <Detail_image.txt>")
        sys.exit(1)
    
    file_path = sys.argv[1]
    if not os.path.exists(file_path):
        print(f"Error: File không tồn tại: {file_path}")
        sys.exit(1)
    
    print("=== Download và Upload ảnh lên MinIO ===\n")
    
    # Parse file
    print("Đang đọc file...")
    products = parse_detail_file(file_path)
    print(f"Tìm thấy {len(products)} sản phẩm\n")
    
    # Kết nối
    try:
        minio_client = connect_minio()
        db_conn = connect_db()
        db_cursor = db_conn.cursor()
        ensure_bucket(minio_client, MINIO_BUCKET)
    except Exception as e:
        print(f"Error: Không thể kết nối - {e}")
        sys.exit(1)
    
    # Tạo thư mục temp
    temp_dir = tempfile.mkdtemp()
    print(f"Thư mục temp: {temp_dir}\n")
    
    # Xử lý từng sản phẩm
    success_count = 0
    error_count = 0
    total_images = 0
    
    for idx, product in enumerate(products, 1):
        product_code = product['code']
        urls = product['urls']
        
        print(f"[{idx}/{len(products)}] {product['name']} ({product_code})")
        print(f"  Số ảnh: {len(urls)}")
        
        if not urls:
            print("  ⚠ Không có URL ảnh")
            continue
        
        # Xử lý từng URL
        for url_idx, url in enumerate(urls, 1):
            print(f"  [{url_idx}/{len(urls)}] Đang tải: {url[:60]}...")
            
            # Download
            file_path, filename = download_image(url, temp_dir)
            if not file_path:
                error_count += 1
                continue
            
            # Tạo object name cho MinIO
            file_ext = Path(filename).suffix or '.jpg'
            if url_idx == 1:
                # Ảnh đầu tiên = thumbnail
                object_name = f"products/{product_code}_thumbnail{file_ext}"
                is_thumbnail = True
            else:
                # Ảnh sau = detail
                object_name = f"products/{product_code}_detail_{url_idx}{file_ext}"
                is_thumbnail = False
            
            # Upload lên MinIO
            image_url = upload_to_minio(minio_client, file_path, object_name)
            if not image_url:
                error_count += 1
                os.remove(file_path)
                continue
            
            print(f"    ✓ Upload thành công: {image_url}")
            
            # Cập nhật database
            if update_database(db_cursor, product_code, image_url, is_thumbnail):
                print(f"    ✓ Database đã cập nhật")
                success_count += 1
                total_images += 1
            else:
                error_count += 1
            
            # Xóa file temp
            os.remove(file_path)
        
        print()
    
    # Commit và đóng kết nối
    db_conn.commit()
    db_cursor.close()
    db_conn.close()
    
    # Xóa thư mục temp
    import shutil
    shutil.rmtree(temp_dir)
    
    print("=== Hoàn thành ===")
    print(f"Tổng sản phẩm: {len(products)}")
    print(f"Ảnh thành công: {success_count}")
    print(f"Ảnh lỗi: {error_count}")
    print(f"Tổng ảnh đã upload: {total_images}")

if __name__ == "__main__":
    main()

