package com.lessonring.api.membership.infrastructure.persistence;

import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MembershipRepositoryImpl implements MembershipRepository {

    private final MembershipJpaRepository membershipJpaRepository;

    @Override
    public Membership save(Membership membership) {
        return membershipJpaRepository.save(membership);
    }

    @Override
    public Optional<Membership> findById(Long id) {
        return membershipJpaRepository.findById(id);
    }

    @Override
    public List<Membership> findAllByMemberId(Long memberId) {
        return membershipJpaRepository.findAllByMemberId(memberId);
    }
}