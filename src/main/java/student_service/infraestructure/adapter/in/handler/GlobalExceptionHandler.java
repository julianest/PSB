package student_service.infraestructure.adapter.in.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import student_service.domain.exception.StudentAlreadyExistsException;
import student_service.infraestructure.adapter.in.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleStudentAlreadyExists(StudentAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflicto",ex.getMessage(),null);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidation(WebExchangeBindException ex) {
        List<String> details = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST,"Error de validación","Los datos del alumno no son validos.",details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST,"Datos inválidos",ex.getMessage(),null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Error interno","Error inesperado. Intenta nuevamente o contacta al administrador.",null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,String errorType,String message,List<String> details) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorType)
                .message(message)
                .details(details)
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}
