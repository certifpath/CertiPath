package com.appAuditza.Auditza.Backend.Service;

import com.appAuditza.Auditza.Backend.dto.AuthResponse;
import com.appAuditza.Auditza.Backend.dto.LoginRequest;
import com.appAuditza.Auditza.Backend.dto.RegisterRequest;
import com.appAuditza.Auditza.Backend.dto.VerifyOtpRequest;
import com.appAuditza.Auditza.Backend.model.User;
import com.appAuditza.Auditza.Backend.repository.UserRepository;
import com.appAuditza.Auditza.Backend.util.OtpUtils;
import com.google.zxing.WriterException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Méthode utilitaire pour configurer Google Authenticator avec une tolérance de temps
    private GoogleAuthenticator getConfiguredGoogleAuthenticator() {
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder =
                new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                        .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
                        .setWindowSize(5); // Tolère 2 codes avant et 2 codes après (2.5 minutes de décalage)

        return new GoogleAuthenticator(configBuilder.build());
    }

    // Register new user
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        GoogleAuthenticator gAuth = getConfiguredGoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();

        User user = new User();
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setCompany(request.getCompany());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        try {
            User.Role userRole = User.Role.valueOf(request.getRole().toUpperCase());
            user.setRole(userRole);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RuntimeException("Rôle invalide ou manquant : " + request.getRole());
        }
        
        // Mettre le statut à ACTIVE pour que l'utilisateur puisse se connecter après inscription
        user.setStatus(User.Status.ACTIVE); 
        user.setOtpSecretKey(key.getKey());
        user.setOtpEnabled(false);

        User savedUser = userRepository.save(user);

        // La réponse d'inscription reste simple, sans token ni nextStep.
        return AuthResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .message("User registered successfully. Please log in.")
                .build();
    }

    // Login user
    public AuthResponse login(LoginRequest request) throws WriterException, IOException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getOtpSecretKey() != null && !user.isOtpEnabled()) {
            String secretKey = user.getOtpSecretKey();
            String qrCodeUrl = OtpUtils.getGoogleAuthenticatorBarCode(secretKey, user.getEmail(), "X-Iso");
            
            String fileName = otpService.createQrCodeFile(qrCodeUrl, 250, 250);
            String imageUrl = "/api/images/" + fileName;

            return AuthResponse.builder()
                    .message("OTP setup required.")
                    .nextStep("SETUP_OTP")
                    .qrCodeImage(imageUrl)
                    .email(user.getEmail())
                    .build();
        } else {
            return AuthResponse.builder()
                    .message("OTP verification required.")
                    .nextStep("VERIFY_OTP")
                    .email(user.getEmail())
                    .build();
        }
    }

    public AuthResponse verifyOtpSetup(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isOtpEnabled()) {
            throw new RuntimeException("OTP is already enabled for this user.");
        }

        GoogleAuthenticator gAuth = getConfiguredGoogleAuthenticator();
        if (!gAuth.authorize(user.getOtpSecretKey(), request.getOtpCode())) {
            throw new RuntimeException("Invalid OTP code.");
        }

        user.setOtpEnabled(true);
        userRepository.save(user);

        // Supprimer le fichier QR code temporaire
        if (request.getQrCodeFileName() != null && !request.getQrCodeFileName().isEmpty()) {
            try {
                Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
                Path filePath = tempDir.resolve(request.getQrCodeFileName()).normalize();
                
                // Sécurité : Vérifier que le fichier est bien dans le répertoire temporaire
                if (filePath.startsWith(tempDir)) {
                    Files.deleteIfExists(filePath);
                }
            } catch (IOException e) {
                System.err.println("Échec de la suppression du fichier QR code: " + request.getQrCodeFileName());
            }
        }

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(jwtToken) // <-- Rempli
                .role(user.getRole().name()) // <-- Rempli
                .message("OTP enabled successfully. You are now logged in.")
                .nextStep("HOME")
                .build();
    }

    public AuthResponse loginWithOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        
        GoogleAuthenticator gAuth = getConfiguredGoogleAuthenticator();
        if (!gAuth.authorize(user.getOtpSecretKey(), request.getOtpCode())) {
            throw new RuntimeException("Invalid OTP code.");
        }
        
        String jwtToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(jwtToken) // <-- Rempli
                .role(user.getRole().name()) // <-- Rempli
                .message("Login successful.")
                .nextStep("HOME")
                .build();
    }
}