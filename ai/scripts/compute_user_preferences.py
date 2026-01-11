#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Tính điểm quan tâm người dùng (brand, color, product) dựa trên hành vi/view/search/purchase.
Chỉ dùng dữ liệu của chính user đó, không pha trộn người khác.

Chạy:
  cd "$(dirname "$0")"
  python3 compute_user_preferences.py
"""

import math
import pymysql
from datetime import datetime

DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "tuanhung1999",
    "database": "e-commerce",
    "charset": "utf8mb4",
}


def connect_db():
    return pymysql.connect(**DB_CONFIG)


def ensure_table(conn):
    with conn.cursor() as cur:
        cur.execute(
            """
            CREATE TABLE IF NOT EXISTS user_preferences (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
                preference_type ENUM('BRAND', 'COLOR', 'PRODUCT') NOT NULL,
                preference_value VARCHAR(255) NOT NULL,
                preference_score DECIMAL(10,4) DEFAULT 0.0000,
                last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_user_id (user_id),
                INDEX idx_type (preference_type),
                INDEX idx_score (preference_score),
                UNIQUE KEY uniq_pref (user_id, preference_type, preference_value)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """
        )
    conn.commit()


def fetch_products(conn):
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT p.id, COALESCE(pb.brand_id, 0) AS brand_id, COALESCE(p.color, 'unknown') AS color
            FROM products p
            LEFT JOIN product_brands pb ON p.id = pb.product_id
            """
        )
        rows = cur.fetchall()
    product_map = {}
    for pid, bid, color in rows:
        product_map[pid] = {"brand_id": bid or 0, "color": color or "unknown"}
    return product_map


def decay_from_datetime(dt: datetime, half_life_days=20):
    """
    Exponential time decay with configurable half-life
    Default: 20 days (more aggressive than 30 days)
    """
    if not dt:
        return 1.0
    delta_days = (datetime.utcnow() - dt).days
    # exponential decay: score * 0.5 every half_life_days
    return math.pow(0.5, delta_days / half_life_days)


def aggregate_scores(product_map, purchases, views, searches, clicks, add_to_carts):
    brand_scores = {}
    color_scores = {}
    product_scores = {}

    # Purchases: strongest weight (confirmed interest)
    for user_id, product_id, created_date in purchases:
        pinfo = product_map.get(product_id, {"brand_id": 0, "color": "unknown"})
        decay = decay_from_datetime(created_date, half_life_days=20)
        add_score(brand_scores, (user_id, pinfo["brand_id"]), 5.0 * decay)
        add_score(color_scores, (user_id, pinfo["color"]), 5.0 * decay)
        add_score(product_scores, (user_id, product_id), 6.0 * decay)

    # Add to cart: very strong signal (high purchase intent)
    for user_id, product_id, created_date in add_to_carts:
        pinfo = product_map.get(product_id, {"brand_id": 0, "color": "unknown"})
        decay = decay_from_datetime(created_date, half_life_days=15)
        add_score(brand_scores, (user_id, pinfo["brand_id"]), 3.5 * decay)
        add_score(color_scores, (user_id, pinfo["color"]), 3.5 * decay)
        add_score(product_scores, (user_id, product_id), 4.0 * decay)

    # Clicks: moderate-high signal (clear interest)
    for user_id, product_id, created_date in clicks:
        pinfo = product_map.get(product_id, {"brand_id": 0, "color": "unknown"})
        decay = decay_from_datetime(created_date, half_life_days=15)
        add_score(brand_scores, (user_id, pinfo["brand_id"]), 2.0 * decay)
        add_score(color_scores, (user_id, pinfo["color"]), 2.0 * decay)
        add_score(product_scores, (user_id, product_id), 2.5 * decay)

    # Views: moderate weight, use view_count
    for user_id, product_id, view_count, last_viewed in views:
        pinfo = product_map.get(product_id, {"brand_id": 0, "color": "unknown"})
        decay = decay_from_datetime(last_viewed, half_life_days=15)
        add_score(brand_scores, (user_id, pinfo["brand_id"]), 1.0 * view_count * decay)
        add_score(color_scores, (user_id, pinfo["color"]), 1.0 * view_count * decay)
        add_score(product_scores, (user_id, product_id), 1.5 * view_count * decay)

    # Searches: lighter weight; map keyword to brand/color if trùng tên
    for user_id, keyword, created_date in searches:
        kw = keyword.lower()
        decay = decay_from_datetime(created_date, half_life_days=10)
        # Heuristic: nếu keyword trùng tên brand -> cộng điểm brand
        for brand_id, brand_name in BRAND_KEYWORDS.items():
            if brand_name in kw:
                add_score(brand_scores, (user_id, brand_id), 0.8 * decay)
        # Nếu có từ màu phổ biến
        for color in COLOR_KEYWORDS:
            if color in kw:
                add_score(color_scores, (user_id, color), 0.8 * decay)
        # Không map sản phẩm cụ thể ở đây (cần search index), giữ đơn giản

    return brand_scores, color_scores, product_scores


def add_score(store, key, val):
    store[key] = store.get(key, 0.0) + val


BRAND_KEYWORDS = {
    1: "nike",
    2: "adidas",
    3: "puma",
    4: "converse",
    5: "vans",
    6: "new balance",
    7: "reebok",
    8: "fila",
}

COLOR_KEYWORDS = [
    "black",
    "white",
    "red",
    "blue",
    "green",
    "yellow",
    "grey",
    "gray",
    "beige",
    "pink",
    "brown",
    "orange",
]


def fetch_purchases(conn):
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT b.user_id, pp.product_id, b.created_date
            FROM bills b
            JOIN product_bills pb ON b.id = pb.bill_id
            JOIN product_properties pp ON pb.product_properties_id = pp.id
            WHERE b.status IN (0,1)
            """
        )
        return cur.fetchall()


def fetch_views(conn):
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT user_id, product_id, view_count, last_viewed_date
            FROM product_views
            """
        )
        return cur.fetchall()


def fetch_searches(conn):
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT user_id, LOWER(search_keyword), created_date
            FROM search_histories
            """
        )
        return cur.fetchall()


def fetch_clicks(conn):
    """Fetch click behaviors from user_behaviors table"""
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT user_id, product_id, created_date
            FROM user_behaviors
            WHERE behavior_type = 'CLICK'
            """
        )
        return cur.fetchall()


def fetch_add_to_cart(conn):
    """Fetch add-to-cart behaviors from user_behaviors table"""
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT user_id, product_id, created_date
            FROM user_behaviors
            WHERE behavior_type = 'ADD_TO_CART'
            """
        )
        return cur.fetchall()


def upsert_preferences(conn, pref_type, scores):
    sql = """
        INSERT INTO user_preferences (user_id, preference_type, preference_value, preference_score)
        VALUES (%s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE preference_score = VALUES(preference_score),
                                last_updated = CURRENT_TIMESTAMP
    """
    data = []
    for (user_id, value), score in scores.items():
        # Bỏ qua user NULL (guest) hoặc score quá nhỏ
        if user_id is None or score <= 0:
            continue
        data.append((user_id, pref_type, str(value), round(score, 4)))
    if not data:
        return
    with conn.cursor() as cur:
        cur.executemany(sql, data)
    conn.commit()


def main():
    conn = connect_db()
    ensure_table(conn)
    product_map = fetch_products(conn)
    purchases = fetch_purchases(conn)
    views = fetch_views(conn)
    searches = fetch_searches(conn)
    clicks = fetch_clicks(conn)
    add_to_carts = fetch_add_to_cart(conn)

    brand_scores, color_scores, product_scores = aggregate_scores(
        product_map, purchases, views, searches, clicks, add_to_carts
    )

    upsert_preferences(conn, "BRAND", brand_scores)
    upsert_preferences(conn, "COLOR", color_scores)
    upsert_preferences(conn, "PRODUCT", product_scores)
    conn.close()
    print(
        f"✓ Done. Brands: {len(brand_scores)} entries, Colors: {len(color_scores)}, Products: {len(product_scores)}"
    )
    print(f"✓ Integrated: Purchases, Add-to-Cart, Clicks, Views, Searches")
    print(f"✓ Time decay: Purchases(20d), Cart(15d), Click(15d), View(15d), Search(10d)")


if __name__ == "__main__":
    main()


