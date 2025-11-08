package com.appAuditza.Auditza.Backend.Service;

import com.appAuditza.Auditza.Backend.dto.AuthResponse;
import com.appAuditza.Auditza.Backend.dto.LoginRequest;
import com.appAuditza.Auditza.Backend.dto.RegisterRequest;
import com.appAuditza.Auditza.Backend.model.User;
import com.appAuditza.Auditza.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register new user
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setCompany(request.getCompany());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        user.setStatus(User.Status.ACTIVE);

        // Save user to database
        User savedUser = userRepository.save(user);

        // Return response
        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getCompany(),
                savedUser.getRole().toString(),
                savedUser.getStatus().toString(),
                null, // Token will be added by JWT team
                "User registered successfully"
        );
    }

    // Login user
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if account is active
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Return response (JWT token will be added by your team)
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getCompany(),
                user.getRole().toString(),
                user.getStatus().toString(),
                null, // Token will be generated here by JWT team
                "Login successful"
        );
    }
}