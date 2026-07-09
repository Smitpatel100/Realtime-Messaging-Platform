package com.smit.RealTimeChat.dto;

public class SendMessageRequest {

    private String content;
    private String fileName;
    private String fileUrl;
    private String fileType;

    public SendMessageRequest() {
    }

    public SendMessageRequest(String content) {
        this.content = content;
    }

    public SendMessageRequest(String content, String fileName, String fileUrl, String fileType) {
        this.content = content;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}