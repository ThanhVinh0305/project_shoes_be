-- SQL Script để tạo các bảng cho Chatbot System
-- Chạy: docker exec -i mysqldb mysql -uroot -ptuanhung1999 e-commerce --default-character-set=utf8mb4 < sql/create_chatbot_tables.sql

-- Thiết lập UTF-8 để tránh lỗi encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- Bảng 1: chat_conversations - Cuộc hội thoại
CREATE TABLE IF NOT EXISTS chat_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL, -- NULL nếu là guest
    session_id VARCHAR(255) NOT NULL, -- Session ID cho guest users
    status ENUM('ACTIVE', 'CLOSED', 'ARCHIVED') DEFAULT 'ACTIVE',
    started_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_message_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    closed_date DATETIME DEFAULT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status),
    INDEX idx_last_message (last_message_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng 2: chat_messages - Tin nhắn trong cuộc hội thoại
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_type ENUM('USER', 'BOT', 'ADMIN') NOT NULL,
    sender_id BIGINT DEFAULT NULL, -- user_id nếu sender_type = USER hoặc ADMIN
    message_text TEXT NOT NULL,
    message_type ENUM('TEXT', 'IMAGE', 'PRODUCT_LINK', 'QUICK_REPLY', 'BUTTON') DEFAULT 'TEXT',
    metadata JSON DEFAULT NULL, -- Lưu thêm thông tin (product_id, image_url, button_data, etc.)
    is_read BOOLEAN DEFAULT FALSE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender_type (sender_type),
    INDEX idx_created_date (created_date),
    FOREIGN KEY (conversation_id) REFERENCES chat_conversations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng 3: chat_faq - Câu hỏi thường gặp và câu trả lời
CREATE TABLE IF NOT EXISTS chat_faq (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    category VARCHAR(100) DEFAULT NULL, -- VD: 'SHIPPING', 'RETURNS', 'PRODUCTS', 'PAYMENT'
    keywords JSON DEFAULT NULL, -- Từ khóa liên quan để matching
    priority INT DEFAULT 0, -- Độ ưu tiên (cao hơn = hiển thị trước)
    is_active BOOLEAN DEFAULT TRUE,
    view_count INT DEFAULT 0,
    helpful_count INT DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_priority (priority),
    INDEX idx_active (is_active),
    FULLTEXT INDEX idx_question_fulltext (question)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng 4: chat_analytics - Phân tích hiệu suất chatbot
CREATE TABLE IF NOT EXISTS chat_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT DEFAULT NULL,
    user_id BIGINT DEFAULT NULL,
    event_type ENUM('CONVERSATION_STARTED', 'MESSAGE_SENT', 'FAQ_MATCHED', 'PRODUCT_RECOMMENDED', 'CONVERSATION_CLOSED', 'SATISFACTION_RATING') NOT NULL,
    event_data JSON DEFAULT NULL, -- Chi tiết sự kiện
    satisfaction_rating INT DEFAULT NULL, -- 1-5 nếu event_type = SATISFACTION_RATING
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_event_type (event_type),
    INDEX idx_created_date (created_date),
    INDEX idx_rating (satisfaction_rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

