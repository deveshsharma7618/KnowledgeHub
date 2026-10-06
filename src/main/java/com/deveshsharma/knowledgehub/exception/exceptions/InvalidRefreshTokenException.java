package com.deveshsharma.knowledgehub.exception.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid, expired, or revoked");
    }

    public InvalidRefreshTokenException(Throwable cause) {
        super("Refresh token is invalid, expired, or revoked", cause);
    }
}
