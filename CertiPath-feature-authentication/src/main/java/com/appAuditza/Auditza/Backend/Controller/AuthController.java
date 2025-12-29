package com.appAuditza.Auditza.Backend.Controller;

import com.appAuditza.Auditza.Backend.dto.AuthResponse;
import com.appAuditza.Auditza.Backend.dto.ErrorResponse;
import com.appAuditza.Auditza.Backend.dto.LoginRequest;
import com.appAuditza.Auditza.Backend.dto.RegisterRequest;
import com.appAuditza.Auditza.Backend.dto.VerifyOtpRequest;
import com.google.zxing.WriterException;
import com.appAuditza.Auditza.Backend.Service.AuthService;
import jakarta.validation.Valid;
import org.bouncycastle.pqc.legacy.math.linearalgebra.PolynomialRingGF2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import com.appAuditza.Auditza.Backend.model.User;
import com.appAuditza.Auditza.Backend.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) throws WriterException, IOException {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        }
    }

    // FIX 1: Rename endpoint to match auth.js
    @PostMapping("/verify-otp-setup")
    public ResponseEntity<?> verifyOtpSetup(@Valid @RequestBody VerifyOtpRequest request, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.verifyOtpSetup(request);

            // Optional: Keep cookie if you want, but JS needs the body
            Cookie cookie = new Cookie("jwt_token", authResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(8 * 60 * 60);
            response.addCookie(cookie);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/login-with-otp")
    public ResponseEntity<?> loginWithOtp(@Valid @RequestBody VerifyOtpRequest request, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.loginWithOtp(request);

            Cookie cookie = new Cookie("jwt_token", authResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(8 * 60 * 60);
            response.addCookie(cookie);

            // FIX 3: DELETE THIS LINE. Do not set token to null!
            // authResponse.setToken(null);

            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        }
    }

    // Validation handlers...
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @GetMapping("/auditors/emails")
    // Autoriser les autres microservices (via le token de l'utilisateur connecté)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getAllAuditorEmails() {
        // Récupérer tous les utilisateurs avec le rôle AUDITOR
        List<User> auditors = userRepository.findByRole(User.Role.AUDITEUR); // ou User.Role.AUDITOR selon ton Enum

        // Extraire seulement les emails
        List<String> emails = auditors.stream()
                .map(User::getEmail)
                .toList();

        return ResponseEntity.ok(emails);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok("Déconnexion réussie.");
    }
}