package org.fathy.fawrylms.repository;

import org.fathy.fawrylms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByIdAndUserId(Long id, Long userId);
}
