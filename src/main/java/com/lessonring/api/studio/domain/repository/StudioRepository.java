package com.lessonring.api.studio.domain.repository;

import com.lessonring.api.studio.domain.Studio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudioRepository extends JpaRepository<Studio, Long> {
}
