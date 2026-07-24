package com.smit.RealTimeChat.service;

import com.smit.RealTimeChat.dto.AddMemberRequest;
import com.smit.RealTimeChat.dto.ChatRoomResponse;
import com.smit.RealTimeChat.dto.CreateGroupChatRequest;
import com.smit.RealTimeChat.dto.CreatePrivateChatRequest;
import com.smit.RealTimeChat.dto.RoomEvent;
import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.ChatType;
import com.smit.RealTimeChat.entity.User;
import com.smit.RealTimeChat.exception.UserNotFoundException;
import com.smit.RealTimeChat.repository.ChatRoomRepository;
import com.smit.RealTimeChat.repository.MessageRepository;
import com.smit.RealTimeChat.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoomService(
            ChatRoomRepository chatRoomRepository,
            UserRepository userRepository,
            MessageRepository messageRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public ChatRoomResponse createPrivateChat(String currentUserEmail, CreatePrivateChatRequest request) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));

        User targetUser = userRepository.findByEmail(request.getTargetEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getTargetEmail()));

        Optional<ChatRoom> existingRoom = chatRoomRepository.findPrivateRoomBetweenUsers(
                ChatType.PRIVATE, currentUser, targetUser
        );

        if (existingRoom.isPresent()) {
            return mapToResponse(existingRoom.get());
        }

        ChatRoom room = new ChatRoom(
                currentUser.getUsername() + " & " + targetUser.getUsername(),
                ChatType.PRIVATE
        );

        room.getUsers().add(currentUser);
        room.getUsers().add(targetUser);

        ChatRoom saved = chatRoomRepository.save(room);

        messagingTemplate.convertAndSend(
         "/topic/room-events",
         new RoomEvent(
                "PRIVATE_CREATED",
                saved.getId(),
                currentUserEmail
        )
       );

      return mapToResponse(saved);
    }

    @Transactional
    public ChatRoomResponse createGroupChat(String currentUserEmail, CreateGroupChatRequest request) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));

        ChatRoom room = new ChatRoom(request.getName(), ChatType.GROUP);
        room.setCreatedBy(currentUser);
        room.getUsers().add(currentUser);

        for (String email : request.getMemberEmails()) {
            User member = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
            room.getUsers().add(member);
        }

        ChatRoom saved = chatRoomRepository.save(room);

      messagingTemplate.convertAndSend(
         "/topic/room-events",
         new RoomEvent(
                "GROUP_CREATED",
                saved.getId(),
                currentUserEmail
         )
     );

      return mapToResponse(saved);
    }

    @Transactional
    public ChatRoomResponse addMember(Long roomId, AddMemberRequest request) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));

        if (room.getType() != ChatType.GROUP) {
            throw new RuntimeException("Members can only be added to GROUP chat rooms");
        }

        User newMember = userRepository.findByEmail(request.getMemberEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getMemberEmail()));

        room.getUsers().add(newMember);

        ChatRoom saved = chatRoomRepository.save(room);
        return mapToResponse(saved);
    }

    public List<ChatRoomResponse> getUserChatRooms(String currentUserEmail) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));

        List<ChatRoom> rooms = chatRoomRepository.findByUsersContaining(currentUser);

        return rooms.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePrivateChat(Long roomId, String currentUserEmail) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));

        if (room.getType() != ChatType.PRIVATE) {
            throw new RuntimeException("This endpoint only deletes PRIVATE chat rooms");
        }

        boolean isParticipant = room.getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUserEmail));
        if (!isParticipant) {
            throw new RuntimeException("Only participants can delete this chat");
        }

        Long deletedRoomId = room.getId();
       messageRepository.deleteByChatRoom(room);
       room.getUsers().clear();
       chatRoomRepository.save(room);
       chatRoomRepository.delete(room);

        messagingTemplate.convertAndSend("/topic/room-events",
                new RoomEvent("PRIVATE_DELETED", deletedRoomId, currentUserEmail));
    }

    @Transactional
    public void leaveGroup(Long roomId, String currentUserEmail) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));

        if (room.getType() != ChatType.GROUP) {
            throw new RuntimeException("This endpoint only applies to GROUP chat rooms");
        }

        User leavingUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));

        boolean isMember = room.getUsers().stream()
                .anyMatch(user -> user.getEmail().equals(currentUserEmail));
        if (!isMember) {
            throw new RuntimeException("You are not a member of this group");
        }

        room.getUsers().remove(leavingUser);

        if (room.getUsers().isEmpty()) {
            Long deletedRoomId = room.getId();
            messageRepository.deleteByChatRoom(room);
            chatRoomRepository.delete(room);
            messagingTemplate.convertAndSend("/topic/room-events",
                    new RoomEvent("GROUP_DELETED", deletedRoomId, currentUserEmail));
            return;
        }

        // If the creator left, hand off admin rights to no one automatically —
        // the group simply becomes creator-less; deletion then requires a new
        // explicit assignment flow (not in scope here).
        chatRoomRepository.save(room);

        messagingTemplate.convertAndSend("/topic/room-events",
                new RoomEvent("MEMBER_LEFT", roomId, currentUserEmail));
    }

    @Transactional
    public void deleteGroup(Long roomId, String currentUserEmail) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + roomId));

        if (room.getType() != ChatType.GROUP) {
            throw new RuntimeException("This endpoint only deletes GROUP chat rooms");
        }

        if (room.getCreatedBy() == null || !room.getCreatedBy().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Only the group creator can delete this group");
        }

        Long deletedRoomId = room.getId();
        messageRepository.deleteByChatRoom(room);
        room.getUsers().clear();
        chatRoomRepository.delete(room);

        messagingTemplate.convertAndSend("/topic/room-events",
                new RoomEvent("GROUP_DELETED", deletedRoomId, currentUserEmail));
    }

    private ChatRoomResponse mapToResponse(ChatRoom room) {

        List<String> memberEmails = room.getUsers()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        return new ChatRoomResponse(
                room.getId(),
                room.getName(),
                room.getType(),
                room.getCreatedAt(),
                memberEmails,
                room.getCreatedBy() != null ? room.getCreatedBy().getEmail() : null
        );
    }
}