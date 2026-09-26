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


    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }


    @Transactional
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

        Enrollment enrollment = enrollmentRepository.save( new Enrollment(student,course,course.getPrice(),paymentReference,
                EnrollmentStatus.PENDING_PAYMENT, LocalDateTime.now()));


        return new EnrollmentResponse(enrollment.getId(), student.getId(), course.getId(),
                course.getPrice(), enrollment.getEnrollmentDate(),enrollment.getStatus(), paymentReference);

    }

    @Transactional
    public EnrollmentResponse processPaymentWebHook(PaymentWebhookRequest paymentWebhookRequest) {
        Enrollment enrollment = enrollmentRepository.findByPaymentReference(paymentWebhookRequest
                .paymentReference()).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        if (paymentWebhookRequest.status().equalsIgnoreCase("PAID")){
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
        } else if(paymentWebhookRequest.status().equalsIgnoreCase("FAILED")){
            enrollment.setStatus(EnrollmentStatus.CANCELED);
        } else {
            throw new IncorrectPaymentStatusException("Given Payment status is incorrect");
        }

        return new EnrollmentResponse(enrollment.getId(),
                enrollment.getStudent().getId(), enrollment.getCourse().getId(), enrollment.getPrice(),
                enrollment.getEnrollmentDate(), enrollment.getStatus(), enrollment.getPaymentReference());
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Enrollment not found")
        );

        return new EnrollmentResponse(enrollment.getId(),
                enrollment.getStudent().getId(), enrollment.getCourse().getId(), enrollment.getPrice(),
                enrollment.getEnrollmentDate(), enrollment.getStatus(), enrollment.getPaymentReference());
    }

    @Transactional(readOnly = true)
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
}
