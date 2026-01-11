#!/usr/bin/env python3
"""
Script để:
1. Import 200 sản phẩm từ DANH_SACH_200_SAN_PHAM.txt vào database
2. Download và upload ảnh detail từ Detail_image.txt lên MinIO
Sử dụng: python3 scripts/import_200_products_and_images.py
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

def parse_product_line(line):
    """Parse dòng sản phẩm từ DANH_SACH_200_SAN_PHAM.txt
    Format: ID: 1 | Tên: Nike Air Max 90 | Mã: NIKE-AM90-001 | Giới tính: 0 | Màu: ... | Giá: 2500000 VNĐ | URL | Mô tả
    """
    # Tách các phần bằng dấu |
    parts = [p.strip() for p in line.split('|')]
    
    product = {}
    description_parts = []
    
    for part in parts:
        if part.startswith('ID:'):
            product['id'] = int(part.replace('ID:', '').strip())
        elif part.startswith('Tên:'):
            product['name'] = part.replace('Tên:', '').strip()
        elif part.startswith('Mã:'):
            product['code'] = part.replace('Mã:', '').strip()
        elif part.startswith('Giới tính:'):
            gender_num = part.replace('Giới tính:', '').strip()
            # 0=nữ, 1=nam, 2=unisex (cả nam và nữ)
            # Mapping mới: 0→1 (Nữ), 1→2 (Nam), 2→3 (Unisex)
            gender_map = {'0': 1, '1': 2, '2': 3}  # FEMALE=1, MALE=2, UNISEX=3
            product['gender_id'] = gender_map.get(gender_num, 3)
        elif part.startswith('Màu:'):
            product['color'] = part.replace('Màu:', '').strip()
        elif part.startswith('Giá:'):
            price_str = part.replace('Giá:', '').replace('VNĐ', '').strip().replace(',', '').replace('.', '')
            try:
                product['price'] = int(price_str)
            except:
                product['price'] = 0
        elif part.startswith('http'):
            product['thumbnail_url'] = part.strip()
        else:
            # Phần còn lại là mô tả
            if part and not part.startswith('ID:') and not part.startswith('Tên:'):
                description_parts.append(part)
    
    product['description'] = ' '.join(description_parts).strip()
    return product

def import_products_from_file(file_path, cursor, minio_client, temp_dir):
    """Import sản phẩm từ file vào database và upload thumbnail lên MinIO"""
    products = []
    current_product = None
    description_lines = []
    
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            
            # Kiểm tra xem có phải là dòng bắt đầu sản phẩm mới không
            if line.startswith('ID:'):
                # Lưu sản phẩm trước đó nếu có
                if current_product:
                    if description_lines:
                        current_product['description'] = ' '.join(description_lines).strip()
                    products.append(current_product)
                
                # Parse sản phẩm mới
                current_product = parse_product_line(line)
                description_lines = []
            else:
                # Dòng mô tả tiếp theo
                if current_product:
                    description_lines.append(line)
    
    # Thêm sản phẩm cuối cùng
    if current_product:
        if description_lines:
            current_product['description'] = ' '.join(description_lines).strip()
        products.append(current_product)
    
    # Insert vào database và upload thumbnail
    inserted = 0
    thumbnail_uploaded = 0
    
    for product in products:
        try:
            # Truncate description nếu quá dài (max 255 ký tự)
            description = product.get('description', '')
            if len(description) > 255:
                description = description[:252] + '...'
            
            # Xử lý thumbnail
            thumbnail_url = product.get('thumbnail_url', '')
            minio_thumbnail_url = ''
            
            if thumbnail_url and thumbnail_url.startswith('http'):
                # Download và upload thumbnail lên MinIO
                product_code = product.get('code', '')
                print(f"  Đang tải thumbnail cho {product_code}...")
                
                file_path, filename = download_image(thumbnail_url, temp_dir)
                if file_path:
                    file_ext = Path(filename).suffix or '.jpg'
                    object_name = f"products/{product_code}_thumbnail{file_ext}"
                    minio_thumbnail_url = upload_to_minio(minio_client, file_path, object_name)
                    os.remove(file_path)
                    
                    if minio_thumbnail_url:
                        print(f"    ✓ Thumbnail đã upload: {minio_thumbnail_url}")
                        thumbnail_uploaded += 1
                    else:
                        print(f"    ✗ Lỗi upload thumbnail, giữ nguyên URL gốc")
                        minio_thumbnail_url = thumbnail_url
                else:
                    print(f"    ✗ Lỗi download thumbnail, giữ nguyên URL gốc")
                    minio_thumbnail_url = thumbnail_url
            else:
                minio_thumbnail_url = thumbnail_url
            
            cursor.execute("""
                INSERT INTO products (code, name, description, price, color, gender_id, thumbnail_img)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                    name = VALUES(name),
                    description = VALUES(description),
                    price = VALUES(price),
                    color = VALUES(color),
                    gender_id = VALUES(gender_id),
                    thumbnail_img = VALUES(thumbnail_img)
            """, (
                product.get('code'),
                product.get('name'),
                description,
                product.get('price', 0),
                product.get('color', ''),
                product.get('gender_id', 3),  # Default UNISEX
                minio_thumbnail_url
            ))
            inserted += 1
        except Exception as e:
            print(f"  ✗ Lỗi insert {product.get('code')}: {e}")
    
    return inserted, products, thumbnail_uploaded

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

def update_product_attachment(cursor, product_id, image_url):
    """Thêm ảnh vào product_attachments"""
    try:
        cursor.execute("""
            INSERT INTO product_attachments (product_id, attachment)
            VALUES (%s, %s)
            ON DUPLICATE KEY UPDATE attachment = VALUES(attachment)
        """, (product_id, image_url))
        return True
    except Exception as e:
        print(f"  ✗ Lỗi cập nhật attachment: {e}")
        return False

def main():
    print("=== Import 200 Sản phẩm và Upload Ảnh Detail ===\n")
    
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
    
    # Bước 1: Import sản phẩm và upload thumbnail
    print("=== BƯỚC 1: Import 200 sản phẩm và upload thumbnail ===")
    products_file = "DANH_SACH_200_SAN_PHAM.txt"
    if not os.path.exists(products_file):
        print(f"Error: File không tồn tại: {products_file}")
        sys.exit(1)
    
    inserted, products_list, thumbnail_uploaded = import_products_from_file(products_file, db_cursor, minio_client, temp_dir)
    db_conn.commit()
    print(f"✓ Đã import {inserted} sản phẩm vào database")
    print(f"✓ Đã upload {thumbnail_uploaded} thumbnail lên MinIO\n")
    
    # Bước 2: Upload ảnh detail
    print("=== BƯỚC 2: Upload ảnh detail lên MinIO ===")
    detail_file = "Detail_image.txt"
    if not os.path.exists(detail_file):
        print(f"⚠ File không tồn tại: {detail_file}")
        print("Bỏ qua bước upload ảnh detail\n")
    else:
        detail_products = parse_detail_file(detail_file)
        print(f"Tìm thấy {len(detail_products)} sản phẩm có ảnh detail\n")
        
        success_count = 0
        error_count = 0
        
        for idx, product in enumerate(detail_products, 1):
            product_code = product['code']
            urls = product['urls']
            
            print(f"[{idx}/{len(detail_products)}] {product['name']} ({product_code})")
            print(f"  Số ảnh: {len(urls)}")
            
            if not urls:
                print("  ⚠ Không có URL ảnh")
                continue
            
            product_id = get_product_id(db_cursor, product_code)
            if not product_id:
                print(f"  ⚠ Không tìm thấy sản phẩm trong database")
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
                object_name = f"products/{product_code}_detail_{url_idx}{file_ext}"
                
                # Upload lên MinIO
                image_url = upload_to_minio(minio_client, file_path, object_name)
                if not image_url:
                    error_count += 1
                    os.remove(file_path)
                    continue
                
                print(f"    ✓ Upload thành công: {image_url}")
                
                # Cập nhật database
                if update_product_attachment(db_cursor, product_id, image_url):
                    print(f"    ✓ Database đã cập nhật")
                    success_count += 1
                else:
                    error_count += 1
                
                # Xóa file temp
                os.remove(file_path)
            
            print()
        
        db_conn.commit()
        
        print(f"✓ Hoàn thành upload ảnh detail")
        print(f"  Thành công: {success_count}")
        print(f"  Lỗi: {error_count}\n")
    
    # Xóa thư mục temp
    import shutil
    shutil.rmtree(temp_dir)
    
    # Đóng kết nối
    db_cursor.close()
    db_conn.close()
    
    print("=== Hoàn thành ===")
    print(f"Tổng sản phẩm đã import: {inserted}")
    print(f"Tổng thumbnail đã upload: {thumbnail_uploaded}")

if __name__ == "__main__":
    main()

