package com.hongik.genieary.domain.recommend.service;

import com.hongik.genieary.domain.recommend.Category;
import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface RecommendService {
    List<RecommendResponseDto.GiftRecommendResultDto> getRecommendations(Long userId, Category category, String event);

    RecommendResponseDto.LikeResultDto togleLikeGift(Long userId, Long recommendId);

    RecommendResponseDto.HateResultDto togleHateGift(Long userId, Long recommendId);

    RecommendResponseDto.VisibilityResultDto togleGiftvisibilty(Long userId, Long recommendId);

    List<RecommendResponseDto.GiftResultDto> getRecommendGifts(Long userId, LocalDate date);

    Page<RecommendResponseDto.LikeListDto> getMyLikedRecommendations(Long userId, int page, int size);
}
