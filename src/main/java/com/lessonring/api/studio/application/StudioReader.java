package com.lessonring.api.studio.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.studio.domain.Studio;
import com.lessonring.api.studio.domain.repository.StudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudioReader {

    private final StudioRepository studioRepository;

    public Studio getStudio(Long id) {
        return studioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
