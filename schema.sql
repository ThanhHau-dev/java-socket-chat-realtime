CREATE DATABASE IF NOT EXISTS chat_app;
USE chat_app;

-- Xóa các bảng cũ nếu tồn tại (để test)
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS room_members;
DROP TABLE IF EXISTS chat_rooms;
DROP TABLE IF EXISTS users;

-- Bảng người dùng
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    status ENUM('online', 'offline', 'away') DEFAULT 'offline',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng phòng chat
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    room_type ENUM('private', 'group') DEFAULT 'group',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Bảng thành viên phòng chat
CREATE TABLE room_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_member (room_id, user_id)
);

-- Bảng tin nhắn
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    message_type ENUM('text', 'image', 'file') DEFAULT 'text',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_name VARCHAR(255),
    file_original_name VARCHAR(255),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Chèn dữ liệu người dùng (password: "password" đã được hash bằng BCrypt)
INSERT INTO users (username, password, full_name) VALUES 
('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'Administrator'),
('user1', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'Người dùng 1'),
('user2', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'Người dùng 2');

-- Tạo phòng chat mặc định
INSERT INTO chat_rooms (id, name, description, room_type, created_by) VALUES 
(1, 'Phòng Chat Chung', 'Phòng chat dành cho tất cả mọi người', 'group', 1),
(2, 'Phòng Thảo Luận', 'Phòng thảo luận các vấn đề chung', 'group', 1),
(3, 'Phòng Hỗ Trợ', 'Phòng hỗ trợ kỹ thuật', 'group', 1);

-- Thêm tất cả user vào phòng chat chung
INSERT INTO room_members (room_id, user_id) VALUES 
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 3);

-- Thêm một số tin nhắn mẫu
INSERT INTO messages (room_id, sender_id, content) VALUES 
(1, 1, 'Chào mừng đến với phòng chat! 👋'),
(1, 2, 'Xin chào mọi người! 😊'),
(2, 1, 'Đây là phòng thảo luận các vấn đề quan trọng'),
(3, 1, 'Có vấn đề gì cần hỗ trợ không?');
