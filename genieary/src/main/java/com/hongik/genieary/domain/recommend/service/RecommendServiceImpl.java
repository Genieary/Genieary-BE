package com.hongik.genieary.domain.recommend.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.ai.service.OpenAiService;
import com.hongik.genieary.domain.friend.repository.FriendRepository;
import com.hongik.genieary.domain.recommend.Category;
import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;
import com.hongik.genieary.domain.recommend.entity.Recommend;
import com.hongik.genieary.domain.recommend.repository.RecommendRepository;
import com.hongik.genieary.domain.user.entity.Interest;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.entity.UserInterest;
import com.hongik.genieary.domain.user.repository.InterestRepository;
import com.hongik.genieary.domain.user.repository.UserInterestRepository;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class RecommendServiceImpl implements RecommendService{

    final private UserRepository userRepository;
    final private UserInterestRepository userInterestRepository;
    final private OpenAiService openAiService;
    final private RecommendRepository recommendRepository;
    final private InterestRepository interestRepository;
    final private GoogleSearchService googleSearchService;
    final private FriendRepository friendRepository;


    @Override
    @Transactional
    public List<RecommendResponseDto.GiftRecommendResultDto> getRecommendations(Long userId, Category category, String event){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 유저 정보 추출
        UserSummary summary = buildUserSummary(user);

        String eventText = (event != null && !event.isBlank())
                ? "The event is " + event + "."
                : "There is no specific event.";

        List<RecommendResponseDto.GiftRecommendResultDto> recommendations = openAiService.getRecommendations(summary.personalities(), summary.interests(), category, eventText);

        // 이미지 검색 및 map변환
        Map<String, String> imageMap = fetchImageMap(recommendations);

        // DB 저장
        List<Recommend> entities = recommendations.stream()
                .map(dto -> Recommend.builder()
                        .user(user)
                        .contentName(dto.getName())
                        .contentDescription(dto.getDescription())
                        .contentImage(imageMap.get(dto.getSearchName()))
                        .build())
                .toList();

        List<Recommend> savedEntities = recommendRepository.saveAll(entities);

        return savedEntities.stream()
                .map(entity -> RecommendResponseDto.GiftRecommendResultDto.builder()
                        .recommendId(entity.getRecommendId())
                        .name(entity.getContentName())
                        .description(entity.getContentDescription())
                        .imageUrl(entity.getContentImage())
                        .build())
                .toList();
    }

    @Override
    public List<RecommendResponseDto.FriendGiftRecommendResultDto> getFriendRecommendations(Long userId, Long friendId) {

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        boolean exists = friendRepository.existsMutual(userId, friendId);
        if (!exists) {
            throw new GeneralException(ErrorStatus.FRIEND_NOT_FOUND);
        }

        // 유저 정보 추출
        UserSummary summary = buildUserSummary(friend);

        List<RecommendResponseDto.GiftRecommendResultDto> recommendations = openAiService.getRecommendations(summary.personalities(), summary.interests());

        // 이미지 검색 및 map변환
        Map<String, String> imageMap = fetchImageMap(recommendations);

        return recommendations.stream()
                .map(dto -> RecommendResponseDto.FriendGiftRecommendResultDto.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .imageUrl(imageMap.get(dto.getSearchName()))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public RecommendResponseDto.LikeResultDto togleLikeGift(Long userId, Long recommendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Recommend recommend = recommendRepository.findByRecommendIdAndUser(recommendId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RECOMMEND_NOT_FOUND));

        if(recommend.isHated())
            throw new GeneralException(ErrorStatus.ALREADY_DISLIKED);


        boolean isLiked = recommend.togleLike();

        return RecommendResponseDto.LikeResultDto.builder()
                .recommendId(recommendId)
                .isLiked(isLiked)
                .build();
    }

    @Override
    @Transactional
    public RecommendResponseDto.HateResultDto togleHateGift(Long userId, Long recommendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Recommend recommend = recommendRepository.findByRecommendIdAndUser(recommendId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RECOMMEND_NOT_FOUND));

        if(recommend.isLiked())
            throw new GeneralException(ErrorStatus.ALREADY_LIKED);


        boolean isHated = recommend.togleHate();

        return RecommendResponseDto.HateResultDto.builder()
                .recommendId(recommendId)
                .isHated(isHated)
                .build();
    }

    @Override
    @Transactional
    public RecommendResponseDto.VisibilityResultDto togleGiftvisibilty(Long userId, Long recommendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Recommend recommend = recommendRepository.findByRecommendIdAndUser(recommendId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RECOMMEND_NOT_FOUND));

        if(!recommend.isLiked()){
            throw new GeneralException(ErrorStatus.RECOMMEND_NOT_LIKED);
        }

        boolean isPublic = recommend.togleVisibilty();

        return RecommendResponseDto.VisibilityResultDto.builder()
                .recommendId(recommendId)
                .isPublic(isPublic)
                .build();
    }
  
    @Override
    public List<RecommendResponseDto.GiftResultDto> getRecommendGifts(Long userId, LocalDate date) {
        List<Recommend> recommends = recommendRepository.findTop3ByUserIdAndCreatedAtOrderByRecommendIdDesc(userId, date);

        return recommends.stream()
                .map(recommend -> RecommendResponseDto.GiftResultDto.builder()
                        .recommendId(recommend.getRecommendId())
                        .name(recommend.getContentName())
                        .imageUrl(recommend.getContentImage())
                        .isLiked(recommend.isLiked())
                        .isHated(recommend.isHated())
                        .build())
                .toList();
    }

    @Override
    public Page<RecommendResponseDto.LikeListDto> getMyLikedRecommendations(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Recommend> likedPage = recommendRepository.findByUser_IdAndIsLikedTrue(user.getId(), pageable);

        return likedPage.map(r -> RecommendResponseDto.LikeListDto.builder()
                .recommendId(r.getRecommendId())
                .name(r.getContentName())
                .imageUrl(r.getContentImage())
                .description(r.getContentDescription())
                .updatedAt(r.getUpdatedAt())
                .isPublic(r.isPublic())
                .build());
    }



    // 공통 메서드
    private static record UserSummary(String personalities, String interests) {}

    private UserSummary buildUserSummary(User user) {

        // 유저 성격 추출
        String personalities = user.getPersonalities().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        // 유저 흥미 추출
        List<Long> interestIds = userInterestRepository.findByUserId(user.getId()).stream()
                .map(UserInterest::getInterestId)
                .toList();

        List<Interest> interestsList = interestRepository.findAllById(interestIds);

        String interests = interestsList.stream()
                .map(Interest::getName)
                .collect(Collectors.joining(", "));

        return new UserSummary(personalities, interests);
    }

    private Map<String, String> fetchImageMap(List<RecommendResponseDto.GiftRecommendResultDto> recommendations) {
        List<String> keywords = recommendations.stream()
                .map(RecommendResponseDto.GiftRecommendResultDto::getSearchName)
                .toList();

        // 이미지 검색 서비스 호출
        List<RecommendResponseDto.GiftImageResultDto> imageResults = googleSearchService.getImageUrls(keywords);

        // searchName -> ImageUrl 매핑
        return imageResults.stream()
                .filter(dto -> dto.getImageUrl() != null)
                .collect(Collectors.toMap(
                        RecommendResponseDto.GiftImageResultDto::getSearchName,
                        RecommendResponseDto.GiftImageResultDto::getImageUrl,
                        (existing, replacement) -> existing
                ));
    }

}
