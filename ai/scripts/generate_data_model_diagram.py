#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script để tạo sơ đồ mô hình dữ liệu dưới dạng hình ảnh
Chỉ hiển thị tiêu đề các node
"""

import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch
import os

def create_database_schema_diagram():
    """Tạo sơ đồ database schema"""
    fig, ax = plt.subplots(1, 1, figsize=(14, 10))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 10)
    ax.axis('off')
    
    # Định nghĩa các node
    nodes = {
        'users': (2, 8),
        'bills': (2, 6),
        'product_bills': (2, 4),
        'product_properties': (2, 2),
        'products': (5, 3),
        'product_brands': (7, 4),
        'product_categories': (7, 2),
        'recommendations': (5, 7)
    }
    
    # Vẽ các node (chỉ tiêu đề)
    for name, (x, y) in nodes.items():
        # Tạo box
        box = FancyBboxPatch(
            (x-0.8, y-0.3), 1.6, 0.6,
            boxstyle="round,pad=0.1",
            edgecolor='black',
            facecolor='lightblue',
            linewidth=2
        )
        ax.add_patch(box)
        
        # Thêm text (tiêu đề)
        ax.text(x, y, name, ha='center', va='center', 
                fontsize=12, fontweight='bold')
    
    # Vẽ các mũi tên (quan hệ)
    arrows = [
        ('users', 'bills'),
        ('bills', 'product_bills'),
        ('product_bills', 'product_properties'),
        ('product_properties', 'products'),
        ('products', 'product_brands'),
        ('products', 'product_categories'),
        ('users', 'recommendations'),
        ('products', 'recommendations')
    ]
    
    for start, end in arrows:
        x1, y1 = nodes[start]
        x2, y2 = nodes[end]
        
        arrow = FancyArrowPatch(
            (x1, y1-0.3), (x2, y2+0.3),
            arrowstyle='->', mutation_scale=20,
            linewidth=1.5, color='gray'
        )
        ax.add_patch(arrow)
    
    ax.set_title('Database Schema - Hệ Thống Gợi Ý', 
                 fontsize=16, fontweight='bold', pad=20)
    
    # Lưu file
    output_dir = os.path.join(os.path.dirname(__file__), '..', 'models')
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, 'database_schema.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Đã tạo: {output_path}")
    plt.close()

def create_data_flow_diagram():
    """Tạo sơ đồ luồng dữ liệu"""
    fig, ax = plt.subplots(1, 1, figsize=(16, 12))
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 10)
    ax.axis('off')
    
    # Training Phase (bên trái)
    training_nodes = {
        'MySQL\nDatabase': (2, 8),
        'Export\nDataset': (2, 6),
        'CSV Dataset': (2, 4),
        'Preprocessing\nOne-hot encoding': (2, 2),
        'Train J48\nModel': (5, 3),
        'Model File\n.pkl': (5, 1)
    }
    
    # Prediction Phase (bên phải)
    prediction_nodes = {
        'User\nRequest': (9, 8),
        'Load\nModel': (9, 6),
        'Query\nMySQL': (9, 4),
        'Predict\nScores': (9, 2),
        'Recommendations\nJSON': (9, 0.5)
    }
    
    # Vẽ Training Phase
    for name, (x, y) in training_nodes.items():
        box = FancyBboxPatch(
            (x-1, y-0.4), 2, 0.8,
            boxstyle="round,pad=0.1",
            edgecolor='black',
            facecolor='lightgreen',
            linewidth=2
        )
        ax.add_patch(box)
        ax.text(x, y, name, ha='center', va='center', 
                fontsize=10, fontweight='bold')
    
    # Vẽ Prediction Phase
    for name, (x, y) in prediction_nodes.items():
        box = FancyBboxPatch(
            (x-1, y-0.4), 2, 0.8,
            boxstyle="round,pad=0.1",
            edgecolor='black',
            facecolor='lightyellow',
            linewidth=2
        )
        ax.add_patch(box)
        ax.text(x, y, name, ha='center', va='center', 
                fontsize=10, fontweight='bold')
    
    # Vẽ mũi tên Training
    training_arrows = [
        ((2, 7.6), (2, 6.4)),
        ((2, 5.6), (2, 4.4)),
        ((2, 3.6), (2, 2.4)),
        ((3, 2), (4, 3)),
        ((5, 2.6), (5, 1.4))
    ]
    
    for (x1, y1), (x2, y2) in training_arrows:
        arrow = FancyArrowPatch(
            (x1, y1), (x2, y2),
            arrowstyle='->', mutation_scale=20,
            linewidth=1.5, color='green'
        )
        ax.add_patch(arrow)
    
    # Vẽ mũi tên Prediction
    prediction_arrows = [
        ((9, 7.6), (9, 6.4)),
        ((9, 5.6), (9, 4.4)),
        ((9, 3.6), (9, 2.4)),
        ((9, 1.6), (9, 0.9))
    ]
    
    for (x1, y1), (x2, y2) in prediction_arrows:
        arrow = FancyArrowPatch(
            (x1, y1), (x2, y2),
            arrowstyle='->', mutation_scale=20,
            linewidth=1.5, color='orange'
        )
        ax.add_patch(arrow)
    
    # Thêm label
    ax.text(2, 9.5, 'TRAINING PHASE (Offline)', 
            ha='center', fontsize=12, fontweight='bold', color='green')
    ax.text(9, 9.5, 'PREDICTION PHASE (Online)', 
            ha='center', fontsize=12, fontweight='bold', color='orange')
    
    ax.set_title('Data Flow - Hệ Thống Gợi Ý', 
                 fontsize=16, fontweight='bold', pad=20)
    
    # Lưu file
    output_dir = os.path.join(os.path.dirname(__file__), '..', 'models')
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, 'data_flow.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Đã tạo: {output_path}")
    plt.close()

def create_decision_tree_diagram():
    """Tạo sơ đồ decision tree"""
    fig, ax = plt.subplots(1, 1, figsize=(12, 8))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 10)
    ax.axis('off')
    
    # Root node
    root_box = FancyBboxPatch(
        (4, 8), 2, 0.8,
        boxstyle="round,pad=0.1",
        edgecolor='black',
        facecolor='lightcoral',
        linewidth=2
    )
    ax.add_patch(root_box)
    ax.text(5, 8.4, 'Root Node\n15,154 samples', 
            ha='center', va='center', fontsize=10, fontweight='bold')
    
    # Decision
    ax.text(5, 7.2, 'product_purchase_count\n<= 109.5?', 
            ha='center', fontsize=9, fontweight='bold', 
            bbox=dict(boxstyle='round', facecolor='yellow', alpha=0.5))
    
    # Left node
    left_box = FancyBboxPatch(
        (1, 5), 2, 0.8,
        boxstyle="round,pad=0.1",
        edgecolor='black',
        facecolor='lightblue',
        linewidth=2
    )
    ax.add_patch(left_box)
    ax.text(2, 5.4, 'Left\n<= 109.5\nNO', 
            ha='center', va='center', fontsize=10, fontweight='bold')
    
    # Right node
    right_box = FancyBboxPatch(
        (7, 5), 2, 0.8,
        boxstyle="round,pad=0.1",
        edgecolor='black',
        facecolor='lightgreen',
        linewidth=2
    )
    ax.add_patch(right_box)
    ax.text(8, 5.4, 'Right\n> 109.5\nYES', 
            ha='center', va='center', fontsize=10, fontweight='bold')
    
    # Arrows
    arrow1 = FancyArrowPatch(
        (4.5, 7.8), (2.5, 5.8),
        arrowstyle='->', mutation_scale=20,
        linewidth=2, color='blue'
    )
    ax.add_patch(arrow1)
    ax.text(3, 6.5, 'YES', ha='center', fontsize=9, color='blue', fontweight='bold')
    
    arrow2 = FancyArrowPatch(
        (5.5, 7.8), (7.5, 5.8),
        arrowstyle='->', mutation_scale=20,
        linewidth=2, color='green'
    )
    ax.add_patch(arrow2)
    ax.text(7, 6.5, 'NO', ha='center', fontsize=9, color='green', fontweight='bold')
    
    ax.set_title('J48 Decision Tree Structure', 
                 fontsize=16, fontweight='bold', pad=20)
    
    # Lưu file
    output_dir = os.path.join(os.path.dirname(__file__), '..', 'models')
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, 'decision_tree_structure.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Đã tạo: {output_path}")
    plt.close()

def main():
    """Hàm chính"""
    print("=" * 60)
    print("TẠO SƠ ĐỒ MÔ HÌNH DỮ LIỆU")
    print("=" * 60)
    
    print("\n1. Đang tạo Database Schema diagram...")
    create_database_schema_diagram()
    
    print("\n2. Đang tạo Data Flow diagram...")
    create_data_flow_diagram()
    
    print("\n3. Đang tạo Decision Tree diagram...")
    create_decision_tree_diagram()
    
    print("\n" + "=" * 60)
    print("HOÀN THÀNH!")
    print("=" * 60)
    print("\nCác file đã được tạo trong: ai/models/")
    print("  - database_schema.png")
    print("  - data_flow.png")
    print("  - decision_tree_structure.png")

if __name__ == "__main__":
    main()

