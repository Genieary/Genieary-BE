package com.hongik.genieary.domain.user.service;

import com.hongik.genieary.domain.enums.Gender;
import com.hongik.genieary.domain.enums.Personality;
import com.hongik.genieary.domain.user.dto.request.ProfileCompleteRequest;
import com.hongik.genieary.domain.user.dto.request.ProfileUpdateRequest;
import com.hongik.genieary.domain.user.dto.response.ProfileResponse;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // 프로필 완성 (첫 로그인 시)
    public ProfileResponse completeProfile(Long userId, ProfileCompleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getIsProfileCompleted()) {
            throw new IllegalStateException("이미 프로필이 완성된 사용자입니다.");
        }

        // 성격 선택 개수 검증 (최소1개 최대3개)
        validatePersonalities(request.getPersonalities());

        user.updateProfile(
                request.getNickname(),
                request.getBirthDate(),
                request.getGender(),
                request.getPersonalities()
        );

        User savedUser = userRepository.save(user);
        return ProfileResponse.from(savedUser);
    }

    // 프로필 수정
    //TODO : 프로필 이미지도 업데이트 가능
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 성격 개수 검증 (성격이 변경되는 경우에만)
        if (request.getPersonalities() != null) {
            validatePersonalities(request.getPersonalities());
        }

        // 변경된 필드만 업데이트
        String nickname = request.getNickname() != null ? request.getNickname() : user.getNickname();
        LocalDate birthDate = request.getBirthDate() != null ? request.getBirthDate() : user.getBirthDate();
        Gender gender = request.getGender() != null ? request.getGender() : user.getGender();
        Set<Personality> personalities = request.getPersonalities() != null ? request.getPersonalities() : user.getPersonalities();

        user.updateProfile(nickname, birthDate, gender, personalities);
        User savedUser = userRepository.save(user);
        return ProfileResponse.from(savedUser);
    }

    // 프로필 조회
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return ProfileResponse.from(user);
    }

    // 프로필 완성 여부 확인
    @Transactional(readOnly = true)
    public boolean isProfileCompleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return user.getIsProfileCompleted();
    }

    // ----- method -----
    private void validatePersonalities(Set<Personality> personalities) {
        if (personalities == null || personalities.isEmpty()) {
            throw new IllegalArgumentException("성격을 최소 1개는 선택해야 합니다.");
        }
        if (personalities.size() > 3) {
            throw new IllegalArgumentException("성격은 최대 3개까지만 선택할 수 있습니다.");
        }
    }
}
