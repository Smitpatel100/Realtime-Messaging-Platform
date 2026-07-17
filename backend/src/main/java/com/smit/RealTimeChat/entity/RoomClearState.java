package com.smit.RealTimeChat.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_clear_states", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "room_id"}))
public class RoomClearState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @Column(name = "cleared_at", nullable = false)
    private LocalDateTime clearedAt;

    public RoomClearState() {
    }

    public RoomClearState(User user, ChatRoom room, LocalDateTime clearedAt) {
        this.user = user;
        this.room = room;
        this.clearedAt = clearedAt;
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

    public ChatRoom getRoom() {
        return room;
    }

    public void setRoom(ChatRoom room) {
        this.room = room;
    }

    public LocalDateTime getClearedAt() {
        return clearedAt;
    }

    public void setClearedAt(LocalDateTime clearedAt) {
        this.clearedAt = clearedAt;
    }
}