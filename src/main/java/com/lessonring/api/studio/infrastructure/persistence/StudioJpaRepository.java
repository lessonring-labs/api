package com.lessonring.api.studio.infrastructure.persistence;

import com.lessonring.api.studio.domain.Studio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudioJpaRepository extends JpaRepository<Studio, Long> {
}
