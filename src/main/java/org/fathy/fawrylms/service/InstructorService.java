package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.instructor.CreateInstructorRequest;
import org.fathy.fawrylms.dto.instructor.InstructorResponse;
import org.fathy.fawrylms.dto.instructor.UpdateInstructorRequest;
import org.fathy.fawrylms.entity.Instructor;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.CourseRepository;
import org.fathy.fawrylms.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    public InstructorService(InstructorRepository instructorRepository, CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public InstructorResponse createInstructor(CreateInstructorRequest createInstructorRequest) {
        if (instructorRepository.existsByEmail(createInstructorRequest.email())) {
            throw new ResourceConflictException("Instructor already exists");
        }

        Instructor instructor = instructorRepository.save(new Instructor(
                createInstructorRequest.name(),
                createInstructorRequest.email()
        ));

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }

    @Transactional(readOnly = true)
    public InstructorResponse getInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }

    @Transactional(readOnly = true)
    public List<InstructorResponse> getAllInstructors() {
        return instructorRepository.findAll().stream()
                .map(i -> new InstructorResponse(i.getId(), i.getName(), i.getEmail()))
                .toList();
    }

    @Transactional
    public InstructorResponse updateInstructor(Long id, UpdateInstructorRequest request) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        if (!instructor.getEmail().equalsIgnoreCase(request.email()) && instructorRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("Email already in use by another instructor");
        }

        instructor.setName(request.name());
        instructor.setEmail(request.email());

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }

    @Transactional
    public void deleteInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        if (courseRepository.existsByInstructorId(id)) {
            throw new ResourceConflictException("Cannot delete instructor assigned to active courses");
        }

        instructorRepository.delete(instructor);
    }
}
