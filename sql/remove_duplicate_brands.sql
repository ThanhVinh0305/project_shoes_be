-- Xóa brands trùng lặp, chỉ giữ lại id 1-8
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce < sql/remove_duplicate_brands.sql

SET NAMES utf8mb4;

-- Kiểm tra product_brands đang dùng brand_id nào
SELECT 'Kiểm tra product_brands trước khi xóa...' as message;
SELECT brand_id, COUNT(*) as count FROM product_brands GROUP BY brand_id ORDER BY brand_id;

-- Cập nhật product_brands: chuyển brand_id 9-16 về 1-8, 17-24 về 1-8
UPDATE product_brands SET brand_id = 1 WHERE brand_id = 9;   -- Nike
UPDATE product_brands SET brand_id = 2 WHERE brand_id = 10;  -- Adidas
UPDATE product_brands SET brand_id = 3 WHERE brand_id = 11;  -- Puma
UPDATE product_brands SET brand_id = 4 WHERE brand_id = 12;  -- Converse
UPDATE product_brands SET brand_id = 5 WHERE brand_id = 13;  -- Vans
UPDATE product_brands SET brand_id = 6 WHERE brand_id = 14;  -- New Balance
UPDATE product_brands SET brand_id = 7 WHERE brand_id = 15;  -- Reebok
UPDATE product_brands SET brand_id = 8 WHERE brand_id = 16;  -- Fila

UPDATE product_brands SET brand_id = 1 WHERE brand_id = 17;  -- Nike
UPDATE product_brands SET brand_id = 2 WHERE brand_id = 18;  -- Adidas
UPDATE product_brands SET brand_id = 3 WHERE brand_id = 19;  -- Puma
UPDATE product_brands SET brand_id = 4 WHERE brand_id = 20;  -- Converse
UPDATE product_brands SET brand_id = 5 WHERE brand_id = 21;  -- Vans
UPDATE product_brands SET brand_id = 6 WHERE brand_id = 22;  -- New Balance
UPDATE product_brands SET brand_id = 7 WHERE brand_id = 23;  -- Reebok
UPDATE product_brands SET brand_id = 8 WHERE brand_id = 24;  -- Fila

-- Xóa các brands trùng lặp (id 9-24)
DELETE FROM brands WHERE id BETWEEN 9 AND 24;

-- Kiểm tra kết quả
SELECT 'Đã xóa brands trùng lặp!' as message;
SELECT COUNT(*) as total_brands FROM brands;
SELECT COUNT(DISTINCT name) as unique_brands FROM brands;
SELECT id, name FROM brands ORDER BY id;


