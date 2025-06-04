package com.hongik.genieary.domain.calendar.repository;

import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    @Query("SELECT c FROM Calendar c WHERE c.user = :user AND FUNCTION('YEAR', c.createdAt) = :year AND FUNCTION('MONTH', c.createdAt) = :month")
    Optional<Calendar> findByUserAndCreatedAtYearAndMonth(@Param("user") User user, @Param("year") int year, @Param("month") int month);

}
