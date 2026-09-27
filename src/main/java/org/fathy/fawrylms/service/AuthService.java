package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.auth.AuthResponse;
import org.fathy.fawrylms.dto.auth.LoginRequest;
import org.fathy.fawrylms.dto.auth.RegisterRequest;
import org.fathy.fawrylms.entity.Student;
import org.fathy.fawrylms.entity.User;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.StudentRepository;
import org.fathy.fawrylms.repository.UserRepository;
import org.fathy.fawrylms.security.JwtService;
import org.fathy.fawrylms.types.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email is already registered");
        }

        Student student = studentRepository.findByEmail(email)
                .orElseGet(() -> new Student(request.name().trim(), email));
        if (student.getUser() != null) {
            throw new ResourceConflictException("Email is already registered");
        }

        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                Role.STUDENT
        );
        User savedUser = userRepository.save(user);

        student.setUser(savedUser);
        studentRepository.save(student);

        String jwt = jwtService.generateToken(savedUser);
        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt, user.getId(), user.getEmail(), user.getRole());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
