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
 * @param user Domain Entity
 */
public record CustomUserDetails(User user) implements UserDetails {

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    @NonNull
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}
