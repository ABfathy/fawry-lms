package org.fathy.fawrylms.repository;

import org.fathy.fawrylms.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor,Long> {
    Optional<Instructor> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByIdAndUserId(Long id, Long userId);
}
