package org.fathy.fawrylms.controller;


import jakarta.validation.Valid;
import org.fathy.fawrylms.dto.instructor.CreateInstructorRequest;
import org.fathy.fawrylms.dto.instructor.InstructorResponse;
import org.fathy.fawrylms.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/{id}")
    public InstructorResponse getInstructor(@PathVariable Long id){
        return instructorService.getInstructor(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstructorResponse createInstructor(@Valid @RequestBody CreateInstructorRequest request){
        return instructorService.createInstructor(request);
    }
}
