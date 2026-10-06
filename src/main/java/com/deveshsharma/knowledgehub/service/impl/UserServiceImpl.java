package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.dto.request.UpdateUserRequest;
import com.deveshsharma.knowledgehub.dto.response.UserResponse;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import com.deveshsharma.knowledgehub.exception.exceptions.ResourceNotFoundException;
import com.deveshsharma.knowledgehub.repository.UserRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse find(UUID userId) {
        return UserResponse.from(ownedUser(userId));
    }

    @Override
    @Transactional
    public UserResponse update(UUID userId, UpdateUserRequest request) {
        User user = ownedUser(userId);
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = ownedUser(userId);
        user.setStatus(UserStatus.DELETED);
    }

    private User ownedUser(UUID userId) {
        UUID currentUserId = currentUserId();
        if (!currentUserId.equals(userId)) {
            throw new ResourceNotFoundException("User");
        }
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User"));
    }

    private UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new ResourceNotFoundException("Authenticated user");
        }
        return details.getUserId();
    }
}
