package com.hongik.genieary.domain.user.repository;

import com.hongik.genieary.domain.user.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByIsActiveTrueOrderByCategoryAscNameAsc();
    List<Interest> findByCategoryAndIsActiveTrueOrderByName(String category);
    List<Interest> findByIdInAndIsActiveTrue(List<Long> ids);
}
