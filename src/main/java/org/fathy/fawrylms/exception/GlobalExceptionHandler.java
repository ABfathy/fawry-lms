package org.fathy.fawrylms.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler
    public ProblemDetail handleResourceConflictException(ResourceConflictException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,e.getMessage());
        problem.setTitle("Resource Conflict");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler
    public ProblemDetail handleIncorrectPaymentStatus(IncorrectPaymentStatusException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
        problem.setTitle("Incorrect Payment Status");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Validation Error");
        problem.setProperty("timestamp", Instant.now());

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Database constraint violation occurred");
        problem.setTitle("Data Integrity Violation");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
