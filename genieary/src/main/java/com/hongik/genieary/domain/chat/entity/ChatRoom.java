package com.hongik.genieary.domain.chat.entity;

import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_uuid", unique = true, nullable = false)
    private String roomUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private User user2;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // 마지막 메시지 업데이트
    public void updateLastMessage(String message, LocalDateTime messageTime) {
        this.lastMessage = message;
        this.lastMessageTime = messageTime;
    }

    // 채팅방 비활성화
    public void deactivate() {
        this.isActive = false;
    }

    // 상대방 조회
    public User getOtherUser(Long userId) {
        return user1.getId().equals(userId) ? user2 : user1;
    }
}
