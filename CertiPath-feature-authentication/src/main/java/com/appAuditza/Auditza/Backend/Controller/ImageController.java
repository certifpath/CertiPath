package com.appAuditza.Auditza.Backend.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
// 1. CHANGE THIS PATH to start with /auth
@RequestMapping("/auth/images")
public class ImageController {

    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = tempDir.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                if (!file.getFileName().toString().equals(filename)) {
                    throw new RuntimeException("Nom de fichier invalide.");
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        // 2. ADD THIS HEADER to prevent ORB blocking
                        .header("X-Content-Type-Options", "nosniff")
                        .body(resource);
            } else {
                throw new RuntimeException("Impossible de lire le fichier: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }
}