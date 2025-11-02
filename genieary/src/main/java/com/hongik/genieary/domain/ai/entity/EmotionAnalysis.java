package com.hongik.genieary.domain.ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmotionAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionAnalysisId;

    @Column(name = "diary_id", nullable = false)
    private Long diaryId;

    private String predictedEmotion;

    @Column(columnDefinition = "JSON")
    private String allPredictions;

    @Column
    private String analysis;
}


