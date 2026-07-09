package com.smit.RealTimeChat.dto;

import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private String content;
    private String senderEmail;
    private String senderUsername;
    private Long chatRoomId;
    private LocalDateTime createdAt;
    private boolean delivered;
    private LocalDateTime deliveredAt;
    private boolean seen;
    private LocalDateTime seenAt;
    private String fileName;
    private String fileUrl;
    private String fileType;

    public MessageResponse() {
    }

    public MessageResponse(Long id, String content, String senderEmail, String senderUsername,
                            Long chatRoomId, LocalDateTime createdAt,
                            boolean delivered, LocalDateTime deliveredAt,
                            boolean seen, LocalDateTime seenAt,
                            String fileName, String fileUrl, String fileType) {
        this.id = id;
        this.content = content;
        this.senderEmail = senderEmail;
        this.senderUsername = senderUsername;
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
        this.delivered = delivered;
        this.deliveredAt = deliveredAt;
        this.seen = seen;
        this.seenAt = seenAt;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public LocalDateTime getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(LocalDateTime seenAt) {
        this.seenAt = seenAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}