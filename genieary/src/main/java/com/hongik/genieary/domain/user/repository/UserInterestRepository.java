package com.hongik.genieary.domain.user.repository;

import com.hongik.genieary.domain.user.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserId(Long userId);
    void deleteByUserId(Long userId);

    @Query("SELECT ui.interestId FROM UserInterest ui WHERE ui.userId = :userId")
    List<Long> findInterestIdsByUserId(@Param("userId") Long userId);
}
