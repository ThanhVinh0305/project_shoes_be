-- Cập nhật gender_id trong products theo mapping mới
-- Mapping cũ → mới:
-- gender_id = 2 (FEMALE cũ) → gender_id = 1 (FEMALE mới)
-- gender_id = 1 (MALE cũ) → gender_id = 2 (MALE mới)  
-- gender_id = 3 (UNISEX) → giữ nguyên = 3
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/update_products_gender_new_mapping.sql

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Tắt foreign key check tạm thời
SET FOREIGN_KEY_CHECKS = 0;

-- Cập nhật: FEMALE từ 2 → 1
UPDATE products SET gender_id = 1 WHERE gender_id = 2;

-- Cập nhật: MALE từ 1 → 2 (sau khi FEMALE đã đổi)
-- Tạm thời set = 99 để tránh conflict
UPDATE products SET gender_id = 99 WHERE gender_id = 1;
UPDATE products SET gender_id = 2 WHERE gender_id = 99;

-- Bật lại foreign key check
SET FOREIGN_KEY_CHECKS = 1;

-- UNISEX (3) giữ nguyên, không cần update

-- Kiểm tra kết quả
SELECT 'Kết quả sau khi cập nhật:' as message;
SELECT g.id, g.name, g.display_name, COUNT(p.id) as so_luong
FROM genders g
LEFT JOIN products p ON g.id = p.gender_id
GROUP BY g.id, g.name, g.display_name
ORDER BY g.id;

