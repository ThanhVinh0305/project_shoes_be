-- Cập nhật gender_id ngẫu nhiên cho tất cả user (trừ user ThanhVinhLe - giữ nguyên gender_id = 2)
-- Mapping DB: 1 = Nữ, 2 = Nam, 3 = Unisex
-- Logic: 0 = Nữ, 1 = Nam (cho registration)
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce < sql/update_users_gender_random.sql

SET NAMES utf8mb4;

-- Cập nhật gender_id ngẫu nhiên (1 hoặc 2) cho tất cả user trừ ThanhVinhLe
-- 1 = Nữ, 2 = Nam trong DB
UPDATE users 
SET gender_id = 1 + FLOOR(RAND() * 2)  -- 1 hoặc 2 (Nữ hoặc Nam)
WHERE username != 'ThanhVinhLe';

-- Đảm bảo ThanhVinhLe có gender_id = 2 (Nam trong DB)
UPDATE users 
SET gender_id = 2 
WHERE username = 'ThanhVinhLe';

-- Kiểm tra kết quả
SELECT 'Kết quả cập nhật gender_id cho users:' as message;
SELECT 
    gender_id,
    CASE 
        WHEN gender_id = 1 THEN 'Nữ'
        WHEN gender_id = 2 THEN 'Nam'
        WHEN gender_id = 3 THEN 'Unisex'
        ELSE 'NULL'
    END as gender_name,
    COUNT(*) as so_luong
FROM users 
GROUP BY gender_id
ORDER BY gender_id;

SELECT 'Kiểm tra user ThanhVinhLe:' as message;
SELECT id, username, gender_id, 
    CASE 
        WHEN gender_id = 1 THEN 'Nữ'
        WHEN gender_id = 2 THEN 'Nam'
        WHEN gender_id = 3 THEN 'Unisex'
        ELSE 'NULL'
    END as gender_name
FROM users 
WHERE username = 'ThanhVinhLe';
