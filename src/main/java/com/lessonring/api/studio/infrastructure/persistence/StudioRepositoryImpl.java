package com.lessonring.api.studio.infrastructure.persistence;

import com.lessonring.api.studio.domain.Studio;
import com.lessonring.api.studio.domain.repository.StudioRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudioRepositoryImpl implements StudioRepository {

    private final StudioJpaRepository studioJpaRepository;

    @Override
    public Optional<Studio> findById(Long id) {
        return studioJpaRepository.findById(id);
    }

    @Override
    public Studio save(Studio studio) {
        return studioJpaRepository.save(studio);
    }

    @Override
    public List<Studio> findAll() {
        return studioJpaRepository.findAll();
    }
}
