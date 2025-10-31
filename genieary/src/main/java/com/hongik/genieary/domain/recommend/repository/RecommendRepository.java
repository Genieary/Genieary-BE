package com.hongik.genieary.domain.recommend.repository;

import com.hongik.genieary.domain.recommend.entity.Recommend;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    List<Recommend> findByUserAndIsLikedTrue(User user);
    Optional<Recommend> findByRecommendIdAndUser(Long recommendId, User user);
    List<Recommend> findTop3ByUserIdAndCreatedAtOrderByRecommendIdDesc(Long userId, LocalDate createdAt);
    Page<Recommend> findByUser_IdAndIsLikedTrueAndIsPublicTrue(Long friendId, Pageable pageable);
    Page<Recommend> findByUser_IdAndIsLikedTrue(Long userId, Pageable pageable);
}

