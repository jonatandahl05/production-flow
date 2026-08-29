package se.jonatandahl.productionflow.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(
            ResourceNotFoundException exception
    ) {
        return createProblem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateJobNumberException.class)
    public ProblemDetail handleDuplicateJobNumber(
            DuplicateJobNumberException exception
    ) {
        return createProblem(
                HttpStatus.CONFLICT,
                "Duplicate job number",
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(
            IllegalStateException exception
    ) {
        return createProblem(
                HttpStatus.CONFLICT,
                "Invalid production state",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDatabaseConstraint(
            DataIntegrityViolationException exception
    ) {
        return createProblem(
                HttpStatus.CONFLICT,
                "Database constraint violation",
                "The submitted data conflicts with existing data"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more fields are invalid"
        );

        problem.setProperty(
                "validationErrors",
                validationErrors
        );

        return problem;
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        status,
                        detail
                );

        problem.setTitle(title);
        return problem;
    }
}