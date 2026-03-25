package com.lessonring.api.instructor.domain.repository;

import com.lessonring.api.instructor.domain.Instructor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    List<Instructor> findAllByStudioId(Long studioId);
}
