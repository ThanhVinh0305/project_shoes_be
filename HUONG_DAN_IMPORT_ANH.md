# Hướng dẫn Import Ảnh Sản Phẩm

## Format tên file

### Ảnh đại diện (Thumbnail):
- `NIKE-AM90-001_thumbnail.jpg`
- `ADIDAS-UB22-001_thumbnail.jpg`

### Ảnh chi tiết:
- `NIKE-AM90-001_1.jpg` (ảnh chi tiết số 1)
- `NIKE-AM90-001_2.jpg` (ảnh chi tiết số 2)
- `NIKE-AM90-001_3.jpg` (ảnh chi tiết số 3)

## Cách sử dụng

### Bước 1: Chuẩn bị folder ảnh
- Tạo folder `images/` trong workspace
- Đặt tất cả ảnh vào folder này
- Đảm bảo tên file trùng khớp với product code

### Bước 2: Chạy script import
```bash
chmod +x scripts/import_images_from_folder.sh
./scripts/import_images_from_folder.sh ./images
```

### Bước 3: Cập nhật database
Script sẽ tạo file SQL tại `sql/update_image_urls.sql`
Chạy lệnh:
```bash
docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/update_image_urls.sql
```

## Lưu ý

1. **Tên file phải trùng với product code** (ví dụ: `NIKE-AM90-001`)
2. **Ảnh thumbnail**: thêm `_thumbnail` vào cuối (trước extension)
3. **Ảnh chi tiết**: thêm `_1`, `_2`, `_3`... vào cuối
4. **Format ảnh**: `.jpg`, `.jpeg`, `.png`, `.webp`, `.avif`

## Ví dụ cấu trúc folder

```
images/
├── NIKE-AM90-001_thumbnail.jpg
├── NIKE-AM90-001_1.jpg
├── NIKE-AM90-001_2.jpg
├── NIKE-AM90-001_3.jpg
├── NIKE-AF1-001_thumbnail.jpg
├── NIKE-AF1-001_1.jpg
└── ...
```

