package com.hongik.genieary.domain.friendRequest.service;

import com.hongik.genieary.domain.user.entity.User;

public interface FriendRequestService {
    void sendRequest(User requester, Long receiverId);
    void acceptRequest(User receiver, Long requestId);
    void rejectRequest(User receiver, Long requestId);
}
