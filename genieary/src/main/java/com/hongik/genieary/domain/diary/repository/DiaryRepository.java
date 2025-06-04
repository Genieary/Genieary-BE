package com.hongik.genieary.domain.diary.repository;

import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    boolean existsByUserAndCreatedAt(User user, LocalDate createdAt);
}
