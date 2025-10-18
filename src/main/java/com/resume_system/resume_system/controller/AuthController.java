package com.resume_system.resume_system.controller;

import com.resume_system.resume_system.dto.AuthResponseDTO;
import com.resume_system.resume_system.dto.LoginRequestDTO;
import com.resume_system.resume_system.dto.MessageDTO;
import com.resume_system.resume_system.dto.RegisterRequestDTO;
import com.resume_system.resume_system.security.JwtUtil;
import com.resume_system.resume_system.security.UserPrincipal;
import com.resume_system.resume_system.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<MessageDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        try {
            authService.register(dto.getEmail(), dto.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageDTO("Registration successful"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageDTO("Email already exists"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        // Authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtUtil.generateToken(auth);

        AuthResponseDTO response = new AuthResponseDTO(token, "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDTO> logout(@RequestBody String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(new MessageDTO("Token required"));
        }

        authService.logout(token);
        return ResponseEntity.ok(new MessageDTO("Logout successful"));
    }
}
