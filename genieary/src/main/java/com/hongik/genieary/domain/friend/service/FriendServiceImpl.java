package com.hongik.genieary.domain.friend.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.friend.converter.FriendConverter;
import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.friend.repository.FriendRepository;
import com.hongik.genieary.domain.friendRequest.repository.FriendRequestRepository;
import com.hongik.genieary.domain.recommend.entity.Recommend;
import com.hongik.genieary.domain.recommend.repository.RecommendRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final RecommendRepository recommendRepository;

    public List<FriendResponseDto.FriendListResultDto> getFriendList(User user) {
        List<Friend> friends = friendRepository.findAllByUser(user);
        return FriendConverter.toFriendListResultDtoList(friends);
    }

    @Transactional
    @Override
    public void deleteFriend(User user, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_USER_NOT_FOUND));

        boolean exists = friendRepository.existsByUserAndFriend(user, friend) ||
                friendRepository.existsByUserAndFriend(friend, user);
        if (!exists) {
            throw new GeneralException(ErrorStatus.FRIEND_NOT_FOUND);
        }

        friendRepository.deleteByUserAndFriend(user, friend);
        friendRepository.deleteByUserAndFriend(friend, user);

        friendRequestRepository.findByRequesterAndReceiver(user, friend)
                .ifPresent(friendRequestRepository::delete);

        friendRequestRepository.findByRequesterAndReceiver(friend, user)
                .ifPresent(friendRequestRepository::delete);
    }

    @Transactional(readOnly = true)
    @Override
    public FriendResponseDto.FriendProfileDto getFriendProfile(User requester, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_NOT_FOUND));

        boolean isFriend = friendRepository.existsByUserAndFriend(requester, friend);
        if (!isFriend) {
            throw new GeneralException(ErrorStatus.FRIEND_NOT_FOUND);
        }

        List<Recommend> likedGifts = recommendRepository.findByUserAndIsLikedTrue(friend);
        return FriendConverter.toFriendProfileDto(friend, likedGifts);
    }
}