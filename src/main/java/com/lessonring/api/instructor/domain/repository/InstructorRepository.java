package com.lessonring.api.instructor.domain.repository;

import com.lessonring.api.instructor.domain.Instructor;
import java.util.List;
import java.util.Optional;

public interface InstructorRepository {

    Optional<Instructor> findById(Long id);

    Instructor save(Instructor instructor);

    List<Instructor> findAllByStudioId(Long studioId);
}
