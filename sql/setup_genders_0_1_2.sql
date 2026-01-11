-- Setup bảng genders với id 0, 1, 2
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce < sql/setup_genders_0_1_2.sql

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa hết dữ liệu users và products trỏ tới genders
UPDATE users SET gender_id = NULL;
UPDATE products SET gender_id = NULL;

-- Xóa hết genders
DELETE FROM genders;

-- Reset AUTO_INCREMENT
ALTER TABLE genders AUTO_INCREMENT = 0;

-- Insert với id 0, 1, 2 (dùng REPLACE để tránh duplicate)
REPLACE INTO genders (id, name, display_name) VALUES
(0, 'FEMALE', 'Nữ'),
(1, 'MALE', 'Nam'),
(2, 'UNISEX', 'Unisex');

-- Set AUTO_INCREMENT tiếp theo
ALTER TABLE genders AUTO_INCREMENT = 3;

SET FOREIGN_KEY_CHECKS = 1;

-- Kiểm tra
SELECT 'Genders đã được setup:' as message;
SELECT * FROM genders ORDER BY id;

