package com.lessonring.api.membership.domain.event;

import com.lessonring.api.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class MembershipUsedEvent extends DomainEvent {

    private final Long membershipId;
    private final Long studioId;
    private final Long memberId;
    private final Integer remainingCount;

    public MembershipUsedEvent(
            Long membershipId,
            Long studioId,
            Long memberId,
            Integer remainingCount
    ) {
        super(membershipId, "MEMBERSHIP_USED");
        this.membershipId = membershipId;
        this.studioId = studioId;
        this.memberId = memberId;
        this.remainingCount = remainingCount;
    }
}