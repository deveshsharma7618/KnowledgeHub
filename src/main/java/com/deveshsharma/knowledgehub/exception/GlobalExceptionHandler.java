package com.deveshsharma.knowledgehub.exception;

import com.deveshsharma.knowledgehub.dto.response.ErrorResponse;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidCreditionals;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidAccessTokenException;
import com.deveshsharma.knowledgehub.exception.exceptions.UserAlreadyExistsException;
import com.deveshsharma.knowledgehub.exception.exceptions.ResourceNotFoundException;
import com.deveshsharma.knowledgehub.exception.exceptions.ForbiddenOperationException;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidRefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts application and validation exceptions into consistent API errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccessToken(InvalidAccessTokenException ex) {
        ErrorResponse error = new ErrorResponse(
                "Invalid access token",
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles attempts to register an existing email address.
     *
     * @param ex the registration exception
     * @return conflict error response
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                "User with this email already exists"
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handles failed authentication attempts.
     *
     * @param ex the authentication exception
     * @return unauthorized error response
     */
    @ExceptionHandler(InvalidCreditionals.class)
    public ResponseEntity<ErrorResponse> handleNotFound(InvalidCreditionals ex) {
        ErrorResponse error = new ErrorResponse("Invalid Creditionals", HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles bean validation failures on request bodies.
     *
     * @param ex validation exception containing field errors
     * @return bad-request error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        for( FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Invalid field", HttpStatus.BAD_REQUEST.value(), message.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles unexpected application failures.
     *
     * @param ex unexpected exception
     * @return internal-server-error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Not found", HttpStatus.NOT_FOUND.value(), ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenOperationException ex) {
        return new ResponseEntity<>(new ErrorResponse("Forbidden", HttpStatus.FORBIDDEN.value(), ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ErrorResponse("Invalid request", HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return new ResponseEntity<>(new ErrorResponse("Invalid refresh token", HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }


}