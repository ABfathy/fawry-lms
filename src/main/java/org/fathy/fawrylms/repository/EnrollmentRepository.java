package org.fathy.fawrylms.repository;

import org.fathy.fawrylms.entity.Enrollment;
import org.fathy.fawrylms.types.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    Optional<Enrollment> findByPaymentReference(String paymentReference);
    boolean existsByCourseIdAndStudentId(Long courseId,Long studentId);
    boolean existsByCourseIdAndStudentIdAndStatusIn(Long courseId, Long studentId, Collection<EnrollmentStatus> statuses);
    boolean existsByStudentId(Long studentId);
    boolean existsByCourseId(Long courseId);
    boolean existsByIdAndStudentUserId(Long id, Long userId);
    boolean existsByIdAndCourseInstructorUserId(Long id, Long userId);
    List<Enrollment> findByStudentId(Long studentId);
}
