#!/bin/bash

# Script import ảnh từ folder vào MinIO và cập nhật database
# Sử dụng: ./scripts/import_images_from_folder.sh <folder_path>

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

FOLDER_PATH="${1:-./images}"
MINIO_URL="http://localhost:9000"
MINIO_BUCKET="products"
MINIO_ACCESS_KEY="kWu8WEj6n28m6CjqUpd2"
MINIO_SECRET_KEY="mqrPrpYnSIw8TEP0Je6IicjpfKU5MjcHmOWvQ1LC"
DB_USER="root"
DB_PASS="tuanhung1999"
DB_NAME="e-commerce"

echo -e "${GREEN}=== Script Import Ảnh Sản Phẩm ===${NC}"
echo "Folder: $FOLDER_PATH"
echo ""

# Kiểm tra folder tồn tại
if [ ! -d "$FOLDER_PATH" ]; then
    echo -e "${RED}Error: Folder không tồn tại: $FOLDER_PATH${NC}"
    exit 1
fi

# Kiểm tra MinIO client (mc) có sẵn không
if ! command -v mc &> /dev/null; then
    echo -e "${YELLOW}MinIO client (mc) chưa được cài đặt.${NC}"
    echo "Đang cài đặt mc..."
    # Có thể cần cài mc hoặc dùng docker exec
    echo "Hoặc sử dụng: docker exec minio mc ..."
fi

# Đếm số file
TOTAL_FILES=$(find "$FOLDER_PATH" -type f \( -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.png" -o -iname "*.webp" -o -iname "*.avif" \) | wc -l)
echo -e "${GREEN}Tìm thấy $TOTAL_FILES file ảnh${NC}"
echo ""

# Tạo file SQL để cập nhật database
SQL_FILE="./sql/update_image_urls.sql"
echo "-- Script cập nhật URL ảnh từ MinIO" > "$SQL_FILE"
echo "-- Được tạo tự động bởi import_images_from_folder.sh" >> "$SQL_FILE"
echo "" >> "$SQL_FILE"
echo "SET NAMES utf8mb4;" >> "$SQL_FILE"
echo "" >> "$SQL_FILE"

# Counter
UPDATED_COUNT=0
ERROR_COUNT=0

# Xử lý từng file
find "$FOLDER_PATH" -type f \( -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.png" -o -iname "*.webp" -o -iname "*.avif" \) | while read -r IMAGE_FILE; do
    FILENAME=$(basename "$IMAGE_FILE")
    FILENAME_NO_EXT="${FILENAME%.*}"
    EXTENSION="${FILENAME##*.}"
    
    echo -e "${YELLOW}Đang xử lý: $FILENAME${NC}"
    
    # Parse tên file để lấy product code
    # Format có thể là: NIKE-AM90-001_thumbnail.jpg hoặc NIKE-AM90-001_1.jpg
    if [[ "$FILENAME_NO_EXT" == *_thumbnail ]]; then
        # Ảnh thumbnail
        PRODUCT_CODE="${FILENAME_NO_EXT%_thumbnail}"
        IS_THUMBNAIL=true
    elif [[ "$FILENAME_NO_EXT" =~ ^(.+)_([0-9]+)$ ]]; then
        # Ảnh chi tiết (có số)
        PRODUCT_CODE="${BASH_REMATCH[1]}"
        IMAGE_NUMBER="${BASH_REMATCH[2]}"
        IS_THUMBNAIL=false
    else
        # Chỉ có product code (ảnh đầu tiên)
        PRODUCT_CODE="$FILENAME_NO_EXT"
        IS_THUMBNAIL=false
        IMAGE_NUMBER="1"
    fi
    
    # Tạo key cho MinIO (giữ nguyên tên file hoặc tạo mới)
    MINIO_KEY="products/$FILENAME"
    
    # Upload vào MinIO (sử dụng docker exec)
    echo "  → Upload vào MinIO: $MINIO_KEY"
    docker cp "$IMAGE_FILE" minio:/tmp/ 2>/dev/null
    if [ $? -eq 0 ]; then
        docker exec minio mc cp "/tmp/$FILENAME" "$MINIO_BUCKET/products/" 2>/dev/null
        if [ $? -eq 0 ]; then
            IMAGE_URL="$MINIO_URL/$MINIO_BUCKET/products/$FILENAME"
            echo -e "  ${GREEN}✓ Upload thành công${NC}"
            
            # Tạo SQL để cập nhật
            if [ "$IS_THUMBNAIL" = true ]; then
                # Cập nhật thumbnail_img
                echo "UPDATE products SET thumbnail_img = '$IMAGE_URL' WHERE code = '$PRODUCT_CODE';" >> "$SQL_FILE"
            else
                # Cập nhật product_attachments
                echo "INSERT INTO product_attachments (product_id, attachment) " >> "$SQL_FILE"
                echo "SELECT id, '$IMAGE_URL' FROM products WHERE code = '$PRODUCT_CODE' " >> "$SQL_FILE"
                echo "ON DUPLICATE KEY UPDATE attachment = '$IMAGE_URL';" >> "$SQL_FILE"
            fi
            
            UPDATED_COUNT=$((UPDATED_COUNT + 1))
        else
            echo -e "  ${RED}✗ Lỗi upload${NC}"
            ERROR_COUNT=$((ERROR_COUNT + 1))
        fi
    else
        echo -e "  ${RED}✗ Lỗi copy file${NC}"
        ERROR_COUNT=$((ERROR_COUNT + 1))
    fi
    
    echo ""
done

echo -e "${GREEN}=== Hoàn thành ===${NC}"
echo "Đã xử lý: $UPDATED_COUNT file"
echo "Lỗi: $ERROR_COUNT file"
echo ""
echo "File SQL đã được tạo: $SQL_FILE"
echo "Chạy lệnh sau để cập nhật database:"
echo "docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < $SQL_FILE"

