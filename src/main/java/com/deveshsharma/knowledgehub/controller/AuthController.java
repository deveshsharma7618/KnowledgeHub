package com.deveshsharma.knowledgehub.controller;

import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;
import com.deveshsharma.knowledgehub.dto.response.ErrorResponse;
import com.deveshsharma.knowledgehub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for user registration, authentication, and user listing.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication end points", description = "Endpoints for managing authenticated users")
public class AuthController {

    private final AuthService authService;

    /**
     * Creates the authentication controller.
     *
     * @param authService authentication use-case service
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user and returns access and refresh tokens.
     *
     * @param signUpRequest validated registration details
     * @return a {@code 201 Created} response containing authentication tokens
     */
    @PostMapping("/signup")
    @Operation(summary = "Create account", description = "Creates account using name, email and password.")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "409", description = "User already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements
    public ResponseEntity<AuthResponse> signup(
            @Valid
            @RequestBody SignUpRequest signUpRequest
            ) {
        return new ResponseEntity<>(authService.signup(signUpRequest), HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and returns access and refresh tokens.
     *
     * @param loginRequest login credentials
     * @return authentication tokens
     */
    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates credentials and rotates the user's refresh token.")
    @ApiResponse(responseCode = "200", description = "Authentication tokens returned")
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements
    public AuthResponse login(
            @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest);
    }

}
