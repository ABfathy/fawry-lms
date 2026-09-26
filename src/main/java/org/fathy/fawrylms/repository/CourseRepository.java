package org.fathy.fawrylms.repository;

import org.fathy.fawrylms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String code);
    boolean existsByCourseCode(String code);
}
