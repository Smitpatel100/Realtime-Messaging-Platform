package com.smit.RealTimeChat.dto;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profileImage;
    private LocalDateTime createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String username, String email, String bio,
                                String profileImage, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}