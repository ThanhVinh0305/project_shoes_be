-- Dọn dẹp dữ liệu mồ côi (orphaned data) từ các sản phẩm đã xóa (ID > 200)
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/cleanup_orphaned_data.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Xóa attachments của sản phẩm đã xóa
DELETE FROM product_attachments WHERE product_id > 200;

-- Xóa product_amounts liên quan đến product_properties của sản phẩm đã xóa
DELETE pa FROM product_amounts pa
INNER JOIN product_properties pp ON pa.product_properties_id = pp.id
WHERE pp.product_id > 200;

-- Xóa product_properties của sản phẩm đã xóa
DELETE FROM product_properties WHERE product_id > 200;

-- Xóa product_brands của sản phẩm đã xóa
DELETE FROM product_brands WHERE product_id > 200;

-- Xóa product_categories của sản phẩm đã xóa
DELETE FROM product_categories WHERE product_id > 200;

-- Kiểm tra kết quả
SELECT 'Cleanup completed!' as message;
SELECT COUNT(*) as remaining_attachments FROM product_attachments;
SELECT COUNT(*) as remaining_properties FROM product_properties;
SELECT COUNT(*) as remaining_brands FROM product_brands;
SELECT COUNT(*) as remaining_categories FROM product_categories;

