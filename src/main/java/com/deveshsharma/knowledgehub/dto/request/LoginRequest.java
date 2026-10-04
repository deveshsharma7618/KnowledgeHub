package com.deveshsharma.knowledgehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

/**
 * Credentials submitted to the login endpoint.
 */
@Getter
public class LoginRequest {
    @Email
    String email;

    @NotEmpty
    String password;
}
