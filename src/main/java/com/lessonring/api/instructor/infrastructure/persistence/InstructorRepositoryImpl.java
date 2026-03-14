package com.lessonring.api.instructor.infrastructure.persistence;

import com.lessonring.api.instructor.domain.Instructor;
import com.lessonring.api.instructor.domain.repository.InstructorRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InstructorRepositoryImpl implements InstructorRepository {

    private final InstructorJpaRepository instructorJpaRepository;

    @Override
    public Optional<Instructor> findById(Long id) {
        return instructorJpaRepository.findById(id);
    }

    @Override
    public Instructor save(Instructor instructor) {
        return instructorJpaRepository.save(instructor);
    }

    @Override
    public List<Instructor> findAllByStudioId(Long studioId) {
        return instructorJpaRepository.findAllByStudioId(studioId);
    }
}
