package org.fathy.fawrylms.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateCourseRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero") BigDecimal price,
        @NotNull(message = "Instructor ID is required") Long instructorId
) {
}
