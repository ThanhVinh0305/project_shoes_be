-- Thêm size cho tất cả sản phẩm theo giới tính
-- Nam (id=2) và Unisex (id=3): size 36-45
-- Nữ (id=1): size 36-40
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/add_sizes_by_gender.sql

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Tắt kiểm tra FK tạm thời
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu cũ
TRUNCATE TABLE product_amounts;
TRUNCATE TABLE product_properties;

-- Chèn size cho Nam (2) và Unisex (3): 36-45
INSERT INTO product_properties (is_able, product_id, size, gender_id)
SELECT b'1', p.id, s.size, p.gender_id
FROM products p
JOIN (
  SELECT 36 AS size UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40
  UNION ALL SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45
) s
WHERE p.gender_id IN (2, 3);

-- Chèn size cho Nữ (1): 36-40
INSERT INTO product_properties (is_able, product_id, size, gender_id)
SELECT b'1', p.id, s.size, p.gender_id
FROM products p
JOIN (
  SELECT 36 AS size UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40
) s
WHERE p.gender_id = 1;

-- Tạo tồn kho mặc định (20 đôi mỗi size)
INSERT INTO product_amounts (amount, product_properties_id, updated_date)
SELECT 20, pp.id, NOW() FROM product_properties pp;

-- Bật lại kiểm tra FK
SET FOREIGN_KEY_CHECKS = 1;

-- Thống kê
SELECT 'Sizes per product' AS info;
SELECT product_id, COUNT(*) AS sizes FROM product_properties GROUP BY product_id LIMIT 5;
SELECT 'Total sizes' AS info;
SELECT COUNT(*) AS total_sizes FROM product_properties;
SELECT 'Total stock records' AS info;
SELECT COUNT(*) AS total_amounts FROM product_amounts;


