package com.lessonring.api.member.domain.repository;

import com.lessonring.api.member.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    List<Member> findAll();
}