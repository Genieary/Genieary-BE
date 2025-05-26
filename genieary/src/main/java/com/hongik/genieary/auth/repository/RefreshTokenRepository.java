package com.hongik.genieary.auth.repository;

import com.hongik.genieary.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByEmail(String email);

    @Modifying
    @Query("update RefreshToken r set r.refreshToken = :token where r.email = :email")
    void updateToken(@Param("email") String email, @Param("token") String token);

    default void saveOrUpdate(String email, String token) {
        findByEmail(email).ifPresentOrElse(
                r -> updateToken(email, token),
                () -> save(new RefreshToken(email, token))
        );
    }
}