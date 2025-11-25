package com.hongik.genieary.domain.chat.controller;

import com.hongik.genieary.domain.chat.dto.response.ChatMessageResponse;
import com.hongik.genieary.domain.chat.dto.response.ChatRoomResponse;
import com.hongik.genieary.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat API", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성 또는 조회
    @PostMapping("/rooms")
    @Operation(summary = "채팅방 생성", description = "두 사용자 간의 채팅방을 생성하거나 기존 채팅방을 조회합니다. (friendId는 userId)")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestParam Long friendId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        ChatRoomResponse chatRoom = chatService.createOrGetChatRoom(userId, friendId);
        return ResponseEntity.ok(chatRoom);
    }

    //채팅방 단일 조회
    @GetMapping("/rooms/{roomUuid}")
    @Operation(summary = "채팅방 단일 조회", description = "roomUuid 기반으로 채팅방 정보를 조회합니다.")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @PathVariable String roomUuid,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        ChatRoomResponse chatRoom = chatService.getChatRoom(roomUuid, userId);
        return ResponseEntity.ok(chatRoom);
    }

    // 사용자의 채팅방 목록 조회
    @GetMapping("/rooms")
    @Operation(summary = "채팅방 목록 조회", description = "현재 사용자가 참여하고 있는 모든 채팅방 목록을 조회합니다.")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(@AuthenticationPrincipal(expression = "id") Long userId) {
        List<ChatRoomResponse> chatRooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅 메시지 조회
    @GetMapping("/rooms/{roomUuid}/messages")
    @Operation(summary = "채팅 메시지 조회", description = "특정 채팅방의 메시지 목록을 페이징하여 조회합니다.")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable String roomUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageResponse> messages = chatService.getChatMessages(roomUuid, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    // 메시지 읽음 처리
    @PutMapping("/rooms/{roomUuid}/read")
    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방의 모든 안읽은 메시지를 읽음 처리합니다.")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable String roomUuid,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        chatService.markMessagesAsRead(roomUuid, userId);
        return ResponseEntity.ok().build();
    }
}
