package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;

public interface AuthService {
    public AuthResponse signup(SignUpRequest signUpRequest);
    public AuthResponse login(LoginRequest loginRequest);
    AuthResponse refresh(String refreshToken, String accessToken);
    void logout(String refreshToken, String accessToken);
}
