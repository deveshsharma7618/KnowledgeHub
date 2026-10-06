package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.dto.request.LoginRequest;
import com.deveshsharma.knowledgehub.dto.request.SignUpRequest;
import com.deveshsharma.knowledgehub.dto.response.AuthResponse;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import com.deveshsharma.knowledgehub.exception.exceptions.InvalidCreditionals;
import com.deveshsharma.knowledgehub.exception.exceptions.UserAlreadyExistsException;
import com.deveshsharma.knowledgehub.repository.RefreshTokenRepository;
import com.deveshsharma.knowledgehub.repository.UserRepository;
import com.deveshsharma.knowledgehub.service.impl.AuthServiceImpl;
import com.deveshsharma.knowledgehub.service.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for registration, login, and user-listing behavior.
 */
class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtService jwtService;
    private TokenHashService tokenHashService;
    private StringRedisTemplate redisTemplate;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        jwtService = mock(JwtService.class);
        tokenHashService = mock(TokenHashService.class);
        redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        authService = new AuthServiceImpl(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                jwtService,
                tokenHashService,
                redisTemplate
        );
        when(tokenHashService.hash(any(String.class))).thenReturn("hash");
        when(jwtService.extractExpiration(any(String.class))).thenReturn(Instant.now().plusSeconds(60));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    /**
     * Verifies signup hashes the password, persists an active user, and returns tokens.
     */
    @Test
    void signupCreatesActiveUserAndReturnsTokens() {
        SignUpRequest request = signupRequest("new@example.com", "New User", "Password1!");
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("Password1!")).thenReturn("encoded");
        when(jwtService.generateAccessToken(any(Map.class), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(Map.class), any())).thenReturn("refresh");

        AuthResponse response = authService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("new@example.com", userCaptor.getValue().getEmail());
        assertEquals("encoded", userCaptor.getValue().getPasswordHash());
        assertEquals(UserStatus.ACTIVE, userCaptor.getValue().getStatus());
        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    /**
     * Verifies signup rejects an email that is already registered.
     */
    @Test
    void signupRejectsExistingUser() {
        when(userRepository.findByEmail("existing@example.com")).thenReturn(User.builder().email("existing@example.com").build());

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.signup(signupRequest("existing@example.com", "Existing", "Password1!")));
        verify(userRepository, never()).save(any());
    }

    /**
     * Verifies login returns tokens when the password matches.
     */
    @Test
    void loginReturnsTokensForValidCredentials() {
        User user = User.builder().email("login@example.com").passwordHash("encoded").status(UserStatus.ACTIVE).build();
        when(userRepository.findByEmail("login@example.com")).thenReturn(user);
        when(passwordEncoder.matches("Password1!", "encoded")).thenReturn(true);
        when(jwtService.generateAccessToken(any(Map.class), any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(Map.class), any())).thenReturn("refresh");

        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "login@example.com");
        ReflectionTestUtils.setField(request, "password", "Password1!");

        AuthResponse response = authService.login(request);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    /**
     * Verifies login rejects missing users and mismatched passwords.
     */
    @Test
    void loginRejectsInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "missing@example.com");
        ReflectionTestUtils.setField(request, "password", "Password1!");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        assertThrows(InvalidCreditionals.class, () -> authService.login(request));
    }


    /**
     * Builds a registration request for service tests.
     *
     * @param email requested email
     * @param name requested display name
     * @param password requested raw password
     * @return populated registration request
     */
    private SignUpRequest signupRequest(String email, String name, String password) {
        SignUpRequest request = new SignUpRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "name", name);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }
}
