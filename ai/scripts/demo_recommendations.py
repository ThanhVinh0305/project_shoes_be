#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script demo để test hệ thống gợi ý
- Generate recommendations cho một vài users để demo
- Hiển thị recommendations của 1 user
"""

import pickle
import pymysql
import pandas as pd
import os
from datetime import datetime

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

def load_model():
    """Load J48 model từ file"""
    model_path = os.path.join(os.path.dirname(__file__), '..', 'models', 'j48_recommendation_model.pkl')
    
    if not os.path.exists(model_path):
        print(f"✗ Không tìm thấy model tại: {model_path}")
        return None
    
    with open(model_path, 'rb') as f:
        model_data = pickle.load(f)
    
    return model_data['model'], model_data['feature_columns']

def get_product_purchase_counts():
    """Tính số lần mua của mỗi sản phẩm"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        sql = """
            SELECT 
                pp.product_id,
                COUNT(*) as purchase_count
            FROM product_bills pb
            JOIN product_properties pp ON pb.product_properties_id = pp.id
            JOIN bills b ON pb.bill_id = b.id
            WHERE b.status IN (0, 1)
            GROUP BY pp.product_id
        """
        cursor.execute(sql)
        result = {row[0]: row[1] for row in cursor.fetchall()}
        return result
    finally:
        cursor.close()
        conn.close()

def get_user_purchase_count(user_id):
    """Tính số lần mua của user"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        cursor.execute("""
            SELECT COUNT(*) 
            FROM bills 
            WHERE user_id = %s AND status IN (0, 1)
        """, (user_id,))
        return cursor.fetchone()[0] or 0
    finally:
        cursor.close()
        conn.close()

def get_all_products():
    """Lấy danh sách tất cả products với features"""
    conn = get_db_connection()
    
    sql = """
        SELECT 
            p.id as product_id,
            p.name as product_name,
            p.price,
            COALESCE(pb2.brand_id, 0) as brand_id,
            COALESCE(pc.category_id, 0) as category_id,
            COALESCE(p.gender_id, 0) as gender_id,
            COALESCE(p.color, 'unknown') as color
        FROM products p
        LEFT JOIN product_categories pc ON p.id = pc.product_id
        LEFT JOIN product_brands pb2 ON p.id = pb2.product_id
        ORDER BY p.id
    """
    
    df = pd.read_sql(sql, conn)
    conn.close()
    
    return df

def prepare_features_for_product(product_row, product_purchase_count, user_purchase_count, user_id):
    """Chuẩn bị features cho 1 product để predict"""
    features = {
        'user_id': user_id,
        'product_id': product_row['product_id'],
        'brand_id': product_row['brand_id'],
        'category_id': product_row['category_id'],
        'gender_id': product_row['gender_id'],
        'product_purchase_count': product_purchase_count,
        'user_purchase_count': user_purchase_count,
    }
    
    # Price range
    price = product_row['price']
    if price < 2000000:
        price_range = 'low'
    elif price < 4000000:
        price_range = 'medium'
    else:
        price_range = 'high'
    
    # One-hot encode color
    top_colors = ['Black', 'Blue', 'White', 'Grey', 'Red', 'Multicolor', 'Pink', 'Black/Blue', 'Black/White']
    color = str(product_row['color'])
    color_features = {f'color_{c}': 1 if c in color else 0 for c in top_colors}
    color_features['color_other'] = 1 if not any(c in color for c in top_colors) else 0
    
    # One-hot encode price_range
    price_features = {
        'price_range_low': 1 if price_range == 'low' else 0,
        'price_range_medium': 1 if price_range == 'medium' else 0,
        'price_range_high': 1 if price_range == 'high' else 0,
    }
    
    all_features = {**features, **color_features, **price_features}
    return all_features

def get_recommendations_for_user(user_id, model, feature_columns, products_df, product_purchase_counts, user_purchase_count):
    """Lấy recommendations cho 1 user"""
    recommendations = []
    
    for _, product_row in products_df.iterrows():
        product_id = product_row['product_id']
        product_purchase_count = product_purchase_counts.get(product_id, 0)
        
        # Prepare features
        features_dict = prepare_features_for_product(
            product_row, product_purchase_count, user_purchase_count, user_id
        )
        
        # Convert to feature vector
        feature_vector = [features_dict.get(col, 0) for col in feature_columns]
        
        # Predict
        score = model.predict([feature_vector])[0]
        
        if score == 1:  # Chỉ lấy products được recommend
            recommendations.append({
                'product_id': product_id,
                'product_name': product_row['product_name'],
                'price': product_row['price'],
                'purchase_count': product_purchase_count,
                'score': float(score)
            })
    
    # Sort by purchase_count descending
    recommendations.sort(key=lambda x: x['purchase_count'], reverse=True)
    
    return recommendations

def demo_recommendations_for_user(user_id):
    """Demo recommendations cho 1 user"""
    print("=" * 60)
    print(f"DEMO RECOMMENDATIONS CHO USER ID: {user_id}")
    print("=" * 60)
    
    # Load model
    print("\n1. Đang load model...")
    model, feature_columns = load_model()
    if not model:
        return
    print(f"   ✓ Model đã load")
    
    # Get data
    print("\n2. Đang lấy dữ liệu...")
    products_df = get_all_products()
    product_purchase_counts = get_product_purchase_counts()
    user_purchase_count = get_user_purchase_count(user_id)
    
    print(f"   ✓ Products: {len(products_df)}")
    print(f"   ✓ User purchase count: {user_purchase_count}")
    
    # Get recommendations
    print("\n3. Đang tính recommendations...")
    recommendations = get_recommendations_for_user(
        user_id, model, feature_columns, products_df,
        product_purchase_counts, user_purchase_count
    )
    
    print(f"\n✓ Tìm thấy {len(recommendations)} sản phẩm được gợi ý")
    
    # Display top 10
    print("\n" + "=" * 60)
    print("TOP 10 RECOMMENDATIONS:")
    print("=" * 60)
    
    for i, rec in enumerate(recommendations[:10], 1):
        print(f"\n{i}. {rec['product_name']}")
        print(f"   - Product ID: {rec['product_id']}")
        print(f"   - Giá: {rec['price']:,} VNĐ")
        print(f"   - Số lần mua: {rec['purchase_count']} lần")
        print(f"   - Score: {rec['score']}")
    
    return recommendations

def save_recommendations_to_db(recommendations, user_id):
    """Lưu recommendations vào database"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        # Xóa recommendations cũ
        cursor.execute("DELETE FROM recommendations WHERE user_id = %s", (user_id,))
        
        # Insert mới
        if recommendations:
            sql = """
                INSERT INTO recommendations 
                (user_id, product_id, recommendation_type, recommendation_score, reason, created_date)
                VALUES (%s, %s, %s, %s, %s, NOW())
            """
            
            values = []
            for rec in recommendations[:50]:  # Top 50
                reason = f"Sản phẩm được mua {rec['purchase_count']} lần"
                values.append((
                    user_id,
                    rec['product_id'],
                    'CONTENT_BASED',
                    rec['score'],
                    reason
                ))
            
            cursor.executemany(sql, values)
        
        conn.commit()
        print(f"\n✓ Đã lưu {len(recommendations[:50])} recommendations vào database")
        return len(recommendations)
    except Exception as e:
        conn.rollback()
        print(f"✗ Lỗi: {e}")
        return 0
    finally:
        cursor.close()
        conn.close()

def view_recommendations_from_db(user_id):
    """Xem recommendations từ database"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        sql = """
            SELECT 
                r.product_id,
                p.name as product_name,
                p.price,
                r.recommendation_score,
                r.reason,
                r.created_date
            FROM recommendations r
            JOIN products p ON r.product_id = p.id
            WHERE r.user_id = %s
            ORDER BY r.recommendation_score DESC
            LIMIT 10
        """
        cursor.execute(sql, (user_id,))
        results = cursor.fetchall()
        
        if not results:
            print(f"✗ Không có recommendations cho user {user_id}")
            return
        
        print(f"\n✓ Tìm thấy {len(results)} recommendations từ database:")
        print("=" * 60)
        
        for i, row in enumerate(results, 1):
            print(f"\n{i}. {row[1]}")  # product_name
            print(f"   - Product ID: {row[0]}")
            print(f"   - Giá: {row[2]:,} VNĐ")
            print(f"   - Score: {row[3]}")
            print(f"   - Lý do: {row[4]}")
    
    finally:
        cursor.close()
        conn.close()

def main():
    """Hàm chính"""
    import sys
    
    if len(sys.argv) < 2:
        print("Usage:")
        print("  python3 demo_recommendations.py <user_id>           # Xem recommendations cho user")
        print("  python3 demo_recommendations.py <user_id> --save    # Tính và lưu recommendations")
        print("  python3 demo_recommendations.py <user_id> --view    # Xem từ database")
        print("\nVí dụ:")
        print("  python3 demo_recommendations.py 3")
        print("  python3 demo_recommendations.py 3 --save")
        print("  python3 demo_recommendations.py 3 --view")
        return
    
    user_id = int(sys.argv[1])
    mode = sys.argv[2] if len(sys.argv) > 2 else None
    
    if mode == '--save':
        # Tính và lưu recommendations
        model, feature_columns = load_model()
        if not model:
            return
        
        products_df = get_all_products()
        product_purchase_counts = get_product_purchase_counts()
        user_purchase_count = get_user_purchase_count(user_id)
        
        recommendations = get_recommendations_for_user(
            user_id, model, feature_columns, products_df,
            product_purchase_counts, user_purchase_count
        )
        
        save_recommendations_to_db(recommendations, user_id)
        
    elif mode == '--view':
        # Xem từ database
        view_recommendations_from_db(user_id)
    else:
        # Demo (tính và hiển thị, không lưu)
        demo_recommendations_for_user(user_id)

if __name__ == "__main__":
    main()

