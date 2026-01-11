-- Xóa các ảnh detail trùng lặp, chỉ giữ lại unique
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/remove_duplicate_attachments.sql

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Xóa các bản ghi trùng lặp, chỉ giữ lại bản ghi đầu tiên (id nhỏ nhất)
DELETE pa1 FROM product_attachments pa1
INNER JOIN product_attachments pa2 
WHERE pa1.id > pa2.id 
AND pa1.product_id = pa2.product_id 
AND pa1.attachment = pa2.attachment;

-- Kiểm tra kết quả
SELECT 'Kết quả sau khi xóa trùng lặp:' as message;
SELECT product_id, COUNT(*) as count 
FROM product_attachments 
GROUP BY product_id 
ORDER BY count DESC 
LIMIT 10;


