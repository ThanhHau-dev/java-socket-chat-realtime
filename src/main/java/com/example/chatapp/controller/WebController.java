package com.example.chatapp.controller;

import com.example.chatapp.entity.User;
import com.example.chatapp.entity.ChatRoom;
import com.example.chatapp.entity.Message;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.repository.ChatRoomRepository;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.ArrayList;

/**
 * Controller xử lý các request HTTP cho giao diện web
 */
@Controller
public class WebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    /**
     * Trang chủ - hiển thị form đăng nhập
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Trang chat chính - sử dụng authentication service
     */
    @GetMapping("/chat")
    public String chatPage(@RequestParam("username") String username, 
                          @RequestParam(value = "roomId", defaultValue = "1") Long roomId,
                          Model model) {
        
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                model.addAttribute("error", "Vui lòng đăng nhập lại!");
                return "index";
            }
            
            // Cập nhật status user online
            user.setStatus(User.UserStatus.online);
            userRepository.save(user);
            
            // Lấy danh sách phòng chat của user
            List<ChatRoom> rooms;
            try {
                rooms = userService.getUserRooms(user.getId());
            } catch (Exception e) {
                // Fallback: lấy tất cả phòng
                rooms = chatRoomRepository.findAll();
            }
            
            // Lấy tin nhắn trong phòng hiện tại
            List<Message> messages = messageRepository.findByRoomIdOrderBySentAtAsc(roomId);
            // Lấy thông tin phòng hiện tại
            ChatRoom currentRoom = chatRoomRepository.findById(roomId).orElse(null);
            
            model.addAttribute("username", username);
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("userId", user.getId());
            model.addAttribute("currentRoomId", roomId);
            model.addAttribute("currentRoomName", currentRoom != null ? currentRoom.getName() : "Phòng Chat");
            model.addAttribute("rooms", rooms);
            model.addAttribute("messages", messages != null ? messages : new ArrayList<>());
            
            return "chat";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "index";
        }
    }

    /**
     * Chuyển phòng chat
     */
    @GetMapping("/chat/room/{roomId}")
    public String switchRoom(@PathVariable Long roomId,
                            @RequestParam("username") String username) {
        return "redirect:/chat?username=" + username + "&roomId=" + roomId;
    }

    /**
     * Test endpoint để kiểm tra database connection
     */
    @GetMapping("/test")
    public String testDatabase(Model model) {
        try {
            long userCount = userRepository.count();
            long roomCount = chatRoomRepository.count();
            long messageCount = messageRepository.count();
            
            model.addAttribute("message", "✅ Database connected successfully!");
            model.addAttribute("userCount", userCount);
            model.addAttribute("roomCount", roomCount);
            model.addAttribute("messageCount", messageCount);
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("rooms", chatRoomRepository.findAll());
            model.addAttribute("databaseStatus", "success");
            
            if (userCount == 0) {
                model.addAttribute("warning", "Database trống! Cần chạy schema.sql để tạo dữ liệu mẫu.");
            }
            
        } catch (Exception e) {
            model.addAttribute("message", "❌ Database error: " + e.getMessage());
            model.addAttribute("databaseStatus", "error");
            model.addAttribute("errorDetail", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return "test";
    }

    /**
     * API tìm kiếm user
     */
    @GetMapping("/api/search-users")
    @ResponseBody
    public List<User> searchUsers(@RequestParam("q") String query) {
        try {
            return userService.searchUsers(query);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tạo phòng chat riêng với user khác
     */
    @GetMapping("/chat/private/{targetUserId}")
    public String createPrivateChat(@PathVariable Long targetUserId,
                                   @RequestParam("username") String username) {
        
        System.out.println("=== CONTROLLER DEBUG ===");
        System.out.println("Request URL: /chat/private/" + targetUserId + "?username=" + username);
        System.out.println("Target user ID: " + targetUserId);
        System.out.println("Current username: " + username);
        
        try {
            User currentUser = userRepository.findByUsername(username).orElse(null);
            if (currentUser == null) {
                System.out.println("ERROR: Current user not found: " + username);
                return "redirect:/?error=user_not_found";
            }
            
            System.out.println("Current user found: " + currentUser.getId() + " (" + currentUser.getFullName() + ")");
            
            // Kiểm tra target user có tồn tại không
            User targetUser = userRepository.findById(targetUserId).orElse(null);
            if (targetUser == null) {
                System.out.println("ERROR: Target user not found: " + targetUserId);
                return "redirect:/chat?username=" + username + "&error=target_not_found";
            }
            
            System.out.println("Target user found: " + targetUser.getId() + " (" + targetUser.getFullName() + ")");
            
            ChatRoom privateRoom = userService.getOrCreatePrivateRoom(currentUser.getId(), targetUserId);
            
            if (privateRoom != null) {
                String redirectUrl = "redirect:/chat?username=" + username + "&roomId=" + privateRoom.getId();
                System.out.println("SUCCESS: Redirecting to: " + redirectUrl);
                return redirectUrl;
            } else {
                System.out.println("ERROR: Could not create private room");
                return "redirect:/chat?username=" + username + "&error=room_creation_failed";
            }
            
        } catch (Exception e) {
            System.out.println("EXCEPTION in createPrivateChat: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/chat?username=" + username + "&error=exception";
        }
    }
}
