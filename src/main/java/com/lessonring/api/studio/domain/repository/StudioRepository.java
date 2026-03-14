package com.lessonring.api.studio.domain.repository;

import com.lessonring.api.studio.domain.Studio;
import java.util.List;
import java.util.Optional;

public interface StudioRepository {

    Optional<Studio> findById(Long id);

    Studio save(Studio studio);

    List<Studio> findAll();
}
