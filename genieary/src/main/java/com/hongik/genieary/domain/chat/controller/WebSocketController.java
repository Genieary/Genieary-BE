package com.hongik.genieary.domain.chat.controller;

import com.hongik.genieary.domain.chat.dto.response.WebSocketMessage;
import com.hongik.genieary.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            @Payload WebSocketMessage message,
            Principal principal
    ) {
        Long senderId = Long.parseLong(principal.getName());
        chatService.sendMessage(roomId, senderId, message.getMessage());
    }

    @MessageMapping("/chat/{roomId}/read")
    public void markAsRead(
            @DestinationVariable String roomId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        chatService.markMessagesAsRead(roomId, userId);
    }
}

