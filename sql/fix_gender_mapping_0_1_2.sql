-- Cập nhật gender_id theo logic: 0=Nữ, 1=Nam, 2=Unisex
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce < sql/fix_gender_mapping_0_1_2.sql

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Bước 1: Xóa dữ liệu cũ trong genders
DELETE FROM genders;

-- Bước 2: Insert lại với id 0, 1, 2
INSERT INTO genders (id, name, display_name) VALUES
(0, 'FEMALE', 'Nữ'),
(1, 'MALE', 'Nam'),
(2, 'UNISEX', 'Unisex');

-- Bước 3: Reset AUTO_INCREMENT
ALTER TABLE genders AUTO_INCREMENT = 3;

-- Bước 4: Cập nhật products theo mapping mới
-- Mapping cũ → mới:
-- gender_id = 1 (FEMALE cũ) → gender_id = 0 (Nữ)
-- gender_id = 2 (MALE cũ) → gender_id = 1 (Nam)
-- gender_id = 3 (UNISEX cũ) → gender_id = 2 (Unisex)

-- Tạm thời set = 99 để tránh conflict
UPDATE products SET gender_id = 99 WHERE gender_id = 1;  -- FEMALE cũ → temp
UPDATE products SET gender_id = 0 WHERE gender_id = 99;   -- → 0 (Nữ)

UPDATE products SET gender_id = 99 WHERE gender_id = 2;  -- MALE cũ → temp
UPDATE products SET gender_id = 1 WHERE gender_id = 99;   -- → 1 (Nam)

UPDATE products SET gender_id = 99 WHERE gender_id = 3;   -- UNISEX cũ → temp
UPDATE products SET gender_id = 2 WHERE gender_id = 99;   -- → 2 (Unisex)

SET FOREIGN_KEY_CHECKS = 1;

-- Kiểm tra kết quả
SELECT 'Kết quả sau khi cập nhật:' as message;
SELECT g.id, g.name, g.display_name, COUNT(p.id) as so_luong
FROM genders g
LEFT JOIN products p ON g.id = p.gender_id
GROUP BY g.id, g.name, g.display_name
ORDER BY g.id;

SELECT 'Phân bố gender_id trong products:' as message;
SELECT gender_id, COUNT(*) as count 
FROM products 
GROUP BY gender_id 
ORDER BY gender_id;


