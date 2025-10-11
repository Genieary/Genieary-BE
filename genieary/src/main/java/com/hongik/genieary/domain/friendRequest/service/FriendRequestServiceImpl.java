package com.hongik.genieary.domain.friendRequest.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.enums.ImageType;
import com.hongik.genieary.domain.friend.converter.FriendConverter;
import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.friend.repository.FriendRepository;
import com.hongik.genieary.domain.friendRequest.converter.FriendRequestConverter;
import com.hongik.genieary.domain.friendRequest.dto.FriendRequestResponseDto;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.friendRequest.repository.FriendRequestRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import com.hongik.genieary.s3.S3Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final S3Service s3Service;

    @Override
    public void sendRequest(User requester, Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_USER_NOT_FOUND));

        if (requester.getId().equals(receiver.getId())) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_SELF);
        }

        boolean alreadyFriends =
                friendRepository.existsByUserAndFriend(requester, receiver) ||
                        friendRepository.existsByUserAndFriend(receiver, requester);

        if (alreadyFriends) {
            throw new GeneralException(ErrorStatus.FRIEND_ALREADY_EXISTS);
        }

        boolean alreadyRequested =
                friendRequestRepository.existsByRequesterAndReceiver(requester, receiver) ||
                        friendRequestRepository.existsByRequesterAndReceiver(receiver, requester);

        if (alreadyRequested) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        FriendRequest friendRequest = FriendRequestConverter.toEntity(requester, receiver);
        friendRequestRepository.save(friendRequest);
    }

    @Transactional
    @Override
    public void acceptRequest(User receiver, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));

        if (!request.getReceiver().getId().equals(receiver.getId())) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ACCESS_FORBIDDEN);
        }

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_HANDLED);
        }

        boolean alreadyFriends = friendRepository.existsByUserAndFriend(request.getRequester(), request.getReceiver()) ||
                friendRepository.existsByUserAndFriend(request.getReceiver(), request.getRequester());

        if (alreadyFriends) {
            throw new GeneralException(ErrorStatus.FRIEND_ALREADY_EXISTS);
        }

        request.accept();
        friendRequestRepository.save(request);

        Friend friend1 = FriendConverter.toEntity(request.getRequester(), request.getReceiver());
        Friend friend2 = FriendConverter.toEntity(request.getReceiver(), request.getRequester());

        friendRepository.save(friend1);
        friendRepository.save(friend2);
    }

    @Transactional
    @Override
    public void rejectRequest(User receiver, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));

        if (!request.getReceiver().getId().equals(receiver.getId())) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ACCESS_FORBIDDEN);
        }

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_HANDLED);
        }

        friendRequestRepository.delete(request);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FriendRequestResponseDto.FriendRequestResultDto> getReceivedRequests(User receiver) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverAndStatus(receiver, FriendStatus.REQUESTED);

        Map<Long, String> userIdToUrlMap = requests.stream()
                .map(FriendRequest::getRequester)
                .distinct()
                .collect(Collectors.toMap(
                        User::getId,
                        requester -> {
                            String key = requester.getImageFileName();
                            return key != null ? s3Service.generatePresignedDownloadUrl(key, ImageType.PROFILE) : "";
                        },
                        (v1, v2) -> v1
                ));

        return FriendRequestConverter.toResponseDtoList(requests, userIdToUrlMap);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FriendRequestResponseDto.SentFriendRequestResultDto> getSentRequests(User requester) {
        List<FriendRequest> requests =
                friendRequestRepository.findByRequesterAndStatus(requester, FriendStatus.REQUESTED);

        Map<Long, String> userIdToUrlMap = requests.stream()
                .map(FriendRequest::getReceiver)
                .distinct()
                .collect(Collectors.toMap(
                        User::getId,
                        r -> {
                            String key = r.getImageFileName();
                            return key != null ? s3Service.generatePresignedDownloadUrl(key, ImageType.PROFILE) : "";
                        },
                        (v1, v2) -> v1
                ));

        return FriendRequestConverter.toSentResponseDtoList(requests, userIdToUrlMap);
    }

    @Transactional(readOnly = true)
    @Override
    public FriendRequestResponseDto.FriendRequestBoxDto getRequestBox(User me) {

        // 1) 내가 받은 요청(REQUESTED)
        List<FriendRequest> receivedEntities =
                friendRequestRepository.findByReceiverAndStatus(me, FriendStatus.REQUESTED);

        Map<Long, String> requesterIdToUrl = receivedEntities.stream()
                .map(FriendRequest::getRequester)
                .distinct()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> {
                            String key = u.getImageFileName();
                            return key != null ? s3Service.generatePresignedDownloadUrl(key, ImageType.PROFILE) : "";
                        },
                        (a, b) -> a
                ));
        List<FriendRequestResponseDto.FriendRequestResultDto> received =
                FriendRequestConverter.toResponseDtoList(receivedEntities, requesterIdToUrl);

        // 2) 내가 보낸 요청(REQUESTED)
        List<FriendRequest> sentEntities =
                friendRequestRepository.findByRequesterAndStatus(me, FriendStatus.REQUESTED);

        Map<Long, String> receiverIdToUrl = sentEntities.stream()
                .map(FriendRequest::getReceiver)
                .distinct()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> {
                            String key = u.getImageFileName();
                            return key != null ? s3Service.generatePresignedDownloadUrl(key, ImageType.PROFILE) : "";
                        },
                        (a, b) -> a
                ));
        List<FriendRequestResponseDto.SentFriendRequestResultDto> sent =
                FriendRequestConverter.toSentResponseDtoList(sentEntities, receiverIdToUrl);

        return FriendRequestResponseDto.FriendRequestBoxDto.builder()
                .received(received)
                .sent(sent)
                .build();
    }

    @Transactional
    @Override
    public void cancelSentRequest(User requester, Long requestId) {
        long deleted = friendRequestRepository
                .deleteByRequestIdAndRequesterIdAndStatus(requestId, requester.getId(), FriendStatus.REQUESTED);

        if (deleted == 0) {
            // 내가 보낸 요청이 아니거나, 이미 수락/거절되었거나, 존재하지 않는 경우
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND);
        }
    }
}
