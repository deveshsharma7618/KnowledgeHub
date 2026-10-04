package com.deveshsharma.knowledgehub.controller;

import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public List<User> index() {
        return authService.getAllUser();
    }

    @PostMapping("/signup")
    public AuthResponse signup(
            @Valid
            @RequestBody SignUpRequest signUpRequest
            ) {
        return authService.signup(signUpRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest);
    }

}
