package com.smit.RealTimeChat.entity;
import jakarta.persistence.*;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "profile_image", length = 500)
    private String profileImage;
    @Column(name = "bio", length = 255)
    private String bio;

    @ManyToMany(mappedBy = "users")
    private Set<ChatRoom> chatRooms = new HashSet<>();
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.profileImage == null || this.profileImage.isBlank()) {
            String nameParam = this.username != null ? this.username.replace(" ", "+") : "User";
            this.profileImage = "https://ui-avatars.com/api/?name=" + nameParam;
        }
    }
	public User() {
		super();
	}
	public User(Long id, String username, String email, String password, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", createdAt=" + createdAt + "]";
	}
	
	public Set<ChatRoom> getChatRooms() {
	    return chatRooms;
	}
	public void setChatRooms(Set<ChatRoom> chatRooms) {
	    this.chatRooms = chatRooms;
	}
    
    
}