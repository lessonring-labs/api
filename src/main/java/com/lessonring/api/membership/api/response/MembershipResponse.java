package com.lessonring.api.membership.api.response;

import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipStatus;
import com.lessonring.api.membership.domain.MembershipType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(description = "이용권 응답")
public class MembershipResponse {

    @Schema(description = "이용권 ID", example = "1")
    private final Long id;

    @Schema(description = "스튜디오 ID", example = "1")
    private final Long studioId;

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "이용권명", example = "10회권")
    private final String name;

    @Schema(description = "이용권 유형", example = "COUNT")
    private final MembershipType type;

    @Schema(description = "총 횟수", example = "10")
    private final Integer totalCount;

    @Schema(description = "잔여 횟수", example = "7")
    private final Integer remainingCount;

    @Schema(description = "이용 시작일", example = "2026-03-18")
    private final LocalDate startDate;

    @Schema(description = "이용 종료일", example = "2026-04-18")
    private final LocalDate endDate;

    @Schema(description = "이용권 상태", example = "ACTIVE")
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