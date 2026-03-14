package com.lessonring.api.membership.infrastructure.persistence;

import com.lessonring.api.membership.domain.Membership;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipJpaRepository extends JpaRepository<Membership, Long> {

    List<Membership> findAllByMemberId(Long memberId);
}