package com.hongik.genieary.domain.ai.entity;

import com.hongik.genieary.domain.diary.entity.Diary;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class EmotionAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionAnalysisId;

    @OneToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    private String predictedEmotion;

    @Column(columnDefinition = "JSON")
    private String allPredictions;

    @Column
    private String analysis;

    private LocalDate diaryDate;
}


