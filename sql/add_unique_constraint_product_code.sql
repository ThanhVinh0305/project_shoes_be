-- Thêm UNIQUE constraint cho cột code trong bảng products
-- Để tránh trùng lặp sản phẩm khi chạy script nhiều lần
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/add_unique_constraint_product_code.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Kiểm tra xem constraint đã tồn tại chưa
SET @exist := (SELECT COUNT(*) FROM information_schema.table_constraints 
               WHERE table_schema = 'e-commerce' 
               AND table_name = 'products' 
               AND constraint_name = 'unique_code');

SET @sqlstmt := IF(@exist > 0, 
    'SELECT "UNIQUE constraint unique_code already exists" as message;',
    'ALTER TABLE products ADD UNIQUE KEY unique_code (code);');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra kết quả
SELECT 'UNIQUE constraint added successfully!' as message;

