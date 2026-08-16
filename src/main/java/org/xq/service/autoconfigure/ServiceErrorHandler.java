package org.xq.service.autoconfigure;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class ServiceErrorHandler {
    @ExceptionHandler(ServiceException.class)
    ProblemDetail serviceException(ServiceException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatus(), exception.getMessage());
        problem.setTitle("Service request failed");
        problem.setProperty("errorCode", exception.getErrorCode());
        CorrelationContext.currentId().ifPresent(id -> problem.setProperty("correlationId", id));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail infrastructureException(Exception exception) {
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Service infrastructure failed");
        problem.setDetail("The service could not complete the request.");
        problem.setProperty("errorCode", "INFRASTRUCTURE_ERROR");
        CorrelationContext.currentId().ifPresent(id -> problem.setProperty("correlationId", id));
        return problem;
    }
}
