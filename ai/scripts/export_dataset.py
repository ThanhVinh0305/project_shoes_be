#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script để export dataset từ database để train J48 model
Dataset tập trung vào giao dịch: sản phẩm nào mua nhiều → gợi ý
"""

import pymysql
import csv
from datetime import datetime
import os

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'tuanhung1999',
    'database': 'e-commerce',
    'charset': 'utf8mb4'
}

def get_db_connection():
    """Kết nối database"""
    return pymysql.connect(**DB_CONFIG)

def export_transaction_dataset():
    """Export dataset từ giao dịch"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    print("Đang export dataset từ giao dịch...")
    
    # Query để lấy dữ liệu giao dịch
    sql = """
        SELECT 
            b.user_id,
            pb.product_properties_id,
            pp.product_id,
            COALESCE(pb2.brand_id, 0) as brand_id,
            COALESCE(pc.category_id, 0) as category_id,
            COALESCE(p.gender_id, 0) as gender_id,
            COALESCE(p.color, 'unknown') as color,
            p.price,
            pb.amount as purchase_amount,
            pb.price as purchase_price,
            CASE 
                WHEN p.price < 2000000 THEN 'low'
                WHEN p.price < 4000000 THEN 'medium'
                ELSE 'high'
            END as price_range,
            COUNT(*) OVER (PARTITION BY pp.product_id) as product_purchase_count,
            COUNT(*) OVER (PARTITION BY b.user_id) as user_purchase_count,
            CASE 
                WHEN b.status = 1 THEN 'YES'
                ELSE 'NO'
            END as will_recommend
        FROM bills b
        JOIN product_bills pb ON b.id = pb.bill_id
        JOIN product_properties pp ON pb.product_properties_id = pp.id
        JOIN products p ON pp.product_id = p.id
        LEFT JOIN product_categories pc ON p.id = pc.product_id
        LEFT JOIN product_brands pb2 ON p.id = pb2.product_id
        WHERE b.status IN (0, 1)  -- CREATED hoặc PURCHASE
        ORDER BY b.created_date DESC
    """
    
    cursor.execute(sql)
    rows = cursor.fetchall()
    
    if not rows:
        print("✗ Không có dữ liệu!")
        return
    
    # Tạo file CSV
    output_dir = os.path.join(os.path.dirname(__file__), '..', 'data')
    os.makedirs(output_dir, exist_ok=True)
    
    csv_file = os.path.join(output_dir, 'transaction_dataset.csv')
    
    with open(csv_file, 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        
        # Header
        writer.writerow([
            'user_id', 'product_id', 'brand_id', 'category_id', 
            'gender_id', 'color', 'price', 'price_range',
            'purchase_amount', 'purchase_price',
            'product_purchase_count', 'user_purchase_count',
            'will_recommend'
        ])
        
        # Data
        for row in rows:
            writer.writerow(row)
    
    print(f"✓ Đã export {len(rows)} records vào {csv_file}")
    
    # Thống kê
    print(f"\n📊 Thống kê dataset:")
    print(f"  - Tổng số records: {len(rows)}")
    
    cursor.execute("SELECT COUNT(DISTINCT user_id) FROM bills WHERE status IN (0, 1)")
    total_users = cursor.fetchone()[0]
    print(f"  - Số users: {total_users}")
    
    cursor.execute("SELECT COUNT(DISTINCT pp.product_id) FROM product_bills pb JOIN product_properties pp ON pb.product_properties_id = pp.id JOIN bills b ON pb.bill_id = b.id WHERE b.status IN (0, 1)")
    total_products = cursor.fetchone()[0]
    print(f"  - Số sản phẩm: {total_products}")
    
    cursor.close()
    conn.close()
    
    return csv_file

def export_arff_format():
    """Export sang ARFF format cho Weka"""
    csv_file = os.path.join(os.path.dirname(__file__), '..', 'data', 'transaction_dataset.csv')
    
    if not os.path.exists(csv_file):
        print("✗ Chưa có file CSV. Chạy export_transaction_dataset() trước.")
        return
    
    arff_file = csv_file.replace('.csv', '.arff')
    
    print(f"Đang convert CSV sang ARFF format...")
    
    with open(csv_file, 'r', encoding='utf-8') as f_in:
        reader = csv.reader(f_in)
        header = next(reader)
        rows = list(reader)
    
    with open(arff_file, 'w', encoding='utf-8') as f_out:
        # ARFF header
        f_out.write(f"@relation transaction_recommendation\n\n")
        
        # Attributes
        for i, col in enumerate(header):
            if col == 'will_recommend':
                f_out.write(f"@attribute {col} {{YES,NO}}\n")
            elif col in ['color']:
                # Nominal attribute
                unique_values = set(row[i] for row in rows if row[i])
                values_str = ','.join(sorted(unique_values))
                f_out.write(f"@attribute {col} {{{values_str}}}\n")
            elif col == 'price_range':
                f_out.write(f"@attribute {col} {{low,medium,high}}\n")
            else:
                f_out.write(f"@attribute {col} numeric\n")
        
        f_out.write("\n@data\n")
        
        # Data
        for row in rows:
            # Replace empty values with ?
            row_clean = [val if val else '?' for val in row]
            f_out.write(','.join(row_clean) + '\n')
    
    print(f"✓ Đã tạo file ARFF: {arff_file}")
    return arff_file

def main():
    """Hàm chính"""
    print("=" * 60)
    print("EXPORT DATASET CHO J48 TRAINING")
    print("=" * 60)
    
    csv_file = export_transaction_dataset()
    if csv_file:
        export_arff_format()
    
    print("\n" + "=" * 60)
    print("HOÀN THÀNH!")
    print("=" * 60)

if __name__ == "__main__":
    main()

