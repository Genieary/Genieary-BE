package com.hongik.genieary.domain.chat.converter;

import com.hongik.genieary.domain.chat.dto.response.ChatMessageResponse;
import com.hongik.genieary.domain.chat.dto.response.ChatRoomResponse;
import com.hongik.genieary.domain.chat.entity.ChatMessage;
import com.hongik.genieary.domain.chat.entity.ChatRoom;
import com.hongik.genieary.domain.chat.repository.ChatMessageRepository;
import com.hongik.genieary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatConverter {

    private final ChatMessageRepository chatMessageRepository;

    //ChatRoom -> ChatRoomResponse 변환
    public ChatRoomResponse convertToChatRoomResponse(ChatRoom chatRoom, Long currentUserId) {
        User otherUser = chatRoom.getOtherUser(currentUserId);
        Long unreadCount = chatMessageRepository.countUnreadMessages(chatRoom.getId(), currentUserId);

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .roomUuid(chatRoom.getRoomUuid())
                .otherUser(convertToUserResponse(otherUser))
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .unreadCount(unreadCount)
                .isActive(chatRoom.getIsActive())
                .build();
    }

    // ChatRoom 리스트 -> ChatRoomResponse 리스트 변환
    public List<ChatRoomResponse> convertToChatRoomResponseList(List<ChatRoom> chatRooms, Long currentUserId) {
        return chatRooms.stream()
                .map(chatRoom -> convertToChatRoomResponse(chatRoom, currentUserId))
                .collect(Collectors.toList());
    }

    //ChatMessage -> ChatMessageResponse 변환
    public ChatMessageResponse convertToChatMessageResponse(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .roomUuid(chatMessage.getChatRoom().getRoomUuid())
                .senderId(chatMessage.getSender().getId())
                .senderNickname(chatMessage.getSender().getNickname())
                .message(chatMessage.getMessage())
                .messageType(ChatMessageResponse.MessageType.valueOf(chatMessage.getMessageType().name()))
                .isRead(chatMessage.getIsRead())
                .sentAt(chatMessage.getSentAt())
                .build();
    }

    //ChatMessage Page -> ChatMessageResponse Page 변환
    public Page<ChatMessageResponse> convertToChatMessageResponsePage(Page<ChatMessage> messages) {
        return messages.map(this::convertToChatMessageResponse);
    }

    //User -> UserResponse 변환 (ChatRoom용)
    private ChatRoomResponse.UserResponse convertToUserResponse(User user) {
        return ChatRoomResponse.UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .imageFileName(user.getImageFileName())
                .build();
    }

}

