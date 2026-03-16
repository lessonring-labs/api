package com.lessonring.api.membership.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.member.domain.repository.MemberRepository;
import com.lessonring.api.membership.api.request.MembershipCreateRequest;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipStatus;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Membership create(MembershipCreateRequest request) {
        memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Membership membership = Membership.create(
                request.getStudioId(),
                request.getMemberId(),
                request.getName(),
                request.getType(),
                request.getTotalCount(),
                request.getStartDate(),
                request.getEndDate()
        );

        return membershipRepository.save(membership);
    }

    @Transactional
    public Membership get(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        refreshStatus(membership);
        return membership;
    }

    @Transactional
    public List<Membership> getAllByMemberId(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        List<Membership> memberships = membershipRepository.findAllByMemberId(memberId);
        memberships.forEach(this::refreshStatus);
        return memberships;
    }

    private void refreshStatus(Membership membership) {
        LocalDate today = LocalDate.now();

        if (membership.getStatus() == MembershipStatus.ACTIVE && membership.isExpired(today)) {
            membership.markExpired();
        }
    }
}