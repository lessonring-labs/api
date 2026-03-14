package com.lessonring.api.member.application;

import com.lessonring.api.member.domain.Member;
import com.lessonring.api.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAppender {

    private final MemberRepository memberRepository;

    public Member append(Member member) {
        return memberRepository.save(member);
    }
}
