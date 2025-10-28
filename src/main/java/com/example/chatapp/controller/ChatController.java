package com.example.chatapp.controller;

import com.example.chatapp.dto.ChatMessage;
import com.example.chatapp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controller xử lý các message WebSocket cho chat
 */
@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ChatService chatService;

    /**
     * Xử lý tin nhắn được gửi đến phòng chat
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // Lưu tin nhắn vào database
        chatService.saveMessage(chatMessage);
        
        // Gửi tin nhắn đến tất cả thành viên trong phòng
        messagingTemplate.convertAndSend(
            "/topic/room/" + chatMessage.getRoomId(), 
            chatMessage
        );
    }

    /**
     * Xử lý khi user join phòng chat
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                       SimpMessageHeaderAccessor headerAccessor) {
        // Lưu username vào session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());
        
        // Cập nhật status user online
        chatService.updateUserStatus(chatMessage.getSender(), "online");
        
        // Thông báo user join
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        messagingTemplate.convertAndSend(
            "/topic/room/" + chatMessage.getRoomId(), 
            chatMessage
        );
    }

    /**
     * Xử lý khi user rời phòng chat
     */
    @MessageMapping("/chat.leaveUser")
    public void leaveUser(@Payload ChatMessage chatMessage,
                         SimpMessageHeaderAccessor headerAccessor) {
        // Cập nhật status user offline
        chatService.updateUserStatus(chatMessage.getSender(), "offline");
        
        // Thông báo user leave
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        messagingTemplate.convertAndSend(
            "/topic/room/" + chatMessage.getRoomId(), 
            chatMessage
        );
    }
}
