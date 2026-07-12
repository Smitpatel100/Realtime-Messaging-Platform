package com.smit.RealTimeChat.repository;
import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
    List<Message> findByChatRoomAndSeenFalseAndSenderEmailNot(ChatRoom chatRoom, String senderEmail);
    List<Message> findByChatRoomAndDeliveredFalseAndSenderEmailNot(ChatRoom chatRoom, String senderEmail);
    List<Message> findByChatRoomAndContentContainingIgnoreCaseOrderByCreatedAtAsc(ChatRoom chatRoom, String keyword);
    long countByChatRoomAndSeenFalseAndSenderEmailNot(ChatRoom chatRoom, String senderEmail);
    long deleteByChatRoom(ChatRoom chatRoom);
}