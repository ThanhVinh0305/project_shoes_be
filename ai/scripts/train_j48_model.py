#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script để train J48 Decision Tree model cho hệ thống gợi ý sản phẩm
Dataset: Tập trung vào giao dịch (sản phẩm nào mua nhiều → gợi ý)
"""

import pandas as pd
import numpy as np
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
import pickle
import os
import pymysql

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

def load_dataset_from_db():
    """Load dataset trực tiếp từ database"""
    conn = get_db_connection()
    
    print("Đang load dataset từ database...")
    
    sql = """
        SELECT 
            b.user_id,
            pp.product_id,
            COALESCE(pb2.brand_id, 0) as brand_id,
            COALESCE(pc.category_id, 0) as category_id,
            COALESCE(p.gender_id, 0) as gender_id,
            COALESCE(p.color, 'unknown') as color,
            p.price,
            pb.amount as purchase_amount,
            CASE 
                WHEN p.price < 2000000 THEN 'low'
                WHEN p.price < 4000000 THEN 'medium'
                ELSE 'high'
            END as price_range,
            COUNT(*) OVER (PARTITION BY pp.product_id) as product_purchase_count,
            COUNT(*) OVER (PARTITION BY b.user_id) as user_purchase_count
        FROM bills b
        JOIN product_bills pb ON b.id = pb.bill_id
        JOIN product_properties pp ON pb.product_properties_id = pp.id
        JOIN products p ON pp.product_id = p.id
        LEFT JOIN product_categories pc ON p.id = pc.product_id
        LEFT JOIN product_brands pb2 ON p.id = pb2.product_id
        WHERE b.status IN (0, 1)  -- CREATED hoặc PURCHASE
        ORDER BY b.created_date DESC
    """
    
    df = pd.read_sql(sql, conn)
    conn.close()
    
    print(f"✓ Đã load {len(df)} records từ database")
    return df

def prepare_features(df):
    """Chuẩn bị features và target"""
    # Tạo target: Sản phẩm nào được mua nhiều (> median) → nên gợi ý
    product_purchase_median = df['product_purchase_count'].median()
    df['will_recommend'] = (df['product_purchase_count'] > product_purchase_median).astype(int)
    
    # Chọn features (chỉ numeric, không dùng price_range string)
    feature_columns = [
        'user_id',
        'product_id',
        'brand_id',
        'category_id',
        'gender_id',
        'product_purchase_count',
        'user_purchase_count'
    ]
    
    # Xử lý color (categorical)
    # Lấy top 10 màu phổ biến nhất
    top_colors = df['color'].value_counts().head(10).index.tolist()
    df['color_encoded'] = df['color'].apply(lambda x: x if x in top_colors else 'other')
    
    # One-hot encode color
    color_dummies = pd.get_dummies(df['color_encoded'], prefix='color')
    df = pd.concat([df, color_dummies], axis=1)
    
    # One-hot encode price_range
    price_dummies = pd.get_dummies(df['price_range'], prefix='price')
    df = pd.concat([df, price_dummies], axis=1)
    
    # Thêm color và price features vào feature_columns
    color_features = [col for col in df.columns if col.startswith('color_')]
    price_features = [col for col in df.columns if col.startswith('price_')]
    
    # Chỉ lấy các cột numeric và one-hot encoded
    all_features = feature_columns + color_features + price_features
    X = df[all_features].copy()
    
    # Đảm bảo tất cả là numeric
    for col in X.columns:
        if X[col].dtype == 'object':
            X[col] = pd.to_numeric(X[col], errors='coerce').fillna(0)
    
    y = df['will_recommend']
    
    return X, y, df

def train_j48_model(X, y):
    """Train J48 Decision Tree model"""
    print("\nĐang train J48 Decision Tree model...")
    
    # Chia train/test (80/20)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    
    print(f"  - Training set: {len(X_train)} samples")
    print(f"  - Test set: {len(X_test)} samples")
    
    # J48 = C4.5 Decision Tree (criterion='entropy')
    model = DecisionTreeClassifier(
        criterion='entropy',  # J48 uses entropy (information gain)
        max_depth=10,         # Giới hạn độ sâu để tránh overfitting
        min_samples_split=20, # Tối thiểu 20 samples để split
        min_samples_leaf=10,  # Tối thiểu 10 samples ở leaf
        random_state=42
    )
    
    # Train
    model.fit(X_train, y_train)
    
    # Evaluate
    y_pred = model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    
    print(f"\n✓ Model đã được train xong!")
    print(f"  - Accuracy: {accuracy:.4f} ({accuracy*100:.2f}%)")
    print(f"\n📊 Classification Report:")
    print(classification_report(y_test, y_pred, target_names=['NO', 'YES']))
    
    return model, X_train, X_test, y_train, y_test

def save_model(model, X_columns):
    """Lưu model và metadata"""
    model_dir = os.path.join(os.path.dirname(__file__), '..', 'models')
    os.makedirs(model_dir, exist_ok=True)
    
    # Lưu model
    model_path = os.path.join(model_dir, 'j48_recommendation_model.pkl')
    with open(model_path, 'wb') as f:
        pickle.dump({
            'model': model,
            'feature_columns': list(X_columns),
            'model_type': 'J48_Decision_Tree'
        }, f)
    
    print(f"\n✓ Model đã được lưu vào: {model_path}")
    return model_path

def print_tree_rules(model, feature_names, max_depth=3):
    """In một số quy tắc từ decision tree"""
    print("\n📋 Một số quy tắc từ Decision Tree (top 5):")
    print("=" * 60)
    
    # Lấy feature importances
    importances = model.feature_importances_
    feature_importance = list(zip(feature_names, importances))
    feature_importance.sort(key=lambda x: x[1], reverse=True)
    
    print("Top 10 Features quan trọng nhất:")
    for i, (feature, importance) in enumerate(feature_importance[:10], 1):
        print(f"  {i}. {feature}: {importance:.4f}")
    
    print("\n" + "=" * 60)

def main():
    """Hàm chính"""
    print("=" * 60)
    print("TRAIN J48 DECISION TREE MODEL")
    print("=" * 60)
    
    # Bước 1: Load dataset
    df = load_dataset_from_db()
    
    if len(df) == 0:
        print("✗ Không có dữ liệu để train!")
        return
    
    # Bước 2: Chuẩn bị features
    print("\nĐang chuẩn bị features...")
    X, y, df_processed = prepare_features(df)
    print(f"✓ Features: {X.shape[1]} features, {len(X)} samples")
    print(f"  - Target distribution: {y.value_counts().to_dict()}")
    
    # Bước 3: Train model
    model, X_train, X_test, y_train, y_test = train_j48_model(X, y)
    
    # Bước 4: In quy tắc
    print_tree_rules(model, X.columns)
    
    # Bước 5: Lưu model
    model_path = save_model(model, X.columns)
    
    print("\n" + "=" * 60)
    print("HOÀN THÀNH!")
    print("=" * 60)
    print(f"\nModel đã sẵn sàng để sử dụng tại: {model_path}")
    print("\nBước tiếp theo:")
    print("  1. Tạo API endpoint để load model và predict")
    print("  2. Tích hợp vào Spring Boot application")

if __name__ == "__main__":
    main()

