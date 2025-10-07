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

    //채팅 전송
    @MessageMapping("/chat/{roomUuid}")
    public void sendMessage(
            @DestinationVariable String roomUuid,
            @Payload WebSocketMessage message,
            Principal principal
    ) {
//        Long senderId = message.getSenderId();
        Long senderId = Long.parseLong(principal.getName());
        chatService.sendMessage(roomUuid, senderId, message.getMessage());
    }

    //채팅 읽음 처리
    @MessageMapping("/chat/{roomUuid}/read")
    public void markAsRead(
            @DestinationVariable String roomUuid,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        chatService.markMessagesAsRead(roomUuid, userId);
    }
}

