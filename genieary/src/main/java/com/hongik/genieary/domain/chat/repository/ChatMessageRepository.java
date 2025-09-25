package com.hongik.genieary.domain.chat.repository;

import com.hongik.genieary.domain.chat.entity.ChatMessage;
import com.hongik.genieary.domain.chat.entity.ChatRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id = :chatRoomId " +
            "ORDER BY cm.sentAt DESC")
    Page<ChatMessage> findByChatRoomIdOrderBySentAtDesc(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id = :chatRoomId " +
            "AND cm.sender.id != :userId " +
            "AND cm.isRead = false")
    Long countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true " +
            "WHERE cm.chatRoom.id = :chatRoomId " +
            "AND cm.sender.id != :userId " +
            "AND cm.isRead = false")
    void markMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
}
