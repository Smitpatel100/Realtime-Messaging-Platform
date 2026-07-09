package com.smit.RealTimeChat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AddMemberRequest {

    @NotBlank(message = "Member email is required")
    @Email(message = "Must be a valid email")
    private String memberEmail;

    public AddMemberRequest() {
    }

    public AddMemberRequest(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
}