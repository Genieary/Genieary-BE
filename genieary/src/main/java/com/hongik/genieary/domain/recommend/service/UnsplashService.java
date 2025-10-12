package com.hongik.genieary.domain.recommend.service;

import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UnsplashService {

    private final RestTemplate restTemplate;

    @Value("${unsplash.access-key}")
    private String accessKey;

    public List<RecommendResponseDto.GiftImageResultDto> getImageUrls(List<String> keywords) {
        List<RecommendResponseDto.GiftImageResultDto> results = new ArrayList<>();

        for (String keyword : keywords) {
            try {
                int randomPage = new Random().nextInt(1) + 1;
                String url = UriComponentsBuilder
                        .fromHttpUrl("https://api.unsplash.com/search/photos")
                        .queryParam("query", keyword)
                        .queryParam("per_page", 1)
                        .queryParam("client_id", accessKey)
                        .toUriString();

                System.out.println(url);

                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                // 결과 검사
                if (response == null || !response.containsKey("results")) {
                    results.add(new RecommendResponseDto.GiftImageResultDto(keyword, null));
                    continue;
                }

                List<Map<String, Object>> photos = (List<Map<String, Object>>) response.get("results");
                if (photos.isEmpty()) {
                    results.add(new RecommendResponseDto.GiftImageResultDto(keyword, null));
                    continue;
                }

                Map<String, Object> urls = (Map<String, Object>) photos.get(0).get("urls");
                String imageUrl = urls != null ? (String) urls.get("small") : null;

                results.add(new RecommendResponseDto.GiftImageResultDto(keyword, imageUrl));

            } catch (Exception e) {
                results.add(new RecommendResponseDto.GiftImageResultDto(keyword, null));
            }
        }

        return results;
    }
}


