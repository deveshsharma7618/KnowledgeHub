package com.deveshsharma.knowledgehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
/**
 * Authentication tokens returned after signup or login.
 */
public class AuthResponse {
    String accessToken;
    String refreshToken;
}
