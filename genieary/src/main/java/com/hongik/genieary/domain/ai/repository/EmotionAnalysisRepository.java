package com.hongik.genieary.domain.ai.repository;

import com.hongik.genieary.domain.ai.entity.EmotionAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmotionAnalysisRepository extends JpaRepository<EmotionAnalysis, Long> {
    Optional<EmotionAnalysis> findByDiaryId(Long diaryId);
}
