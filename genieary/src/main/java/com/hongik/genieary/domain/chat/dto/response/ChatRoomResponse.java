package com.hongik.genieary.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomResponse {
    private Long id;
    private String roomUuid;
    private UserResponse otherUser;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
    private Boolean isActive;

    @Getter
    @Builder
    public static class UserResponse {
        private Long id;
        private String nickname;
        private String imageFileName;
    }
}