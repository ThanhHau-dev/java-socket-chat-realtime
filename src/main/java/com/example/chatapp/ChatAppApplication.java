package com.example.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.chatapp.repository.UserRepository;

/**
 * Lớp chính để khởi chạy ứng dụng chat
 */
@SpringBootApplication
public class ChatAppApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(ChatAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n🚀 Chat App đã khởi chạy thành công!");
        System.out.println("📱 Truy cập: http://localhost:8080");
        System.out.println("🔧 Test database: http://localhost:8080/test");
        System.out.println("👤 Demo users: admin, user1, user2");
        
        try {
            long userCount = userRepository.count();
            System.out.println("✅ Database connected! Users: " + userCount);
            
            if (userCount == 0) {
                System.out.println("⚠️  Database trống! Hãy chạy file schema.sql");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Database error: " + e.getMessage());
            System.out.println("🔧 Hãy chạy schema.sql trong MySQL để tạo database!");
        }
        System.out.println();
    }
}
