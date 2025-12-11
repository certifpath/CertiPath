package com.certifpath.storage.controller;

import com.certifpath.storage.entity.FileMetadata;
import com.certifpath.storage.repository.FileMetadataRepository;
import com.certifpath.storage.service.StorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class StorageController {

    private final StorageService storageService;
    private final FileMetadataRepository repository;

    public StorageController(StorageService storageService, FileMetadataRepository repository) {
        this.storageService = storageService;
        this.repository = repository;
    }

    // UPLOAD
    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> upload(@RequestParam("file") MultipartFile file) throws Exception {
        FileMetadata meta = storageService.upload(file);
        return ResponseEntity.ok(meta);
    }

    // LIST ALL FILES
    @GetMapping
    public ResponseEntity<List<FileMetadata>> listFiles() {
        return ResponseEntity.ok(repository.findAll());
    }

    // DOWNLOAD
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        FileMetadata meta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        byte[] data = storageService.downloadFile(meta.getStoredName());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + meta.getFileName())
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .body(data);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws Exception {
        FileMetadata meta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        storageService.deleteFile(meta.getStoredName());
        repository.delete(meta);

        return ResponseEntity.ok("File deleted successfully");
    }
}
