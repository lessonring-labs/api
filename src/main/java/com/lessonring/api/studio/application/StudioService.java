package com.lessonring.api.studio.application;

import com.lessonring.api.studio.domain.repository.StudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudioService {

    private final StudioRepository studioRepository;
}
