package com.smit.RealTimeChat.repository;
import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.RoomClearState;
import com.smit.RealTimeChat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomClearStateRepository extends JpaRepository<RoomClearState, Long> {
    Optional<RoomClearState> findByUserAndRoom(User user, ChatRoom room);
}