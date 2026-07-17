package com.smit.RealTimeChat.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deleted_messages", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "message_id"}))
public class DeletedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "deleted_at", nullable = false, updatable = false)
    private LocalDateTime deletedAt;

    public DeletedMessage() {
    }

    public DeletedMessage(User user, Message message) {
        this.user = user;
        this.message = message;
    }

    @PrePersist
    protected void onCreate() {
        this.deletedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}