-- Thêm các field gender và color vào các bảng
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/add_gender_and_color_fields.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Thêm gender vào bảng users
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS gender VARCHAR(20) DEFAULT NULL COMMENT 'MALE, FEMALE, OTHER';

-- Thêm gender và color vào bảng products
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS gender VARCHAR(20) DEFAULT NULL COMMENT 'MALE, FEMALE, UNISEX',
ADD COLUMN IF NOT EXISTS color VARCHAR(50) DEFAULT NULL COMMENT 'Màu sắc sản phẩm';

-- Thêm gender vào bảng product_properties
ALTER TABLE product_properties 
ADD COLUMN IF NOT EXISTS gender VARCHAR(20) DEFAULT NULL COMMENT 'MALE, FEMALE, UNISEX';

-- Tạo indexes cho các field mới
-- Index cho products.gender
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'products' 
               AND index_name = 'idx_products_gender');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Index idx_products_gender already exists" as message;',
    'CREATE INDEX idx_products_gender ON products(gender);');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Index cho products.color
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'products' 
               AND index_name = 'idx_products_color');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Index idx_products_color already exists" as message;',
    'CREATE INDEX idx_products_color ON products(color);');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Index cho users.gender
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'users' 
               AND index_name = 'idx_users_gender');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Index idx_users_gender already exists" as message;',
    'CREATE INDEX idx_users_gender ON users(gender);');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Gender and color fields added successfully!' as message;

