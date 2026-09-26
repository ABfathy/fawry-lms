package org.fathy.fawrylms.service;


import org.fathy.fawrylms.dto.instructor.CreateInstructorRequest;
import org.fathy.fawrylms.dto.instructor.InstructorResponse;
import org.fathy.fawrylms.entity.Instructor;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository){
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public InstructorResponse createInstructor(CreateInstructorRequest createInstructorRequest){

        if(instructorRepository.existsByEmail(createInstructorRequest.email())){
            throw new ResourceConflictException("Instructor already exists");
        }

        Instructor instructor = instructorRepository.save(new Instructor(createInstructorRequest.name(),
                createInstructorRequest.email()));

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }


    @Transactional(readOnly = true)
    public InstructorResponse getInstructor(Long id){
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail());
    }

}
