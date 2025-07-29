package com.hongik.genieary.domain.chat.repository;

import com.hongik.genieary.domain.chat.entity.ChatRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomUuid(String roomId);

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE (cr.user1.id = :userId OR cr.user2.id = :userId) " +
            "AND cr.isActive = true " +
            "ORDER BY cr.lastMessageTime DESC")
    List<ChatRoom> findByUserIdOrderByLastMessageTimeDesc(@Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE ((cr.user1.id = :user1Id AND cr.user2.id = :user2Id) " +
            "OR (cr.user1.id = :user2Id AND cr.user2.id = :user1Id)) " +
            "AND cr.isActive = true")
    Optional<ChatRoom> findByTwoUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
