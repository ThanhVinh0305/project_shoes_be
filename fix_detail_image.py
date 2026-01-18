#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script để chuyển đổi Detail_image.txt sang JSON
Xử lý đúng trường hợp không có dòng trống giữa các sản phẩm
"""

import json

# Đọc file Detail_image.txt
with open('Detail_image.txt', 'r', encoding='utf-8') as f:
    content = f.read()

# Parse data
products = []
lines = content.strip().split('\n')

current_product = None
current_images = []

for i, line in enumerate(lines):
    line = line.strip()
    
    # Kiểm tra nếu là dòng sản phẩm (có dấu – và KHÔNG phải URL)
    if ' – ' in line and not line.startswith('http'):
        # Lưu sản phẩm trước (nếu có)
        if current_product:
            products.append({
                "name": current_product["name"],
                "code": current_product["code"],
                "images": current_images
            })
        
        # Bắt đầu sản phẩm mới
        parts = line.split(' – ')
        current_product = {
            "name": parts[0].strip(),
            "code": parts[1].strip() if len(parts) > 1 else ""
        }
        current_images = []
        print(f"✓ Tìm thấy sản phẩm: {current_product['name']}")
        
    elif line.startswith('http'):  # URL hình ảnh
        if current_product:  # Chỉ thêm nếu đã có sản phẩm
            current_images.append(line)
    # Bỏ qua dòng trống

# Thêm sản phẩm cuối cùng
if current_product:
    products.append({
        "name": current_product["name"],
        "code": current_product["code"],
        "images": current_images
    })

# Thống kê
total_products = len(products)
products_with_images = len([p for p in products if p['images']])
products_without_images = len([p for p in products if not p['images']])
total_images = sum(len(p['images']) for p in products)

# Đếm theo brand
brands = {}
for p in products:
    brand = p['code'].split('-')[0] if p['code'] else 'UNKNOWN'
    brands[brand] = brands.get(brand, 0) + 1

# Ghi ra file JSON
with open('detail_image.json', 'w', encoding='utf-8') as f:
    json.dump(products, f, ensure_ascii=False, indent=2)

print(f"\n{'='*60}")
print(f"✅ Đã tạo file detail_image.json")
print(f"{'='*60}")
print(f"📊 Tổng số sản phẩm: {total_products}")
print(f"   - Có hình ảnh: {products_with_images}")
print(f"   - Không có hình ảnh: {products_without_images}")
print(f"📸 Tổng số hình ảnh: {total_images}")

print(f"\n📦 Phân bố theo thương hiệu:")
for brand, count in sorted(brands.items()):
    print(f"   - {brand}: {count} sản phẩm")

# Liệt kê sản phẩm không có hình ảnh
if products_without_images > 0:
    print(f"\n⚠️ Các sản phẩm KHÔNG CÓ hình ảnh ({products_without_images}):")
    for p in products:
        if not p['images']:
            print(f"   - {p['name']} ({p['code']})")
