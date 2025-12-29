package com.appAuditza.Auditza.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AuditzaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditzaBackendApplication.class, args);
	}

}
