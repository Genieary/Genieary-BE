package com.hongik.genieary.domain.recommend.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.ai.service.OpenAiService;
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
    final private UnsplashService unsplashService;


    @Override
    @Transactional
    public List<RecommendResponseDto.GiftResultDto> getRecommendations(Long userId, Category category, String event){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<UserInterest> userInterests = userInterestRepository.findByUserId(userId);

        String personalities = user.getPersonalities().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        List<Long> interestIds = userInterests.stream()
                .map(UserInterest::getInterestId)
                .toList();

        List<Interest> interestsList = interestRepository.findAllById(interestIds);

        String interests = interestsList.stream()
                .map(Interest::getName)
                .collect(Collectors.joining(", "));

        String eventText = (event != null && !event.isBlank())
                ? "The event is " + event + "."
                : "There is no specific event.";

        List<RecommendResponseDto.GiftResultDto> recommendations = openAiService.getRecommendations(personalities, interests, category, eventText);

        // Unsplash로 검색할 키워드 정리
        List<String> keywords = recommendations.stream()
                .map(RecommendResponseDto.GiftResultDto::getName)
                .toList();

        System.out.println(keywords);

        // Unsplash로 이미지 검색
        List<RecommendResponseDto.GiftImageResultDto> imageResults = unsplashService.getImageUrls(keywords);

        // name 기준으로 이미지 URL 매핑
        Map<String, String> imageMap = imageResults.stream()
                .filter(dto -> dto.getImageUrl() != null) // null 제거
                .collect(Collectors.toMap(
                        RecommendResponseDto.GiftImageResultDto::getName,
                        RecommendResponseDto.GiftImageResultDto::getImageUrl));

        // 이미지 url 추가한 dto생성
        List<RecommendResponseDto.GiftResultDto> enrichedRecommendations = recommendations.stream()
                .map(dto -> RecommendResponseDto.GiftResultDto.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .imageUrl(imageMap.get(dto.getName()))
                        .build())
                .toList();

        // DB 저장
        List<Recommend> entities = enrichedRecommendations.stream()
                .map(dto -> Recommend.builder()
                        .user(user)
                        .contentName(dto.getName())
                        .contentDescription(dto.getDescription())
                        .contentImage(dto.getImageUrl())
                        .build())
                .toList();


        recommendRepository.saveAll(entities);

        return enrichedRecommendations;
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
                        .name(recommend.getContentName())
                        .description(recommend.getContentDescription())
                        .build())
                .toList();
    }
}
