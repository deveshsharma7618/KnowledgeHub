package com.deveshsharma.knowledgehub.security;

import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
/**
 * Loads users from the application repository for Spring Security.
 */
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Creates a user-details service backed by the user repository.
     *
     * @param userRepository user persistence gateway
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NonNull
    /**
     * Loads a user by email and adapts it to Spring Security.
     *
     * @param username email supplied by the authentication flow
     * @return Spring Security user details
     * @throws UsernameNotFoundException when the email is not present
     */
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new CustomUserDetails(user);
    }
}