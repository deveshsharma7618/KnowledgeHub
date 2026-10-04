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
/**
 * Converts application and validation exceptions into consistent API errors.
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    /**
     * Handles attempts to register an existing email address.
     *
     * @param ex the registration exception
     * @return conflict error response
     */
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                "User already exists",
                HttpStatus.CONFLICT.value(),
                "User with this email already exists"
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCreditionals.class)
    /**
     * Handles failed authentication attempts.
     *
     * @param ex the authentication exception
     * @return unauthorized error response
     */
    public ResponseEntity<ErrorResponse> handleNotFound(InvalidCreditionals ex) {
        ErrorResponse error = new ErrorResponse("Invalid Creditionals", HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    /**
     * Handles bean validation failures on request bodies.
     *
     * @param ex validation exception containing field errors
     * @return bad-request error response
     */
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        for( FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Invalid field", HttpStatus.BAD_REQUEST.value(), message.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    /**
     * Handles unexpected application failures.
     *
     * @param ex unexpected exception
     * @return internal-server-error response
     */
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}