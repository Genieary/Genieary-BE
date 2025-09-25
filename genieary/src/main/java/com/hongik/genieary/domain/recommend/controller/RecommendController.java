package com.hongik.genieary.domain.recommend.controller;

import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.recommend.Category;
import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;
import com.hongik.genieary.domain.recommend.service.RecommendService;
import com.hongik.genieary.domain.user.dto.request.ProfileCompleteRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "RECOMMEND API", description = "선물추천 관련 API")
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {
    final private RecommendService recommendService;

    @PostMapping
    public ResponseEntity<ApiResponse> recommendGifts(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam Category category,
            @RequestParam(required = false) String event){

        List<RecommendResponseDto.GiftResultDto> gifts = recommendService.getRecommendations(userId, category, event);

        return ApiResponse.onSuccess(SuccessStatus._OK, gifts);
    }
}
