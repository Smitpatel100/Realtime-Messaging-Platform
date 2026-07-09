package com.smit.RealTimeChat.dto;

public class PublicProfileResponse {

    private String username;
    private String email;
    private String bio;
    private String profileImage;

    public PublicProfileResponse() {
    }

    public PublicProfileResponse(String username, String email, String bio, String profileImage) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profileImage = profileImage;
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
}