package com.hongik.genieary.domain.recommend.repository;

import com.hongik.genieary.domain.recommend.entity.Recommend;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    List<Recommend> findByUserAndIsLikedTrue(User user);
}

