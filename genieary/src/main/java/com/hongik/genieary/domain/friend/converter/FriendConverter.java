package com.hongik.genieary.domain.friend.converter;

import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.recommend.entity.Recommend;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendConverter {

    public static FriendResponseDto.FriendListResultDto toFriendListResultDto(User friendUser, String presignedUrl) {
        return FriendResponseDto.FriendListResultDto.builder()
                .friendId(friendUser.getId())
                .nickname(friendUser.getNickname())
                .profileImage(presignedUrl)
                .build();
    }

    public static List<FriendResponseDto.FriendListResultDto> toFriendListResultDtoList(List<Friend> friends, Map<Long, String> friendIdToUrlMap) {
        return friends.stream()
                .map(friend -> {
                    User friendUser = friend.getFriend();
                    String url = friendIdToUrlMap.getOrDefault(friendUser.getId(), null);
                    return toFriendListResultDto(friendUser, url);
                })
                .collect(Collectors.toList());
    }
    public static Friend toEntity(User user, User friend) {
        return Friend.builder()
                .user(user)
                .friend(friend)
                .build();
    }

    public static FriendResponseDto.FriendProfileDto toFriendProfileDto(User friend, String presignedUrl, List<Recommend> likedGifts) {
        return FriendResponseDto.FriendProfileDto.builder()
                .friendId(friend.getId())
                .nickname(friend.getNickname())
                .email(friend.getEmail())
                .profileImage(presignedUrl)
                .giftLikes(likedGifts.stream()
                        .map(gift -> FriendResponseDto.FriendProfileDto.GiftPreviewDto.builder()
                                .giftId(gift.getRecommendId())
                                .name(gift.getContentName())
                                .imageUrl(gift.getContentImage())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static FriendResponseDto.FriendSearchResultDto toFriendSearchResultDto(User user, String presignedUrl) {
        return FriendResponseDto.FriendSearchResultDto.builder()
                .friendId(user.getId())
                .nickname(user.getNickname())
                .profileImage(presignedUrl)
                .email(user.getEmail())
                .build();
    }

    public static Page<FriendResponseDto.FriendSearchResultDto> toFriendSearchResultPage(
            Page<User> friendsPage, User requester, Map<Long, String> userIdToPresignedUrlMap, Pageable pageable) {

        List<FriendResponseDto.FriendSearchResultDto> filteredList = friendsPage.getContent().stream()
                .filter(friend -> !friend.getId().equals(requester.getId()))
                .map(friend -> {
                    String url = userIdToPresignedUrlMap.get(friend.getId());
                    return toFriendSearchResultDto(friend, url);
                })
                .toList();

        return new PageImpl<>(filteredList, pageable, filteredList.size());
    }

}
