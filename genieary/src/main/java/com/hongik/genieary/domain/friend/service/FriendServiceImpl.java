package com.hongik.genieary.domain.friend.service;

import com.hongik.genieary.domain.friend.converter.FriendConverter;
import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.friend.repository.FriendRepository;
import com.hongik.genieary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    public List<FriendResponseDto.FriendListResultDto> getFriendList(User user) {
        List<Friend> friends = friendRepository.findAllByUserId(user);
        return FriendConverter.toFriendListResultDtoList(friends);
    }
}