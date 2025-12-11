package com.appAuditza.Auditza.Backend.Controller;

import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = tempDir.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // S'assurer que le nom de fichier est "propre" pour éviter les failles de sécurité
                if (!file.getFileName().toString().equals(filename)) {
                    throw new RuntimeException("Nom de fichier invalide.");
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                throw new RuntimeException("Impossible de lire le fichier: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }
}