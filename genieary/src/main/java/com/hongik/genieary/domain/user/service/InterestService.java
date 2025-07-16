package com.hongik.genieary.domain.user.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.user.dto.response.InterestResponse;
import com.hongik.genieary.domain.user.entity.Interest;
import com.hongik.genieary.domain.user.entity.UserInterest;
import com.hongik.genieary.domain.user.repository.InterestRepository;
import com.hongik.genieary.domain.user.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestService {

    private final InterestRepository interestRepository;
    private final UserInterestRepository userInterestRepository;

    // 카테고리별 관심사 조회 (Map으로 간단하게)
    public Map<String, List<InterestResponse>> getInterestsGroupedByCategory() {
        List<Interest> interests = interestRepository.findByIsActiveTrueOrderByCategoryAscNameAsc();

        return interests.stream()
                .collect(Collectors.groupingBy(
                        Interest::getCategory,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                InterestResponse::fromWithoutCategory,
                                Collectors.toList()
                        )
                ));
    }

    // 사용자 관심사 저장
    @Transactional
    public void saveUserInterests(Long userId, List<Long> interestIds) {
        // 기존 관심사 삭제
        userInterestRepository.deleteByUserId(userId);

        // 새로운 관심사 저장
        List<UserInterest> userInterests = interestIds.stream()
                .map(interestId -> UserInterest.builder()
                        .userId(userId)
                        .interestId(interestId)
                        .build())
                .collect(Collectors.toList());

        userInterestRepository.saveAll(userInterests);
    }

    // 사용자 관심사 조회
    public List<InterestResponse> getUserInterests(Long userId) {
        List<Long> interestIds = userInterestRepository.findInterestIdsByUserId(userId);

        if (interestIds.isEmpty()) {
            return new ArrayList<>();
        }

        return interestRepository.findByIdInAndIsActiveTrue(interestIds)
                .stream()
                .map(InterestResponse::from)
                .collect(Collectors.toList());
    }

    // 관심사 검증
    public void validateInterests(List<Long> interestIds) {
        if (interestIds == null || interestIds.isEmpty()) {
            throw new GeneralException(ErrorStatus.INTEREST_REQUIRED);
        }
        if (interestIds.size() > 5) {
            throw new GeneralException(ErrorStatus.INTEREST_LIMIT_EXCEEDED);
        }

        // 중복 체크
        Set<Long> uniqueIds = new HashSet<>(interestIds);
        if (uniqueIds.size() != interestIds.size()) {
            throw new GeneralException(ErrorStatus.DUPLICATE_INTEREST_SELECTED);
        }

        // 존재하는 관심사인지 확인
        List<Interest> existingInterests = interestRepository.findByIdInAndIsActiveTrue(interestIds);
        if (existingInterests.size() != interestIds.size()) {
            throw new GeneralException(ErrorStatus.INTEREST_NOT_FOUND);
        }
    }
}
