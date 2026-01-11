-- Fix UTF-8 encoding issues trong bảng products.description
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/fix_products_description_utf8.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Update descriptions với UTF-8 đúng
-- Nike Products
UPDATE products SET description = 'Giày thể thao Nike Air Max 90 - Thiết kế cổ điển, đệm khí Air Max' WHERE code = 'NIKE-AM90-001';
UPDATE products SET description = 'Giày sneaker Nike Air Force 1 - Biểu tượng thời trang' WHERE code = 'NIKE-AF1-001';
UPDATE products SET description = 'Giày bóng rổ Nike Dunk Low - Phong cách retro' WHERE code = 'NIKE-DUNK-001';
UPDATE products SET description = 'Giày chạy bộ Nike React - Công nghệ React foam' WHERE code = 'NIKE-RE55-001';
UPDATE products SET description = 'Giày bóng rổ Air Jordan 1 - Huyền thoại' WHERE code = 'NIKE-AJ1-001';
UPDATE products SET description = 'Giày casual Nike Blazer Mid - Phong cách cổ điển' WHERE code = 'NIKE-BLZ-001';
UPDATE products SET description = 'Giày chạy bộ Nike Pegasus 38 - Đệm khí React' WHERE code = 'NIKE-PEG38-001';
UPDATE products SET description = 'Giày thể thao Nike Cortez - Biểu tượng văn hóa' WHERE code = 'NIKE-CORT-001';
UPDATE products SET description = 'Giày chạy bộ Nike VaporMax - Đệm khí toàn bộ' WHERE code = 'NIKE-VM-001';
UPDATE products SET description = 'Giày trượt ván Nike SB Dunk - Đế bền chắc' WHERE code = 'NIKE-SB-001';
UPDATE products SET description = 'Giày thể thao Nike Air Max 270 - Đệm khí lớn' WHERE code = 'NIKE-AM270-001';
UPDATE products SET description = 'Giày chạy bộ Nike ZoomX - Công nghệ carbon' WHERE code = 'NIKE-ZX-001';
UPDATE products SET description = 'Giày thể thao Nike Air Max 97 - Thiết kế tương lai' WHERE code = 'NIKE-AM97-001';
UPDATE products SET description = 'Giày chạy bộ Nike Free RN - Tự nhiên như chân trần' WHERE code = 'NIKE-FREE-001';
UPDATE products SET description = 'Giày bóng rổ Nike Kyrie 7 - Kiểm soát tốt' WHERE code = 'NIKE-KY7-001';
UPDATE products SET description = 'Giày bóng rổ Nike LeBron 18 - Hiệu suất cao' WHERE code = 'NIKE-LB18-001';
UPDATE products SET description = 'Giày thể thao Nike Air Max 95 - Phong cách 90s' WHERE code = 'NIKE-AM95-001';
UPDATE products SET description = 'Giày tập luyện Nike Metcon 7 - Ổn định cao' WHERE code = 'NIKE-MET7-001';
UPDATE products SET description = 'Giày thể thao Nike Air Max 720 - Đệm khí lớn nhất' WHERE code = 'NIKE-AM720-001';
UPDATE products SET description = 'Giày chạy bộ Nike Zoom Fly - Tốc độ cao' WHERE code = 'NIKE-ZF-001';
UPDATE products SET description = 'Giày thể thao Nike Air Max 98 - Retro style' WHERE code = 'NIKE-AM98-001';
UPDATE products SET description = 'Giày bóng rổ Nike Hyperdunk - Nhảy cao' WHERE code = 'NIKE-HD-001';
UPDATE products SET description = 'Giày thể thao Nike Air Max Plus - Tuned Air' WHERE code = 'NIKE-AMP-001';
UPDATE products SET description = 'Giày chạy bộ Nike Epic React - Phản hồi nhanh' WHERE code = 'NIKE-ER-001';
UPDATE products SET description = 'Giày thể thao Nike Air Max 200 - Giá rẻ' WHERE code = 'NIKE-AM200-001';

-- Adidas Products
UPDATE products SET description = 'Giày chạy bộ Adidas Ultraboost 22 - Boost technology' WHERE code = 'ADIDAS-UB22-001';
UPDATE products SET description = 'Giày sneaker Adidas Stan Smith - Classic' WHERE code = 'ADIDAS-SS-001';
UPDATE products SET description = 'Giày sneaker Adidas Superstar - Shell toe' WHERE code = 'ADIDAS-SUP-001';
UPDATE products SET description = 'Giày thể thao Adidas NMD R1 - Boost midsole' WHERE code = 'ADIDAS-NMD-001';
UPDATE products SET description = 'Giày sneaker Adidas Yeezy 350 - Limited edition' WHERE code = 'ADIDAS-YZ350-001';
UPDATE products SET description = 'Giày casual Adidas Gazelle - Retro style' WHERE code = 'ADIDAS-GAZ-001';
UPDATE products SET description = 'Giày đá bóng Adidas Samba - Indoor football' WHERE code = 'ADIDAS-SAM-001';
UPDATE products SET description = 'Giày đá bóng Adidas Predator - Control' WHERE code = 'ADIDAS-PRED-001';
UPDATE products SET description = 'Giày đá bóng Adidas Copa - Classic' WHERE code = 'ADIDAS-COP-001';
UPDATE products SET description = 'Giày chạy bộ Adidas Pureboost - Comfort' WHERE code = 'ADIDAS-PB-001';
UPDATE products SET description = 'Giày thể thao Adidas ZX 8000 - Retro running' WHERE code = 'ADIDAS-ZX8-001';
UPDATE products SET description = 'Giày bóng rổ Adidas Forum - High top' WHERE code = 'ADIDAS-FOR-001';
UPDATE products SET description = 'Giày sneaker Adidas Tubular - Futuristic' WHERE code = 'ADIDAS-TUB-001';
UPDATE products SET description = 'Giày chạy bộ Adidas Solarboost - Energy return' WHERE code = 'ADIDAS-SB-001';
UPDATE products SET description = 'Giày casual Adidas Originals - Street style' WHERE code = 'ADIDAS-ORG-001';
UPDATE products SET description = 'Giày leo núi Adidas Terrex - Traction' WHERE code = 'ADIDAS-TER-001';
UPDATE products SET description = 'Giày chạy bộ Adidas Response - Cushioning' WHERE code = 'ADIDAS-RES-001';
UPDATE products SET description = 'Giày chạy bộ Adidas Duramo - Daily runner' WHERE code = 'ADIDAS-DUR-001';
UPDATE products SET description = 'Giày thể thao Adidas Lite Racer - Lightweight' WHERE code = 'ADIDAS-LR-001';
UPDATE products SET description = 'Giày casual Adidas Cloudfoam - Comfort' WHERE code = 'ADIDAS-CF-001';
UPDATE products SET description = 'Giày tennis Adidas Advantage - Court grip' WHERE code = 'ADIDAS-ADV-001';
UPDATE products SET description = 'Giày tennis Adidas Grand Court - Classic' WHERE code = 'ADIDAS-GC-001';
UPDATE products SET description = 'Giày tennis Adidas Courtvantage - Performance' WHERE code = 'ADIDAS-CV-001';
UPDATE products SET description = 'Giày tennis Adidas Barricade - Stability' WHERE code = 'ADIDAS-BAR-001';
UPDATE products SET description = 'Giày chạy bộ Adidas Adizero - Speed' WHERE code = 'ADIDAS-AZ-001';

-- Puma Products
UPDATE products SET description = 'Giày sneaker Puma Suede Classic - Iconic style' WHERE code = 'PUMA-SUE-001';
UPDATE products SET description = 'Giày thể thao Puma RS-X - Retro future' WHERE code = 'PUMA-RSX-001';
UPDATE products SET description = 'Giày đua xe Puma Speedcat - Racing inspired' WHERE code = 'PUMA-SC-001';
UPDATE products SET description = 'Giày đá bóng Puma Future - Modern football' WHERE code = 'PUMA-FUT-001';
UPDATE products SET description = 'Giày đá bóng Puma One - Touch control' WHERE code = 'PUMA-ONE-001';
UPDATE products SET description = 'Giày sneaker Puma Thunder - Chunky design' WHERE code = 'PUMA-TH-001';
UPDATE products SET description = 'Giày casual Puma Cali - California style' WHERE code = 'PUMA-CAL-001';
UPDATE products SET description = 'Giày thể thao Puma Cell - Cushioning technology' WHERE code = 'PUMA-CEL-001';
UPDATE products SET description = 'Giày chạy bộ Puma Ignite - Energy return' WHERE code = 'PUMA-IGN-001';
UPDATE products SET description = 'Giày sneaker Puma Disc - Disc closure system' WHERE code = 'PUMA-DIS-001';
UPDATE products SET description = 'Giày bóng rổ Puma Basket - Court classic' WHERE code = 'PUMA-BAS-001';
UPDATE products SET description = 'Giày sneaker Puma Clyde - Basketball heritage' WHERE code = 'PUMA-CLY-001';
UPDATE products SET description = 'Giày sneaker Puma TSUGI - Japanese inspired' WHERE code = 'PUMA-TSU-001';
UPDATE products SET description = 'Giày casual Puma Leadcat - Slip-on style' WHERE code = 'PUMA-LEA-001';
UPDATE products SET description = 'Giày sneaker Puma Fierce - Bold design' WHERE code = 'PUMA-FIE-001';
UPDATE products SET description = 'Giày casual Puma Enzo - Italian style' WHERE code = 'PUMA-ENZ-001';
UPDATE products SET description = 'Giày sneaker Puma Vikky - Platform style' WHERE code = 'PUMA-VIK-001';
UPDATE products SET description = 'Giày sneaker Puma Mayze - Modern classic' WHERE code = 'PUMA-MAY-001';
UPDATE products SET description = 'Giày tennis Puma Smash - Court performance' WHERE code = 'PUMA-SMA-001';
UPDATE products SET description = 'Giày tennis Puma Court - Classic tennis' WHERE code = 'PUMA-COU-001';
UPDATE products SET description = 'Giày chạy bộ Puma Speed - Lightweight speed' WHERE code = 'PUMA-SPE-001';
UPDATE products SET description = 'Giày chạy bộ Puma Flex - Natural movement' WHERE code = 'PUMA-FLE-001';
UPDATE products SET description = 'Giày chạy bộ Puma NRGY - Energy foam' WHERE code = 'PUMA-NRG-001';
UPDATE products SET description = 'Giày chạy bộ Puma Hybrid - Hybrid cushioning' WHERE code = 'PUMA-HYB-001';
UPDATE products SET description = 'Giày đá bóng Puma Evospeed - Speed boot' WHERE code = 'PUMA-EVO-001';

-- Converse Products
UPDATE products SET description = 'Giày sneaker Converse Chuck Taylor All Star - Iconic classic' WHERE code = 'CONV-CT-001';
UPDATE products SET description = 'Giày sneaker Converse Chuck 70 - Premium version' WHERE code = 'CONV-C70-001';
UPDATE products SET description = 'Giày sneaker Converse One Star - Star logo' WHERE code = 'CONV-OS-001';
UPDATE products SET description = 'Giày sneaker Converse Jack Purcell - Smile design' WHERE code = 'CONV-JP-001';
UPDATE products SET description = 'Giày sneaker Converse Run Star - Platform style' WHERE code = 'CONV-RS-001';
UPDATE products SET description = 'Giày bóng rổ Converse Pro Leather - Classic' WHERE code = 'CONV-PL-001';
UPDATE products SET description = 'Giày bóng rổ Converse Fastbreak - Basketball heritage' WHERE code = 'CONV-FB-001';
UPDATE products SET description = 'Giày bóng rổ Converse Weapon - 80s basketball' WHERE code = 'CONV-WE-001';
UPDATE products SET description = 'Giày bóng rổ Converse All Star BB - Modern basketball' WHERE code = 'CONV-BB-001';
UPDATE products SET description = 'Giày bóng rổ Converse Star Player - Court performance' WHERE code = 'CONV-SP-001';
UPDATE products SET description = 'Giày sneaker Converse CONS - Skateboarding' WHERE code = 'CONV-CON-001';
UPDATE products SET description = 'Giày sneaker Converse Gianno - Japanese design' WHERE code = 'CONV-GIA-001';
UPDATE products SET description = 'Giày sneaker Converse ERX - 80s running' WHERE code = 'CONV-ERX-001';
UPDATE products SET description = 'Giày sneaker Converse Aerojam - Basketball style' WHERE code = 'CONV-AER-001';
UPDATE products SET description = 'Giày sneaker Converse All Star Lift - Platform' WHERE code = 'CONV-ASL-001';
UPDATE products SET description = 'Giày sneaker Converse Renew - Sustainable materials' WHERE code = 'CONV-REN-001';
UPDATE products SET description = 'Giày sneaker Converse CX - Cushioning technology' WHERE code = 'CONV-CX-001';
UPDATE products SET description = 'Giày sneaker Converse Move - Comfort focus' WHERE code = 'CONV-MOV-001';
UPDATE products SET description = 'Giày sneaker Converse Utility - Workwear style' WHERE code = 'CONV-UT-001';
UPDATE products SET description = 'Giày sneaker Converse First String - Premium collection' WHERE code = 'CONV-FS-001';
UPDATE products SET description = 'Giày sneaker Converse All Star Dainty - Low profile' WHERE code = 'CONV-DAI-001';
UPDATE products SET description = 'Giày sneaker Converse All Star Platform - Elevated' WHERE code = 'CONV-ASP-001';
UPDATE products SET description = 'Giày sneaker Converse All Star Move - Comfort' WHERE code = 'CONV-ASM-001';
UPDATE products SET description = 'Giày bóng rổ Converse All Star BB Evo - Evolution' WHERE code = 'CONV-BBE-001';
UPDATE products SET description = 'Giày sneaker Converse All Star Modern - Updated classic' WHERE code = 'CONV-ASM2-001';

-- Vans Products
UPDATE products SET description = 'Giày sneaker Vans Old Skool - Classic skate' WHERE code = 'VANS-OS-001';
UPDATE products SET description = 'Giày sneaker Vans Authentic - Original design' WHERE code = 'VANS-AUT-001';
UPDATE products SET description = 'Giày sneaker Vans Sk8-Hi - High top skate' WHERE code = 'VANS-SK8-001';
UPDATE products SET description = 'Giày sneaker Vans Era - Padded collar' WHERE code = 'VANS-ERA-001';
UPDATE products SET description = 'Giày sneaker Vans Slip-On - Easy on/off' WHERE code = 'VANS-SO-001';
UPDATE products SET description = 'Giày skate Vans Half Cab - Mid top' WHERE code = 'VANS-HC-001';
UPDATE products SET description = 'Giày sneaker Vans Chukka - Low top' WHERE code = 'VANS-CHU-001';
UPDATE products SET description = 'Giày sneaker Vans SK8-Mid - Mid top' WHERE code = 'VANS-SK8M-001';
UPDATE products SET description = 'Giày sneaker Vans Atwood - Classic style' WHERE code = 'VANS-AT-001';
UPDATE products SET description = 'Giày sneaker Vans Ward - Simple design' WHERE code = 'VANS-WA-001';
UPDATE products SET description = 'Giày skate Vans Old Skool Pro - Pro version' WHERE code = 'VANS-OSP-001';
UPDATE products SET description = 'Giày skate Vans Authentic Pro - Pro version' WHERE code = 'VANS-AUTP-001';
UPDATE products SET description = 'Giày skate Vans SK8-Hi Pro - Pro version' WHERE code = 'VANS-SK8P-001';
UPDATE products SET description = 'Giày sneaker Vans ComfyCush - Extra comfort' WHERE code = 'VANS-CC-001';
UPDATE products SET description = 'Giày chạy bộ Vans UltraRange - Running inspired' WHERE code = 'VANS-UR-001';
UPDATE products SET description = 'Giày sneaker Vans Wayvee - Modern style' WHERE code = 'VANS-WAY-001';
UPDATE products SET description = 'Giày skate Vans Kyle Walker - Pro signature' WHERE code = 'VANS-KW-001';
UPDATE products SET description = 'Giày skate Vans Rowan - Pro signature' WHERE code = 'VANS-ROW-001';
UPDATE products SET description = 'Giày skate Vans AVE - Pro signature' WHERE code = 'VANS-AVE-001';
UPDATE products SET description = 'Giày skate Vans Crockett - Pro signature' WHERE code = 'VANS-CRO-001';
UPDATE products SET description = 'Giày skate Vans Gilbert - Pro signature' WHERE code = 'VANS-GIL-001';
UPDATE products SET description = 'Giày skate Vans BMX - BMX inspired' WHERE code = 'VANS-BMX-001';
UPDATE products SET description = 'Giày sneaker Vans MTE - All weather' WHERE code = 'VANS-MTE-001';
UPDATE products SET description = 'Giày chạy bộ Vans Ultrarange Rapidweld - Running' WHERE code = 'VANS-URR-001';
UPDATE products SET description = 'Giày sneaker Vans Old Skool MTE - All weather' WHERE code = 'VANS-OSM-001';

-- New Balance Products
UPDATE products SET description = 'Giày sneaker New Balance 550 - Basketball heritage' WHERE code = 'NB-550-001';
UPDATE products SET description = 'Giày sneaker New Balance 574 - Classic runner' WHERE code = 'NB-574-001';
UPDATE products SET description = 'Giày chạy bộ New Balance 990 - Made in USA' WHERE code = 'NB-990-001';
UPDATE products SET description = 'Giày sneaker New Balance 327 - Retro runner' WHERE code = 'NB-327-001';
UPDATE products SET description = 'Giày chạy bộ New Balance 993 - Premium runner' WHERE code = 'NB-993-001';
UPDATE products SET description = 'Giày sneaker New Balance 530 - 90s runner' WHERE code = 'NB-530-001';
UPDATE products SET description = 'Giày chạy bộ New Balance 2002R - Modern retro' WHERE code = 'NB-2002-001';
UPDATE products SET description = 'Giày chạy bộ New Balance 1080 - Maximum cushioning' WHERE code = 'NB-1080-001';
UPDATE products SET description = 'Giày sneaker New Balance 997 - Made in USA' WHERE code = 'NB-997-001';
UPDATE products SET description = 'Giày sneaker New Balance 998 - Made in USA' WHERE code = 'NB-998-001';
UPDATE products SET description = 'Giày sneaker New Balance 1500 - Made in UK' WHERE code = 'NB-1500-001';
UPDATE products SET description = 'Giày sneaker New Balance 920 - Made in USA' WHERE code = 'NB-920-001';
UPDATE products SET description = 'Giày sneaker New Balance 1300 - Made in USA' WHERE code = 'NB-1300-001';
UPDATE products SET description = 'Giày sneaker New Balance 991 - Made in UK' WHERE code = 'NB-991-001';
UPDATE products SET description = 'Giày sneaker New Balance 992 - Made in USA' WHERE code = 'NB-992-001';
UPDATE products SET description = 'Giày sneaker New Balance 9060 - Modern design' WHERE code = 'NB-9060-001';
UPDATE products SET description = 'Giày chạy bộ New Balance 1906 - Retro runner' WHERE code = 'NB-1906-001';
UPDATE products SET description = 'Giày chạy bộ New Balance 860 - Stability runner' WHERE code = 'NB-860-001';
UPDATE products SET description = 'Giày chạy bộ New Balance 880 - Neutral runner' WHERE code = 'NB-880-001';
UPDATE products SET description = 'Giày sneaker New Balance 410 - Classic trail' WHERE code = 'NB-410-001';
UPDATE products SET description = 'Giày sneaker New Balance 373 - Entry level' WHERE code = 'NB-373-001';
UPDATE products SET description = 'Giày sneaker New Balance 420 - Classic style' WHERE code = 'NB-420-001';
UPDATE products SET description = 'Giày sneaker New Balance 515 - Classic style' WHERE code = 'NB-515-001';
UPDATE products SET description = 'Giày sneaker New Balance 624 - Classic style' WHERE code = 'NB-624-001';
UPDATE products SET description = 'Giày sneaker New Balance 680 - Classic style' WHERE code = 'NB-680-001';

-- Reebok Products
UPDATE products SET description = 'Giày sneaker Reebok Classic Leather - Iconic style' WHERE code = 'REE-CL-001';
UPDATE products SET description = 'Giày sneaker Reebok Club C 85 - Tennis heritage' WHERE code = 'REE-CC85-001';
UPDATE products SET description = 'Giày bóng rổ Reebok Pump - Inflatable technology' WHERE code = 'REE-PU-001';
UPDATE products SET description = 'Giày bóng rổ Reebok Question - Allen Iverson' WHERE code = 'REE-Q-001';
UPDATE products SET description = 'Giày bóng rổ Reebok Answer - Allen Iverson' WHERE code = 'REE-A-001';
UPDATE products SET description = 'Giày chạy bộ Reebok Zig - Zigzag sole' WHERE code = 'REE-ZIG-001';
UPDATE products SET description = 'Giày tập luyện Reebok Nano - CrossFit' WHERE code = 'REE-NA-001';
UPDATE products SET description = 'Giày chạy bộ Reebok Floatride - Energy foam' WHERE code = 'REE-FR-001';
UPDATE products SET description = 'Giày sneaker Reebok Instapump - Pump technology' WHERE code = 'REE-IP-001';
UPDATE products SET description = 'Giày tập luyện Reebok Workout - Classic training' WHERE code = 'REE-WO-001';
UPDATE products SET description = 'Giày sneaker Reebok Royal - Classic style' WHERE code = 'REE-RO-001';
UPDATE products SET description = 'Giày sneaker Reebok Ventilator - Breathable' WHERE code = 'REE-VE-001';
UPDATE products SET description = 'Giày chạy bộ Reebok DMX - Cushioning technology' WHERE code = 'REE-DMX-001';
UPDATE products SET description = 'Giày sneaker Reebok Aztrek - 90s runner' WHERE code = 'REE-AZ-001';
UPDATE products SET description = 'Giày sneaker Reebok Classic Nylon - Nylon upper' WHERE code = 'REE-CN-001';
UPDATE products SET description = 'Giày chạy bộ Reebok Fury - Speed focus' WHERE code = 'REE-FU-001';
UPDATE products SET description = 'Giày chạy bộ Reebok Forever Floatride - Long distance' WHERE code = 'REE-FFF-001';
UPDATE products SET description = 'Giày tập luyện Reebok Speed TR - Training' WHERE code = 'REE-ST-001';
UPDATE products SET description = 'Giày tập luyện Reebok CrossFit - CrossFit' WHERE code = 'REE-CF-001';
UPDATE products SET description = 'Giày sneaker Reebok Legacy - Heritage style' WHERE code = 'REE-LE-001';
UPDATE products SET description = 'Giày bóng rổ Reebok Kamikaze - Shawn Kemp' WHERE code = 'REE-KA-001';
UPDATE products SET description = 'Giày bóng rổ Reebok Shaq - Shaquille O''Neal' WHERE code = 'REE-SH-001';
UPDATE products SET description = 'Giày bóng rổ Reebok BB 4000 - Basketball' WHERE code = 'REE-BB4-001';
UPDATE products SET description = 'Giày tập luyện Reebok Ex-O-Fit - Classic training' WHERE code = 'REE-EX-001';
UPDATE products SET description = 'Giày sneaker Reebok Classic Plus - Updated classic' WHERE code = 'REE-CP-001';

-- Fila Products
UPDATE products SET description = 'Giày sneaker Fila Disruptor - Chunky platform' WHERE code = 'FILA-DIS-001';
UPDATE products SET description = 'Giày sneaker Fila Ray - Classic style' WHERE code = 'FILA-RAY-001';
UPDATE products SET description = 'Giày sneaker Fila Mindblower - Bold design' WHERE code = 'FILA-MB-001';
UPDATE products SET description = 'Giày bóng rổ Fila Grant Hill - Signature shoe' WHERE code = 'FILA-GH-001';
UPDATE products SET description = 'Giày sneaker Fila T-1 - Classic tennis' WHERE code = 'FILA-T1-001';
UPDATE products SET description = 'Giày sneaker Fila Spaghetti - Unique design' WHERE code = 'FILA-SPA-001';
UPDATE products SET description = 'Giày tập luyện Fila Original Fitness - Classic training' WHERE code = 'FILA-OF-001';
UPDATE products SET description = 'Giày sneaker Fila Trailblazer - Outdoor style' WHERE code = 'FILA-TB-001';
UPDATE products SET description = 'Giày chạy bộ Fila Axilus - Running' WHERE code = 'FILA-AX-001';
UPDATE products SET description = 'Giày bóng rổ Fila M-Squad - Basketball' WHERE code = 'FILA-MS-001';
UPDATE products SET description = 'Giày bóng rổ Fila 96 - 90s basketball' WHERE code = 'FILA-96-001';
UPDATE products SET description = 'Giày bóng rổ Fila Stackhouse - Signature shoe' WHERE code = 'FILA-ST-001';
UPDATE products SET description = 'Giày chạy bộ Fila FX-100 - Running' WHERE code = 'FILA-FX-001';
UPDATE products SET description = 'Giày sneaker Fila Renno - Modern style' WHERE code = 'FILA-RE-001';
UPDATE products SET description = 'Giày chạy bộ Fila Venom - Speed focus' WHERE code = 'FILA-VE-001';
UPDATE products SET description = 'Giày sneaker Fila Volta - Classic style' WHERE code = 'FILA-VO-001';
UPDATE products SET description = 'Giày chạy bộ Fila Tracer - Speed' WHERE code = 'FILA-TR-001';
UPDATE products SET description = 'Giày sneaker Fila K-Plus - Korean style' WHERE code = 'FILA-KP-001';
UPDATE products SET description = 'Giày bóng rổ Fila B-Plus - Basketball' WHERE code = 'FILA-BP-001';
UPDATE products SET description = 'Giày chạy bộ Fila R-Plus - Running' WHERE code = 'FILA-RP-001';
UPDATE products SET description = 'Giày sneaker Fila Skeletoes - Unique design' WHERE code = 'FILA-SK-001';
UPDATE products SET description = 'Giày tennis Fila Original Tennis - Court classic' WHERE code = 'FILA-OT-001';
UPDATE products SET description = 'Giày tập luyện Fila Pro Trainer - Training' WHERE code = 'FILA-PT-001';
UPDATE products SET description = 'Giày chạy bộ Fila Speedshape - Aerodynamic' WHERE code = 'FILA-SS-001';
UPDATE products SET description = 'Giày sneaker Fila Fusion - Hybrid design' WHERE code = 'FILA-FU-001';

SELECT 'Products descriptions UTF-8 fixed successfully!' as message;

