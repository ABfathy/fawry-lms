package org.fathy.fawrylms.dto.instructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInstructorRequest(
        @NotBlank(message = "Name is Required") String name,

        @Email @NotBlank(message = "Email is Required") String email) {
}
