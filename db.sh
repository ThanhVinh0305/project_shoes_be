#!/bin/bash

# Script để thao tác với MySQL database trong Docker container
# Usage: ./db.sh [command]

CONTAINER_NAME="mysqldb"
DB_NAME="e-commerce"
DB_USER="root"
DB_PASSWORD="tuanhung1999"

# Màu sắc cho output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

show_help() {
    echo -e "${BLUE}=== MySQL Database Helper Script ===${NC}"
    echo ""
    echo "Usage: ./db.sh [command]"
    echo ""
    echo "Commands:"
    echo "  connect      - Kết nối vào MySQL CLI (interactive mode)"
    echo "  tables       - Xem danh sách tất cả tables"
    echo "  users        - Xem tất cả users trong database"
    echo "  roles        - Xem tất cả roles"
    echo "  exec [sql]   - Chạy SQL query (ví dụ: ./db.sh exec 'SELECT * FROM users')"
    echo "  describe [table] - Xem cấu trúc bảng (ví dụ: ./db.sh describe users)"
    echo "  logs         - Xem logs của MySQL container"
    echo "  status       - Kiểm tra trạng thái container"
    echo "  help         - Hiển thị hướng dẫn này"
    echo ""
    echo "Connection Info:"
    echo "  Host: localhost"
    echo "  Port: 3306"
    echo "  Database: $DB_NAME"
    echo "  Username: $DB_USER"
    echo "  Password: $DB_PASSWORD"
    echo ""
}

check_container() {
    if ! docker ps | grep -q "$CONTAINER_NAME"; then
        echo -e "${YELLOW}⚠️  Container $CONTAINER_NAME không đang chạy!${NC}"
        echo "Hãy chạy: docker-compose up -d mysql"
        exit 1
    fi
}

case "$1" in
    connect)
        check_container
        echo -e "${GREEN}🔌 Đang kết nối vào MySQL...${NC}"
        echo -e "${BLUE}Tip: Gõ 'exit' để thoát${NC}"
        docker exec -it $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME
        ;;
    tables)
        check_container
        echo -e "${GREEN}📋 Danh sách tables trong database '$DB_NAME':${NC}"
        docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "SHOW TABLES;" 2>/dev/null | grep -v "Warning"
        ;;
    users)
        check_container
        echo -e "${GREEN}👥 Danh sách users:${NC}"
        docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "SELECT id, username, email, phone_number, first_name, last_name, active FROM users;" 2>/dev/null | grep -v "Warning"
        ;;
    roles)
        check_container
        echo -e "${GREEN}🔐 Danh sách roles:${NC}"
        docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "SELECT * FROM roles;" 2>/dev/null | grep -v "Warning"
        ;;
    exec)
        check_container
        if [ -z "$2" ]; then
            echo -e "${YELLOW}⚠️  Vui lòng cung cấp SQL query${NC}"
            echo "Ví dụ: ./db.sh exec 'SELECT * FROM users LIMIT 5'"
            exit 1
        fi
        echo -e "${GREEN}🔍 Chạy SQL query:${NC}"
        docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "$2" 2>/dev/null | grep -v "Warning"
        ;;
    describe)
        check_container
        if [ -z "$2" ]; then
            echo -e "${YELLOW}⚠️  Vui lòng cung cấp tên bảng${NC}"
            echo "Ví dụ: ./db.sh describe users"
            exit 1
        fi
        echo -e "${GREEN}📊 Cấu trúc bảng '$2':${NC}"
        docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "DESCRIBE $2;" 2>/dev/null | grep -v "Warning"
        ;;
    logs)
        check_container
        echo -e "${GREEN}📜 MySQL logs:${NC}"
        docker logs --tail 50 $CONTAINER_NAME
        ;;
    status)
        echo -e "${GREEN}📊 Trạng thái container:${NC}"
        docker ps | grep $CONTAINER_NAME || echo -e "${YELLOW}Container không chạy${NC}"
        echo ""
        echo -e "${GREEN}Port mapping:${NC}"
        docker port $CONTAINER_NAME 2>/dev/null || echo -e "${YELLOW}Container không chạy${NC}"
        ;;
    help|--help|-h|"")
        show_help
        ;;
    *)
        echo -e "${YELLOW}⚠️  Lệnh không hợp lệ: $1${NC}"
        echo ""
        show_help
        exit 1
        ;;
esac


