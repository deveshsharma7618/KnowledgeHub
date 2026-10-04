package com.deveshsharma.knowledgehub.exception;

import com.deveshsharma.knowledgehub.dto.response.ErrorResponse;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidCreditionals;
import com.deveshsharma.knowledgehub.exception.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                "User already exists",
                HttpStatus.CONFLICT.value(),
                "User with this email already exists"
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCreditionals.class)
    public ResponseEntity<ErrorResponse> handleNotFound(InvalidCreditionals ex) {
        ErrorResponse error = new ErrorResponse("Invalid Creditionals", HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        for( FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Invalid field", HttpStatus.BAD_REQUEST.value(), message.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}