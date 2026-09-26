package org.fathy.fawrylms.controller;

import jakarta.validation.Valid;
import org.fathy.fawrylms.dto.instructor.CreateInstructorRequest;
import org.fathy.fawrylms.dto.instructor.InstructorResponse;
import org.fathy.fawrylms.dto.instructor.UpdateInstructorRequest;
import org.fathy.fawrylms.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstructorResponse createInstructor(@Valid @RequestBody CreateInstructorRequest request) {
        return instructorService.createInstructor(request);
    }

    @GetMapping("/{id}")
    public InstructorResponse getInstructor(@PathVariable Long id) {
        return instructorService.getInstructor(id);
    }

    @GetMapping
    public List<InstructorResponse> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @PutMapping("/{id}")
    public InstructorResponse updateInstructor(@PathVariable Long id, @Valid @RequestBody UpdateInstructorRequest request) {
        return instructorService.updateInstructor(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
    }
}
