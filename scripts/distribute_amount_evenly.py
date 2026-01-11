#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Chia đều 100 đôi cho tất cả các size của mỗi sản phẩm.
Nếu chia không đều, size đầu tiên sẽ nhận thêm số dư.
"""

import pymysql

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'tuanhung1999',
    'database': 'e-commerce',
    'charset': 'utf8mb4'
}

def update_amount_evenly(total_amount=100):
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    try:
        cursor.execute('SELECT id FROM products')
        product_ids = [row[0] for row in cursor.fetchall()]
        for product_id in product_ids:
            cursor.execute('SELECT id FROM product_properties WHERE product_id = %s', (product_id,))
            size_ids = [row[0] for row in cursor.fetchall()]
            if not size_ids:
                continue
            per_size = total_amount // len(size_ids)
            remainder = total_amount % len(size_ids)
            for idx, size_id in enumerate(size_ids):
                amount = per_size + (1 if idx < remainder else 0)
                cursor.execute('UPDATE product_amounts SET amount = %s WHERE product_properties_id = %s', (amount, size_id))
        conn.commit()
        print(f"✓ Đã chia đều {total_amount} đôi cho tất cả size của mỗi sản phẩm!")
    except Exception as e:
        print(f"✗ Lỗi: {e}")
        conn.rollback()
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    update_amount_evenly(100)
