package com.hongik.genieary.domain.friend.converter;

import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.recommend.entity.Recommend;
import com.hongik.genieary.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class FriendConverter {

    public static FriendResponseDto.FriendListResultDto toFriendListResultDto(User friendUser) {
        return FriendResponseDto.FriendListResultDto.builder()
                .friendId(friendUser.getId())
                .nickname(friendUser.getNickname())
                .profileImage(friendUser.getProfileImg())
                .build();
    }

    public static List<FriendResponseDto.FriendListResultDto> toFriendListResultDtoList(List<Friend> friends) {
        return friends.stream()
                .map(friend -> toFriendListResultDto(friend.getFriend()))
                .collect(Collectors.toList());
    }

    public static Friend toEntity(User user, User friend) {
        return Friend.builder()
                .user(user)
                .friend(friend)
                .build();
    }

    public static FriendResponseDto.FriendProfileDto toFriendProfileDto(User friend, List<Recommend> likedGifts) {
        return FriendResponseDto.FriendProfileDto.builder()
                .friendId(friend.getId())
                .nickname(friend.getNickname())
                .email(friend.getEmail())
                .profileImage(friend.getProfileImg())
                .giftLikes(likedGifts.stream()
                        .map(gift -> FriendResponseDto.FriendProfileDto.GiftPreviewDto.builder()
                                .giftId(gift.getRecommendId())
                                .name(gift.getContentName())
                                .imageUrl(gift.getContentImage())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
