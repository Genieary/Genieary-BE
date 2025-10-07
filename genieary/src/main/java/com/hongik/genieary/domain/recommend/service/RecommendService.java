package com.hongik.genieary.domain.recommend.service;

import com.hongik.genieary.domain.recommend.Category;
import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;

import java.util.List;

public interface RecommendService {
    List<RecommendResponseDto.GiftResultDto> getRecommendations(Long userId, Category category, String event);
}
