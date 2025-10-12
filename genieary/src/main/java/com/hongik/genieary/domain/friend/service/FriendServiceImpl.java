package com.hongik.genieary.domain.friend.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.enums.ImageType;
import com.hongik.genieary.domain.friend.converter.FriendConverter;
import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.friend.repository.FriendRecommendationRepository;
import com.hongik.genieary.domain.friend.repository.projection.FriendRecommendationRow;
import com.hongik.genieary.domain.friend.repository.FriendRepository;
import com.hongik.genieary.domain.friendRequest.repository.FriendRequestRepository;
import com.hongik.genieary.domain.recommend.entity.Recommend;
import com.hongik.genieary.domain.recommend.repository.RecommendRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import com.hongik.genieary.s3.S3Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final RecommendRepository recommendRepository;
    private final S3Service s3Service;
    private final FriendRecommendationRepository friendRecommendationRepository;


    @Override
    public List<FriendResponseDto.FriendListResultDto> getFriendList(User user) {
        List<Friend> friends = friendRepository.findAllByUser(user);

        // Presigned URL 발급
        Map<Long, String> friendIdToUrlMap = friends.stream()
                .map(Friend::getFriend)
                .filter(friendUser -> friendUser.getId() != null && friendUser.getImageFileName() != null)
                .collect(Collectors.toMap(
                        User::getId,
                        friendUser -> s3Service.generatePresignedDownloadUrl(friendUser.getImageFileName(), ImageType.PROFILE)
                ));


        return FriendConverter.toFriendListResultDtoList(friends, friendIdToUrlMap);
    }

    @Transactional
    @Override
    public void deleteFriend(User user, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_USER_NOT_FOUND));

        boolean exists = friendRepository.existsByUserAndFriend(user, friend)
                || friendRepository.existsByUserAndFriend(friend, user);
        if (!exists) {
            throw new GeneralException(ErrorStatus.FRIEND_NOT_FOUND);
        }

        // 1) 친구요청(보낸/받은) 흔적 일괄 삭제
        friendRequestRepository.deleteRequestsBetween(user.getId(), friend.getId());

        // 2) 친구관계 양방향 한 번에 삭제
        friendRepository.deletePair(user.getId(), friend.getId());
    }

    @Override
    public FriendResponseDto.FriendProfileDto getFriendProfile(User requester, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_NOT_FOUND));

        boolean isFriend = friendRepository.existsByUserAndFriend(requester, friend);
        if (!isFriend) {
            throw new GeneralException(ErrorStatus.FRIEND_NOT_FOUND);
        }

        List<Recommend> likedGifts = recommendRepository.findByUserAndIsLikedTrue(friend);

        String presignedUrl = null;
        if (friend.getImageFileName() != null) {
            presignedUrl = s3Service.generatePresignedDownloadUrl(friend.getImageFileName(), ImageType.PROFILE);
        }

        return FriendConverter.toFriendProfileDto(friend, presignedUrl, likedGifts);
    }

    @Override
    public Page<FriendResponseDto.FriendSearchResultDto> searchFriends(User requester, String nickname, Pageable pageable) {

        if (nickname == null || nickname.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_SEARCH_KEYWORD);
        }

        Page<User> friendsPage = userRepository.findByNicknameContaining(nickname, pageable);

        Map<Long, String> userIdToPresignedUrlMap = friendsPage.getContent().stream()
                .filter(friend -> !friend.getId().equals(requester.getId()))
                .collect(Collectors.toMap(
                        User::getId,
                        user -> {
                            String key = user.getImageFileName();
                            return key != null ? s3Service.generatePresignedDownloadUrl(key, ImageType.PROFILE) : "";
                        },
                        (v1, v2) -> v1
                ));

        return FriendConverter.toFriendSearchResultPage(friendsPage, requester, userIdToPresignedUrlMap, pageable);
    }

    @Override
    public List<FriendResponseDto.RecommendItem> getFriendRecommendationsRandom(User me, int maxCount) {
        int minOverlap = 2;                           // 정책 고정
        int cap = Math.min(Math.max(maxCount, 1), 5); // 1~5 캡핑

        List<FriendRecommendationRow> rows =
                friendRecommendationRepository.findCandidates(me.getId(), minOverlap, cap);

        List<Long> ids = rows.stream()
                .map(FriendRecommendationRow::getUserId)
                .toList();

        Map<Long, User> usersById = userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, String> urlMap = usersById.values().stream()
                .filter(u -> u.getImageFileName() != null)
                .collect(Collectors.toMap(
                        User::getId,
                        u -> s3Service.generatePresignedDownloadUrl(u.getImageFileName(), ImageType.PROFILE)
                ));

        return rows.stream()
                .map(r -> FriendConverter.toRecommendItem(
                        usersById.get(r.getUserId()),
                        urlMap.get(r.getUserId()),
                        r.getTotalOverlap(),
                        r.getPersonalityOverlap(),
                        r.getInterestOverlap()
                ))
                .toList();
    }

    @Override
    public Page<FriendResponseDto.FriendGiftDto> getFriendPublicLikedGifts(Long meId, Long friendId, int page, int size) {

        if (!friendRepository.existsMutual(meId, friendId)) {
            throw new GeneralException(ErrorStatus.FRIEND_NOT_FOUND); // 403/404 등 사내 규칙에 맞춰 처리
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Recommend> pageData = recommendRepository
             .findByUser_IdAndIsLikedTrueAndIsPublicTrue(friendId, pageable);
        Page<FriendResponseDto.FriendGiftDto> result = pageData.map(r -> new FriendResponseDto.FriendGiftDto(
             r.getRecommendId(), r.getContentName(), r.getContentImage(),
             r.getContentDescription(), r.getUpdatedAt()
        ));

        return result;
    }
}