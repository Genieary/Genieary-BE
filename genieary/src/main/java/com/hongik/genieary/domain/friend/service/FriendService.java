package com.hongik.genieary.domain.friend.service;


import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendService {

    List<FriendResponseDto.FriendListResultDto> getFriendList(User user);
    void deleteFriend(User user, Long friendId);
    FriendResponseDto.FriendProfileDto getFriendProfile(User requester, Long friendId);
    Page<FriendResponseDto.FriendSearchResultDto> searchFriends(User requester, String nickname, Pageable pageable);
    List<FriendResponseDto.RecommendItem> getFriendRecommendationsRandom(User me, int maxCount);
    Page<FriendResponseDto.FriendGiftDto> getFriendPublicLikedGifts(Long meId, Long friendId, int page, int size);

    }
