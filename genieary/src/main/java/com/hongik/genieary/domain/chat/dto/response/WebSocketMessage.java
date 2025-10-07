package com.hongik.genieary.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type; // CHAT, JOIN, LEAVE, READ
    private String roomId;
    private Long senderId;
    private String message;
    private LocalDateTime timestamp;
}
