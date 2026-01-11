-- Xóa tất cả sản phẩm và dữ liệu liên quan
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/delete_all_products.sql

-- Thiết lập UTF-8
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Tắt kiểm tra foreign key tạm thời
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu liên quan đến sản phẩm (theo thứ tự để tránh lỗi foreign key)
-- Xóa tất cả dữ liệu trong các bảng liên quan (không cần WHERE vì xóa hết)
DELETE FROM product_comments;
DELETE FROM product_comment_attachments;
DELETE FROM product_promotions;
DELETE FROM product_attachments;
DELETE FROM product_amounts;
DELETE FROM product_properties;
DELETE FROM product_brands;
DELETE FROM product_categories;
DELETE FROM product_carts;
DELETE FROM import_ticket_products;

-- Xóa tất cả sản phẩm
DELETE FROM products;

-- Bật lại kiểm tra foreign key
SET FOREIGN_KEY_CHECKS = 1;

-- Reset AUTO_INCREMENT về 1
ALTER TABLE products AUTO_INCREMENT = 1;

-- Hiển thị kết quả
SELECT 'Đã xóa tất cả sản phẩm và dữ liệu liên quan' as message;
SELECT COUNT(*) as remaining_products FROM products;

