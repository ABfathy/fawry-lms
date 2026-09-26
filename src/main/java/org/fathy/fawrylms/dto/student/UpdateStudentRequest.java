package org.fathy.fawrylms.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateStudentRequest(
        @NotBlank(message = "Name is required") String name,
        @Email @NotBlank(message = "Email is required") String email
) {
}
