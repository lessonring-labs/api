package com.lessonring.api.instructor.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.instructor.domain.Instructor;
import com.lessonring.api.instructor.domain.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstructorReader {

    private final InstructorRepository instructorRepository;

    public Instructor getInstructor(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
