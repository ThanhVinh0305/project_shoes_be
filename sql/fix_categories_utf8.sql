-- Fix UTF-8 encoding issues trong bảng categories
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/fix_categories_utf8.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Xóa các categories cũ (nếu có lỗi encoding)
DELETE FROM product_categories;
DELETE FROM categories;

-- Insert lại categories với UTF-8 đúng
INSERT INTO categories (name, router_link, image) VALUES
('Giày thể thao', '/giay-the-thao', 'https://example.com/category-sport.jpg'),
('Giày chạy bộ', '/giay-chay-bo', 'https://example.com/category-running.jpg'),
('Giày bóng rổ', '/giay-bong-ro', 'https://example.com/category-basketball.jpg'),
('Giày đá bóng', '/giay-da-bong', 'https://example.com/category-football.jpg'),
('Giày casual', '/giay-casual', 'https://example.com/category-casual.jpg'),
('Giày sneaker', '/giay-sneaker', 'https://example.com/category-sneaker.jpg'),
('Giày cao cổ', '/giay-cao-co', 'https://example.com/category-high-top.jpg'),
('Giày thấp cổ', '/giay-thap-co', 'https://example.com/category-low-top.jpg')
ON DUPLICATE KEY UPDATE name=VALUES(name);

SELECT 'Categories UTF-8 fixed successfully!' as message;

