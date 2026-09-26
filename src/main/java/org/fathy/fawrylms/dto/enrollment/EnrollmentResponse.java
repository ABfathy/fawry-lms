package org.fathy.fawrylms.dto.enrollment;

import org.fathy.fawrylms.types.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EnrollmentResponse(Long id, Long studentId, Long courseId, BigDecimal price, LocalDateTime enrollmentDate,
                                 EnrollmentStatus enrollmentStatus, String paymentReference) {
}
