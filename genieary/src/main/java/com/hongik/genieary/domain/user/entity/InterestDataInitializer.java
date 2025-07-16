package com.hongik.genieary.domain.user.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.hongik.genieary.config.InterestDataConfig;
import com.hongik.genieary.domain.user.repository.InterestRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestDataInitializer {

    private final InterestRepository interestRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void initializeData() {
        if (interestRepository.count() == 0) {
            createInterests();
        }
    }

    private void createInterests() {
        try {
            // YAML 파일 로드
            ClassPathResource resource = new ClassPathResource("data/interests.yml");

            // Jackson YAML 파서 사용
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            InterestDataConfig config = yamlMapper.readValue(resource.getInputStream(), InterestDataConfig.class);

            List<Interest> interests = config.getInterests().stream()
                    .flatMap(categoryData -> categoryData.getItems().stream()
                            .map(name -> Interest.builder()
                                    .name(name)
                                    .category(categoryData.getCategory())
                                    .build()))
                    .collect(Collectors.toList());

            interestRepository.saveAll(interests);
            log.info("YAML 파일에서 관심사 데이터 초기화 완료: {} 개의 관심사가 생성되었습니다.", interests.size());

        } catch (IOException e) {
            log.error("관심사 데이터 초기화 실패", e);
            throw new RuntimeException("관심사 데이터 초기화 실패", e);
        }
    }
}
