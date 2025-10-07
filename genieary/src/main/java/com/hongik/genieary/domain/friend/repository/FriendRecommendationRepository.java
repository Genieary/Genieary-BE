package com.hongik.genieary.domain.friend.repository;

import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FriendRecommendationRepository extends JpaRepository<User, Long> {

    interface Row {
        Long getUserId();
        Integer getTotalOverlap();
        Integer getPersonalityOverlap();
        Integer getInterestOverlap();
    }

    @Query(value = """
    SELECT base.user_id AS userId,
           SUM(base.cnt) AS totalOverlap,
           SUM(CASE WHEN base.src='P' THEN base.cnt ELSE 0 END) AS personalityOverlap,
           SUM(CASE WHEN base.src='I' THEN base.cnt ELSE 0 END) AS interestOverlap
    FROM (
        SELECT up2.user_id, COUNT(*) AS cnt, 'P' AS src
        FROM user_personalities up1
        JOIN user_personalities up2 ON up1.personality = up2.personality
        WHERE up1.user_id = :userId AND up2.user_id <> :userId
        GROUP BY up2.user_id

        UNION ALL

        SELECT ui2.user_id, COUNT(*) AS cnt, 'I' AS src
        FROM user_interests ui1
        JOIN user_interests ui2 ON ui1.interest_id = ui2.interest_id
        WHERE ui1.user_id = :userId AND ui2.user_id <> :userId
        GROUP BY ui2.user_id
    ) base
    LEFT JOIN `friend` f1 ON f1.user_id = :userId AND f1.friend_id = base.user_id
    LEFT JOIN `friend` f2 ON f2.user_id = base.user_id AND f2.friend_id = :userId
    WHERE f1.user_id IS NULL AND f2.user_id IS NULL
    GROUP BY base.user_id
    HAVING SUM(base.cnt) >= :minOverlap
    ORDER BY RAND()
    LIMIT :maxCount
    """, nativeQuery = true)
    List<Row> findRandomCandidates(
            @Param("userId") Long userId,
            @Param("minOverlap") int minOverlap,
            @Param("maxCount") int maxCount
    );
}