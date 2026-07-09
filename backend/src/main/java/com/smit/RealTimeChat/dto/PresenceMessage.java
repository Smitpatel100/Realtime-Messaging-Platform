package com.smit.RealTimeChat.dto;

public class PresenceMessage {

    private String email;
    private String username;
    private boolean online;

    public PresenceMessage() {
    }

    public PresenceMessage(String email, String username, boolean online) {
        this.email = email;
        this.username = username;
        this.online = online;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}