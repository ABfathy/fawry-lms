package org.fathy.fawrylms.security;

import org.fathy.fawrylms.repository.CourseRepository;
import org.fathy.fawrylms.repository.EnrollmentRepository;
import org.fathy.fawrylms.repository.InstructorRepository;
import org.fathy.fawrylms.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
public class SecurityService {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public SecurityService(
            StudentRepository studentRepository,
            InstructorRepository instructorRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    public boolean isAdmin() {
        return isAdmin(currentAuthentication());
    }

    public boolean isStudentSelf(Long studentId) {
        return isStudentSelf(studentId, currentAuthentication());
    }

    public boolean isStudentSelf(Long studentId, Authentication authentication) {
        if (studentId == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        return userId(authentication)
                .map(id -> studentRepository.existsByIdAndUserId(studentId, id))
                .orElse(false);
    }

    public boolean isStudentOwner(Long studentId) {
        return isStudentSelf(studentId);
    }

    public boolean isStudentOwner(Long studentId, Authentication authentication) {
        return isStudentSelf(studentId, authentication);
    }

    public boolean isInstructorSelf(Long instructorId) {
        return isInstructorSelf(instructorId, currentAuthentication());
    }

    public boolean isInstructorSelf(Long instructorId, Authentication authentication) {
        if (instructorId == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        return userId(authentication)
                .map(id -> instructorRepository.existsByIdAndUserId(instructorId, id))
                .orElse(false);
    }

    public boolean isInstructorOwner(Long instructorId) {
        return isInstructorSelf(instructorId);
    }

    public boolean isInstructorOwner(Long instructorId, Authentication authentication) {
        return isInstructorSelf(instructorId, authentication);
    }

    public boolean isCourseOwner(Long courseId) {
        return isCourseOwner(courseId, currentAuthentication());
    }

    public boolean isCourseOwner(Long courseId, Authentication authentication) {
        if (courseId == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        return userId(authentication)
                .map(id -> courseRepository.existsByIdAndInstructorUserId(courseId, id))
                .orElse(false);
    }

    public boolean isEnrollmentOwner(Long enrollmentId) {
        return isEnrollmentOwner(enrollmentId, currentAuthentication());
    }

    public boolean isEnrollmentOwner(Long enrollmentId, Authentication authentication) {
        if (enrollmentId == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        return userId(authentication)
                .map(id -> enrollmentRepository.existsByIdAndStudentUserId(enrollmentId, id))
                .orElse(false);
    }

    public boolean canViewEnrollment(Long enrollmentId) {
        return canViewEnrollment(enrollmentId, currentAuthentication());
    }

    public boolean canViewEnrollment(Long enrollmentId, Authentication authentication) {
        if (enrollmentId == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        return userId(authentication)
                .map(id -> enrollmentRepository.existsByIdAndStudentUserId(enrollmentId, id)
                        || enrollmentRepository.existsByIdAndCourseInstructorUserId(enrollmentId, id))
                .orElse(false);
    }

    private Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Optional<Long> userId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(authentication.getName()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
