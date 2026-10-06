package com.deveshsharma.knowledgehub.controller;

import com.deveshsharma.knowledgehub.dto.request.UpdateUserRequest;
import com.deveshsharma.knowledgehub.dto.response.UserResponse;
import com.deveshsharma.knowledgehub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        return userService.find(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(userId, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }
}
