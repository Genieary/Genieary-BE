package com.hongik.genieary.domain.user.entity;

import com.hongik.genieary.domain.user.repository.InterestRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InterestDataInitializer {

    private final InterestRepository interestRepository;

    @PostConstruct
    public void initializeData() {
        if (interestRepository.count() == 0) {
            createInterests();
        }
    }

    private void createInterests() {
        Map<String, String[]> categoryInterests = Map.of(
                "푸드.드링크", new String[]{"맛집투어", "요리", "주류", "베이킹", "디저트", "커피", "티", "파인다이닝"},
                "자기계발", new String[]{"독서", "스터디", "스피치", "커리어", "브랜딩", "창작", "외국어"},
                "실내", new String[]{"사진", "드로잉", "댄스", "공예", "노래", "악기 연주", "글쓰기", "봉사", "음악 감상", "향수", "뷰티", "쇼핑", "영상", "캘리그라피", "만화"},
                "액티비티", new String[]{"등산", "야구", "산책", "스포츠관람", "러닝", "클라이밍", "요가", "다이어트", "헬스", "테니스 배드민턴", "자전거", "풋살", "볼링", "농구", "필라테스", "골프", "수영", "축구", "스케이트보드", "수상스포츠"},
                "소셜게임", new String[]{"보드게임", "컨셉게임", "추리게임", "방탈출", "온라인게임"},
                "문화예술", new String[]{"전시", "영화", "페스티벌", "연극", "뮤지컬", "공연", "콘서트", "연주회", "팝업"},
                "재테크", new String[]{"투자금융", "부동산", "창업", "주식", "경제", "블로그", "SNS"},
                "여행.나들이", new String[]{"국내여행", "피크닉", "해외여행", "캠핑", "드라이브", "놀이공원"}
        );

        List<Interest> interests = categoryInterests.entrySet().stream()
                .flatMap(entry -> Arrays.stream(entry.getValue())
                        .map(name -> Interest.builder()
                                .name(name)
                                .category(entry.getKey())
                                .build()))
                .collect(Collectors.toList());

        interestRepository.saveAll(interests);
    }
}
