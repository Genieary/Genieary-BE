package com.hongik.genieary.domain.user.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.enums.Gender;
import com.hongik.genieary.domain.enums.ImageType;
import com.hongik.genieary.domain.enums.Personality;
import com.hongik.genieary.domain.user.dto.request.InterestProfileCompleteRequest;
import com.hongik.genieary.domain.user.dto.request.ProfileCompleteRequest;
import com.hongik.genieary.domain.user.dto.request.ProfileUpdateRequest;
import com.hongik.genieary.domain.user.dto.response.InterestResponse;
import com.hongik.genieary.domain.user.dto.response.ProfileResponse;
import com.hongik.genieary.domain.user.dto.response.UserInterestResponse;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import com.hongik.genieary.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final InterestService interestService;

    // 프로필 완성 (첫 로그인 시)
    public ProfileResponse completeProfile(Long userId, ProfileCompleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (user.getBasicProfileCompleted()) {
            throw new GeneralException(ErrorStatus.PROFILE_ALREADY_COMPLETED);
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

        user.updateProfile(nickname, birthDate, gender, personalities);
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

        return user.isProfileCompleted();
    }

    // 2단계: 관심사 프로필 완성
    public UserInterestResponse completeInterestProfile(Long userId, InterestProfileCompleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        //1단계 기존정보 입력 후에 가능
        if (!user.getBasicProfileCompleted()) {
            throw new GeneralException(ErrorStatus.BASIC_PROFILE_NOT_COMPLETED);
        }

        if (user.getInterestProfileCompleted()) {
            throw new GeneralException(ErrorStatus.INTEREST_PROFILE_ALREADY_COMPLETED);
        }

        // 관심사 검증 및 저장
        interestService.validateInterests(request.getInterestIds());
        interestService.saveUserInterests(userId, request.getInterestIds());

        // 관심사 프로필 완성 처리
        user.completeInterestProfile();
        userRepository.save(user);

        List<InterestResponse> interests = interestService.getUserInterests(userId);
        return UserInterestResponse.from(userId, interests);
    }

    // 관심사 수정
    @Transactional
    public UserInterestResponse updateUserInterests(Long userId, InterestProfileCompleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 관심사 검증 및 저장
        interestService.validateInterests(request.getInterestIds());
        interestService.saveUserInterests(userId, request.getInterestIds());

        // 관심사 정보만 반환
        List<InterestResponse> interests = interestService.getUserInterests(userId);
        return UserInterestResponse.from(userId, interests);
    }

    // 사용자 관심사 조회
    @Transactional(readOnly = true)
    public UserInterestResponse getUserInterests(Long userId) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }

        List<InterestResponse> interests = interestService.getUserInterests(userId);
        return UserInterestResponse.from(userId, interests);
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

    @Transactional
    // 프로필 이미지 presigned url 발급
    public ProfileResponse.ProfilePresignedUrlResponse uploadProfileImage(Long userId,String contentType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String fileName = "profile_" + userId;
        String url = s3Service.generatePresignedUploadUrl(fileName, ImageType.PROFILE, contentType);

        user.updateImageFileName(fileName);

        return ProfileResponse.ProfilePresignedUrlResponse.builder()
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
