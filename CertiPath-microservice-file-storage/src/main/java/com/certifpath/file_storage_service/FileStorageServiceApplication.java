package com.certifpath.file_storage_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.certifpath") // Scans both 'storage' and 'file_storage_service'
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.certifpath.storage.client")
@EnableJpaRepositories(basePackages = "com.certifpath.storage.repository")
@EntityScan(basePackages = "com.certifpath.storage.entity")
public class FileStorageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileStorageServiceApplication.class, args);
	}

}
