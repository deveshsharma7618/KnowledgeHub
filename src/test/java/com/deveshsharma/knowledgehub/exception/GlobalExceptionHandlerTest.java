package com.deveshsharma.knowledgehub.exception;

import com.deveshsharma.knowledgehub.exception.exceptions.InvalidAccessTokenException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void mapsInvalidAccessTokenToUnauthorizedErrorResponse() {
        var response = new GlobalExceptionHandler()
                .handleInvalidAccessToken(new InvalidAccessTokenException());

        assertEquals(401, response.getStatusCode().value());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Invalid access token", response.getBody().getTitle());
    }
}
