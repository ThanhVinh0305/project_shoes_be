-- Sửa lại mapping gender_id cho dễ nhớ
-- 0 = Nữ → gender_id = 1
-- 1 = Nam → gender_id = 2
-- 2 = Unisex → gender_id = 3
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/fix_gender_mapping.sql

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Bước 1: Xóa dữ liệu cũ trong genders
DELETE FROM genders;

-- Bước 2: Insert lại với thứ tự mới
INSERT INTO genders (id, name, display_name) VALUES
(1, 'FEMALE', 'Nữ'),
(2, 'MALE', 'Nam'),
(3, 'UNISEX', 'Unisex');

-- Bước 3: Reset AUTO_INCREMENT
ALTER TABLE genders AUTO_INCREMENT = 4;

-- Bước 4: Cập nhật lại gender_id trong products theo mapping mới
-- Mapping cũ: 0→2, 1→1, 2→3
-- Mapping mới: 0→1, 1→2, 2→3

-- Tạm thời set gender_id = NULL để tránh conflict
UPDATE products SET gender_id = NULL;

-- Cập nhật theo mapping mới dựa trên file (cần import lại hoặc update thủ công)
-- Vì không có dữ liệu gốc trong DB, ta sẽ cần import lại từ file

-- Hiển thị kết quả
SELECT 'Đã cập nhật bảng genders với mapping mới:' as message;
SELECT * FROM genders ORDER BY id;


