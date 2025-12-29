package com.certifpath.storage.service;

import com.certifpath.storage.entity.FileMetadata;
import com.certifpath.storage.repository.FileMetadataRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;
    private final FileMetadataRepository fileRepo;

    @Value("${minio.bucket}")
    private String bucket;

    public FileMetadata upload(MultipartFile file) throws Exception {

        String storedName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(storedName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());

        FileMetadata meta = new FileMetadata();
        meta.setFileName(file.getOriginalFilename());
        meta.setStoredName(storedName);
        meta.setContentType(file.getContentType());
        meta.setSize(file.getSize());
        meta.setUrl("http://localhost:9000/" + bucket + "/" + storedName);

        return fileRepo.save(meta);
    }

    public byte[] downloadFile(String storedName) throws Exception {
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(storedName)
                        .build());
        return stream.readAllBytes();
    }

    public void deleteFile(String storedName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(storedName)
                        .build());
    }

    public List<FileMetadata> listAll() {
        return fileRepo.findAll();
    }

    public FileMetadata getById(Long id) {
        return fileRepo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
    }

    public void deleteMeta(FileMetadata meta) {
        fileRepo.delete(meta);
    }
}
