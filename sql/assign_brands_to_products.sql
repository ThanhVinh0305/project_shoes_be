-- Gán brand cho sản phẩm dựa trên tên sản phẩm
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce < sql/assign_brands_to_products.sql

SET NAMES utf8mb4;

-- Xóa dữ liệu cũ trong product_brands (nếu có)
DELETE FROM product_brands;

-- Gán brand cho sản phẩm dựa trên tên sản phẩm
INSERT INTO product_brands (product_id, brand_id)
SELECT p.id, b.id
FROM products p
JOIN brands b ON (
    (p.name LIKE CONCAT(b.name, '%') OR p.name LIKE CONCAT('%', b.name, '%'))
    AND b.name IN ('Nike', 'Adidas', 'Puma', 'Converse', 'Vans', 'New Balance', 'Reebok', 'Fila')
)
WHERE NOT EXISTS (
    SELECT 1 FROM product_brands pb 
    WHERE pb.product_id = p.id AND pb.brand_id = b.id
);

-- Kiểm tra kết quả
SELECT 'Đã gán brand cho sản phẩm!' as message;
SELECT COUNT(*) as total_product_brands FROM product_brands;
SELECT b.name as brand_name, COUNT(*) as product_count 
FROM product_brands pb
JOIN brands b ON pb.brand_id = b.id
GROUP BY b.id, b.name
ORDER BY b.id;

-- Kiểm tra sản phẩm chưa có brand
SELECT COUNT(*) as products_without_brand
FROM products p
WHERE NOT EXISTS (
    SELECT 1 FROM product_brands pb WHERE pb.product_id = p.id
);


