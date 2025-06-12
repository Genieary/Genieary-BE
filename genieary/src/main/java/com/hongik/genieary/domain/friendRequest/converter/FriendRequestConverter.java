package com.hongik.genieary.domain.friendRequest.converter;

import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.user.entity.User;

public class FriendRequestConverter {

    public static FriendRequest toEntity(User requester, User receiver) {
        return FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendStatus.REQUESTED)
                .build();
    }
}
