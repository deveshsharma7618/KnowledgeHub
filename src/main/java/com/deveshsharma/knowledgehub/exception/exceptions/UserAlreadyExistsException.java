package com.deveshsharma.knowledgehub.exception.exceptions;

/**
 * Signals that registration was attempted with an existing email address.
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Creates an exception with the standard duplicate-user message.
     */
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
