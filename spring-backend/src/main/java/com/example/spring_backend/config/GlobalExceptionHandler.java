package com.example.spring_backend.config;

import com.example.spring_backend.auth.custom_exceptions.UserAlreadyRegistered;
import com.example.spring_backend.book_related.book.custom_exceptions.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserAlreadyRegistered.class,
            UsernameNotFoundException.class,
    })
    public ResponseEntity<?> handleAuthError()
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse<>("Unauthorized"));
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
            BookNotFoundException.class,
    })
    public ResponseEntity<?> handleNotFoundEntity(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse<>(ex.getMessage()));
    }
}
