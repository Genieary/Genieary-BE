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

import java.time.LocalDateTime;
import java.util.List;
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


    @Override
    @Transactional
    public List<RecommendResponseDto.GiftResultDto> getRecommendations(Long userId, Category category){
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

        System.out.println(interests);
        System.out.println(personalities);

        List<RecommendResponseDto.GiftResultDto> recommendations = openAiService.getRecommendations(personalities, interests, category);

        List<Recommend> entities = recommendations.stream()
                .map(dto -> Recommend.builder()
                        .user(user)
                        .contentName(dto.getName())
                        .contentDescription(dto.getDescription())
                        .build())
                .toList();

        recommendRepository.saveAll(entities);

        return recommendations;
    }


}
