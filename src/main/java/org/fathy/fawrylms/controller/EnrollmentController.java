package org.fathy.fawrylms.controller;

import jakarta.validation.Valid;
import org.fathy.fawrylms.dto.enrollment.CreateEnrollmentRequest;
import org.fathy.fawrylms.dto.enrollment.EnrollmentResponse;
import org.fathy.fawrylms.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        return enrollmentService.createEnrollment(request);
    }

    @GetMapping
    public List<EnrollmentResponse> getEnrollments(@RequestParam(required = false) Long studentId) {
        if (studentId != null) {
            return enrollmentService.getEnrollmentsByStudentId(studentId);
        }
        return enrollmentService.getAllEnrollments();
    }

    @GetMapping("/{id}")
    public EnrollmentResponse getEnrollment(@PathVariable Long id) {
        return enrollmentService.getEnrollment(id);
    }

    @PatchMapping("/{id}/cancel")
    public EnrollmentResponse cancelEnrollment(@PathVariable Long id) {
        return enrollmentService.cancelEnrollment(id);
    }
}
