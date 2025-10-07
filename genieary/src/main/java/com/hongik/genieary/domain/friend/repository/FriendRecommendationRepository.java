package com.hongik.genieary.domain.friend.repository;

import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.hongik.genieary.domain.friend.repository.projection.FriendRecommendationRow;

import java.util.Collection;
import java.util.List;

@Repository
public interface FriendRecommendationRepository extends JpaRepository<User, Long> {

    @Query(value = """
      SELECT
          base.user_id AS userId,
          SUM(base.cnt) AS totalOverlap,
          SUM(CASE WHEN base.src='P' THEN base.cnt ELSE 0 END) AS personalityOverlap,
          SUM(CASE WHEN base.src='I' THEN base.cnt ELSE 0 END) AS interestOverlap
      FROM (
          -- 성격 겹침
          SELECT up2.user_id, COUNT(*) AS cnt, 'P' AS src
          FROM user_personalities up1
          JOIN user_personalities up2
            ON up1.personality = up2.personality
          WHERE up1.user_id = :meId
            AND up2.user_id <> :meId
          GROUP BY up2.user_id

          UNION ALL

          -- 관심사 겹침
          SELECT ui2.user_id, COUNT(*) AS cnt, 'I' AS src
          FROM user_interests ui1
          JOIN user_interests ui2
            ON ui1.interest_id = ui2.interest_id
          WHERE ui1.user_id = :meId
            AND ui2.user_id <> :meId
          GROUP BY ui2.user_id
      ) base
      WHERE
          -- 1) 이미 친구면 제외 (양방향)
          NOT EXISTS (
              SELECT 1 FROM friend f
              WHERE (f.user_id = :meId AND f.friend_id = base.user_id)
                 OR (f.user_id = base.user_id AND f.friend_id = :meId)
          )
          -- 2) 친구요청이 진행중(PENDING) 이거나 이미 수락됨(ACCEPTED)이면 제외 (양방향)
          AND NOT EXISTS (
              SELECT 1 FROM friend_request r
              WHERE (
                       (r.requester_id = :meId AND r.receiver_id = base.user_id)
                    OR (r.requester_id = base.user_id AND r.receiver_id = :meId)
                    )
                AND r.status IN ('REQUESTED','ACCEPTED')
          )
      GROUP BY base.user_id
      HAVING SUM(base.cnt) >= :minOverlap
      ORDER BY totalOverlap DESC
      LIMIT :limit
      """, nativeQuery = true)
    List<FriendRecommendationRow> findCandidates(
            @Param("meId") Long userId,
            @Param("minOverlap") int minOverlap,
            @Param("limit") int limit
    );
}