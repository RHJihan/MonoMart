package com.monomart.controller;

import com.monomart.domain.User;
import com.monomart.domain.enums.Role;
import com.monomart.dto.auth.AuthDtos;
import com.monomart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Claims;
import com.monomart.security.JwtTokenService;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new customer account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @SecurityRequirements({}) // Override global security - no authentication required
    public ResponseEntity<?> signup(@Valid @RequestBody AuthDtos.SignupRequest request) {
        User user = userService.signupCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.buildTokensFor(user));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @SecurityRequirements({}) // Override global security - no authentication required
    public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        User user = userService.findByUsernameOrEmail(request.getUsernameOrEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );
        if (!auth.isAuthenticated()) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return ResponseEntity.ok(userService.buildTokensFor(user));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody AuthDtos.LoginRequest request) {
        User user = userService.findByUsernameOrEmail(request.getUsernameOrEmail())
                .filter(u -> u.getRole() == Role.ROLE_ADMIN)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );
        if (!auth.isAuthenticated()) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return ResponseEntity.ok(userService.buildTokensFor(user));
    }

    @lombok.Data
    public static class RefreshTokenRequest {
        @jakarta.validation.constraints.NotBlank
        private String refreshToken;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        Claims claims = jwtTokenService.parseToken(request.getRefreshToken());
        String username = claims.getSubject();
        var user = userService.findByUsernameOrEmail(username).orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        return ResponseEntity.ok(userService.buildTokensFor(user));
    }
}


