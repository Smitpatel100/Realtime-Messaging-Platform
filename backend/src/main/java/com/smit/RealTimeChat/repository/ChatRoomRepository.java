package com.smit.RealTimeChat.repository;

import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.ChatType;
import com.smit.RealTimeChat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUsersContaining(User user);

    @Query("SELECT r FROM ChatRoom r WHERE r.type = :type AND :user1 MEMBER OF r.users AND :user2 MEMBER OF r.users")
    Optional<ChatRoom> findPrivateRoomBetweenUsers(
            @Param("type") ChatType type,
            @Param("user1") User user1,
            @Param("user2") User user2
    );
}