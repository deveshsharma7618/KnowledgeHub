package com.deveshsharma.knowledgehub.exception.exceptions;

/**
 * Signals that supplied login credentials are invalid.
 */
public class InvalidCreditionals extends RuntimeException{
    /**
     * Creates an exception with the standard authentication failure message.
     */
    public InvalidCreditionals(){
        super("Invalid email or password.");
    }
}