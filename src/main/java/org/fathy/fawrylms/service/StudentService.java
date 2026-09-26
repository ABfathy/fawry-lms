package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.student.CreateStudentRequest;
import org.fathy.fawrylms.dto.student.StudentResponse;
import org.fathy.fawrylms.dto.student.UpdateStudentRequest;
import org.fathy.fawrylms.entity.Student;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.EnrollmentRepository;
import org.fathy.fawrylms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("Email already exists");
        }

        Student student = studentRepository.save(new Student(request.name(), request.email()));
        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Student not found with id: " + id)
        );
        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getEmail()))
                .toList();
    }

    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Student not found with id: " + id)
        );

        if (!student.getEmail().equalsIgnoreCase(request.email()) && studentRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("Email already in use by another student");
        }

        student.setName(request.name());
        student.setEmail(request.email());

        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Student not found with id: " + id)
        );

        if (enrollmentRepository.existsByStudentId(id)) {
            throw new ResourceConflictException("Cannot delete student with active course enrollments");
        }

        studentRepository.delete(student);
    }
}
