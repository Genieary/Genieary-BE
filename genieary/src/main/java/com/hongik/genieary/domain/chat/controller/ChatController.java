package com.hongik.genieary.domain.chat.controller;

import com.hongik.genieary.domain.chat.dto.response.ChatMessageResponse;
import com.hongik.genieary.domain.chat.dto.response.ChatRoomResponse;
import com.hongik.genieary.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성 또는 조회
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestParam Long friendId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        ChatRoomResponse chatRoom = chatService.createOrGetChatRoom(userId, friendId);
        return ResponseEntity.ok(chatRoom);
    }

    // 사용자의 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(@AuthenticationPrincipal(expression = "id") Long userId) {
        List<ChatRoomResponse> chatRooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅 메시지 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageResponse> messages = chatService.getChatMessages(roomId, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    // 메시지 읽음 처리
    @PutMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable String roomId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        chatService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }
}
