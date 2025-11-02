package com.example.chatapp.dto;

public class MessageView {
    private Long senderId;
    private String content;
    private java.time.LocalDateTime sentAt;
    private String type;
    private Boolean isImage;
    private String fileOriginalName;

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public java.time.LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(java.time.LocalDateTime sentAt) { this.sentAt = sentAt; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getIsImage() { return isImage; }
    public void setIsImage(Boolean isImage) { this.isImage = isImage; }
    public String getFileOriginalName() { return fileOriginalName; }
    public void setFileOriginalName(String fileOriginalName) { this.fileOriginalName = fileOriginalName; }
}