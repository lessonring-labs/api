package com.lessonring.api.instructor.infrastructure.persistence;

import com.lessonring.api.instructor.domain.Instructor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorJpaRepository extends JpaRepository<Instructor, Long> {

    List<Instructor> findAllByStudioId(Long studioId);
}
