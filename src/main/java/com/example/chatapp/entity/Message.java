package com.example.chatapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity đại diện cho tin nhắn trong chat
 */
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_id", nullable = false)
    private Long roomId;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.text;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_original_name")
    private String fileOriginalName;
    
    public enum MessageType {
        text, image, file
    }
    
    // Constructors
    public Message() {
        this.sentAt = LocalDateTime.now();
    }
    
    public Message(Long roomId, Long senderId, String content) {
        this();
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
    }
    
    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileOriginalName() { return fileOriginalName; }
    public void setFileOriginalName(String fileOriginalName) { this.fileOriginalName = fileOriginalName; }

    public Boolean getIsImage() { 
        return messageType == MessageType.image;
    }
    public void setIsImage(Boolean isImage) {
        if (isImage) {
            this.messageType = MessageType.image;
        } else {
            this.messageType = MessageType.file;
        }
    }
}
