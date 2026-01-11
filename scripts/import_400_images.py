#!/usr/bin/env python3
"""
Script import 400 ảnh từ folder/file vào MinIO và cập nhật database
Hỗ trợ: folder chứa ảnh, file zip, file tar
Sử dụng: python3 scripts/import_400_images.py <folder_or_archive_path>
"""

import os
import sys
import re
import zipfile
import tarfile
import tempfile
import shutil
from pathlib import Path
try:
    import pymysql
    from minio import Minio
    from minio.error import S3Error
except ImportError:
    print("Cần cài đặt: pip install pymysql minio")
    sys.exit(1)

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

# Format ảnh hỗ trợ
IMAGE_EXTENSIONS = {'.jpg', '.jpeg', '.png', '.webp', '.avif'}

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

def extract_archive(archive_path, extract_to):
    """Giải nén file zip/tar"""
    archive_path = Path(archive_path)
    
    if zipfile.is_zipfile(archive_path):
        with zipfile.ZipFile(archive_path, 'r') as zip_ref:
            zip_ref.extractall(extract_to)
        return True
    elif tarfile.is_tarfile(archive_path):
        with tarfile.open(archive_path, 'r:*') as tar_ref:
            tar_ref.extractall(extract_to)
        return True
    return False

def get_image_files(path):
    """Lấy tất cả file ảnh từ path (folder hoặc đã giải nén)"""
    path = Path(path)
    image_files = []
    
    if path.is_file():
        # Nếu là file nén, giải nén trước
        temp_dir = tempfile.mkdtemp()
        try:
            if extract_archive(path, temp_dir):
                # Tìm file ảnh trong thư mục đã giải nén
                for root, dirs, files in os.walk(temp_dir):
                    for file in files:
                        if Path(file).suffix.lower() in IMAGE_EXTENSIONS:
                            image_files.append(Path(root) / file)
        except Exception as e:
            print(f"Lỗi giải nén: {e}")
            shutil.rmtree(temp_dir, ignore_errors=True)
            return []
    elif path.is_dir():
        # Nếu là folder, tìm tất cả file ảnh
        for root, dirs, files in os.walk(path):
            for file in files:
                if Path(file).suffix.lower() in IMAGE_EXTENSIONS:
                    image_files.append(Path(root) / file)
    
    return sorted(image_files)

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
        client.fput_object(MINIO_BUCKET, object_name, str(file_path))
        url = f"http://{MINIO_URL}/{MINIO_BUCKET}/{object_name}"
        return url
    except S3Error as e:
        print(f"  ✗ Lỗi upload: {e}")
        return None

def get_product_id(cursor, product_code):
    """Lấy product_id từ product_code"""
    cursor.execute("SELECT id FROM products WHERE code = %s", (product_code,))
    result = cursor.fetchone()
    return result[0] if result else None

def update_thumbnail(cursor, product_id, image_url):
    """Cập nhật thumbnail_img"""
    sql = "UPDATE products SET thumbnail_img = %s WHERE id = %s"
    cursor.execute(sql, (image_url, product_id))
    return cursor.rowcount > 0

def update_or_insert_attachment(cursor, product_id, image_url, image_num):
    """Cập nhật hoặc thêm ảnh chi tiết"""
    # Lấy danh sách attachments hiện có
    cursor.execute(
        "SELECT id FROM product_attachments WHERE product_id = %s ORDER BY id",
        (product_id,)
    )
    existing_attachments = [row[0] for row in cursor.fetchall()]
    
    if image_num and image_num <= len(existing_attachments):
        # Update ảnh hiện có (index bắt đầu từ 1)
        attachment_id = existing_attachments[image_num - 1]
        sql = "UPDATE product_attachments SET attachment = %s WHERE id = %s"
        cursor.execute(sql, (image_url, attachment_id))
    else:
        # Insert ảnh mới
        sql = "INSERT INTO product_attachments (product_id, attachment) VALUES (%s, %s)"
        cursor.execute(sql, (product_id, image_url))

def main():
    if len(sys.argv) < 2:
        print("Sử dụng: python3 scripts/import_400_images.py <folder_or_archive_path>")
        print("Ví dụ: python3 scripts/import_400_images.py ./images")
        print("Ví dụ: python3 scripts/import_400_images.py ./images.zip")
        sys.exit(1)
    
    input_path = sys.argv[1]
    
    if not os.path.exists(input_path):
        print(f"Error: Không tìm thấy: {input_path}")
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
    
    # Lấy danh sách file ảnh
    print(f"\nĐang quét file ảnh từ: {input_path}")
    image_files = get_image_files(input_path)
    
    if not image_files:
        print("Không tìm thấy file ảnh nào!")
        sys.exit(1)
    
    print(f"Tìm thấy {len(image_files)} file ảnh\n")
    
    # Lấy danh sách product codes từ database để validate
    db_cursor.execute("SELECT code FROM products")
    valid_codes = {row[0] for row in db_cursor.fetchall()}
    
    # Xử lý từng file
    success_count = 0
    error_count = 0
    skipped_count = 0
    
    # Nhóm ảnh theo product code
    products_images = {}
    for image_file in image_files:
        filename = image_file.name
        product_code, image_type, image_num = parse_filename(filename)
        
        if product_code not in products_images:
            products_images[product_code] = {'thumbnail': None, 'details': []}
        
        if image_type == 'thumbnail':
            products_images[product_code]['thumbnail'] = (image_file, image_num)
        else:
            products_images[product_code]['details'].append((image_file, image_num))
    
    # Sắp xếp details theo số
    for product_code in products_images:
        products_images[product_code]['details'].sort(key=lambda x: x[1] or 0)
    
    print(f"Tìm thấy {len(products_images)} sản phẩm\n")
    
    # Upload và cập nhật database
    for product_code, images in products_images.items():
        print(f"Đang xử lý: {product_code}")
        
        # Kiểm tra product code có tồn tại
        product_id = get_product_id(db_cursor, product_code)
        if not product_id:
            print(f"  ⚠ Không tìm thấy product với code: {product_code}")
            skipped_count += 1
            continue
        
        # Upload thumbnail
        if images['thumbnail']:
            image_file, _ = images['thumbnail']
            object_name = f"products/{image_file.name}"
            image_url = upload_to_minio(minio_client, image_file, object_name)
            
            if image_url:
                if update_thumbnail(db_cursor, product_id, image_url):
                    print(f"  ✓ Thumbnail: {image_file.name}")
                    success_count += 1
                else:
                    error_count += 1
            else:
                error_count += 1
        
        # Upload ảnh chi tiết
        for idx, (image_file, image_num) in enumerate(images['details'], 1):
            object_name = f"products/{image_file.name}"
            image_url = upload_to_minio(minio_client, image_file, object_name)
            
            if image_url:
                update_or_insert_attachment(db_cursor, product_id, image_url, image_num or idx)
                print(f"  ✓ Ảnh {idx}: {image_file.name}")
                success_count += 1
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
    print(f"Bỏ qua: {skipped_count} sản phẩm (không tìm thấy code)")

if __name__ == "__main__":
    main()

