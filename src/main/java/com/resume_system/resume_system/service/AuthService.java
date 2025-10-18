package com.resume_system.resume_system.service;

import com.resume_system.resume_system.entity.User;

public interface AuthService {

    User register(String email, String password);

    void logout(String token);

    boolean isTokenBlacklisted(String token);

    User findByEmail(String email);
}
