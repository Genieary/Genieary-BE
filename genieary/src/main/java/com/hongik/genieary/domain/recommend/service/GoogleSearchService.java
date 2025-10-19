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
public class GoogleSearchService {

    private final RestTemplate restTemplate;

    @Value("${google.custom-search.api-key}")
    private String apiKey;

    @Value("${google.custom-search.cx}")
    private String cx;

    public List<RecommendResponseDto.GiftImageResultDto> getImageUrls(List<String> keywords) {
        List<RecommendResponseDto.GiftImageResultDto> results = new ArrayList<>();

        for (String keyword : keywords) {
            try {
                String url = UriComponentsBuilder
                        .fromHttpUrl("https://www.googleapis.com/customsearch/v1")
                        .queryParam("key", apiKey)
                        .queryParam("cx", cx)
                        .queryParam("q", keyword)
                        .queryParam("searchType", "image")
                        .queryParam("num", 1)
                        .toUriString();

                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response == null || !response.containsKey("items")) {
                    results.add(new RecommendResponseDto.GiftImageResultDto(keyword, null));
                    continue;
                }

                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                if (items.isEmpty()) {
                    results.add(new RecommendResponseDto.GiftImageResultDto(keyword, null));
                    continue;
                }

                String imageUrl = (String) items.get(0).get("link");

                results.add(RecommendResponseDto.GiftImageResultDto.builder()
                        .searchName(keyword)
                        .imageUrl(imageUrl)
                        .build());

            } catch (Exception e) {
                results.add(RecommendResponseDto.GiftImageResultDto.builder()
                        .searchName(keyword)
                        .imageUrl(null)
                        .build());
            }
        }

        return results;
    }


}