package org.fathy.fawrylms.dto.instructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateInstructorRequest(
        @NotBlank(message = "Name is required") String name,
        @Email @NotBlank(message = "Email is required") String email
) {
}
