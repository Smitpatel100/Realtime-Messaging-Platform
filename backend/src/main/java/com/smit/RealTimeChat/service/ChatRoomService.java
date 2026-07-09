package com.smit.RealTimeChat.service;

import com.smit.RealTimeChat.dto.AddMemberRequest;
import com.smit.RealTimeChat.dto.ChatRoomResponse;
import com.smit.RealTimeChat.dto.CreateGroupChatRequest;
import com.smit.RealTimeChat.dto.CreatePrivateChatRequest;
import com.smit.RealTimeChat.entity.ChatRoom;
import com.smit.RealTimeChat.entity.ChatType;
import com.smit.RealTimeChat.entity.User;
import com.smit.RealTimeChat.exception.UserNotFoundException;
import com.smit.RealTimeChat.repository.ChatRoomRepository;
import com.smit.RealTimeChat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
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
        return mapToResponse(saved);
    }

    @Transactional
    public ChatRoomResponse createGroupChat(String currentUserEmail, CreateGroupChatRequest request) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserEmail));

        ChatRoom room = new ChatRoom(request.getName(), ChatType.GROUP);
        room.getUsers().add(currentUser);

        for (String email : request.getMemberEmails()) {
            User member = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
            room.getUsers().add(member);
        }

        ChatRoom saved = chatRoomRepository.save(room);
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
                memberEmails
        );
    }
}