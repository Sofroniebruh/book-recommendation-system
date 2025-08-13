package com.example.spring_backend.auth;

import com.example.spring_backend.auth.customexceptions.UserAlreadyRegistered;
import com.example.spring_backend.auth.records.AuthenticationResponse;
import com.example.spring_backend.auth.records.LoginRequest;
import com.example.spring_backend.auth.records.RegisterRequest;
import com.example.spring_backend.jwt.JwtService;
import com.example.spring_backend.user.Role;
import com.example.spring_backend.user.User;
import com.example.spring_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public AuthenticationResponse register(RegisterRequest request) {
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
        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), jwtToken);
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.getUserByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(user.getId(), user.getUsername(), user.getEmail(), jwtToken);
    }
}
