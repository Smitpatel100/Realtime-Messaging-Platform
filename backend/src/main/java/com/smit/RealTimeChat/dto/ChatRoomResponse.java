package com.smit.RealTimeChat.dto;

import com.smit.RealTimeChat.entity.ChatType;
import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponse {

    private Long id;
    private String name;
    private ChatType type;
    private LocalDateTime createdAt;
    private List<String> memberEmails;

    public ChatRoomResponse() {
    }

    public ChatRoomResponse(Long id, String name, ChatType type, LocalDateTime createdAt, List<String> memberEmails) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.memberEmails = memberEmails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getMemberEmails() {
        return memberEmails;
    }

    public void setMemberEmails(List<String> memberEmails) {
        this.memberEmails = memberEmails;
    }
}