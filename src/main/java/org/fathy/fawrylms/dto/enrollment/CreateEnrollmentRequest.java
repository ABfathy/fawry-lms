package org.fathy.fawrylms.dto.enrollment;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(@NotNull Long studentId, @NotNull Long courseId) {
}
