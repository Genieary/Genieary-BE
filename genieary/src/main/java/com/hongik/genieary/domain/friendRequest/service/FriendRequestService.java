package com.hongik.genieary.domain.friendRequest.service;

import com.hongik.genieary.domain.friendRequest.dto.FriendRequestResponseDto;
import com.hongik.genieary.domain.user.entity.User;

import java.util.List;

public interface FriendRequestService {
    void sendRequest(User requester, Long receiverId);
    void acceptRequest(User receiver, Long requestId);
    void rejectRequest(User receiver, Long requestId);
    List<FriendRequestResponseDto.FriendRequestResultDto> getReceivedRequests(User receiver);
}
