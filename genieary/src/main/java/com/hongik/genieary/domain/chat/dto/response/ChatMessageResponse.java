package com.hongik.genieary.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {
    private Long id;
    private String roomId;
    private Long senderId;
    private String senderNickname;
    private String message;
    private MessageType messageType;
    private Boolean isRead;
    private LocalDateTime sentAt;

    public enum MessageType {
        CHAT, JOIN, LEAVE, FILE, IMAGE
    }
}
