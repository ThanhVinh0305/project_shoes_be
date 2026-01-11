-- Script tạo 200 sản phẩm đầy đủ với indexes
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/insert_200_products_with_indexes.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Bước 1: Tạo Brands (nếu chưa có)
INSERT INTO brands (name, phone_number, address) VALUES
('Nike', '0123456789', '123 Đường ABC, Quận 1, TP.HCM'),
('Adidas', '0123456790', '456 Đường XYZ, Quận 2, TP.HCM'),
('Puma', '0123456791', '789 Đường DEF, Quận 3, TP.HCM'),
('Converse', '0123456792', '321 Đường GHI, Quận 4, TP.HCM'),
('Vans', '0123456793', '654 Đường JKL, Quận 5, TP.HCM'),
('New Balance', '0123456794', '987 Đường MNO, Quận 7, TP.HCM'),
('Reebok', '0123456795', '147 Đường PQR, Quận 10, TP.HCM'),
('Fila', '0123456796', '258 Đường STU, Quận 11, TP.HCM')
ON DUPLICATE KEY UPDATE name=name;

-- Bước 2: Tạo Categories (nếu chưa có)
INSERT INTO categories (name, router_link, image) VALUES
('Giày thể thao', '/giay-the-thao', 'https://example.com/category-sport.jpg'),
('Giày chạy bộ', '/giay-chay-bo', 'https://example.com/category-running.jpg'),
('Giày bóng rổ', '/giay-bong-ro', 'https://example.com/category-basketball.jpg'),
('Giày đá bóng', '/giay-da-bong', 'https://example.com/category-football.jpg'),
('Giày casual', '/giay-casual', 'https://example.com/category-casual.jpg'),
('Giày sneaker', '/giay-sneaker', 'https://example.com/category-sneaker.jpg'),
('Giày cao cổ', '/giay-cao-co', 'https://example.com/category-high-top.jpg'),
('Giày thấp cổ', '/giay-thap-co', 'https://example.com/category-low-top.jpg')
ON DUPLICATE KEY UPDATE name=name;
-- Bước 3: Tạo 200 Products
INSERT INTO products (name, code, description, gender, color, price, thumbnail_img) VALUES
('Nike Air Max 90', 'NIKE-AM90-001', 'Giày thể thao Nike Air Max 90 - Thiết kế cổ điển, đệm khí Air Max', 'UNISEX', 'Black', 2500000, 'https://example.com/nike-am90-001.jpg')
,('Nike Air Force 1', 'NIKE-AF1-001', 'Giày sneaker Nike Air Force 1 - Biểu tượng thời trang', 'UNISEX', 'Black', 2200000, 'https://example.com/nike-af1-001.jpg')
,('Nike Dunk Low', 'NIKE-DUNK-001', 'Giày bóng rổ Nike Dunk Low - Phong cách retro', 'UNISEX', 'Black', 2300000, 'https://example.com/nike-dunk-001.jpg')
,('Nike React Element 55', 'NIKE-RE55-001', 'Giày chạy bộ Nike React - Công nghệ React foam', 'UNISEX', 'Black', 3200000, 'https://example.com/nike-re55-001.jpg')
,('Nike Air Jordan 1', 'NIKE-AJ1-001', 'Giày bóng rổ Air Jordan 1 - Huyền thoại', 'UNISEX', 'Black', 4500000, 'https://example.com/nike-aj1-001.jpg')
,('Nike Blazer Mid', 'NIKE-BLZ-001', 'Giày casual Nike Blazer Mid - Phong cách cổ điển', 'UNISEX', 'Black', 2100000, 'https://example.com/nike-blz-001.jpg')
,('Nike Pegasus 38', 'NIKE-PEG38-001', 'Giày chạy bộ Nike Pegasus 38 - Đệm khí React', 'UNISEX', 'Black', 2800000, 'https://example.com/nike-peg38-001.jpg')
,('Nike Cortez', 'NIKE-CORT-001', 'Giày thể thao Nike Cortez - Biểu tượng văn hóa', 'UNISEX', 'Black', 1900000, 'https://example.com/nike-cort-001.jpg')
,('Nike VaporMax', 'NIKE-VM-001', 'Giày chạy bộ Nike VaporMax - Đệm khí toàn bộ', 'UNISEX', 'Black', 4200000, 'https://example.com/nike-vm-001.jpg')
,('Nike SB Dunk', 'NIKE-SB-001', 'Giày trượt ván Nike SB Dunk - Đế bền chắc', 'UNISEX', 'Black', 2400000, 'https://example.com/nike-sb-001.jpg')
,('Nike Air Max 270', 'NIKE-AM270-001', 'Giày thể thao Nike Air Max 270 - Đệm khí lớn', 'UNISEX', 'Black', 3100000, 'https://example.com/nike-am270-001.jpg')
,('Nike ZoomX Vaporfly', 'NIKE-ZX-001', 'Giày chạy bộ Nike ZoomX - Công nghệ carbon', 'UNISEX', 'Black', 5500000, 'https://example.com/nike-zx-001.jpg')
,('Nike Air Max 97', 'NIKE-AM97-001', 'Giày thể thao Nike Air Max 97 - Thiết kế tương lai', 'UNISEX', 'Black', 2900000, 'https://example.com/nike-am97-001.jpg')
,('Nike Free RN', 'NIKE-FREE-001', 'Giày chạy bộ Nike Free RN - Tự nhiên như chân trần', 'UNISEX', 'Black', 2600000, 'https://example.com/nike-free-001.jpg')
,('Nike Kyrie 7', 'NIKE-KY7-001', 'Giày bóng rổ Nike Kyrie 7 - Kiểm soát tốt', 'UNISEX', 'Black', 3500000, 'https://example.com/nike-ky7-001.jpg')
,('Nike LeBron 18', 'NIKE-LB18-001', 'Giày bóng rổ Nike LeBron 18 - Hiệu suất cao', 'UNISEX', 'Black', 4800000, 'https://example.com/nike-lb18-001.jpg')
,('Nike Air Max 95', 'NIKE-AM95-001', 'Giày thể thao Nike Air Max 95 - Phong cách 90s', 'UNISEX', 'Black', 2700000, 'https://example.com/nike-am95-001.jpg')
,('Nike Metcon 7', 'NIKE-MET7-001', 'Giày tập luyện Nike Metcon 7 - Ổn định cao', 'UNISEX', 'Black', 3300000, 'https://example.com/nike-met7-001.jpg')
,('Nike Air Max 720', 'NIKE-AM720-001', 'Giày thể thao Nike Air Max 720 - Đệm khí lớn nhất', 'UNISEX', 'Black', 3800000, 'https://example.com/nike-am720-001.jpg')
,('Nike Zoom Fly', 'NIKE-ZF-001', 'Giày chạy bộ Nike Zoom Fly - Tốc độ cao', 'UNISEX', 'Black', 3400000, 'https://example.com/nike-zf-001.jpg')
,('Nike Air Max 98', 'NIKE-AM98-001', 'Giày thể thao Nike Air Max 98 - Retro style', 'UNISEX', 'Black', 2800000, 'https://example.com/nike-am98-001.jpg')
,('Nike Hyperdunk', 'NIKE-HD-001', 'Giày bóng rổ Nike Hyperdunk - Nhảy cao', 'UNISEX', 'Black', 3600000, 'https://example.com/nike-hd-001.jpg')
,('Nike Air Max Plus', 'NIKE-AMP-001', 'Giày thể thao Nike Air Max Plus - Tuned Air', 'UNISEX', 'Black', 3000000, 'https://example.com/nike-amp-001.jpg')
,('Nike Epic React', 'NIKE-ER-001', 'Giày chạy bộ Nike Epic React - Phản hồi nhanh', 'UNISEX', 'Black', 3200000, 'https://example.com/nike-er-001.jpg')
,('Nike Air Max 200', 'NIKE-AM200-001', 'Giày thể thao Nike Air Max 200 - Giá rẻ', 'UNISEX', 'Black', 2000000, 'https://example.com/nike-am200-001.jpg')
,('Adidas Ultraboost 22', 'ADIDAS-UB22-001', 'Giày chạy bộ Adidas Ultraboost 22 - Boost technology', 'UNISEX', 'Black', 4200000, 'https://example.com/adidas-ub22-001.jpg')
,('Adidas Stan Smith', 'ADIDAS-SS-001', 'Giày sneaker Adidas Stan Smith - Classic', 'UNISEX', 'Black', 1800000, 'https://example.com/adidas-ss-001.jpg')
,('Adidas Superstar', 'ADIDAS-SUP-001', 'Giày sneaker Adidas Superstar - Shell toe', 'UNISEX', 'Black', 2000000, 'https://example.com/adidas-sup-001.jpg')
,('Adidas NMD R1', 'ADIDAS-NMD-001', 'Giày thể thao Adidas NMD R1 - Boost midsole', 'UNISEX', 'Black', 3500000, 'https://example.com/adidas-nmd-001.jpg')
,('Adidas Yeezy 350', 'ADIDAS-YZ350-001', 'Giày sneaker Adidas Yeezy 350 - Limited edition', 'UNISEX', 'Black', 8500000, 'https://example.com/adidas-yz350-001.jpg')
,('Adidas Gazelle', 'ADIDAS-GAZ-001', 'Giày casual Adidas Gazelle - Retro style', 'UNISEX', 'Black', 1900000, 'https://example.com/adidas-gaz-001.jpg')
,('Adidas Samba', 'ADIDAS-SAM-001', 'Giày đá bóng Adidas Samba - Indoor football', 'UNISEX', 'Black', 2100000, 'https://example.com/adidas-sam-001.jpg')
,('Adidas Predator', 'ADIDAS-PRED-001', 'Giày đá bóng Adidas Predator - Control', 'UNISEX', 'Black', 3800000, 'https://example.com/adidas-pred-001.jpg')
,('Adidas Copa Mundial', 'ADIDAS-COP-001', 'Giày đá bóng Adidas Copa - Classic', 'UNISEX', 'Black', 3200000, 'https://example.com/adidas-cop-001.jpg')
,('Adidas Pureboost', 'ADIDAS-PB-001', 'Giày chạy bộ Adidas Pureboost - Comfort', 'UNISEX', 'Black', 2800000, 'https://example.com/adidas-pb-001.jpg')
,('Adidas ZX 8000', 'ADIDAS-ZX8-001', 'Giày thể thao Adidas ZX 8000 - Retro running', 'UNISEX', 'Black', 2400000, 'https://example.com/adidas-zx8-001.jpg')
,('Adidas Forum', 'ADIDAS-FOR-001', 'Giày bóng rổ Adidas Forum - High top', 'UNISEX', 'Black', 2600000, 'https://example.com/adidas-for-001.jpg')
,('Adidas Tubular', 'ADIDAS-TUB-001', 'Giày sneaker Adidas Tubular - Futuristic', 'UNISEX', 'Black', 2200000, 'https://example.com/adidas-tub-001.jpg')
,('Adidas Solarboost', 'ADIDAS-SB-001', 'Giày chạy bộ Adidas Solarboost - Energy return', 'UNISEX', 'Black', 3600000, 'https://example.com/adidas-sb-001.jpg')
,('Adidas Originals', 'ADIDAS-ORG-001', 'Giày casual Adidas Originals - Street style', 'UNISEX', 'Black', 2000000, 'https://example.com/adidas-org-001.jpg')
,('Adidas Terrex', 'ADIDAS-TER-001', 'Giày leo núi Adidas Terrex - Traction', 'UNISEX', 'Black', 3400000, 'https://example.com/adidas-ter-001.jpg')
,('Adidas Response', 'ADIDAS-RES-001', 'Giày chạy bộ Adidas Response - Cushioning', 'UNISEX', 'Black', 2500000, 'https://example.com/adidas-res-001.jpg')
,('Adidas Duramo', 'ADIDAS-DUR-001', 'Giày chạy bộ Adidas Duramo - Daily runner', 'UNISEX', 'Black', 1800000, 'https://example.com/adidas-dur-001.jpg')
,('Adidas Lite Racer', 'ADIDAS-LR-001', 'Giày thể thao Adidas Lite Racer - Lightweight', 'UNISEX', 'Black', 1600000, 'https://example.com/adidas-lr-001.jpg')
,('Adidas Cloudfoam', 'ADIDAS-CF-001', 'Giày casual Adidas Cloudfoam - Comfort', 'UNISEX', 'Black', 1700000, 'https://example.com/adidas-cf-001.jpg')
,('Adidas Advantage', 'ADIDAS-ADV-001', 'Giày tennis Adidas Advantage - Court grip', 'UNISEX', 'Black', 1900000, 'https://example.com/adidas-adv-001.jpg')
,('Adidas Grand Court', 'ADIDAS-GC-001', 'Giày tennis Adidas Grand Court - Classic', 'UNISEX', 'Black', 2000000, 'https://example.com/adidas-gc-001.jpg')
,('Adidas Courtvantage', 'ADIDAS-CV-001', 'Giày tennis Adidas Courtvantage - Performance', 'UNISEX', 'Black', 2100000, 'https://example.com/adidas-cv-001.jpg')
,('Adidas Barricade', 'ADIDAS-BAR-001', 'Giày tennis Adidas Barricade - Stability', 'UNISEX', 'Black', 3500000, 'https://example.com/adidas-bar-001.jpg')
,('Adidas Adizero', 'ADIDAS-AZ-001', 'Giày chạy bộ Adidas Adizero - Speed', 'UNISEX', 'Black', 4000000, 'https://example.com/adidas-az-001.jpg')
,('Puma Suede Classic', 'PUMA-SUE-001', 'Giày sneaker Puma Suede - Classic style', 'UNISEX', 'Black', 1500000, 'https://example.com/puma-sue-001.jpg')
,('Puma RS-X', 'PUMA-RSX-001', 'Giày thể thao Puma RS-X - Retro future', 'UNISEX', 'Black', 2200000, 'https://example.com/puma-rsx-001.jpg')
,('Puma Speedcat', 'PUMA-SC-001', 'Giày đua xe Puma Speedcat - Racing style', 'UNISEX', 'Black', 2800000, 'https://example.com/puma-sc-001.jpg')
,('Puma Future', 'PUMA-FUT-001', 'Giày đá bóng Puma Future - Adaptive fit', 'UNISEX', 'Black', 3600000, 'https://example.com/puma-fut-001.jpg')
,('Puma One', 'PUMA-ONE-001', 'Giày đá bóng Puma One - Touch control', 'UNISEX', 'Black', 3200000, 'https://example.com/puma-one-001.jpg')
,('Puma Thunder', 'PUMA-TH-001', 'Giày sneaker Puma Thunder - Chunky design', 'UNISEX', 'Black', 2400000, 'https://example.com/puma-th-001.jpg')
,('Puma Cali', 'PUMA-CAL-001', 'Giày casual Puma Cali - California vibes', 'UNISEX', 'Black', 1900000, 'https://example.com/puma-cal-001.jpg')
,('Puma Cell', 'PUMA-CEL-001', 'Giày chạy bộ Puma Cell - Cushioning', 'UNISEX', 'Black', 2600000, 'https://example.com/puma-cel-001.jpg')
,('Puma Ignite', 'PUMA-IGN-001', 'Giày chạy bộ Puma Ignite - Energy return', 'UNISEX', 'Black', 3000000, 'https://example.com/puma-ign-001.jpg')
,('Puma Disc', 'PUMA-DIS-001', 'Giày thể thao Puma Disc - Disc closure', 'UNISEX', 'Black', 2700000, 'https://example.com/puma-dis-001.jpg')
,('Puma Basket', 'PUMA-BAS-001', 'Giày bóng rổ Puma Basket - Court style', 'UNISEX', 'Black', 2500000, 'https://example.com/puma-bas-001.jpg')
,('Puma Clyde', 'PUMA-CLY-001', 'Giày bóng rổ Puma Clyde - Classic', 'UNISEX', 'Black', 2300000, 'https://example.com/puma-cly-001.jpg')
,('Puma TSUGI', 'PUMA-TSU-001', 'Giày sneaker Puma TSUGI - Japanese design', 'UNISEX', 'Black', 2100000, 'https://example.com/puma-tsu-001.jpg')
,('Puma Leadcat', 'PUMA-LEA-001', 'Giày đua xe Puma Leadcat - Racing heritage', 'UNISEX', 'Black', 2900000, 'https://example.com/puma-lea-001.jpg')
,('Puma Fierce', 'PUMA-FIE-001', 'Giày tập luyện Puma Fierce - Training', 'UNISEX', 'Black', 2400000, 'https://example.com/puma-fie-001.jpg')
,('Puma Enzo', 'PUMA-ENZ-001', 'Giày casual Puma Enzo - Italian style', 'UNISEX', 'Black', 2000000, 'https://example.com/puma-enz-001.jpg')
,('Puma Vikky', 'PUMA-VIK-001', 'Giày sneaker Puma Vikky - Platform', 'UNISEX', 'Black', 1800000, 'https://example.com/puma-vik-001.jpg')
,('Puma Mayze', 'PUMA-MAY-001', 'Giày sneaker Puma Mayze - Chunky sole', 'UNISEX', 'Black', 1900000, 'https://example.com/puma-may-001.jpg')
,('Puma Smash', 'PUMA-SMA-001', 'Giày tennis Puma Smash - Court classic', 'UNISEX', 'Black', 1700000, 'https://example.com/puma-sma-001.jpg')
,('Puma Court', 'PUMA-COU-001', 'Giày tennis Puma Court - Performance', 'UNISEX', 'Black', 1800000, 'https://example.com/puma-cou-001.jpg')
,('Puma Speed', 'PUMA-SPE-001', 'Giày chạy bộ Puma Speed - Lightweight', 'UNISEX', 'Black', 2500000, 'https://example.com/puma-spe-001.jpg')
,('Puma Flex', 'PUMA-FLE-001', 'Giày chạy bộ Puma Flex - Flexibility', 'UNISEX', 'Black', 2200000, 'https://example.com/puma-fle-001.jpg')
,('Puma NRGY', 'PUMA-NRG-001', 'Giày thể thao Puma NRGY - Energy beads', 'UNISEX', 'Black', 2800000, 'https://example.com/puma-nrg-001.jpg')
,('Puma Hybrid', 'PUMA-HYB-001', 'Giày chạy bộ Puma Hybrid - Dual foam', 'UNISEX', 'Black', 3100000, 'https://example.com/puma-hyb-001.jpg')
,('Puma Evospeed', 'PUMA-EVO-001', 'Giày chạy bộ Puma Evospeed - Speed training', 'UNISEX', 'Black', 2700000, 'https://example.com/puma-evo-001.jpg')
,('Converse Chuck Taylor All Star', 'CONV-CT-001', 'Giày sneaker Converse Chuck Taylor - Classic high top', 'UNISEX', 'Black', 1200000, 'https://example.com/conv-ct-001.jpg')
,('Converse Chuck 70', 'CONV-C70-001', 'Giày sneaker Converse Chuck 70 - Premium', 'UNISEX', 'Black', 1800000, 'https://example.com/conv-c70-001.jpg')
,('Converse One Star', 'CONV-OS-001', 'Giày sneaker Converse One Star - Star logo', 'UNISEX', 'Black', 1500000, 'https://example.com/conv-os-001.jpg')
,('Converse Jack Purcell', 'CONV-JP-001', 'Giày sneaker Converse Jack Purcell - Smile design', 'UNISEX', 'Black', 1600000, 'https://example.com/conv-jp-001.jpg')
,('Converse Run Star', 'CONV-RS-001', 'Giày sneaker Converse Run Star - Platform', 'UNISEX', 'Black', 2000000, 'https://example.com/conv-rs-001.jpg')
,('Converse Pro Leather', 'CONV-PL-001', 'Giày bóng rổ Converse Pro Leather - Classic', 'UNISEX', 'Black', 1900000, 'https://example.com/conv-pl-001.jpg')
,('Converse Fastbreak', 'CONV-FB-001', 'Giày bóng rổ Converse Fastbreak - Retro', 'UNISEX', 'Black', 1700000, 'https://example.com/conv-fb-001.jpg')
,('Converse Weapon', 'CONV-WE-001', 'Giày bóng rổ Converse Weapon - 80s style', 'UNISEX', 'Black', 2100000, 'https://example.com/conv-we-001.jpg')
,('Converse All Star BB', 'CONV-BB-001', 'Giày bóng rổ Converse All Star BB - Modern', 'UNISEX', 'Black', 2400000, 'https://example.com/conv-bb-001.jpg')
,('Converse Star Player', 'CONV-SP-001', 'Giày tennis Converse Star Player - Court', 'UNISEX', 'Black', 1800000, 'https://example.com/conv-sp-001.jpg')
,('Converse CONS', 'CONV-CON-001', 'Giày trượt ván Converse CONS - Skate', 'UNISEX', 'Black', 2000000, 'https://example.com/conv-con-001.jpg')
,('Converse Gianno', 'CONV-GIA-001', 'Giày sneaker Converse Gianno - Chunky', 'UNISEX', 'Black', 1900000, 'https://example.com/conv-gia-001.jpg')
,('Converse ERX', 'CONV-ERX-001', 'Giày thể thao Converse ERX - 90s retro', 'UNISEX', 'Black', 1700000, 'https://example.com/conv-erx-001.jpg')
,('Converse Aerojam', 'CONV-AER-001', 'Giày bóng rổ Converse Aerojam - High top', 'UNISEX', 'Black', 2200000, 'https://example.com/conv-aer-001.jpg')
,('Converse All Star Lift', 'CONV-ASL-001', 'Giày sneaker Converse All Star Lift - Platform', 'UNISEX', 'Black', 1800000, 'https://example.com/conv-asl-001.jpg')
,('Converse Renew', 'CONV-REN-001', 'Giày sneaker Converse Renew - Recycled', 'UNISEX', 'Black', 1600000, 'https://example.com/conv-ren-001.jpg')
,('Converse CX', 'CONV-CX-001', 'Giày sneaker Converse CX - Cushioning', 'UNISEX', 'Black', 2000000, 'https://example.com/conv-cx-001.jpg')
,('Converse Move', 'CONV-MOV-001', 'Giày chạy bộ Converse Move - Running', 'UNISEX', 'Black', 1900000, 'https://example.com/conv-mov-001.jpg')
,('Converse Utility', 'CONV-UT-001', 'Giày casual Converse Utility - Workwear', 'UNISEX', 'Black', 1700000, 'https://example.com/conv-ut-001.jpg')
,('Converse First String', 'CONV-FS-001', 'Giày bóng rổ Converse First String - Premium', 'UNISEX', 'Black', 2500000, 'https://example.com/conv-fs-001.jpg')
,('Converse All Star Dainty', 'CONV-DAI-001', 'Giày sneaker Converse All Star Dainty - Low top', 'UNISEX', 'Black', 1300000, 'https://example.com/conv-dai-001.jpg')
,('Converse All Star Platform', 'CONV-ASP-001', 'Giày sneaker Converse All Star Platform - High', 'UNISEX', 'Black', 1900000, 'https://example.com/conv-asp-001.jpg')
,('Converse All Star Move', 'CONV-ASM-001', 'Giày chạy bộ Converse All Star Move - Comfort', 'UNISEX', 'Black', 2000000, 'https://example.com/conv-asm-001.jpg')
,('Converse All Star BB Evo', 'CONV-BBE-001', 'Giày bóng rổ Converse All Star BB Evo - Evolution', 'UNISEX', 'Black', 2600000, 'https://example.com/conv-bbe-001.jpg')
,('Converse All Star Modern', 'CONV-ASM2-001', 'Giày sneaker Converse All Star Modern - Updated', 'UNISEX', 'Black', 1700000, 'https://example.com/conv-asm2-001.jpg')
,('Vans Old Skool', 'VANS-OS-001', 'Giày sneaker Vans Old Skool - Classic skate', 'UNISEX', 'Black', 1400000, 'https://example.com/vans-os-001.jpg')
,('Vans Authentic', 'VANS-AUT-001', 'Giày sneaker Vans Authentic - Original', 'UNISEX', 'Black', 1200000, 'https://example.com/vans-aut-001.jpg')
,('Vans Sk8-Hi', 'VANS-SK8-001', 'Giày sneaker Vans Sk8-Hi - High top', 'UNISEX', 'Black', 1500000, 'https://example.com/vans-sk8-001.jpg')
,('Vans Era', 'VANS-ERA-001', 'Giày sneaker Vans Era - Padded collar', 'UNISEX', 'Black', 1300000, 'https://example.com/vans-era-001.jpg')
,('Vans Slip-On', 'VANS-SO-001', 'Giày sneaker Vans Slip-On - Easy on', 'UNISEX', 'Black', 1200000, 'https://example.com/vans-so-001.jpg')
,('Vans Half Cab', 'VANS-HC-001', 'Giày trượt ván Vans Half Cab - Skate pro', 'UNISEX', 'Black', 1600000, 'https://example.com/vans-hc-001.jpg')
,('Vans Chukka', 'VANS-CHU-001', 'Giày sneaker Vans Chukka - Mid top', 'UNISEX', 'Black', 1400000, 'https://example.com/vans-chu-001.jpg')
,('Vans SK8-Mid', 'VANS-SK8M-001', 'Giày sneaker Vans SK8-Mid - Mid top', 'UNISEX', 'Black', 1500000, 'https://example.com/vans-sk8m-001.jpg')
,('Vans Atwood', 'VANS-AT-001', 'Giày sneaker Vans Atwood - Low profile', 'UNISEX', 'Black', 1300000, 'https://example.com/vans-at-001.jpg')
,('Vans Ward', 'VANS-WA-001', 'Giày sneaker Vans Ward - Classic low', 'UNISEX', 'Black', 1200000, 'https://example.com/vans-wa-001.jpg')
,('Vans Old Skool Pro', 'VANS-OSP-001', 'Giày trượt ván Vans Old Skool Pro - Pro skate', 'UNISEX', 'Black', 1800000, 'https://example.com/vans-osp-001.jpg')
,('Vans Authentic Pro', 'VANS-AUTP-001', 'Giày trượt ván Vans Authentic Pro - Pro model', 'UNISEX', 'Black', 1700000, 'https://example.com/vans-autp-001.jpg')
,('Vans SK8-Hi Pro', 'VANS-SK8P-001', 'Giày trượt ván Vans SK8-Hi Pro - Pro high', 'UNISEX', 'Black', 1900000, 'https://example.com/vans-sk8p-001.jpg')
,('Vans ComfyCush', 'VANS-CC-001', 'Giày sneaker Vans ComfyCush - Extra comfort', 'UNISEX', 'Black', 1600000, 'https://example.com/vans-cc-001.jpg')
,('Vans UltraRange', 'VANS-UR-001', 'Giày chạy bộ Vans UltraRange - Running', 'UNISEX', 'Black', 2000000, 'https://example.com/vans-ur-001.jpg')
,('Vans Wayvee', 'VANS-WAY-001', 'Giày sneaker Vans Wayvee - Chunky', 'UNISEX', 'Black', 1700000, 'https://example.com/vans-way-001.jpg')
,('Vans Kyle Walker', 'VANS-KW-001', 'Giày trượt ván Vans Kyle Walker - Pro signature', 'UNISEX', 'Black', 2100000, 'https://example.com/vans-kw-001.jpg')
,('Vans Rowan', 'VANS-ROW-001', 'Giày trượt ván Vans Rowan - Pro model', 'UNISEX', 'Black', 2000000, 'https://example.com/vans-row-001.jpg')
,('Vans AVE', 'VANS-AVE-001', 'Giày trượt ván Vans AVE - Pro skate', 'UNISEX', 'Black', 1900000, 'https://example.com/vans-ave-001.jpg')
,('Vans Crockett', 'VANS-CRO-001', 'Giày trượt ván Vans Crockett - High top pro', 'UNISEX', 'Black', 1800000, 'https://example.com/vans-cro-001.jpg')
,('Vans Gilbert', 'VANS-GIL-001', 'Giày trượt ván Vans Gilbert - Pro signature', 'UNISEX', 'Black', 2000000, 'https://example.com/vans-gil-001.jpg')
,('Vans BMX', 'VANS-BMX-001', 'Giày đạp xe Vans BMX - BMX specific', 'UNISEX', 'Black', 1900000, 'https://example.com/vans-bmx-001.jpg')
,('Vans MTE', 'VANS-MTE-001', 'Giày sneaker Vans MTE - All weather', 'UNISEX', 'Black', 2200000, 'https://example.com/vans-mte-001.jpg')
,('Vans Ultrarange Rapidweld', 'VANS-URR-001', 'Giày chạy bộ Vans Ultrarange - Seamless', 'UNISEX', 'Black', 2100000, 'https://example.com/vans-urr-001.jpg')
,('Vans Old Skool MTE', 'VANS-OSM-001', 'Giày sneaker Vans Old Skool MTE - Weatherized', 'UNISEX', 'Black', 2000000, 'https://example.com/vans-osm-001.jpg')
,('New Balance 550', 'NB-550-001', 'Giày sneaker New Balance 550 - Retro basketball', 'UNISEX', 'Black', 2500000, 'https://example.com/nb-550-001.jpg')
,('New Balance 574', 'NB-574-001', 'Giày sneaker New Balance 574 - Classic runner', 'UNISEX', 'Black', 2000000, 'https://example.com/nb-574-001.jpg')
,('New Balance 990', 'NB-990-001', 'Giày chạy bộ New Balance 990 - Made in USA', 'UNISEX', 'Black', 4500000, 'https://example.com/nb-990-001.jpg')
,('New Balance 327', 'NB-327-001', 'Giày sneaker New Balance 327 - Retro runner', 'UNISEX', 'Black', 2200000, 'https://example.com/nb-327-001.jpg')
,('New Balance 993', 'NB-993-001', 'Giày chạy bộ New Balance 993 - Premium', 'UNISEX', 'Black', 5000000, 'https://example.com/nb-993-001.jpg')
,('New Balance 530', 'NB-530-001', 'Giày chạy bộ New Balance 530 - 90s runner', 'UNISEX', 'Black', 2400000, 'https://example.com/nb-530-001.jpg')
,('New Balance 2002R', 'NB-2002-001', 'Giày chạy bộ New Balance 2002R - Protection', 'UNISEX', 'Black', 3200000, 'https://example.com/nb-2002-001.jpg')
,('New Balance 1080', 'NB-1080-001', 'Giày chạy bộ New Balance 1080 - Maximum cushion', 'UNISEX', 'Black', 3800000, 'https://example.com/nb-1080-001.jpg')
,('New Balance 997', 'NB-997-001', 'Giày sneaker New Balance 997 - Made in USA', 'UNISEX', 'Black', 4200000, 'https://example.com/nb-997-001.jpg')
,('New Balance 998', 'NB-998-001', 'Giày sneaker New Balance 998 - Premium', 'UNISEX', 'Black', 4500000, 'https://example.com/nb-998-001.jpg')
,('New Balance 1500', 'NB-1500-001', 'Giày chạy bộ New Balance 1500 - UK made', 'UNISEX', 'Black', 4000000, 'https://example.com/nb-1500-001.jpg')
,('New Balance 920', 'NB-920-001', 'Giày chạy bộ New Balance 920 - Retro', 'UNISEX', 'Black', 2800000, 'https://example.com/nb-920-001.jpg')
,('New Balance 1300', 'NB-1300-001', 'Giày sneaker New Balance 1300 - Made in USA', 'UNISEX', 'Black', 4800000, 'https://example.com/nb-1300-001.jpg')
,('New Balance 991', 'NB-991-001', 'Giày chạy bộ New Balance 991 - UK made', 'UNISEX', 'Black', 4600000, 'https://example.com/nb-991-001.jpg')
,('New Balance 992', 'NB-992-001', 'Giày chạy bộ New Balance 992 - Made in USA', 'UNISEX', 'Black', 5000000, 'https://example.com/nb-992-001.jpg')
,('New Balance 9060', 'NB-9060-001', 'Giày sneaker New Balance 9060 - Chunky', 'UNISEX', 'Black', 3000000, 'https://example.com/nb-9060-001.jpg')
,('New Balance 1906', 'NB-1906-001', 'Giày chạy bộ New Balance 1906 - Protection', 'UNISEX', 'Black', 3400000, 'https://example.com/nb-1906-001.jpg')
,('New Balance 860', 'NB-860-001', 'Giày chạy bộ New Balance 860 - Stability', 'UNISEX', 'Black', 3200000, 'https://example.com/nb-860-001.jpg')
,('New Balance 880', 'NB-880-001', 'Giày chạy bộ New Balance 880 - Neutral', 'UNISEX', 'Black', 3000000, 'https://example.com/nb-880-001.jpg')
,('New Balance 410', 'NB-410-001', 'Giày chạy bộ New Balance 410 - Trail', 'UNISEX', 'Black', 2200000, 'https://example.com/nb-410-001.jpg')
,('New Balance 373', 'NB-373-001', 'Giày sneaker New Balance 373 - Classic', 'UNISEX', 'Black', 1800000, 'https://example.com/nb-373-001.jpg')
,('New Balance 420', 'NB-420-001', 'Giày sneaker New Balance 420 - Retro', 'UNISEX', 'Black', 1900000, 'https://example.com/nb-420-001.jpg')
,('New Balance 515', 'NB-515-001', 'Giày sneaker New Balance 515 - Classic', 'UNISEX', 'Black', 2000000, 'https://example.com/nb-515-001.jpg')
,('New Balance 624', 'NB-624-001', 'Giày sneaker New Balance 624 - Comfort', 'UNISEX', 'Black', 2100000, 'https://example.com/nb-624-001.jpg')
,('New Balance 680', 'NB-680-001', 'Giày chạy bộ New Balance 680 - Cushioning', 'UNISEX', 'Black', 2600000, 'https://example.com/nb-680-001.jpg')
,('Reebok Classic Leather', 'REE-CL-001', 'Giày sneaker Reebok Classic Leather - Timeless', 'UNISEX', 'Black', 1800000, 'https://example.com/ree-cl-001.jpg')
,('Reebok Club C 85', 'REE-CC85-001', 'Giày sneaker Reebok Club C 85 - Vintage', 'UNISEX', 'Black', 1700000, 'https://example.com/ree-cc85-001.jpg')
,('Reebok Pump', 'REE-PU-001', 'Giày thể thao Reebok Pump - Inflatable', 'UNISEX', 'Black', 3500000, 'https://example.com/ree-pu-001.jpg')
,('Reebok Question', 'REE-Q-001', 'Giày bóng rổ Reebok Question - Iverson', 'UNISEX', 'Black', 4200000, 'https://example.com/ree-q-001.jpg')
,('Reebok Answer', 'REE-A-001', 'Giày bóng rổ Reebok Answer - Iverson signature', 'UNISEX', 'Black', 4500000, 'https://example.com/ree-a-001.jpg')
,('Reebok Zig', 'REE-ZIG-001', 'Giày chạy bộ Reebok Zig - Zigzag sole', 'UNISEX', 'Black', 2800000, 'https://example.com/ree-zig-001.jpg')
,('Reebok Nano', 'REE-NA-001', 'Giày tập luyện Reebok Nano - CrossFit', 'UNISEX', 'Black', 3200000, 'https://example.com/ree-na-001.jpg')
,('Reebok Floatride', 'REE-FR-001', 'Giày chạy bộ Reebok Floatride - Energy foam', 'UNISEX', 'Black', 3600000, 'https://example.com/ree-fr-001.jpg')
,('Reebok Instapump', 'REE-IP-001', 'Giày thể thao Reebok Instapump - Pump tech', 'UNISEX', 'Black', 3400000, 'https://example.com/ree-ip-001.jpg')
,('Reebok Workout', 'REE-WO-001', 'Giày tập luyện Reebok Workout - Classic', 'UNISEX', 'Black', 2000000, 'https://example.com/ree-wo-001.jpg')
,('Reebok Royal', 'REE-RO-001', 'Giày sneaker Reebok Royal - Classic', 'UNISEX', 'Black', 1900000, 'https://example.com/ree-ro-001.jpg')
,('Reebok Ventilator', 'REE-VE-001', 'Giày chạy bộ Reebok Ventilator - Breathable', 'UNISEX', 'Black', 2400000, 'https://example.com/ree-ve-001.jpg')
,('Reebok DMX', 'REE-DMX-001', 'Giày chạy bộ Reebok DMX - Air cushioning', 'UNISEX', 'Black', 3000000, 'https://example.com/ree-dmx-001.jpg')
,('Reebok Aztrek', 'REE-AZ-001', 'Giày chạy bộ Reebok Aztrek - 90s runner', 'UNISEX', 'Black', 2200000, 'https://example.com/ree-az-001.jpg')
,('Reebok Classic Nylon', 'REE-CN-001', 'Giày sneaker Reebok Classic Nylon - Lightweight', 'UNISEX', 'Black', 1600000, 'https://example.com/ree-cn-001.jpg')
,('Reebok Fury', 'REE-FU-001', 'Giày chạy bộ Reebok Fury - Speed', 'UNISEX', 'Black', 2600000, 'https://example.com/ree-fu-001.jpg')
,('Reebok Forever Floatride', 'REE-FFF-001', 'Giày chạy bộ Reebok Forever Floatride - Energy', 'UNISEX', 'Black', 3400000, 'https://example.com/ree-fff-001.jpg')
,('Reebok Speed TR', 'REE-ST-001', 'Giày tập luyện Reebok Speed TR - Training', 'UNISEX', 'Black', 2800000, 'https://example.com/ree-st-001.jpg')
,('Reebok CrossFit', 'REE-CF-001', 'Giày tập luyện Reebok CrossFit - WOD ready', 'UNISEX', 'Black', 3000000, 'https://example.com/ree-cf-001.jpg')
,('Reebok Legacy', 'REE-LE-001', 'Giày bóng rổ Reebok Legacy - Retro', 'UNISEX', 'Black', 2500000, 'https://example.com/ree-le-001.jpg')
,('Reebok Kamikaze', 'REE-KA-001', 'Giày bóng rổ Reebok Kamikaze - Shawn Kemp', 'UNISEX', 'Black', 3800000, 'https://example.com/ree-ka-001.jpg')
,('Reebok Shaq', 'REE-SH-001', 'Giày bóng rổ Reebok Shaq - ONeal signature', 'UNISEX', 'Black', 4000000, 'https://example.com/ree-sh-001.jpg')
,('Reebok BB 4000', 'REE-BB4-001', 'Giày bóng rổ Reebok BB 4000 - Classic', 'UNISEX', 'Black', 2300000, 'https://example.com/ree-bb4-001.jpg')
,('Reebok Ex-O-Fit', 'REE-EX-001', 'Giày tập luyện Reebok Ex-O-Fit - Hi-top', 'UNISEX', 'Black', 2100000, 'https://example.com/ree-ex-001.jpg')
,('Reebok Classic Plus', 'REE-CP-001', 'Giày sneaker Reebok Classic Plus - Enhanced', 'UNISEX', 'Black', 1900000, 'https://example.com/ree-cp-001.jpg')
,('Fila Disruptor', 'FILA-DIS-001', 'Giày sneaker Fila Disruptor - Chunky platform', 'UNISEX', 'Black', 1800000, 'https://example.com/fila-dis-001.jpg')
,('Fila Ray', 'FILA-RAY-001', 'Giày sneaker Fila Ray - Retro runner', 'UNISEX', 'Black', 1600000, 'https://example.com/fila-ray-001.jpg')
,('Fila Mindblower', 'FILA-MB-001', 'Giày sneaker Fila Mindblower - 90s style', 'UNISEX', 'Black', 1700000, 'https://example.com/fila-mb-001.jpg')
,('Fila Grant Hill', 'FILA-GH-001', 'Giày bóng rổ Fila Grant Hill - Signature', 'UNISEX', 'Black', 3500000, 'https://example.com/fila-gh-001.jpg')
,('Fila T-1', 'FILA-T1-001', 'Giày sneaker Fila T-1 - Classic', 'UNISEX', 'Black', 1500000, 'https://example.com/fila-t1-001.jpg')
,('Fila Spaghetti', 'FILA-SPA-001', 'Giày sneaker Fila Spaghetti - Retro', 'UNISEX', 'Black', 1600000, 'https://example.com/fila-spa-001.jpg')
,('Fila Original Fitness', 'FILA-OF-001', 'Giày tập luyện Fila Original Fitness - Classic', 'UNISEX', 'Black', 1400000, 'https://example.com/fila-of-001.jpg')
,('Fila Trailblazer', 'FILA-TB-001', 'Giày chạy bộ Fila Trailblazer - Trail running', 'UNISEX', 'Black', 2000000, 'https://example.com/fila-tb-001.jpg')
,('Fila Axilus', 'FILA-AX-001', 'Giày chạy bộ Fila Axilus - Energy return', 'UNISEX', 'Black', 2200000, 'https://example.com/fila-ax-001.jpg')
,('Fila M-Squad', 'FILA-MS-001', 'Giày sneaker Fila M-Squad - Basketball heritage', 'UNISEX', 'Black', 1900000, 'https://example.com/fila-ms-001.jpg')
,('Fila 96', 'FILA-96-001', 'Giày sneaker Fila 96 - 90s retro', 'UNISEX', 'Black', 1700000, 'https://example.com/fila-96-001.jpg')
,('Fila Stackhouse', 'FILA-ST-001', 'Giày bóng rổ Fila Stackhouse - Signature', 'UNISEX', 'Black', 3200000, 'https://example.com/fila-st-001.jpg')
,('Fila FX-100', 'FILA-FX-001', 'Giày chạy bộ Fila FX-100 - Performance', 'UNISEX', 'Black', 2100000, 'https://example.com/fila-fx-001.jpg')
,('Fila Renno', 'FILA-RE-001', 'Giày sneaker Fila Renno - Modern classic', 'UNISEX', 'Black', 1800000, 'https://example.com/fila-re-001.jpg')
,('Fila Venom', 'FILA-VE-001', 'Giày bóng rổ Fila Venom - High performance', 'UNISEX', 'Black', 2800000, 'https://example.com/fila-ve-001.jpg')
,('Fila Volta', 'FILA-VO-001', 'Giày sneaker Fila Volta - Retro runner', 'UNISEX', 'Black', 1600000, 'https://example.com/fila-vo-001.jpg')
,('Fila Tracer', 'FILA-TR-001', 'Giày chạy bộ Fila Tracer - Speed', 'UNISEX', 'Black', 2000000, 'https://example.com/fila-tr-001.jpg')
,('Fila K-Plus', 'FILA-KP-001', 'Giày sneaker Fila K-Plus - Korean style', 'UNISEX', 'Black', 1700000, 'https://example.com/fila-kp-001.jpg')
,('Fila B-Plus', 'FILA-BP-001', 'Giày bóng rổ Fila B-Plus - Basketball', 'UNISEX', 'Black', 2400000, 'https://example.com/fila-bp-001.jpg')
,('Fila R-Plus', 'FILA-RP-001', 'Giày chạy bộ Fila R-Plus - Running', 'UNISEX', 'Black', 1900000, 'https://example.com/fila-rp-001.jpg')
,('Fila Skeletoes', 'FILA-SK-001', 'Giày sneaker Fila Skeletoes - Unique design', 'UNISEX', 'Black', 1800000, 'https://example.com/fila-sk-001.jpg')
,('Fila Original Tennis', 'FILA-OT-001', 'Giày tennis Fila Original Tennis - Court classic', 'UNISEX', 'Black', 1500000, 'https://example.com/fila-ot-001.jpg')
,('Fila Pro Trainer', 'FILA-PT-001', 'Giày tập luyện Fila Pro Trainer - Training', 'UNISEX', 'Black', 2000000, 'https://example.com/fila-pt-001.jpg')
,('Fila Speedshape', 'FILA-SS-001', 'Giày chạy bộ Fila Speedshape - Aerodynamic', 'UNISEX', 'Black', 2200000, 'https://example.com/fila-ss-001.jpg')
,('Fila Fusion', 'FILA-FU-001', 'Giày sneaker Fila Fusion - Hybrid design', 'UNISEX', 'Black', 1900000, 'https://example.com/fila-fu-001.jpg')
;

-- Bước 3: Lấy ID của brands và categories
SET @brand_nike = (SELECT id FROM brands WHERE name = 'Nike' LIMIT 1);
SET @brand_adidas = (SELECT id FROM brands WHERE name = 'Adidas' LIMIT 1);
SET @brand_puma = (SELECT id FROM brands WHERE name = 'Puma' LIMIT 1);
SET @brand_converse = (SELECT id FROM brands WHERE name = 'Converse' LIMIT 1);
SET @brand_vans = (SELECT id FROM brands WHERE name = 'Vans' LIMIT 1);
SET @brand_newbalance = (SELECT id FROM brands WHERE name = 'New Balance' LIMIT 1);
SET @brand_reebok = (SELECT id FROM brands WHERE name = 'Reebok' LIMIT 1);
SET @brand_fila = (SELECT id FROM brands WHERE name = 'Fila' LIMIT 1);

SET @cat_sport = (SELECT id FROM categories WHERE name = 'Giày thể thao' LIMIT 1);
SET @cat_running = (SELECT id FROM categories WHERE name = 'Giày chạy bộ' LIMIT 1);
SET @cat_basketball = (SELECT id FROM categories WHERE name = 'Giày bóng rổ' LIMIT 1);
SET @cat_football = (SELECT id FROM categories WHERE name = 'Giày đá bóng' LIMIT 1);
SET @cat_casual = (SELECT id FROM categories WHERE name = 'Giày casual' LIMIT 1);
SET @cat_sneaker = (SELECT id FROM categories WHERE name = 'Giày sneaker' LIMIT 1);
SET @cat_hightop = (SELECT id FROM categories WHERE name = 'Giày cao cổ' LIMIT 1);
SET @cat_lowtop = (SELECT id FROM categories WHERE name = 'Giày thấp cổ' LIMIT 1);

-- Bước 4: Liên kết Products với Brands
INSERT INTO product_brands (product_id, brand_id)
SELECT p.id, 
    CASE 
        WHEN p.code LIKE 'NIKE-%' THEN @brand_nike
        WHEN p.code LIKE 'ADIDAS-%' THEN @brand_adidas
        WHEN p.code LIKE 'PUMA-%' THEN @brand_puma
        WHEN p.code LIKE 'CONV-%' THEN @brand_converse
        WHEN p.code LIKE 'VANS-%' THEN @brand_vans
        WHEN p.code LIKE 'NB-%' THEN @brand_newbalance
        WHEN p.code LIKE 'REE-%' THEN @brand_reebok
        WHEN p.code LIKE 'FILA-%' THEN @brand_fila
    END as brand_id
FROM products p
WHERE p.id > (SELECT COALESCE(MAX(id), 0) - 200 FROM products);

-- Bước 5: Liên kết Products với Categories (mỗi sản phẩm 1 category)
INSERT INTO product_categories (product_id, category_id)
SELECT p.id, 
    CASE (p.id % 8)
        WHEN 0 THEN @cat_sport
        WHEN 1 THEN @cat_running
        WHEN 2 THEN @cat_basketball
        WHEN 3 THEN @cat_football
        WHEN 4 THEN @cat_casual
        WHEN 5 THEN @cat_sneaker
        WHEN 6 THEN @cat_hightop
        ELSE @cat_lowtop
    END as category_id
FROM products p
WHERE p.id > (SELECT COALESCE(MAX(id), 0) - 200 FROM products);

-- Bước 6: Tạo Product Properties (Sizes) cho mỗi sản phẩm
INSERT INTO product_properties (product_id, size, is_able, gender)
SELECT p.id, size_val, 1, p.gender
FROM products p
CROSS JOIN (
    SELECT 38 as size_val UNION SELECT 39 UNION SELECT 40 UNION SELECT 41 
    UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45
) sizes
WHERE p.id > (SELECT COALESCE(MAX(id), 0) - 200 FROM products);

-- Bước 7: Tạo Product Amounts (Số lượng trong kho) - Random 10-100 cho mỗi size
INSERT INTO product_amounts (product_properties_id, amount, updated_date)
SELECT pp.id, 
    FLOOR(10 + RAND() * 91) as amount,  -- Random 10-100
    NOW()
FROM product_properties pp
JOIN products p ON pp.product_id = p.id
WHERE p.id > (SELECT COALESCE(MAX(id), 0) - 200 FROM products);

-- Bước 8: Tạo Product Attachments (Hình ảnh) - 3 hình mỗi sản phẩm
INSERT INTO product_attachments (product_id, attachment)
SELECT p.id, 
    CONCAT('https://example.com/', LOWER(REPLACE(p.code, '-', '_')), '_', img_num, '.jpg') as attachment
FROM products p
CROSS JOIN (SELECT 1 as img_num UNION SELECT 2 UNION SELECT 3) imgs
WHERE p.id > (SELECT COALESCE(MAX(id), 0) - 200 FROM products);

-- Indexes đã được tạo trong CREATE TABLE
