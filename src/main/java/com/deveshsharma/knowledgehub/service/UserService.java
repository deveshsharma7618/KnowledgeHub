package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.dto.request.UpdateUserRequest;
import com.deveshsharma.knowledgehub.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse find(UUID userId);
    UserResponse update(UUID userId, UpdateUserRequest request);
    void delete(UUID userId);
}
