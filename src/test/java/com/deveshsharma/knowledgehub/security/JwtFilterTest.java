package com.deveshsharma.knowledgehub.security;

import com.deveshsharma.knowledgehub.service.JwtTokenService;
import com.deveshsharma.knowledgehub.service.TokenHashService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Test
    void rejectsAccessTokenMissingFromRedis() throws Exception {
        JwtTokenService jwtService = mock(JwtTokenService.class);
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        TokenHashService tokenHashService = mock(TokenHashService.class);
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        HandlerExceptionResolver resolver = mock(HandlerExceptionResolver.class);
        FilterChain chain = mock(FilterChain.class);
        when(tokenHashService.hash("token")).thenReturn("hash");
        when(redisTemplate.hasKey("access-token:hash")).thenReturn(false);

        JwtFilter filter = new JwtFilter(jwtService, userDetailsService, tokenHashService, redisTemplate, resolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        verify(resolver).resolveException(any(), any(), isNull(), any());
        verify(chain, never()).doFilter(any(), any());
        verifyNoInteractions(jwtService, userDetailsService);
    }
}
