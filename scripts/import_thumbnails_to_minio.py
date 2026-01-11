#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Download thumbnails và upload lên MinIO, sau đó update database
"""

import pymysql
import requests
import os
from urllib.parse import urlparse
from minio import Minio
from minio.error import S3Error

# Database config
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'tuanhung1999',
    'database': 'e-commerce',
    'charset': 'utf8mb4'
}

# MinIO config
MINIO_CONFIG = {
    'endpoint': 'localhost:9000',
    'access_key': 'kWu8WEj6n28m6CjqUpd2',
    'secret_key': 'mqrPrpYnSIw8TEP0Je6IicjpfKU5MjcHmOWvQ1LC',
    'bucket': 'products',
    'secure': False
}

def parse_thumbnail_file():
    """Parse file thumbnail_urls.txt"""
    products = []
    
    with open('../thumbnail_urls.txt', 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    for line in lines:
        line = line.strip()
        if not line or line.startswith('#'):
            continue
        
        parts = [p.strip() for p in line.split('|')]
        if len(parts) == 4:
            products.append({
                'id': int(parts[0]),
                'code': parts[1],
                'name': parts[2],
                'url': parts[3]
            })
    
    print(f"✓ Parsed {len(products)} products")
    return products

def init_minio_client():
    """Initialize MinIO client"""
    client = Minio(
        MINIO_CONFIG['endpoint'],
        access_key=MINIO_CONFIG['access_key'],
        secret_key=MINIO_CONFIG['secret_key'],
        secure=MINIO_CONFIG['secure']
    )
    
    # Ensure bucket exists
    if not client.bucket_exists(MINIO_CONFIG['bucket']):
        client.make_bucket(MINIO_CONFIG['bucket'])
        print(f"✓ Created bucket: {MINIO_CONFIG['bucket']}")
    
    return client

def download_and_upload_thumbnail(product, minio_client, temp_dir='../temp_thumbnails'):
    """Download thumbnail và upload lên MinIO"""
    try:
        # Tạo temp directory
        os.makedirs(temp_dir, exist_ok=True)
        
        # Parse URL để lấy extension
        url = product['url']
        parsed = urlparse(url)
        filename = os.path.basename(parsed.path)
        ext = os.path.splitext(filename)[1] or '.jpg'
        
        # Tạo tên file mới: CODE_thumbnail.ext
        new_filename = f"{product['code']}_thumbnail{ext}"
        temp_filepath = os.path.join(temp_dir, new_filename)
        
        # Download
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        
        with open(temp_filepath, 'wb') as f:
            f.write(response.content)
        
        # Upload to MinIO
        minio_path = f"thumbnails/{new_filename}"
        minio_client.fput_object(
            MINIO_CONFIG['bucket'],
            minio_path,
            temp_filepath,
            content_type=response.headers.get('Content-Type', 'image/jpeg')
        )
        
        # Delete temp file
        os.remove(temp_filepath)
        
        # Generate MinIO URL
        minio_url = f"http://{MINIO_CONFIG['endpoint']}/{MINIO_CONFIG['bucket']}/{minio_path}"
        
        return minio_url
        
    except Exception as e:
        print(f"  ✗ Error {product['code']}: {e}")
        return None

def update_database(product_code, minio_url):
    """Update database với MinIO URL"""
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    
    try:
        cursor.execute(
            "UPDATE products SET thumbnail_img = %s WHERE code = %s",
            (minio_url, product_code)
        )
        conn.commit()
        return True
    except Exception as e:
        print(f"  ✗ DB Error {product_code}: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()
        conn.close()

def main():
    print("🔄 Starting thumbnail import to MinIO...")
    
    products = parse_thumbnail_file()
    minio_client = init_minio_client()
    
    success_count = 0
    
    for i, product in enumerate(products, 1):
        print(f"[{i}/{len(products)}] Processing {product['code']}...")
        
        # Download and upload
        minio_url = download_and_upload_thumbnail(product, minio_client)
        
        if minio_url:
            # Update database
            if update_database(product['code'], minio_url):
                success_count += 1
                print(f"  ✓ {product['code']}: {minio_url}")
    
    print(f"\n✅ Done! Successfully imported {success_count}/{len(products)} thumbnails to MinIO")

if __name__ == '__main__':
    main()
