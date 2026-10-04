package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Persistence operations for application users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by the unique email address.
     *
     * @param email email address to search for
     * @return the matching user, or {@code null} when no user exists
     */
    User findByEmail(String email);
}
