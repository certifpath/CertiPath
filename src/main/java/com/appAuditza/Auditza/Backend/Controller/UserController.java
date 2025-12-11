package com.appAuditza.Auditza.Backend.Controller;

import com.appAuditza.Auditza.Backend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // Cette méthode est protégée par défaut par la règle ".anyRequest().authenticated()"
    // de SecurityConfig.
    @GetMapping("/me")
    public ResponseEntity<User> getAuthenticatedUser(@AuthenticationPrincipal User currentUser) {
        // @AuthenticationPrincipal est une façon plus propre de récupérer l'utilisateur connecté.
        // Spring Security s'occupe de le récupérer depuis le contexte de sécurité.
        return ResponseEntity.ok(currentUser);
    }
}