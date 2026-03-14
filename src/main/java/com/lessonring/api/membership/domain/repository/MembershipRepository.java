package com.lessonring.api.membership.domain.repository;

import com.lessonring.api.membership.domain.Membership;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository {

    Membership save(Membership membership);

    Optional<Membership> findById(Long id);

    List<Membership> findAllByMemberId(Long memberId);
}