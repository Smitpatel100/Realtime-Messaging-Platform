package com.smit.RealTimeChat.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class UpdateProfileRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @Size(max = 255, message = "Bio must be at most 255 characters")
    private String bio;

    @Size(max = 500, message = "Profile image URL must be at most 500 characters")
    private String profileImage;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String username, String bio, String profileImage) {
        this.username = username;
        this.bio = bio;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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