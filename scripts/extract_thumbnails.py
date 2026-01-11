#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Extract thumbnail URLs từ DANH_SACH_200_SAN_PHAM.txt
"""

import re

def extract_thumbnails():
    """Parse file và extract thumbnail URLs"""
    products = []
    
    with open('../DANH_SACH_200_SAN_PHAM.txt', 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    for line in lines:
        line = line.strip()
        
        # Tìm dòng bắt đầu với ID:
        if line.startswith('ID:'):
            # Extract ID và Code
            id_match = re.search(r'ID:\s*(\d+)', line)
            code_match = re.search(r'Mã:\s*([\w-]+)', line)
            name_match = re.search(r'Tên:\s*([^|]+)', line)
            
            if id_match and code_match:
                product_id = int(id_match.group(1))
                code = code_match.group(1).strip()
                name = name_match.group(1).strip() if name_match else code
                
                # Tìm URL trong cùng dòng
                url_match = re.search(r'(https://authentic-shoes\.com[^\s|]+)', line)
                if url_match:
                    thumbnail_url = url_match.group(1).strip()
                    products.append({
                        'id': product_id,
                        'code': code,
                        'name': name,
                        'thumbnail_url': thumbnail_url
                    })
    
    return products

def write_thumbnail_file(products):
    """Ghi ra file thumbnail_urls.txt"""
    with open('../thumbnail_urls.txt', 'w', encoding='utf-8') as f:
        f.write("# Thumbnail URLs for ShoeStore Products\n")
        f.write("# Format: ID | CODE | NAME | THUMBNAIL_URL\n\n")
        
        for p in products:
            f.write(f"{p['id']} | {p['code']} | {p['name']} | {p['thumbnail_url']}\n")
    
    print(f"✓ Wrote {len(products)} thumbnails to thumbnail_urls.txt")

if __name__ == '__main__':
    print("🔄 Extracting thumbnails...")
    products = extract_thumbnails()
    write_thumbnail_file(products)
    print(f"✅ Done! Total: {len(products)} products with thumbnails")
