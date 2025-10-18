package com.resume_system.resume_system.service.serviceimpl;

import com.resume_system.resume_system.entity.User;
import com.resume_system.resume_system.repository.UserRepository;
import com.resume_system.resume_system.security.JwtUtil;
import com.resume_system.resume_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final Set<String> blacklistedJti = new HashSet<>();

    @Override
    public User register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User already exists with this email");
        }
        return userRepository.save(
                User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .build()
        );
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedJti.add(jwtUtil.getJti(token));
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedJti.contains(jwtUtil.getJti(token));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}