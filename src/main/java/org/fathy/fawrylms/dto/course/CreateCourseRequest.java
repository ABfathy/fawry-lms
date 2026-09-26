package org.fathy.fawrylms.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateCourseRequest(
        @NotBlank String courseCode,
        @NotBlank String name, @NotBlank String description,
        @NotNull @Positive(message = "Price must be greater than zero") BigDecimal price,
        @NotNull(message = "Instructor ID is required") Long instructorId) {
}
