-- Cập nhật giới tính sản phẩm ngẫu nhiên (Nam, Nữ, Unisex)
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/update_products_gender_random.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Cập nhật giới tính ngẫu nhiên cho tất cả sản phẩm
-- Phân bổ: ~33% MALE, ~33% FEMALE, ~34% UNISEX
UPDATE products 
SET gender_id = CASE 
    WHEN (id % 3) = 0 THEN 1  -- MALE (Nam)
    WHEN (id % 3) = 1 THEN 2  -- FEMALE (Nữ)
    ELSE 3                     -- UNISEX
END;

-- Hoặc phân bổ ngẫu nhiên thực sự (nếu muốn random mỗi lần chạy)
-- UPDATE products 
-- SET gender_id = FLOOR(1 + RAND() * 3);

-- Kiểm tra kết quả
SELECT 
    g.name as gender,
    g.display_name,
    COUNT(*) as product_count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM products), 2) as percentage
FROM products p
LEFT JOIN genders g ON p.gender_id = g.id
GROUP BY g.id, g.name, g.display_name
ORDER BY g.id;

SELECT 'Products gender updated successfully!' as message;

