package com.hongik.genieary.domain.friendRequest.repository;

import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findByRequesterAndReceiver(User requester, User receiver);
    boolean existsByRequesterAndReceiver(User requester, User receiver);
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendStatus status);
    List<FriendRequest> findByRequesterAndStatus(User requester, FriendStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from FriendRequest fr
        where (fr.requester.id = :uId and fr.receiver.id = :fId)
           or (fr.requester.id = :fId and fr.receiver.id = :uId)
    """)
    int deleteRequestsBetween(@Param("uId") Long uId, @Param("fId") Long fId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.transaction.annotation.Transactional
    long deleteByRequestIdAndRequesterIdAndStatus(Long requestId, Long requesterId, FriendStatus status);
}