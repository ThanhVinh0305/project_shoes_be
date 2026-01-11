# AI Recommendation System

Folder này chứa các script và model cho hệ thống gợi ý sản phẩm thông minh sử dụng LLM và J48 Decision Tree.

## Cấu trúc

```
ai/
├── scripts/          # Python scripts
│   ├── generate_transaction_data.py  # Tạo dữ liệu giao dịch
│   ├── export_dataset.py             # Export dataset từ DB để train
│   └── train_j48_model.py            # Train J48 model
├── models/           # Saved models (J48, etc.)
├── data/             # Dataset files (CSV, ARFF)
└── README.md         # File này
```

## Yêu cầu

```bash
pip install pymysql faker
```

## Sử dụng

### 1. Tạo dữ liệu giao dịch

```bash
cd ai/scripts
python3 generate_transaction_data.py
```

Script này sẽ:
- Tạo 10 users mới
- Tạo 1000 giao dịch phân bổ cho các users
- Một số sản phẩm sẽ được mua nhiều hơn (để tạo pattern cho J48)

### 2. Export dataset để train

```bash
python3 export_dataset.py
```

Export dữ liệu từ `bills` và `product_bills` thành CSV/ARFF format cho Weka.

### 3. Train J48 model

```bash
python3 train_j48_model.py
```

Train Decision Tree model và lưu vào `ai/models/`.

## Kiến trúc

- **Dataset**: Tập trung vào giao dịch (bills) - sản phẩm nào mua nhiều → gợi ý
- **Model**: J48 Decision Tree để phân loại sản phẩm nên gợi ý
- **LLM**: Tích hợp sau để giải thích recommendations

## Lưu ý

- Scripts chạy trực tiếp từ Spring Boot project (không cần service riêng)
- Database connection: localhost:3306
- Cần có đủ product_properties trong database


