package com.monomart.service;

import com.monomart.entities.User;
import com.monomart.entities.enums.Role;
import com.monomart.dto.auth.AuthDtos;
import com.monomart.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.monomart.security.JwtTokenService jwtTokenService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       com.monomart.security.JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public User signupCustomer(AuthDtos.SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_CUSTOMER);
        return userRepository.save(user);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail).or(() -> userRepository.findByEmail(usernameOrEmail));
    }

    public AuthDtos.TokenResponse buildTokensFor(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("uid", user.getId());
        String access = jwtTokenService.generateAccessToken(user.getUsername(), claims);
        String refresh = jwtTokenService.generateRefreshToken(user.getUsername(), claims);
        return AuthDtos.TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();
    }
}


