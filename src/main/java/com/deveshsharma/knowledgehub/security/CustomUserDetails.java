package com.deveshsharma.knowledgehub.security;

import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

/**
 * Adapts a domain user to Spring Security's user-details contract.
 *
 * @param user domain user entity
 */
public record CustomUserDetails(User user) implements UserDetails {

    /**
     * Returns the single application role assigned to authenticated users.
     *
     * @return collection containing {@code ROLE_USER}
     */
    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    /**
     * Returns the password value exposed to Spring Security.
     *
     * @return an empty value because password verification occurs in the service
     */
    @Override
    public String getPassword() {
        return "";
    }

    /**
     * Returns the user's unique login identifier.
     *
     * @return user email
     */
    @Override
    @NonNull
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Reports whether the account status permits authentication.
     *
     * @return {@code true} only for active users
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    /**
     * Reports whether the account is not locked.
     *
     * @return {@code true} only for active users
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    /**
     * Reports whether the account is enabled.
     *
     * @return {@code true} only for active users
     */
    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}
