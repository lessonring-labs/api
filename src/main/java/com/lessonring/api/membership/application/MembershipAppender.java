package com.lessonring.api.membership.application;

import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembershipAppender {

    private final MembershipRepository membershipRepository;

    public Membership append(Membership membership) {
        return membershipRepository.save(membership);
    }
}
