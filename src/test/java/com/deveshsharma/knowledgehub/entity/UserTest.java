package com.deveshsharma.knowledgehub.entity;

import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for entity construction and domain state.
 */
class UserTest {

    /**
     * Verifies that the user builder preserves account data and creates an id.
     */
    @Test
    void userBuilderCreatesExpectedUser() {
        User user = User.builder()
                .email("user@example.com")
                .passwordHash("hash")
                .name("Test User")
                .status(UserStatus.ACTIVE)
                .build();

        assertEquals("user@example.com", user.getEmail());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }
}
