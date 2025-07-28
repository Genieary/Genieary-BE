package com.hongik.genieary.domain.chat.service;

import com.hongik.genieary.domain.chat.converter.ChatConverter;
import com.hongik.genieary.domain.chat.dto.response.ChatMessageResponse;
import com.hongik.genieary.domain.chat.dto.response.ChatRoomResponse;
import com.hongik.genieary.domain.chat.dto.response.WebSocketMessage;
import com.hongik.genieary.domain.chat.entity.ChatMessage;
import com.hongik.genieary.domain.chat.entity.ChatRoom;
import com.hongik.genieary.domain.chat.repository.ChatMessageRepository;
import com.hongik.genieary.domain.chat.repository.ChatRoomRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatConverter chatConverter;

    // 채팅방 생성 또는 조회
    public ChatRoomResponse createOrGetChatRoom(Long user1Id, Long user2Id) {
        // 기존 채팅방 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByTwoUsers(user1Id, user2Id);

        if (existingRoom.isPresent()) {
            ChatRoom chatRoom = existingRoom.get();
            return chatConverter.convertToChatRoomResponse(chatRoom, user1Id);
        }

        // 새 채팅방 생성
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        String roomId = UUID.randomUUID().toString();

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .user1(user1)
                .user2(user2)
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);
        return chatConverter.convertToChatRoomResponse(chatRoom, user1Id);
    }

    // 사용자의 채팅방 목록 조회
    public List<ChatRoomResponse> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserIdOrderByLastMessageTimeDesc(userId);
        return chatConverter.convertToChatRoomResponseList(chatRooms, userId);
    }

    // 채팅 메시지 전송
    public ChatMessageResponse sendMessage(String roomId, Long senderId, String message) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

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
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, messageResponse);

        return messageResponse;
    }

    // 채팅 상세 메시지 조회
    public Page<ChatMessageResponse> getChatMessages(String roomId, Long userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        // 사용자가 해당 채팅방에 참여하고 있는지 확인
        validateUserAccess(chatRoom, userId);

        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(
                chatRoom.getId(), pageable);

        return chatConverter.convertToChatMessageResponsePage(messages);
    }

    // 메시지 읽음 처리
    public void markMessagesAsRead(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        chatMessageRepository.markMessagesAsRead(chatRoom.getId(), userId);

        // 읽음 상태 변경 알림
        WebSocketMessage readMessage = new WebSocketMessage(
                "READ", roomId, userId, null, LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, readMessage);
    }

    // ------ private method -------
    private void validateUserAccess(ChatRoom chatRoom, Long userId) {
        if (!chatRoom.getUser1().getId().equals(userId) && !chatRoom.getUser2().getId().equals(userId)) {
            throw new IllegalArgumentException("채팅방에 접근할 권한이 없습니다.");
        }
    }
}

