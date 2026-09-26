package org.fathy.fawrylms.controller;


import jakarta.validation.Valid;
import org.fathy.fawrylms.dto.course.CourseResponse;
import org.fathy.fawrylms.dto.course.CreateCourseRequest;
import org.fathy.fawrylms.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable Long id){
        return courseService.getCourse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest){
        return courseService.createCourse(createCourseRequest);
    }
}
