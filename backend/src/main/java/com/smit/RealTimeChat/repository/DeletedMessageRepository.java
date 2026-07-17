package com.smit.RealTimeChat.repository;
import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.DeletedMessage;
import com.smit.RealTimeChat.entity.Message;
import com.smit.RealTimeChat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DeletedMessageRepository extends JpaRepository<DeletedMessage, Long> {

    Optional<DeletedMessage> findByUserAndMessage(User user, Message message);

    @Query("SELECT dm.message.id FROM DeletedMessage dm WHERE dm.user = :user AND dm.message.chatRoom = :room")
    List<Long> findMessageIdsByUserAndChatRoom(@Param("user") User user, @Param("room") ChatRoom room);
}