package com.example.spring_backend.config;

import com.example.spring_backend.config.custom_exceptions.AuthErrorException;
import com.example.spring_backend.config.custom_exceptions.EntityNotFoundException;
import com.example.spring_backend.user.custom_exception.UserBookListException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AuthErrorException.class,
    })
    public ResponseEntity<?> handleAuthError(AuthErrorException ex)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse<>(ex.getMessage()));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage()
                ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse<>(errors));
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
    })
    public ResponseEntity<?> handleNotFoundEntity(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse<>(ex.getMessage()));
    }

    @ExceptionHandler({
            UserBookListException.class
    })
    public ResponseEntity<?> handleExistingBookInUserList(UserBookListException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse<>(ex.getMessage()));
    }

}
