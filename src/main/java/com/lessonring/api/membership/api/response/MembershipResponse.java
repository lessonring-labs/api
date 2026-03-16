package com.lessonring.api.membership.api.response;

import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipStatus;
import com.lessonring.api.membership.domain.MembershipType;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MembershipResponse {

    private final Long id;
    private final Long studioId;
    private final Long memberId;
    private final String name;
    private final MembershipType type;
    private final Integer totalCount;
    private final Integer remainingCount;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final MembershipStatus status;

    public MembershipResponse(Membership membership) {
        this.id = membership.getId();
        this.studioId = membership.getStudioId();
        this.memberId = membership.getMemberId();
        this.name = membership.getName();
        this.type = membership.getType();
        this.totalCount = membership.getTotalCount();
        this.remainingCount = membership.getRemainingCount();
        this.startDate = membership.getStartDate();
        this.endDate = membership.getEndDate();
        this.status = membership.getStatus();
    }
}