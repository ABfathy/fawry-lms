package org.fathy.fawrylms.dto.auth;

import org.fathy.fawrylms.types.Role;

public record AuthResponse(
        String token,
        Long userId,
        String email,
        Role role
) {}
