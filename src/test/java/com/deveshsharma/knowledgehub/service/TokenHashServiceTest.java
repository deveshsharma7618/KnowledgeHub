package com.deveshsharma.knowledgehub.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TokenHashServiceTest {

    private final TokenHashService service = new TokenHashService();

    @Test
    void hashesSameTokenDeterministicallyWithoutReturningPlaintext() {
        String token = "access-token";

        String first = service.hash(token);
        String second = service.hash(token);

        assertEquals(first, second);
        assertNotEquals(token, first);
        assertEquals(64, first.length());
    }
}
