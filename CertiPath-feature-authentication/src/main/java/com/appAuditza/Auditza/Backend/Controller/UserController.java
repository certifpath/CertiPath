package com.appAuditza.Auditza.Backend.Controller;

import com.appAuditza.Auditza.Backend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users") // <--- CHANGE THIS (Was /api/users)
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<User> getAuthenticatedUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(currentUser);
    }
}