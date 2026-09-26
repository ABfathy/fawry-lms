package org.fathy.fawrylms.service;

import org.fathy.fawrylms.dto.course.CourseResponse;
import org.fathy.fawrylms.dto.course.CreateCourseRequest;
import org.fathy.fawrylms.entity.Course;
import org.fathy.fawrylms.entity.Instructor;
import org.fathy.fawrylms.exception.ResourceConflictException;
import org.fathy.fawrylms.exception.ResourceNotFoundException;
import org.fathy.fawrylms.repository.CourseRepository;
import org.fathy.fawrylms.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public CourseService(CourseRepository courseRepository, InstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest createCourseRequest) {
        if(courseRepository.existsByCourseCode(createCourseRequest.courseCode())){
            throw new ResourceConflictException("Course code already exists");
        }

        Instructor instructor = instructorRepository.findById(createCourseRequest.instructorId()).orElseThrow(
                () -> new ResourceNotFoundException("Instructor not found")
        );

        Course course = courseRepository.save(new Course(createCourseRequest.courseCode()
                ,createCourseRequest.name(),createCourseRequest.description(),
                instructor,createCourseRequest.price()));
        return new CourseResponse(course.getId(), course.getCourseCode(), course.getName(),
                course.getDescription(), course.getPrice(), instructor.getId());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long id){
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course not found")
        );
        return new CourseResponse(course.getId(), course.getCourseCode(), course.getName(),
                course.getDescription(), course.getPrice(), course.getInstructor().getId());
    }
}
