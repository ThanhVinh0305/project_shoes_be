#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script để xem thông tin model J48 đã train
"""

import pickle
import os
from sklearn.tree import export_text

try:
    from sklearn.tree import plot_tree
    import matplotlib.pyplot as plt
    HAS_MATPLOTLIB = True
except ImportError:
    HAS_MATPLOTLIB = False

def load_model():
    """Load model từ file .pkl"""
    model_path = os.path.join(os.path.dirname(__file__), '..', 'models', 'j48_recommendation_model.pkl')
    
    if not os.path.exists(model_path):
        print(f"✗ Không tìm thấy file model tại: {model_path}")
        return None
    
    print(f"Đang load model từ: {model_path}\n")
    
    with open(model_path, 'rb') as f:
        model_data = pickle.load(f)
    
    return model_data

def view_model_info(model_data):
    """Hiển thị thông tin model"""
    print("=" * 60)
    print("THÔNG TIN MODEL J48")
    print("=" * 60)
    
    model = model_data['model']
    feature_columns = model_data['feature_columns']
    model_type = model_data.get('model_type', 'Unknown')
    
    print(f"\n📋 Thông tin cơ bản:")
    print(f"  - Loại model: {model_type}")
    print(f"  - Số features: {len(feature_columns)}")
    print(f"  - Features: {', '.join(feature_columns[:10])}...")
    
    print(f"\n📊 Thông số model:")
    print(f"  - Criterion: {model.criterion}")
    print(f"  - Max depth: {model.max_depth}")
    print(f"  - Min samples split: {model.min_samples_split}")
    print(f"  - Min samples leaf: {model.min_samples_leaf}")
    print(f"  - Number of features: {model.n_features_in_}")
    print(f"  - Number of classes: {model.n_classes_}")
    
    # Feature importances
    print(f"\n🔝 Top 15 Features quan trọng nhất:")
    importances = model.feature_importances_
    feature_importance = list(zip(feature_columns, importances))
    feature_importance.sort(key=lambda x: x[1], reverse=True)
    
    for i, (feature, importance) in enumerate(feature_importance[:15], 1):
        bar = '█' * int(importance * 50)  # Bar chart
        print(f"  {i:2d}. {feature:30s} {importance:6.4f} {bar}")
    
    return model, feature_columns

def view_decision_tree_rules(model, feature_columns):
    """Hiển thị quy tắc từ decision tree"""
    print(f"\n" + "=" * 60)
    print("QUY TẮC DECISION TREE (Text Format)")
    print("=" * 60)
    
    # Export tree dưới dạng text
    tree_rules = export_text(
        model, 
        feature_names=feature_columns,
        max_depth=5,  # Chỉ hiển thị 5 level đầu
        spacing=2
    )
    
    print(tree_rules)
    
    print(f"\n💡 Lưu ý: Chỉ hiển thị 5 level đầu của tree.")
    print(f"   Để xem toàn bộ, tăng max_depth hoặc visualize tree.")

def visualize_tree(model, feature_columns, max_depth=3):
    """Visualize decision tree (nếu có matplotlib)"""
    if not HAS_MATPLOTLIB:
        print(f"\n⚠️  Không có matplotlib để visualize tree.")
        print(f"   Cài đặt: pip install matplotlib")
        return
    
    try:
        print(f"\n" + "=" * 60)
        print("VISUALIZE DECISION TREE")
        print("=" * 60)
        
        plt.figure(figsize=(20, 10))
        plot_tree(
            model,
            feature_names=feature_columns,
            max_depth=max_depth,
            filled=True,
            rounded=True,
            fontsize=8
        )
        
        output_path = os.path.join(os.path.dirname(__file__), '..', 'models', 'decision_tree_visualization.png')
        plt.savefig(output_path, dpi=300, bbox_inches='tight')
        print(f"✓ Đã lưu visualization vào: {output_path}")
        print(f"   (Chỉ hiển thị {max_depth} level đầu)")
        
        # Cũng có thể hiển thị trực tiếp nếu có GUI
        # plt.show()
        
    except Exception as e:
        print(f"⚠️  Không thể visualize tree: {e}")
        print(f"   (Có thể cần cài: pip install matplotlib)")

def view_model_structure(model):
    """Xem cấu trúc tree"""
    print(f"\n" + "=" * 60)
    print("CẤU TRÚC TREE")
    print("=" * 60)
    
    n_nodes = model.tree_.node_count
    children_left = model.tree_.children_left
    children_right = model.tree_.children_right
    feature = model.tree_.feature
    threshold = model.tree_.threshold
    
    print(f"  - Tổng số nodes: {n_nodes}")
    print(f"  - Số leaf nodes: {sum(1 for i in range(n_nodes) if children_left[i] == children_right[i])}")
    print(f"  - Số decision nodes: {n_nodes - sum(1 for i in range(n_nodes) if children_left[i] == children_right[i])}")

def main():
    """Hàm chính"""
    # Load model
    model_data = load_model()
    if not model_data:
        return
    
    # Hiển thị thông tin
    model, feature_columns = view_model_info(model_data)
    
    # Xem cấu trúc tree
    view_model_structure(model)
    
    # Hiển thị quy tắc
    view_decision_tree_rules(model, feature_columns)
    
    # Visualize (optional)
    try:
        visualize_tree(model, feature_columns, max_depth=3)
    except:
        pass
    
    print("\n" + "=" * 60)
    print("HOÀN THÀNH!")
    print("=" * 60)

if __name__ == "__main__":
    main()

