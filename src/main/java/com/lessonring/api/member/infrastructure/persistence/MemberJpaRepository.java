package com.lessonring.api.member.infrastructure.persistence;

import com.lessonring.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}