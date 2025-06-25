package com.hongik.genieary.domain.friendRequest.repository;

import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findByRequesterAndReceiver(User requester, User receiver);
    boolean existsByRequesterAndReceiver(User requester, User receiver);
    List<FriendRequest> findByReceiver(User receiver);
}