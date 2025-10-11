package com.hongik.genieary.domain.friend.repository.projection;

public interface FriendRecommendationRow {
    Long getUserId();
    Long getTotalOverlap();
    Long getPersonalityOverlap();
    Long getInterestOverlap();
}