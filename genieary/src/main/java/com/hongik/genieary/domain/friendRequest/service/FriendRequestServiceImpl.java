package com.hongik.genieary.domain.friendRequest.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.friend.repository.FriendRepository;
import com.hongik.genieary.domain.friendRequest.converter.FriendRequestConverter;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.friendRequest.repository.FriendRequestRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Override
    public void sendRequest(User requester, Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_USER_NOT_FOUND));

        if (requester.getId().equals(receiver.getId())) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_SELF);
        }

        boolean alreadyFriends =
                friendRepository.existsByUserAndFriend(requester, receiver) ||
                        friendRepository.existsByUserAndFriend(receiver, requester);

        if (alreadyFriends) {
            throw new GeneralException(ErrorStatus.FRIEND_ALREADY_EXISTS);
        }

        boolean alreadyRequested =
                friendRequestRepository.existsByRequesterAndReceiver(requester, receiver) ||
                        friendRequestRepository.existsByRequesterAndReceiver(receiver, requester);

        if (alreadyRequested) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        FriendRequest friendRequest = FriendRequestConverter.toEntity(requester, receiver);
        friendRequestRepository.save(friendRequest);
    }

    @Transactional
    @Override
    public void acceptRequest(User receiver, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));

        if (!request.getReceiver().getId().equals(receiver.getId())) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ACCESS_FORBIDDEN);
        }

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_HANDLED);
        }

        boolean alreadyFriends = friendRepository.existsByUserAndFriend(request.getRequester(), request.getReceiver()) ||
                friendRepository.existsByUserAndFriend(request.getReceiver(), request.getRequester());

        if (alreadyFriends) {
            throw new GeneralException(ErrorStatus.FRIEND_ALREADY_EXISTS);
        }

        request.accept();
        friendRequestRepository.save(request);

        Friend friend1 = Friend.builder()
                .user(request.getRequester())
                .friend(request.getReceiver())
                .build();

        Friend friend2 = Friend.builder()
                .user(request.getReceiver())
                .friend(request.getRequester())
                .build();

        friendRepository.save(friend1);
        friendRepository.save(friend2);
    }

    @Transactional
    @Override
    public void rejectRequest(User receiver, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));

        if (!request.getReceiver().getId().equals(receiver.getId())) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ACCESS_FORBIDDEN);
        }

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_HANDLED);
        }

        request.reject();
        friendRequestRepository.save(request);
    }
}
