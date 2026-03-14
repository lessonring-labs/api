package com.lessonring.api.member.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.member.api.request.MemberCreateRequest;
import com.lessonring.api.member.domain.Member;
import com.lessonring.api.member.domain.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member create(MemberCreateRequest request) {
        Member member = Member.create(
                request.getStudioId(),
                request.getName(),
                request.getPhone(),
                request.getEmail(),
                request.getGender(),
                request.getBirthDate(),
                request.getMemo()
        );

        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member get(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Member> getAll() {
        return memberRepository.findAll();
    }
}