#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Cập nhật amount = 100 cho tất cả các product_properties (size sản phẩm) trong bảng product_amounts
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

def update_all_amounts(new_amount=100):
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    try:
        sql = """
            UPDATE product_amounts SET amount = %s
        """
        cursor.execute(sql, (new_amount,))
        conn.commit()
        print(f"✓ Đã cập nhật amount = {new_amount} cho tất cả các size sản phẩm!")
    except Exception as e:
        print(f"✗ Lỗi: {e}")
        conn.rollback()
    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    update_all_amounts(100)
