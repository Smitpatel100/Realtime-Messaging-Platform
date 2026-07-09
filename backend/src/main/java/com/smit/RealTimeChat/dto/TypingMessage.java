package com.smit.RealTimeChat.dto;

public class TypingMessage {

    private Long roomId;
    private String senderEmail;
    private String senderUsername;
    private boolean typing;

    public TypingMessage() {
    }

    public TypingMessage(Long roomId, String senderEmail, String senderUsername, boolean typing) {
        this.roomId = roomId;
        this.senderEmail = senderEmail;
        this.senderUsername = senderUsername;
        this.typing = typing;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}