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

    public void register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User already exists with this email");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedJti.add(jwtUtil.getJti(token));
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedJti.contains(jwtUtil.getJti(token));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}