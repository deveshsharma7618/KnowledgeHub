package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Persistence tests for {@link UserRepository}.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Verifies that a user can be persisted and found by its unique email.
     */
    @Test
    void findByEmailReturnsPersistedUser() {
        User saved = userRepository.save(User.builder()
                .email("repository@example.com")
                .passwordHash("hash")
                .name("Repository User")
                .status(UserStatus.ACTIVE)
                .build());

        User found = userRepository.findByEmail("repository@example.com");

        assertNotNull(saved.getId());
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Repository User", found.getName());
    }
}
