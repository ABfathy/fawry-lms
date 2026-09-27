package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.enrollment.CreateEnrollmentRequest;
import org.fathy.fawrylms.dto.enrollment.EnrollmentResponse;
import org.fathy.fawrylms.dto.payment.PaymentWebhookRequest;
import org.fathy.fawrylms.entity.Course;
import org.fathy.fawrylms.entity.Enrollment;
import org.fathy.fawrylms.entity.Student;
import org.fathy.fawrylms.exception.IncorrectPaymentStatusException;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.CourseRepository;
import org.fathy.fawrylms.repository.EnrollmentRepository;
import org.fathy.fawrylms.repository.StudentRepository;
import org.fathy.fawrylms.types.EnrollmentStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and @securityService.isStudentSelf(#enrollmentRequest.studentId()))")
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest enrollmentRequest) {
        Student student = studentRepository.findById(enrollmentRequest.studentId()).orElseThrow(
                () -> new ResourceNotFoundException("Student not found")
        );

        Course course = courseRepository.findById(enrollmentRequest.courseId()).orElseThrow(
                () -> new ResourceNotFoundException("Course not found")
        );

        if (enrollmentRepository.existsByCourseIdAndStudentIdAndStatusIn(
                course.getId(),
                student.getId(),
                List.of(EnrollmentStatus.PENDING_PAYMENT, EnrollmentStatus.ACTIVE))) {
            throw new ResourceConflictException("Student already enrolled");
        }

        String paymentReference = "FAWRY-" + UUID.randomUUID().toString().toUpperCase();

        Enrollment enrollment = enrollmentRepository.save(new Enrollment(student, course, course.getPrice(), paymentReference,
                EnrollmentStatus.PENDING_PAYMENT, LocalDateTime.now()));

        return new EnrollmentResponse(enrollment.getId(), student.getId(), course.getId(),
                course.getPrice(), enrollment.getEnrollmentDate(), enrollment.getStatus(), paymentReference);
    }

    @Transactional
    public EnrollmentResponse processPaymentWebHook(PaymentWebhookRequest paymentWebhookRequest) {
        Enrollment enrollment = enrollmentRepository.findByPaymentReference(paymentWebhookRequest
                .paymentReference()).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        EnrollmentStatus requestedStatus;
        if (paymentWebhookRequest.status().equalsIgnoreCase("PAID")) {
            requestedStatus = EnrollmentStatus.ACTIVE;
        } else if (paymentWebhookRequest.status().equalsIgnoreCase("FAILED")) {
            requestedStatus = EnrollmentStatus.CANCELED;
        } else {
            throw new IncorrectPaymentStatusException("Given Payment status is incorrect");
        }

        if (enrollment.getStatus() != EnrollmentStatus.PENDING_PAYMENT) {
            if (enrollment.getStatus() != requestedStatus) {
                throw new ResourceConflictException("Payment status is already final");
            }
            return toResponse(enrollment);
        }

        enrollment.setStatus(requestedStatus);

        return toResponse(enrollment);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @securityService.canViewEnrollment(#id)")
    public EnrollmentResponse getEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Enrollment not found")
        );

        return new EnrollmentResponse(enrollment.getId(),
                enrollment.getStudent().getId(), enrollment.getCourse().getId(), enrollment.getPrice(),
                enrollment.getEnrollmentDate(), enrollment.getStatus(), enrollment.getPaymentReference());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and @securityService.isStudentSelf(#studentId))")
    public List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found");
        }

        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(enrollment -> new EnrollmentResponse(
                        enrollment.getId(),
                        enrollment.getStudent().getId(),
                        enrollment.getCourse().getId(),
                        enrollment.getPrice(),
                        enrollment.getEnrollmentDate(),
                        enrollment.getStatus(),
                        enrollment.getPaymentReference()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollment -> new EnrollmentResponse(
                        enrollment.getId(),
                        enrollment.getStudent().getId(),
                        enrollment.getCourse().getId(),
                        enrollment.getPrice(),
                        enrollment.getEnrollmentDate(),
                        enrollment.getStatus(),
                        enrollment.getPaymentReference()
                ))
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and @securityService.isEnrollmentOwner(#id))")
    public EnrollmentResponse cancelEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Enrollment not found with id: " + id)
        );

        if (enrollment.getStatus() == EnrollmentStatus.CANCELED) {
            throw new ResourceConflictException("Enrollment is already canceled");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELED);

        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getCourse().getId(),
                enrollment.getPrice(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus(),
                enrollment.getPaymentReference()
        );
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getCourse().getId(),
                enrollment.getPrice(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus(),
                enrollment.getPaymentReference()
        );
    }
}
