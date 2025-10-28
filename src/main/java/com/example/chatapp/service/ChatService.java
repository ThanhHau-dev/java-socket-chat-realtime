package com.example.chatapp.service;

import com.example.chatapp.dto.ChatMessage;
import com.example.chatapp.entity.Message;
import com.example.chatapp.entity.User;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service xử lý các logic nghiệp vụ cho chat
 */
@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Lưu tin nhắn vào database
     */
    public void saveMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
            if (sender != null) {
                Message message = new Message();
                message.setRoomId(chatMessage.getRoomId());
                message.setSenderId(sender.getId());
                message.setContent(chatMessage.getContent());
                messageRepository.save(message);
            }
        }
    }

    /**
     * Lấy lịch sử tin nhắn trong phòng chat
     */
    public List<Message> getMessageHistory(Long roomId) {
        return messageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    /**
     * Cập nhật trạng thái online/offline của user
     */
    public void updateUserStatus(String username, String status) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setStatus(User.UserStatus.valueOf(status));
            userRepository.save(user);
        }
    }

    /**
     * Lấy thông tin user theo username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
