package com.deveshsharma.knowledgehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Authentication tokens returned after signup or login.
 */
@AllArgsConstructor
@Getter
public class AuthResponse {
    String accessToken;
    String refreshToken;
}
