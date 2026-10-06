package com.deveshsharma.knowledgehub.dto.response;

import com.deveshsharma.knowledgehub.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String name, String email, String status, Instant createdAt, Instant updatedAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getStatus().name(),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}
