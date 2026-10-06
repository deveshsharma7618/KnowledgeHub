package com.deveshsharma.knowledgehub.exception.exceptions;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException() {
        super("You do not have permission to perform this operation");
    }
}
