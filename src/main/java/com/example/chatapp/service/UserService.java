package com.example.chatapp.service;

import com.example.chatapp.entity.User;
import com.example.chatapp.entity.ChatRoom;
import com.example.chatapp.entity.RoomMember;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.repository.ChatRoomRepository;
import com.example.chatapp.repository.RoomMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý logic nghiệp vụ cho User
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private RoomMemberRepository roomMemberRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Tạo user mới
     */
    @Transactional
    public User createUser(String username, String password, String fullName) {
        if (existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại!");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setStatus(User.UserStatus.offline);
        
        user = userRepository.save(user);
        
        // Tự động thêm user vào phòng chat chung (room ID = 1)
        try {
            RoomMember member = new RoomMember(1L, user.getId());
            roomMemberRepository.save(member);
        } catch (Exception e) {
            System.out.println("Không thể thêm user vào phòng chat chung: " + e.getMessage());
        }
        
        return user;
    }

    /**
     * Xác thực user
     */
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }

    /**
     * Tìm kiếm user theo username hoặc full name
     */
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findByUsernameContainingOrFullNameContaining(
            query.trim(), query.trim()
        );
    }

    /**
     * Lấy danh sách phòng chat của user
     */
    public List<ChatRoom> getUserRooms(Long userId) {
        return chatRoomRepository.findRoomsByUserId(userId);
    }

    /**
     * Tạo hoặc lấy phòng chat private giữa 2 user
     */
    @Transactional
    public ChatRoom getOrCreatePrivateRoom(Long userId1, Long userId2) {
        System.out.println("=== getOrCreatePrivateRoom DEBUG ===");
        System.out.println("User1 ID: " + userId1);
        System.out.println("User2 ID: " + userId2);
        
        try {
            // Kiểm tra 2 user có tồn tại không
            User user1 = userRepository.findById(userId1).orElse(null);
            User user2 = userRepository.findById(userId2).orElse(null);
            
            if (user1 == null) {
                System.out.println("ERROR: User1 not found with ID: " + userId1);
                return null;
            }
            if (user2 == null) {
                System.out.println("ERROR: User2 not found with ID: " + userId2);
                return null;
            }
            
            System.out.println("Found User1: " + user1.getUsername() + " (" + user1.getFullName() + ")");
            System.out.println("Found User2: " + user2.getUsername() + " (" + user2.getFullName() + ")");
            
            // Tìm phòng private đã tồn tại (cả 2 chiều)
            List<ChatRoom> existingRooms1 = chatRoomRepository.findPrivateRoomBetweenUsers(userId1, userId2);
            List<ChatRoom> existingRooms2 = chatRoomRepository.findPrivateRoomBetweenUsers(userId2, userId1);
            
            System.out.println("Existing rooms user1->user2: " + existingRooms1.size());
            System.out.println("Existing rooms user2->user1: " + existingRooms2.size());
            
            if (!existingRooms1.isEmpty()) {
                ChatRoom room = existingRooms1.get(0);
                System.out.println("Using existing room: " + room.getId() + " - " + room.getName());
                return room;
            }
            
            if (!existingRooms2.isEmpty()) {
                ChatRoom room = existingRooms2.get(0);
                System.out.println("Using existing room: " + room.getId() + " - " + room.getName());
                return room;
            }
            
            // Tạo phòng mới
            System.out.println("Creating new private room...");
            
            ChatRoom privateRoom = new ChatRoom();
            privateRoom.setName(user1.getFullName() + " & " + user2.getFullName());
            privateRoom.setDescription("Chat riêng giữa " + user1.getFullName() + " và " + user2.getFullName());
            privateRoom.setRoomType("private"); // Sử dụng String thay vì enum
            privateRoom.setCreatedBy(userId1);
            
            privateRoom = chatRoomRepository.save(privateRoom);
            System.out.println("Created room with ID: " + privateRoom.getId());
            System.out.println("Room name: " + privateRoom.getName());
            System.out.println("Room type: " + privateRoom.getRoomType());
            
            // Thêm 2 user vào phòng
            System.out.println("Adding members to room...");
            
            RoomMember member1 = new RoomMember(privateRoom.getId(), userId1);
            RoomMember member2 = new RoomMember(privateRoom.getId(), userId2);
            
            roomMemberRepository.save(member1);
            System.out.println("Added member1: " + userId1);
            
            roomMemberRepository.save(member2);
            System.out.println("Added member2: " + userId2);
            
            // Verify members were added
            List<RoomMember> members = roomMemberRepository.findByRoomId(privateRoom.getId());
            System.out.println("Total members in room " + privateRoom.getId() + ": " + members.size());
            
            System.out.println("Private room created successfully!");
            return privateRoom;
            
        } catch (Exception e) {
            System.out.println("ERROR in getOrCreatePrivateRoom: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
