package com.hongik.genieary.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
public class RefreshToken {
    @Id
    private String email;
    private String refreshToken;

    public RefreshToken(String email, String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;
    }

    public void updateToken(String newToken) {
        this.refreshToken = newToken;
    }
}

