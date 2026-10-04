package com.deveshsharma.knowledgehub.controller;

import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication end points", description = "Endpoints for managing authenticate users")
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
    @Operation(summary = "Create account", description = "Creates account using name, email and password.")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @ApiResponse(responseCode = "409", description = "User already exists")
    public ResponseEntity<AuthResponse> signup(
            @Valid
            @RequestBody SignUpRequest signUpRequest
            ) {
        return new ResponseEntity<>(authService.signup(signUpRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest);
    }

}
