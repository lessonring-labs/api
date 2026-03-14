package com.lessonring.api.member.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studioId;

    private String name;

    private String phone;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime joinedAt;

    private String memo;

    private Member(
            Long studioId,
            String name,
            String phone,
            String email,
            Gender gender,
            LocalDate birthDate,
            MemberStatus status,
            LocalDateTime joinedAt,
            String memo
    ) {
        this.studioId = studioId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.status = status;
        this.joinedAt = joinedAt;
        this.memo = memo;
    }

    public static Member create(
            Long studioId,
            String name,
            String phone,
            String email,
            Gender gender,
            LocalDate birthDate,
            String memo
    ) {
        return new Member(
                studioId,
                name,
                phone,
                email,
                gender,
                birthDate,
                MemberStatus.ACTIVE,
                LocalDateTime.now(),
                memo
        );
    }
}