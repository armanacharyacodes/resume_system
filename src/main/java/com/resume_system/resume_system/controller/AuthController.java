package com.resume_system.resume_system.controller;

import com.resume_system.resume_system.dto.AuthResponseDTO;
import com.resume_system.resume_system.dto.LoginRequestDTO;
import com.resume_system.resume_system.dto.MessageDTO;
import com.resume_system.resume_system.dto.RegisterRequestDTO;
import com.resume_system.resume_system.entity.User;
import com.resume_system.resume_system.security.UserPrincipal;
import com.resume_system.resume_system.service.AuthService;
import com.resume_system.resume_system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<MessageDTO> register(@RequestBody RegisterRequestDTO dto) {
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
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(auth);

        AuthResponseDTO resp = new AuthResponseDTO(token, "Login successful");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Token required");
        }
        authService.logout(token);
        return ResponseEntity.ok("Logout successful");
    }
}