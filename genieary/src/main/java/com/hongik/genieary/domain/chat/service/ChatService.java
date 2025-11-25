package com.hongik.genieary.domain.chat.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.chat.converter.ChatConverter;
import com.hongik.genieary.domain.chat.dto.response.ChatMessageResponse;
import com.hongik.genieary.domain.chat.dto.response.ChatRoomResponse;
import com.hongik.genieary.domain.chat.dto.response.WebSocketMessage;
import com.hongik.genieary.domain.chat.entity.ChatMessage;
import com.hongik.genieary.domain.chat.entity.ChatRoom;
import com.hongik.genieary.domain.chat.repository.ChatMessageRepository;
import com.hongik.genieary.domain.chat.repository.ChatRoomRepository;
import com.hongik.genieary.domain.enums.ImageType;
import com.hongik.genieary.domain.friend.repository.FriendRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import com.hongik.genieary.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatConverter chatConverter;
    private final S3Service s3Service;

    // 채팅방 생성 또는 조회
    public ChatRoomResponse createOrGetChatRoom(Long user1Id, Long user2Id) {

        if (user1Id.equals(user2Id)) {
            throw new GeneralException(ErrorStatus.SELF_CHAT_NOT_ALLOWED);
        }

        // 기존 채팅방 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByTwoUsers(user1Id, user2Id);

        if (existingRoom.isPresent()) {
            ChatRoom chatRoom = existingRoom.get();
            User otherUser = chatRoom.getOtherUser(user1Id);
            String profileImageUrl = generateProfileImageUrl(otherUser);
            return chatConverter.convertToChatRoomResponse(chatRoom, user1Id, profileImageUrl);
        }

        // 새 채팅방 생성
        User user1 = userRepository.findById(user1Id) //본인
                .orElseThrow(() ->  new GeneralException(ErrorStatus.USER_NOT_FOUND));
        User user2 = userRepository.findById(user2Id) //친구
                .orElseThrow(() ->  new GeneralException(ErrorStatus.USER_NOT_FOUND));

        validateFriendship(user1Id, user2Id);

        String roomUuId = UUID.randomUUID().toString();

        ChatRoom chatRoom = ChatRoom.builder()
                .roomUuid(roomUuId )
                .user1(user1)
                .user2(user2)
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);
        String profileImageUrl = generateProfileImageUrl(user2);
        return chatConverter.convertToChatRoomResponse(chatRoom, user1Id, profileImageUrl);
    }

    public ChatRoomResponse getChatRoom(String roomUuid, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        // 참여중인 채팅방인지 검증
        if (!chatRoom.isParticipant(userId)) {
            throw new GeneralException(ErrorStatus.CHAT_ROOM_ACCESS_DENIED);
        }

        User otherUser = chatRoom.getOtherUser(userId);
        String profileImageUrl = generateProfileImageUrl(otherUser);

        return chatConverter.convertToChatRoomResponse(chatRoom, userId, profileImageUrl);
    }

    // 사용자의 채팅방 목록 조회
    public List<ChatRoomResponse> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserIdOrderByLastMessageTimeDesc(userId);
        return chatRooms.stream()
                .map(chatRoom -> {
                    User otherUser = chatRoom.getOtherUser(userId);
                    String profileImageUrl = generateProfileImageUrl(otherUser);
                    return chatConverter.convertToChatRoomResponse(chatRoom, userId, profileImageUrl);
                })
                .toList();
    }

    // 채팅 메시지 전송
    public ChatMessageResponse sendMessage(String roomUuid, Long senderId, String message) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(message)
                .messageType(ChatMessage.MessageType.CHAT)
                .sentAt(now)
                .build();

        chatMessage = chatMessageRepository.save(chatMessage);

        // 채팅방 마지막 메시지 업데이트
        chatRoom.updateLastMessage(message, now);
        chatRoomRepository.save(chatRoom);

        ChatMessageResponse messageResponse = chatConverter.convertToChatMessageResponse(chatMessage);

        // WebSocket으로 실시간 전송
        messagingTemplate.convertAndSend("/topic/chat/" + roomUuid, messageResponse);

        return messageResponse;
    }

    // 채팅 상세 메시지 조회
    public Page<ChatMessageResponse> getChatMessages(String roomUuid, Long userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        // 사용자가 해당 채팅방에 참여하고 있는지 확인
        validateUserAccess(chatRoom, userId);

        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(
                chatRoom.getId(), pageable);

        return chatConverter.convertToChatMessageResponsePage(messages);
    }

    // 메시지 읽음 처리
    public void markMessagesAsRead(String roomUuid, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        chatMessageRepository.markMessagesAsRead(chatRoom.getId(), userId);

        // 읽음 상태 변경 알림
        WebSocketMessage readMessage = new WebSocketMessage(
                "READ", roomUuid, userId, null, LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat/" + roomUuid, readMessage);
    }

    // ------ private method -------
    private void validateUserAccess(ChatRoom chatRoom, Long userId) {
        if (!chatRoom.getUser1().getId().equals(userId) && !chatRoom.getUser2().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.CHAT_ROOM_ACCESS_DENIED);
        }
    }
    private void validateFriendship(Long user1Id, Long user2Id) {
        boolean isFriend = friendRepository.existsByUserIdAndFriendId(user1Id, user2Id);
        if (!isFriend) {
            throw new GeneralException(ErrorStatus.NOT_FRIEND_RELATIONSHIP);
        }
    }

    private String generateProfileImageUrl(User user) {
        if (user.getImageFileName() == null || user.getImageFileName().isEmpty()) {
            return null;
        }
        return s3Service.generatePresignedDownloadUrl(
                user.getImageFileName(),
                ImageType.PROFILE
        );
    }
}

