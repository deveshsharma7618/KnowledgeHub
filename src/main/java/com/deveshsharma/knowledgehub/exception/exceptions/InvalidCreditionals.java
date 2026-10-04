package com.deveshsharma.knowledgehub.exception.exceptions;

public class InvalidCreditionals extends RuntimeException{
    public InvalidCreditionals(){
        super("Invalid email or password.");
    }
}