-- Xóa tất cả categories cũ
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce < sql/delete_all_categories.sql

SET NAMES utf8mb4;

-- Xóa bảng liên kết trước (tránh foreign key constraint)
DELETE FROM product_categories;

-- Xóa tất cả categories
DELETE FROM categories;

-- Kiểm tra kết quả
SELECT 'Đã xóa tất cả categories!' as message;
SELECT COUNT(*) as remaining_categories FROM categories;
SELECT COUNT(*) as remaining_product_categories FROM product_categories;


