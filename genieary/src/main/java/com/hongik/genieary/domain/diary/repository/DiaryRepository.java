package com.hongik.genieary.domain.diary.repository;

import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    boolean existsByUserAndCreatedAt(User user, LocalDate createdAt);
    Optional<Diary> findByDiaryIdAndUser(Long id, User user);
}
