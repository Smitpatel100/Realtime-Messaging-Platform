package com.smit.RealTimeChat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateGroupChatRequest {

    @NotBlank(message = "Group name is required")
    @Size(min = 2, max = 100, message = "Group name must be between 2 and 100 characters")
    private String name;

    @NotEmpty(message = "At least one member email is required")
    private List<String> memberEmails;

    public CreateGroupChatRequest() {
    }

    public CreateGroupChatRequest(String name, List<String> memberEmails) {
        this.name = name;
        this.memberEmails = memberEmails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMemberEmails() {
        return memberEmails;
    }

    public void setMemberEmails(List<String> memberEmails) {
        this.memberEmails = memberEmails;
    }
}