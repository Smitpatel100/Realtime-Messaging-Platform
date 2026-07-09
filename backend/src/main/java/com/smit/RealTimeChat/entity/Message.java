package com.smit.RealTimeChat.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    // Many messages can be sent by one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    // Many messages belong to one chat room
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    @Column(name = "delivered", nullable = false)
    private boolean delivered = false;
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    @Column(name = "seen", nullable = false)
    private boolean seen = false;
    @Column(name = "seen_at")
    private LocalDateTime seenAt;
    @Column(name = "file_name", length = 255)
    private String fileName;
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    @Column(name = "file_type", length = 100)
    private String fileType;
    public Message() {
    }
    public Message(Long id, String content, LocalDateTime createdAt, User sender, ChatRoom chatRoom) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.sender = sender;
        this.chatRoom = chatRoom;
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public User getSender() {
        return sender;
    }
    public void setSender(User sender) {
        this.sender = sender;
    }
    public ChatRoom getChatRoom() {
        return chatRoom;
    }
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    public boolean isDelivered() {
        return delivered;
    }
    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    public boolean isSeen() {
        return seen;
    }
    public void setSeen(boolean seen) {
        this.seen = seen;
    }
    public LocalDateTime getSeenAt() {
        return seenAt;
    }
    public void setSeenAt(LocalDateTime seenAt) {
        this.seenAt = seenAt;
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