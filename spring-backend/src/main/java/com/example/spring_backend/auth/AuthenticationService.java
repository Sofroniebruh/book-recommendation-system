package com.example.spring_backend.auth;

import com.example.spring_backend.auth.custom_exceptions.InvalidTokenException;
import com.example.spring_backend.auth.custom_exceptions.UserAlreadyRegistered;
import com.example.spring_backend.auth.records.AuthenticationResponse;
import com.example.spring_backend.auth.records.LoginRequest;
import com.example.spring_backend.auth.records.RegisterRequest;
import com.example.spring_backend.jwt.JwtService;
import com.example.spring_backend.user.Role;
import com.example.spring_backend.user.User;
import com.example.spring_backend.user.UserRepository;
import com.example.spring_backend.user.custom_exception.UserNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final int ACCESS_TOKEN_TTL = 1000 * 60 * 24;
    private final int REFRESH_TOKEN_TTL = 1000 * 60 * 60 * 24 * 2;
    private final int REFRESH_TOKEN_COOKIE_TTL = 60 * 60 * 24 * 2;

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        Optional<User> existingUser = userRepository.getUserByEmail(request.email());

        if (existingUser.isPresent()) {
            throw new UserAlreadyRegistered("User already registered");
        }

        var user = User
                .builder()
                .username(request.email())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .isFromDataset(false)
                .build();

        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user, ACCESS_TOKEN_TTL);
        var refreshToken = jwtService.generateToken(user, REFRESH_TOKEN_TTL);

        updateCookies(response, refreshToken);

        return AuthenticationResponse.fromEntity(savedUser, jwtToken);
    }

    public AuthenticationResponse authenticate(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.getUserByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user, ACCESS_TOKEN_TTL);
        var refreshToken = jwtService.generateToken(user, REFRESH_TOKEN_TTL);

        updateCookies(response, refreshToken);

        return AuthenticationResponse.fromEntity(user, jwtToken);
    }

    private void updateCookies(HttpServletResponse response, String refreshToken) {
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(REFRESH_TOKEN_COOKIE_TTL);
        response.addCookie(refreshCookie);
    }

    public AuthenticationResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = getRefreshTokenFromCookies(request);

            if (refreshToken == null) {
                throw new InvalidTokenException("Invalid refresh token");
            }

            String email = jwtService.extractUserEmail(refreshToken);

            if (email == null || email.trim().isEmpty()) {
                throw new InvalidTokenException("Invalid token format");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            User user = userRepository.getUserByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                updateCookies(response, refreshToken);

                String token = jwtService.generateToken(userDetails, ACCESS_TOKEN_TTL);

                return AuthenticationResponse.fromEntity(user, token);
            }
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token: " + e.getMessage());
        }

        throw new InvalidTokenException("Invalid refresh token");
    }
}
