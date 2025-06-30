package com.hongik.genieary.domain.user.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.enums.Gender;
import com.hongik.genieary.domain.enums.ImageType;
import com.hongik.genieary.domain.enums.Personality;
import com.hongik.genieary.domain.user.dto.request.ProfileCompleteRequest;
import com.hongik.genieary.domain.user.dto.request.ProfileUpdateRequest;
import com.hongik.genieary.domain.user.dto.response.ProfileResponse;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import com.hongik.genieary.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 프로필 완성 (첫 로그인 시)
    public ProfileResponse completeProfile(Long userId, ProfileCompleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (user.getIsProfileCompleted()) {
            throw new GeneralException(ErrorStatus.PROFILE_ALREADY_COMPLETED);
        }

        // 성격 선택 개수 검증 (최소1개 최대3개)
        validatePersonalities(request.getPersonalities());

        user.updateProfile(
                request.getNickname(),
                request.getBirthDate(),
                request.getGender(),
                request.getPersonalities(),
                request.getImageFileName()
        );

        User savedUser = userRepository.save(user);
        return ProfileResponse.from(savedUser);
    }

    // 프로필 수정
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 성격 개수 검증 (성격이 변경되는 경우에만)
        if (request.getPersonalities() != null) {
            validatePersonalities(request.getPersonalities());
        }

        // 변경된 필드만 업데이트
        String nickname = request.getNickname() != null ? request.getNickname() : user.getNickname();
        LocalDate birthDate = request.getBirthDate() != null ? request.getBirthDate() : user.getBirthDate();
        Gender gender = request.getGender() != null ? request.getGender() : user.getGender();
        Set<Personality> personalities = request.getPersonalities() != null ? request.getPersonalities() : user.getPersonalities();
        String imageFileName = request.getImageFileName() != null ? request.getImageFileName() : user.getImageFileName();

        user.updateProfile(nickname, birthDate, gender, personalities, imageFileName);
        User savedUser = userRepository.save(user);
        return ProfileResponse.from(savedUser);
    }

    // 프로필 조회
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return ProfileResponse.from(user);
    }

    // 프로필 완성 여부 확인
    @Transactional(readOnly = true)
    public boolean isProfileCompleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return user.getIsProfileCompleted();
    }

    // ----- method -----
    private void validatePersonalities(Set<Personality> personalities) {
        if (personalities == null || personalities.isEmpty()) {
            throw new GeneralException(ErrorStatus.PERSONALITY_REQUIRED);
        }
        if (personalities.size() > 3) {
            throw new GeneralException(ErrorStatus.PERSONALITY_LIMIT_EXCEEDED);
        }
    }

    // 프로필 이미지 presigned url 발급
    public ProfileResponse.ProfilePresignedUrlResponse uploadProfileImage(Long userId) {
        String fileName = "profile_" + userId + "_" + UUID.randomUUID() + ".jpg";
        String url = s3Service.generatePresignedUploadUrl(fileName, ImageType.PROFILE);

        return ProfileResponse.ProfilePresignedUrlResponse.builder()
                .fileName(fileName)
                .url(url)
                .build();
    }

    @Transactional(readOnly = true)
    public String getProfileImageUrl(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String fileName = user.getImageFileName();
        if (fileName == null || fileName.isBlank()) {
            throw new GeneralException(ErrorStatus.IMAGE_NOT_FOUND);
        }

        return s3Service.generatePresignedDownloadUrl(fileName, ImageType.PROFILE);
    }

}
