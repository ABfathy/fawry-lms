package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.student.CreateStudentRequest;
import org.fathy.fawrylms.dto.student.StudentResponse;
import org.fathy.fawrylms.entity.Student;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {

        if (studentRepository.existsByEmail(request.email())){
            throw new ResourceConflictException("Email already exists");
        };

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
}
