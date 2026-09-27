package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.student.CreateStudentRequest;
import org.fathy.fawrylms.dto.student.StudentResponse;
import org.fathy.fawrylms.dto.student.UpdateStudentRequest;
import org.fathy.fawrylms.entity.Student;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.EnrollmentRepository;
import org.fathy.fawrylms.repository.StudentRepository;
import org.fathy.fawrylms.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public StudentService(
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository
    ) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse createStudent(CreateStudentRequest request) {
        String email = normalizeEmail(request.email());
        if (studentRepository.existsByEmail(email) || userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email already exists");
        }

        Student student = studentRepository.save(new Student(request.name(), email));
        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and @securityService.isStudentOwner(#id))")
    public StudentResponse getStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Student not found with id: " + id)
        );
        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getEmail()))
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and @securityService.isStudentOwner(#id))")
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Student not found with id: " + id)
        );

        String email = normalizeEmail(request.email());
        if (!student.getEmail().equals(email) && studentRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email already in use by another student");
        }
        if (student.getUser() != null
                && userRepository.existsByEmailAndIdNot(email, student.getUser().getId())) {
            throw new ResourceConflictException("Email already in use by another account");
        }

        student.setName(request.name());
        student.setEmail(email);
        if (student.getUser() != null) {
            student.getUser().setEmail(email);
        }

        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Student not found with id: " + id)
        );

        if (enrollmentRepository.existsByStudentId(id)) {
            throw new ResourceConflictException("Cannot delete student with active course enrollments");
        }

        var user = student.getUser();
        studentRepository.delete(student);
        studentRepository.flush();
        if (user != null) {
            userRepository.delete(user);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
