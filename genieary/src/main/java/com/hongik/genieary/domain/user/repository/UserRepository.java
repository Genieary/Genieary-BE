package com.hongik.genieary.domain.user.repository;

import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname% ORDER BY CASE WHEN u.nickname = :nickname THEN 0 ELSE 1 END, u.nickname ASC")
    Page<User> findByNicknameContaining(@Param("nickname")String nickname, Pageable pageable);
}
