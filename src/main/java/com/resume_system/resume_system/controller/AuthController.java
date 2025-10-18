package com.resume_system.resume_system.controller;

import com.resume_system.resume_system.dto.AuthResponseDTO;
import com.resume_system.resume_system.dto.LoginRequestDTO;
import com.resume_system.resume_system.dto.MessageDTO;
import com.resume_system.resume_system.dto.RegisterRequestDTO;
import com.resume_system.resume_system.security.JwtUtil;
import com.resume_system.resume_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<MessageDTO> register(@RequestBody RegisterRequestDTO dto) {
        try {
            authService.register(dto.getEmail(), dto.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(MessageDTO.builder()
                            .message("Registration successful")
                            .build());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(MessageDTO.builder()
                            .message("Email already exists")
                            .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        String token = jwtUtil.generateToken(auth);
        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .accessToken(token)
                        .message("Login successful")
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO> logout(@RequestBody String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(AuthResponseDTO.builder()
                            .message("Token required")
                            .build());
        }
        authService.logout(token);
        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .message("Logout successful")
                        .build()
        );
    }
}