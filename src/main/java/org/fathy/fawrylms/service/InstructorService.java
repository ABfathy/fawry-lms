package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.instructor.CreateInstructorRequest;
import org.fathy.fawrylms.dto.instructor.InstructorResponse;
import org.fathy.fawrylms.dto.instructor.UpdateInstructorRequest;
import org.fathy.fawrylms.entity.Instructor;
import org.fathy.fawrylms.entity.User;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.CourseRepository;
import org.fathy.fawrylms.repository.InstructorRepository;
import org.fathy.fawrylms.repository.UserRepository;
import org.fathy.fawrylms.types.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InstructorService(
            InstructorRepository instructorRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public InstructorResponse createInstructor(CreateInstructorRequest createInstructorRequest) {
        String email = normalizeEmail(createInstructorRequest.email());
        if (instructorRepository.existsByEmail(email) || userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Instructor already exists");
        }

        User user = userRepository.save(new User(
                email,
                passwordEncoder.encode(createInstructorRequest.password()),
                Role.INSTRUCTOR
        ));
        Instructor instructor = instructorRepository.save(new Instructor(
                createInstructorRequest.name(),
                email,
                user
        ));

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public InstructorResponse getInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<InstructorResponse> getAllInstructors() {
        return instructorRepository.findAll().stream()
                .map(i -> new InstructorResponse(i.getId(), i.getName(), i.getEmail()))
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('INSTRUCTOR') and @securityService.isInstructorOwner(#id))")
    public InstructorResponse updateInstructor(Long id, UpdateInstructorRequest request) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        String email = normalizeEmail(request.email());
        if (!instructor.getEmail().equals(email) && instructorRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email already in use by another instructor");
        }
        if (instructor.getUser() != null
                && userRepository.existsByEmailAndIdNot(email, instructor.getUser().getId())) {
            throw new ResourceConflictException("Email already in use by another account");
        }

        instructor.setName(request.name());
        instructor.setEmail(email);
        if (instructor.getUser() != null) {
            instructor.getUser().setEmail(email);
        }

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        if (courseRepository.existsByInstructorId(id)) {
            throw new ResourceConflictException("Cannot delete instructor assigned to active courses");
        }

        var user = instructor.getUser();
        instructorRepository.delete(instructor);
        instructorRepository.flush();
        if (user != null) {
            userRepository.delete(user);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
