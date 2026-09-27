package org.fathy.fawrylms.dto.instructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInstructorRequest(
        @NotBlank(message = "Name is Required") String name,

        @Email @NotBlank(message = "Email is Required") String email,

        @NotBlank(message = "Password is Required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password) {
}
