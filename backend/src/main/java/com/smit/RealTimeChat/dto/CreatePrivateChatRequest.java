package com.smit.RealTimeChat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreatePrivateChatRequest {

    @NotBlank(message = "Target user email is required")
    @Email(message = "Must be a valid email")
    private String targetEmail;

    public CreatePrivateChatRequest() {
    }

    public CreatePrivateChatRequest(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }
}