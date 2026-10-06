package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.constants.JwtConstants;
import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;
import com.deveshsharma.knowledgehub.entity.RefreshToken;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidCreditionals;
import com.deveshsharma.knowledgehub.exception.exceptions.UserAlreadyExistsException;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidRefreshTokenException;
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
 * Implements user registration, login, and refresh, logout use cases.
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
     * @param refreshTokenRepository refresh token persistence gateway
     * @param passwordEncoder password hashing and verification component
     * @param redisTemplate Redis template for adding data in cache
     * @param jwtService JWT issuance component
     * @param tokenHashService for hashing token
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

        return issueTokens(newUser);
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
                return issueTokens(savedUser);
            }
        }
        throw new InvalidCreditionals();
    }

    @Transactional
    @Override
    public AuthResponse refresh(String refreshToken) {
        User user = validateRefreshToken(refreshToken);
        revokeRefreshToken(refreshToken);
        redisTemplate.delete(JwtConstants.ACCESS_TOKEN + ":" + user.getEmail());
        return issueTokens(user);
    }

    @Transactional
    @Override
    public void logout(String refreshToken) {
        revokeRefreshToken(refreshToken);
        redisTemplate.delete(JwtConstants.ACCESS_TOKEN + ":" + jwtService.extractUsername(refreshToken));
    }

    @NonNull
    private AuthResponse issueTokens(User savedUser) {
        Map<String, Object> extraClaims = new HashMap<>();
        CustomUserDetails customUserDetails = new CustomUserDetails(savedUser);

        String accessToken = jwtService.generateAccessToken(extraClaims, customUserDetails );
        String refreshToken = jwtService.generateRefreshToken(extraClaims, customUserDetails );

        Instant now = Instant.now();
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(savedUser.getId())
                .tokenHash(tokenHashService.hash(refreshToken))
                .expiresAt(jwtService.extractExpiration(refreshToken))
                .build());

        Instant accessExpiresAt = jwtService.extractExpiration(accessToken);
        Duration accessTokenTtl = Duration.between(now, accessExpiresAt);
        redisTemplate.opsForValue().set(
                JwtConstants.ACCESS_TOKEN + ":" + savedUser.getEmail(),
                tokenHashService.hash(accessToken),
                accessTokenTtl
        );

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

    private User validateRefreshToken(String refreshToken) {
        try {
            String tokenHash = tokenHashService.hash(refreshToken);
            RefreshToken savedToken = refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(tokenHash)
                    .orElseThrow(InvalidRefreshTokenException::new);
            if (savedToken.getExpiresAt().isBefore(Instant.now())) {
                throw new InvalidRefreshTokenException();
            }
            User user = userRepository.findById(savedToken.getUserId())
                    .orElseThrow(InvalidRefreshTokenException::new);
            if (!jwtService.isTokenValid(refreshToken, new CustomUserDetails(user), JwtConstants.REFRESH_TOKEN)) {
                throw new InvalidRefreshTokenException();
            }
            return user;
        } catch (InvalidRefreshTokenException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new InvalidRefreshTokenException(ex);
        }
    }

    private void revokeRefreshToken(String refreshToken) {
        RefreshToken savedToken = refreshTokenRepository
                .findByTokenHashAndRevokedAtIsNull(tokenHashService.hash(refreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);
        savedToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(savedToken);
    }


}
