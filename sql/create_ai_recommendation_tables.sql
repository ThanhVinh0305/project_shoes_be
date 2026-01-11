-- SQL Script để tạo các bảng cho AI Recommendation System
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/create_ai_recommendation_tables.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Bảng 1: user_behaviors - Ghi nhận hành vi người dùng
CREATE TABLE IF NOT EXISTS user_behaviors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    behavior_type ENUM('VIEW', 'CLICK', 'SEARCH', 'ADD_TO_CART', 'PURCHASE', 'RATING') NOT NULL,
    behavior_data JSON DEFAULT NULL, -- Lưu thêm thông tin chi tiết (search keyword, rating value, etc.)
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_behavior_type (behavior_type),
    INDEX idx_created_date (created_date),
    INDEX idx_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng 2: search_histories - Lịch sử tìm kiếm
CREATE TABLE IF NOT EXISTS search_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL, -- NULL nếu là guest user
    search_keyword VARCHAR(255) NOT NULL,
    search_filters JSON DEFAULT NULL, -- Lưu filters (brand, category, price range, etc.)
    result_count INT DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_keyword (search_keyword),
    INDEX idx_created_date (created_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng 3: product_views - Chi tiết lượt xem sản phẩm
CREATE TABLE IF NOT EXISTS product_views (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL,
    product_id BIGINT NOT NULL,
    view_duration INT DEFAULT 0, -- Thời gian xem (giây)
    view_count INT DEFAULT 1,
    last_viewed_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_last_viewed (last_viewed_date),
    UNIQUE KEY unique_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng 4: user_preferences - Sở thích người dùng (được tính toán từ AI)
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preference_type ENUM('BRAND', 'CATEGORY', 'PRICE_RANGE', 'COLOR', 'GENDER', 'STYLE') NOT NULL,
    preference_value VARCHAR(255) NOT NULL,
    preference_score DECIMAL(5,2) DEFAULT 0.00, -- Điểm số từ 0-100
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_preference_type (preference_type),
    INDEX idx_score (preference_score),
    UNIQUE KEY unique_user_preference (user_id, preference_type, preference_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng 5: recommendations - Kết quả gợi ý từ AI
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    recommendation_type ENUM('COLLABORATIVE_FILTERING', 'CONTENT_BASED', 'HYBRID', 'TRENDING', 'SIMILAR_PRODUCTS') NOT NULL,
    recommendation_score DECIMAL(10,6) DEFAULT 0.000000, -- Điểm số gợi ý
    reason TEXT DEFAULT NULL, -- Lý do gợi ý (VD: "Người dùng khác cũng mua", "Sản phẩm tương tự")
    is_shown BOOLEAN DEFAULT FALSE, -- Đã hiển thị cho user chưa
    is_clicked BOOLEAN DEFAULT FALSE, -- User có click vào không
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_type (recommendation_type),
    INDEX idx_score (recommendation_score),
    INDEX idx_created_date (created_date),
    INDEX idx_user_type (user_id, recommendation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

