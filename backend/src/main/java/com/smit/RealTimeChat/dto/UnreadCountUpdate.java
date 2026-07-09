package com.smit.RealTimeChat.dto;

public class UnreadCountUpdate {

    private Long roomId;
    private String roomName;
    private String senderEmail;
    private String senderUsername;
    private String preview;

    public UnreadCountUpdate() {
    }

    public UnreadCountUpdate(Long roomId, String roomName, String senderEmail, String senderUsername, String preview) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.senderEmail = senderEmail;
        this.senderUsername = senderUsername;
        this.preview = preview;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
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

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }
}