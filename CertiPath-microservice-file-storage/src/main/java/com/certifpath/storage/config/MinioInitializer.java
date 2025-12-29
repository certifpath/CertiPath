package com.certifpath.storage.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioInitializer {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            // Check if bucket exists
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            // Create bucket if it doesn't exist
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                System.out.println("MinIO bucket '" + bucketName + "' created successfully");
            } else {
                System.out.println("MinIO bucket '" + bucketName + "' already exists");
            }
        } catch (Exception e) {
            System.err.println("Error initializing MinIO bucket: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
