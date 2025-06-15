package com.hongik.genieary.domain.friend.service;


import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.user.entity.User;

import java.util.List;

public interface FriendService {

    List<FriendResponseDto.FriendListResultDto> getFriendList(User user);
    void deleteFriend(User user, Long friendId);
    FriendResponseDto.FriendProfileDto getFriendProfile(User requester, Long friendId);
}
