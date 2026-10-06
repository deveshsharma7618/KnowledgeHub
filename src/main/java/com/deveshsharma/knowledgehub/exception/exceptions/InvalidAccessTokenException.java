package com.deveshsharma.knowledgehub.exception.exceptions;

/**
 * Raised when an access token is missing, expired, malformed, or revoked.
 */
public class InvalidAccessTokenException extends RuntimeException {

    public InvalidAccessTokenException() {
        super("Access token is invalid or expired");
    }

    public InvalidAccessTokenException(Throwable cause) {
        super("Access token is invalid or expired", cause);
    }
}
