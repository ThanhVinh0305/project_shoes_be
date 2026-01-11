#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script để tính recommendations sẵn và lưu vào database (Proactive approach)
Chạy định kỳ (ví dụ: 2h sáng mỗi ngày) để update recommendations cho tất cả users
"""

import pickle
import pymysql
import pandas as pd
import numpy as np
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

def get_all_users():
    """Lấy danh sách tất cả users"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        cursor.execute("SELECT id FROM users ORDER BY id")
        user_ids = [row[0] for row in cursor.fetchall()]
        return user_ids
    finally:
        cursor.close()
        conn.close()

def get_all_products():
    """Lấy danh sách tất cả products với features"""
    conn = get_db_connection()
    
    sql = """
        SELECT 
            p.id as product_id,
            COALESCE(pb2.brand_id, 0) as brand_id,
            COALESCE(pc.category_id, 0) as category_id,
            COALESCE(p.gender_id, 0) as gender_id,
            COALESCE(p.color, 'unknown') as color,
            p.price
        FROM products p
        LEFT JOIN product_categories pc ON p.id = pc.product_id
        LEFT JOIN product_brands pb2 ON p.id = pb2.product_id
        ORDER BY p.id
    """
    
    df = pd.read_sql(sql, conn)
    conn.close()
    
    return df

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

def prepare_features_for_product(product_row, product_purchase_count, user_purchase_count, user_id):
    """Chuẩn bị features cho 1 product để predict"""
    # Base features
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
    
    # One-hot encode color (top colors)
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
    
    # Combine all features
    all_features = {**features, **color_features, **price_features}
    
    return all_features

def predict_recommendations_for_user(user_id, model, feature_columns, products_df, product_purchase_counts, user_purchase_count):
    """Predict recommendations cho 1 user"""
    recommendations = []
    
    # Với mỗi product
    for _, product_row in products_df.iterrows():
        product_id = product_row['product_id']
        product_purchase_count = product_purchase_counts.get(product_id, 0)
        
        # Prepare features
        features_dict = prepare_features_for_product(
            product_row, product_purchase_count, user_purchase_count, user_id
        )
        
        # Convert to feature vector (theo thứ tự feature_columns)
        feature_vector = [features_dict.get(col, 0) for col in feature_columns]
        
        # Predict
        score = model.predict([feature_vector])[0]
        probability = model.predict_proba([feature_vector])[0][1] if hasattr(model, 'predict_proba') else float(score)
        
        if score == 1:  # Chỉ lưu products được recommend
            recommendations.append({
                'user_id': user_id,
                'product_id': product_id,
                'score': float(probability),
                'purchase_count': product_purchase_count
            })
    
    # Sort by score descending
    recommendations.sort(key=lambda x: x['score'], reverse=True)
    
    return recommendations

def save_recommendations_to_db(recommendations, user_id):
    """Lưu recommendations vào database"""
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        # Xóa recommendations cũ của user này
        cursor.execute("DELETE FROM recommendations WHERE user_id = %s", (user_id,))
        
        # Insert recommendations mới
        if recommendations:
            sql = """
                INSERT INTO recommendations 
                (user_id, product_id, recommendation_type, recommendation_score, reason, created_date)
                VALUES (%s, %s, %s, %s, %s, NOW())
            """
            
            values = []
            for rec in recommendations[:50]:  # Top 50 recommendations
                reason = f"Sản phẩm được mua {rec['purchase_count']} lần"
                values.append((
                    rec['user_id'],
                    rec['product_id'],
                    'CONTENT_BASED',  # Hoặc 'HYBRID'
                    rec['score'],
                    reason
                ))
            
            cursor.executemany(sql, values)
        
        conn.commit()
        return len(recommendations)
    except Exception as e:
        conn.rollback()
        print(f"✗ Lỗi khi lưu recommendations cho user {user_id}: {e}")
        return 0
    finally:
        cursor.close()
        conn.close()

def generate_all_recommendations():
    """Tính recommendations cho tất cả users"""
    print("=" * 60)
    print("GENERATE RECOMMENDATIONS CHO TẤT CẢ USERS")
    print("=" * 60)
    
    # Load model
    print("\n1. Đang load model...")
    model, feature_columns = load_model()
    if not model:
        return
    print(f"   ✓ Model đã load: {len(feature_columns)} features")
    
    # Get all users
    print("\n2. Đang lấy danh sách users...")
    user_ids = get_all_users()
    print(f"   ✓ Tìm thấy {len(user_ids)} users")
    
    # Get all products
    print("\n3. Đang lấy danh sách products...")
    products_df = get_all_products()
    print(f"   ✓ Tìm thấy {len(products_df)} products")
    
    # Get product purchase counts
    print("\n4. Đang tính product purchase counts...")
    product_purchase_counts = get_product_purchase_counts()
    print(f"   ✓ Đã tính purchase counts cho {len(product_purchase_counts)} products")
    
    # Generate recommendations cho mỗi user
    print(f"\n5. Đang tính recommendations cho {len(user_ids)} users...")
    total_recommendations = 0
    
    for i, user_id in enumerate(user_ids, 1):
        try:
            # Get user purchase count
            user_purchase_count = get_user_purchase_count(user_id)
            
            # Predict recommendations
            recommendations = predict_recommendations_for_user(
                user_id, model, feature_columns, products_df,
                product_purchase_counts, user_purchase_count
            )
            
            # Save to database
            saved_count = save_recommendations_to_db(recommendations, user_id)
            total_recommendations += saved_count
            
            if i % 100 == 0:
                print(f"   Đã xử lý {i}/{len(user_ids)} users ({i*100//len(user_ids)}%) - {total_recommendations:,} recommendations")
        
        except Exception as e:
            print(f"   ✗ Lỗi với user {user_id}: {e}")
            continue
    
    print(f"\n✓ Hoàn thành!")
    print(f"   - Đã xử lý: {len(user_ids)} users")
    print(f"   - Tổng recommendations: {total_recommendations:,}")
    
    # Thống kê
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT COUNT(*) FROM recommendations")
        total = cursor.fetchone()[0]
        cursor.execute("SELECT COUNT(DISTINCT user_id) FROM recommendations")
        users_with_recs = cursor.fetchone()[0]
        print(f"   - Recommendations trong DB: {total:,}")
        print(f"   - Users có recommendations: {users_with_recs}")
    finally:
        cursor.close()
        conn.close()

def main():
    """Hàm chính"""
    start_time = datetime.now()
    generate_all_recommendations()
    end_time = datetime.now()
    duration = (end_time - start_time).total_seconds()
    print(f"\n⏱️  Thời gian thực hiện: {duration:.2f} giây ({duration/60:.2f} phút)")

if __name__ == "__main__":
    main()


