package org.fathy.fawrylms.entity;


import jakarta.persistence.*;
import org.fathy.fawrylms.types.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollment_student_course", columnNames = {"student_id", "course_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(nullable = false)
    private LocalDateTime enrollmentDate;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false,unique = true)
    private String paymentReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    protected Enrollment() {}

    public Enrollment(Student student, Course course, BigDecimal price, String paymentReference,
                      EnrollmentStatus status, LocalDateTime enrollmentDate) {
        this.student = student;
        this.course = course;
        this.price = price;
        this.paymentReference = paymentReference;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
