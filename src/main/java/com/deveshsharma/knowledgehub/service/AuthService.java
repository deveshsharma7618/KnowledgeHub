package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidCreditionals;
import com.deveshsharma.knowledgehub.exception.exceptions.UserAlreadyExistsException;
import com.deveshsharma.knowledgehub.repository.UserRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse signup(SignUpRequest signUpRequest) {

        User savedUser = userRepository.findByEmail(signUpRequest.getEmail());
        if (savedUser != null) {
            throw new UserAlreadyExistsException();
        }

        CharSequence rawPassword = signUpRequest.getPassword();
        String passwordHash = passwordEncoder.encode(rawPassword);
        User newUser =  User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .passwordHash(passwordHash)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(newUser);

        Map<String, Object> extraClaims = new HashMap<>();
        CustomUserDetails  customUserDetails = new CustomUserDetails(newUser);

        String accessToken = jwtService.generateAccessToken(extraClaims, customUserDetails );
        String refreshToken = jwtService.generateRefreshToken(extraClaims, customUserDetails );

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

    public AuthResponse login(LoginRequest loginRequest)
    {
        User savedUser = userRepository.findByEmail(loginRequest.getEmail());
        if(savedUser == null){
            throw new InvalidCreditionals();
        }else{
            if( passwordEncoder.matches(loginRequest.getPassword(), savedUser.getPasswordHash()) ){
                Map<String, Object> extraClaims = new HashMap<>();
                CustomUserDetails  customUserDetails = new CustomUserDetails(savedUser);

                String accessToken = jwtService.generateAccessToken(extraClaims, customUserDetails );
                String refreshToken = jwtService.generateRefreshToken(extraClaims, customUserDetails );

                return new AuthResponse(
                        accessToken,
                        refreshToken
                );
            }
        }
        throw new InvalidCreditionals();
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
