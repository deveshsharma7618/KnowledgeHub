package com.deveshsharma.knowledgehub.security;

import com.deveshsharma.knowledgehub.exception.exceptions.InvalidAccessTokenException;
import com.deveshsharma.knowledgehub.service.JwtTokenService;
import com.deveshsharma.knowledgehub.service.TokenHashService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.JwtException;

import java.io.IOException;

/**
 * Extracts bearer tokens and establishes authenticated request context.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenHashService tokenHashService;
    private final StringRedisTemplate redisTemplate;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Creates a JWT authentication filter.
     *
     * @param jwtService JWT parsing and validation service
     * @param userDetailsService user lookup service
     */
    public JwtFilter(JwtTokenService jwtService,
                     UserDetailsService userDetailsService,
                     TokenHashService tokenHashService,
                     StringRedisTemplate redisTemplate,
                     HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenHashService = tokenHashService;
        this.redisTemplate = redisTemplate;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Processes a request's bearer token before protected controllers run.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param filterChain remaining servlet filter chain
     * @throws ServletException when request processing fails
     * @throws IOException when request or response I/O fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            String cacheKey = "access-token:" + tokenHashService.hash(jwt);
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                throw new InvalidAccessTokenException();
            }

            final String username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (!jwtService.isTokenValid(jwt, userDetails, "access_token")) {
                    throw new InvalidAccessTokenException();
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (InvalidAccessTokenException | JwtException | IllegalArgumentException ex) {
            handlerExceptionResolver.resolveException(request, response, null,
                    ex instanceof InvalidAccessTokenException
                            ? ex
                            : new InvalidAccessTokenException(ex));
            return;
        }
        filterChain.doFilter(request, response);
    }
}