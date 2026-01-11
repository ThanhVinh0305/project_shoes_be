-- Tạo bảng genders và migrate dữ liệu
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/create_genders_table.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Bước 1: Tạo bảng genders
CREATE TABLE IF NOT EXISTS genders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'MALE, FEMALE, UNISEX, OTHER',
    display_name VARCHAR(100) DEFAULT NULL COMMENT 'Tên hiển thị: Nam, Nữ, Unisex, Khác',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bước 2: Insert dữ liệu genders
INSERT INTO genders (name, display_name) VALUES
('MALE', 'Nam'),
('FEMALE', 'Nữ'),
('UNISEX', 'Unisex'),
('OTHER', 'Khác')
ON DUPLICATE KEY UPDATE display_name=VALUES(display_name);

-- Bước 3: Thêm cột gender_id vào bảng products (tạm thời giữ nguyên cột gender cũ)
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'products' 
               AND column_name = 'gender_id');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Column gender_id already exists in products" as message;',
    'ALTER TABLE products ADD COLUMN gender_id BIGINT DEFAULT NULL COMMENT ''Foreign key to genders.id'';');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Bước 4: Migrate dữ liệu từ gender (VARCHAR) sang gender_id (BIGINT)
UPDATE products p
LEFT JOIN genders g ON UPPER(TRIM(p.gender)) COLLATE utf8mb4_unicode_ci = g.name COLLATE utf8mb4_unicode_ci
SET p.gender_id = g.id
WHERE p.gender IS NOT NULL;

-- Bước 5: Thêm foreign key constraint
SET @exist := (SELECT COUNT(*) FROM information_schema.table_constraints 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'products' 
               AND constraint_name = 'fk_products_gender');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Foreign key fk_products_gender already exists" as message;',
    'ALTER TABLE products ADD CONSTRAINT fk_products_gender FOREIGN KEY (gender_id) REFERENCES genders(id) ON DELETE SET NULL;');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Bước 6: Tạo index cho gender_id
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'products' 
               AND index_name = 'idx_products_gender_id');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Index idx_products_gender_id already exists" as message;',
    'CREATE INDEX idx_products_gender_id ON products(gender_id);');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Bước 7: Tương tự cho bảng users
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'users' 
               AND column_name = 'gender_id');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Column gender_id already exists in users" as message;',
    'ALTER TABLE users ADD COLUMN gender_id BIGINT DEFAULT NULL COMMENT ''Foreign key to genders.id'';');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE users u
LEFT JOIN genders g ON UPPER(TRIM(u.gender)) COLLATE utf8mb4_unicode_ci = g.name COLLATE utf8mb4_unicode_ci
SET u.gender_id = g.id
WHERE u.gender IS NOT NULL;

SET @exist := (SELECT COUNT(*) FROM information_schema.table_constraints 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'users' 
               AND constraint_name = 'fk_users_gender');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Foreign key fk_users_gender already exists" as message;',
    'ALTER TABLE users ADD CONSTRAINT fk_users_gender FOREIGN KEY (gender_id) REFERENCES genders(id) ON DELETE SET NULL;');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'users' 
               AND index_name = 'idx_users_gender_id');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Index idx_users_gender_id already exists" as message;',
    'CREATE INDEX idx_users_gender_id ON users(gender_id);');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Bước 8: Tương tự cho bảng product_properties
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'product_properties' 
               AND column_name = 'gender_id');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Column gender_id already exists in product_properties" as message;',
    'ALTER TABLE product_properties ADD COLUMN gender_id BIGINT DEFAULT NULL COMMENT ''Foreign key to genders.id'';');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE product_properties pp
LEFT JOIN genders g ON UPPER(TRIM(pp.gender)) COLLATE utf8mb4_unicode_ci = g.name COLLATE utf8mb4_unicode_ci
SET pp.gender_id = g.id
WHERE pp.gender IS NOT NULL;

SET @exist := (SELECT COUNT(*) FROM information_schema.table_constraints 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'product_properties' 
               AND constraint_name = 'fk_product_properties_gender');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Foreign key fk_product_properties_gender already exists" as message;',
    'ALTER TABLE product_properties ADD CONSTRAINT fk_product_properties_gender FOREIGN KEY (gender_id) REFERENCES genders(id) ON DELETE SET NULL;');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'product_properties' 
               AND index_name = 'idx_product_properties_gender_id');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "Index idx_product_properties_gender_id already exists" as message;',
    'CREATE INDEX idx_product_properties_gender_id ON product_properties(gender_id);');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra kết quả
SELECT 'Genders table created and data migrated successfully!' as message;
SELECT id, name, display_name FROM genders ORDER BY id;

