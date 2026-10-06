package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;
import com.deveshsharma.knowledgehub.entity.RefreshToken;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidCreditionals;
import com.deveshsharma.knowledgehub.exception.exceptions.UserAlreadyExistsException;
import com.deveshsharma.knowledgehub.repository.RefreshTokenRepository;
import com.deveshsharma.knowledgehub.repository.UserRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.service.AuthService;
import com.deveshsharma.knowledgehub.service.JwtTokenService;
import com.deveshsharma.knowledgehub.service.TokenHashService;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.time.Duration;
import java.time.Instant;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Implements user registration, login, and user listing use cases.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtService;
    private final TokenHashService tokenHashService;
    private final StringRedisTemplate redisTemplate;

    /**
     * Creates the authentication service.
     *
     * @param userRepository user persistence gateway
     * @param passwordEncoder password hashing and verification component
     * @param redisTemplate Redis template for adding data in cache
     * @param jwtService JWT issuance component
     */
    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtService,
                           TokenHashService tokenHashService,
                           StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenHashService = tokenHashService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Registers a user, persists the account, and issues both JWTs.
     *
     * @param signUpRequest registration details
     * @return access and refresh tokens
     * @throws UserAlreadyExistsException when the email is already registered
     */
    @Transactional
    @Override
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

        newUser = userRepository.save(newUser);

        return getAuthResponse(newUser);
    }

    /**
     * Authenticates credentials and issues both JWTs.
     *
     * @param loginRequest login credentials
     * @return access and refresh tokens
     * @throws InvalidCreditionals when the user does not exist or the password is incorrect
     */
    @Transactional
    @Override
    public AuthResponse login(LoginRequest loginRequest)
    {
        User savedUser = userRepository.findByEmail(loginRequest.getEmail());
        if(savedUser == null){
            throw new InvalidCreditionals();
        }else{
            if( passwordEncoder.matches(loginRequest.getPassword(), savedUser.getPasswordHash()) ){
                return getAuthResponse(savedUser);
            }
        }
        throw new InvalidCreditionals();
    }

    /**
     * It returns refresh token and access token in AuthResponse
     * @param savedUser {@link User}
     * @return {@link AuthResponse}
     */
    @NonNull
    private AuthResponse getAuthResponse(User savedUser) {
        Map<String, Object> extraClaims = new HashMap<>();
        CustomUserDetails customUserDetails = new CustomUserDetails(savedUser);

        String accessToken = jwtService.generateAccessToken(extraClaims, customUserDetails );
        String refreshToken = jwtService.generateRefreshToken(extraClaims, customUserDetails );

        Instant now = Instant.now();
        refreshTokenRepository.revokeActiveTokens(savedUser.getId(), now);
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(savedUser.getId())
                .tokenHash(tokenHashService.hash(refreshToken))
                .expiresAt(jwtService.extractExpiration(refreshToken))
                .build());

        Instant accessExpiresAt = jwtService.extractExpiration(accessToken);
        Duration accessTokenTtl = Duration.between(now, accessExpiresAt);
        redisTemplate.opsForValue().set(
                "access-token:" + tokenHashService.hash(accessToken),
                savedUser.getEmail(),
                accessTokenTtl
        );

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

}
