package com.hongik.genieary.domain.friendRequest.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.friendRequest.converter.FriendRequestConverter;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.friendRequest.repository.FriendRequestRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Override
    public void sendRequest(User requester, Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_USER_NOT_FOUND));

        if (requester.getId().equals(receiver.getId())) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_SELF);
        }

        friendRequestRepository.findByRequesterAndReceiver(requester, receiver)
                .ifPresent(r -> {
                    throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_EXISTS);
                });

        FriendRequest friendRequest = FriendRequestConverter.toEntity(requester, receiver);
        friendRequestRepository.save(friendRequest);
    }
}
