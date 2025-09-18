package com.hongik.genieary.domain.diary.repository;

import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    boolean existsByUserIdAndDiaryDate(Long userId, LocalDate diaryDate);

    @Query("SELECT d FROM Diary d WHERE d.diaryId = :diaryId AND d.user.id = :userId")
    Optional<Diary> findByDiaryIdAndUserId(@Param("diaryId") Long diaryId, @Param("userId") Long userId);

    Optional<Diary> findByUserIdAndDiaryDate(Long userId, LocalDate date);

    List<Diary> findAllByUserIdAndCalendar_CalendarId(Long userId, Long calendarId);

}
